package com.github.davsx.llearn.service.LearnQuiz;

import android.util.Log;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.entity.JournalEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import com.github.davsx.llearn.service.BaseQuiz.BaseQuizCardScheduler;
import com.github.davsx.llearn.service.BaseQuiz.QuizData;
import com.github.davsx.llearn.service.BaseQuiz.QuizTypeEnum;
import com.github.davsx.llearn.service.CardImage.CardImageService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class LearnQuizCard {

    private static final String TAG = "LearnQuizCard";

    private final List<QuizTypeEnum> learnQuizTypes = Arrays.asList(
            QuizTypeEnum.CHOICE_1of4,
            QuizTypeEnum.CHOICE_1of4_REVERSE,
            QuizTypeEnum.KEYBOARD_INPUT
    );

    private CardRepository cardRepository;
    private JournalRepository journalRepository;
    private CardImageService cardImageService;
    private CardEntity cardEntity;
    private Boolean doShowCard;
    private Integer completedRounds;
    private Integer plannedRounds;
    private Random rng;
    private Boolean gotBadAnswer = false;

    LearnQuizCard(CardRepository cardRepository,
                  JournalRepository journalRepository,
                  CardImageService cardImageService,
                  CardEntity cardEntity) {
        this.cardRepository = cardRepository;
        this.journalRepository = journalRepository;
        this.cardImageService = cardImageService;
        this.cardEntity = cardEntity;
        this.doShowCard = cardEntity.getLearnScore() == 0;
        this.completedRounds = 0;
        this.plannedRounds = calculatePlannedRounds();
        this.rng = new Random(System.currentTimeMillis());

        logCard("init");
    }

    private void logCard(String prefix) {
        Log.d(TAG, String.format("%s cardId:%d learnScore:%d completedRounds: %d plannedRounds:%d front:%s back:%s",
                prefix, cardEntity.getId(), cardEntity.getLearnScore(), completedRounds, plannedRounds,
                cardEntity.getFront(), cardEntity.getBack()));
    }

    void handleAnswer(BaseQuizCardScheduler<LearnQuizCard> scheduler, String answer) {
        boolean isCorrectAnswer = evaluateAnswer(answer);
        boolean saveJournal = !doShowCard;

        JournalEntity journal = new JournalEntity();
        journal.setTimestamp(System.currentTimeMillis());
        journal.setCardType(LLearnConstants.CARD_TYPE_LEARN);
        journal.setCardId(cardEntity.getId());

        logCard("handleAnswer");

        if (isCorrectAnswer) {
            journal.setAnswer(LLearnConstants.JOURNAL_ANSWER_GOOD);
            Log.d(TAG, String.format("isCorrectAnswer:true gotBadAnswer:%s doShowCard:%s", gotBadAnswer, doShowCard));
            if (!gotBadAnswer) {
                if (doShowCard) {
                    doShowCard = false;
                } else {
                    completedRounds++;
                    cardEntity.processCorrectLearnAnswer();
                    cardRepository.save(cardEntity);
                }
                Integer scheduleOffset = calculateScheduleOffset();
                if (scheduleOffset > 0) {
                    scheduler.scheduleAfterOffset(scheduleOffset, this);
                }
            } else {
                if (doShowCard) {
                    doShowCard = false;
                } else {
                    gotBadAnswer = false;
                }
                Integer scheduleOffset = calculateScheduleOffset();
                if (scheduleOffset > 0) {
                    scheduler.scheduleToExactOffset(scheduleOffset, this);
                }
            }
        } else {
            journal.setAnswer(LLearnConstants.JOURNAL_ANSWER_BAD);
            gotBadAnswer = true;
            doShowCard = true;
            scheduler.scheduleToExactOffset(1, this);
            Log.d(TAG, "isCorrectAnswer:false answer:" + answer);
        }

        if (saveJournal) {
            journalRepository.save(journal);
        }
    }

    private Integer calculateScheduleOffset() {
        if (completedRounds >= plannedRounds) {
            return 0;
        }
        if (gotBadAnswer) {
            return 2;
        }

        if (completedRounds == 0) {
            return 2;
        } else if (completedRounds == 1) {
            return Math.round(4 + rng.nextInt(1));
        } else {
            return Math.round(8 + rng.nextInt(8));
        }
    }

    private Integer calculatePlannedRounds() {
        int limit1 = Double.valueOf(LLearnConstants.MAX_CARD_LEARN_SCORE * 0.50).intValue();
        int limit2 = Double.valueOf(LLearnConstants.MAX_CARD_LEARN_SCORE * 0.75).intValue();

        Random rng = new Random(System.currentTimeMillis());
        double rand = rng.nextDouble();

        if (cardEntity.getLearnScore() < limit1) {
            if (rand > 0.5) {
                return 3;
            } else {
                return 2;
            }
        }
        if (cardEntity.getLearnScore() < limit2) {
            return 2;
        }
        return 1;
    }

    private boolean evaluateAnswer(String answer) {
        QuizTypeEnum type = getQuizType();
        if (type.equals(QuizTypeEnum.SHOW_CARD)) {
            return true;
        }
        if (type.equals(QuizTypeEnum.CHOICE_1of4) || type.equals(QuizTypeEnum.KEYBOARD_INPUT)) {
            return answer.equals(cardEntity.getBack());
        }
        if (type.equals(QuizTypeEnum.CHOICE_1of4_REVERSE)) {
            return answer.equals(cardEntity.getFront());
        }
        return false;
    }

    QuizData buildQuizData(List<CardEntity> randomCards) {
        QuizTypeEnum quizType = getQuizType();
        logCard("buildQuizData quizType:" + quizType);
        return QuizData.build(quizType, cardImageService, cardEntity, randomCards);
    }

    Integer getCompletedRounds() {
        return completedRounds;
    }

    Integer getPlannedRounds() {
        return plannedRounds;
    }

    private QuizTypeEnum getQuizType() {
        if (doShowCard) return QuizTypeEnum.SHOW_CARD;

        Integer learnScore = cardEntity.getLearnScore();

        if (gotBadAnswer || learnScore == 0) {
            return QuizTypeEnum.CHOICE_1of4;
        }

        if (learnScore == 2 || learnScore == 4 || learnScore == 7) {
            return QuizTypeEnum.CHOICE_1of4;
        } else if (learnScore == 1 || learnScore == 5) {
            return QuizTypeEnum.CHOICE_1of4_REVERSE;
        } else {
            return QuizTypeEnum.KEYBOARD_INPUT;
        }
    }

    public static class LearnQuizCardComparator implements Comparator<LearnQuizCard> {
        @Override
        public int compare(LearnQuizCard c1, LearnQuizCard c2) {
            int c = c2.doShowCard.compareTo(c1.doShowCard); // true, then false
            if (c == 0) {
                c = c1.plannedRounds.compareTo(c2.plannedRounds); // 0, then 5
            }
            return c;
        }
    }
}
