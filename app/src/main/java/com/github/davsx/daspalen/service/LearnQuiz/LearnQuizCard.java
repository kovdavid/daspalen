package com.github.davsx.daspalen.service.LearnQuiz;

import android.util.Log;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.entity.CardEntity;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.BaseQuiz.BaseQuizCard;
import com.github.davsx.daspalen.service.BaseQuiz.QuizData;
import com.github.davsx.daspalen.service.BaseQuiz.QuizScheduler;
import com.github.davsx.daspalen.service.BaseQuiz.QuizTypeEnum;
import com.github.davsx.daspalen.service.CardImage.CardImageService;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

class LearnQuizCard implements BaseQuizCard {

    private static final String TAG = "daspalen|LearnQuizC";

    private DaspalenRepository repository;
    private CardImageService cardImageService;
    private Card card;
    private Boolean doShowCard;
    private Integer completedRounds;
    private Integer plannedRounds;
    private Random rng;
    private Boolean gotBadAnswer = false;

    LearnQuizCard(DaspalenRepository repository, CardImageService cardImageService, Card card) {
        this.repository = repository;
        this.cardImageService = cardImageService;
        this.card = card;
        this.doShowCard = card.getLearnScore() == 0;
        this.completedRounds = 0;
        this.plannedRounds = calculatePlannedRounds();
        this.rng = new Random(System.currentTimeMillis());

        Log.i(TAG, String.format("init cardId:%d front:%s back:%s learnScore:%d plannedRounds:%d",
                card.getCardId(),
                card.getFrontText(),
                card.getBackText(),
                card.getLearnScore(),
                plannedRounds));
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
        int limit1 = Double.valueOf(DaspalenConstants.MAX_CARD_LEARN_SCORE * 0.50).intValue();
        int limit2 = Double.valueOf(DaspalenConstants.MAX_CARD_LEARN_SCORE * 0.75).intValue();

        Random rng = new Random(System.currentTimeMillis());
        double rand = rng.nextDouble();

        if (card.getLearnScore() < limit1) {
            if (rand > 0.5) {
                return 3;
            } else {
                return 2;
            }
        }
        if (card.getLearnScore() < limit2) {
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
            return answer.equals(card.getBackText());
        }
        if (type.equals(QuizTypeEnum.CHOICE_1of4_REVERSE)) {
            return answer.equals(card.getFrontText());
        }
        return false;
    }

    @Override
    public void handleAnswer(QuizScheduler scheduler, String answer) {
        boolean isCorrectAnswer = evaluateAnswer(answer);

        if (isCorrectAnswer) {
            Log.i(TAG, String.format("handleAnswer correct cardId:%d gotBadAnswer:%s doShowCard:%s",
                    card.getCardId(), gotBadAnswer, doShowCard));

            if (!gotBadAnswer) {
                if (doShowCard) {
                    doShowCard = false;
                } else {
                    completedRounds++;
                    card.processCorrectLearnAnswer();
                    repository.updateCard(card);
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
            Log.i(TAG, String.format("handleAnswer incorrect cardId:%d", card.getCardId()));
            gotBadAnswer = true;
            doShowCard = true;
            scheduler.scheduleToExactOffset(1, this);
        }
    }

    @Override
    public QuizData buildQuizData(List<CardEntity> randomCards) {
        QuizTypeEnum quizType = getQuizType();
        Log.i(TAG, String.format("buildQuizData cardId:%d quizType:%s", card.getCardId(), quizType));
        return QuizData.build(quizType, cardImageService, card, randomCards);
    }

    @Override
    public long getCardId() {
        return card.getCardId();
    }

    @Override
    public int getCompletedRounds() {
        return completedRounds;
    }

    Integer getPlannedRounds() {
        return plannedRounds;
    }

    private QuizTypeEnum getQuizType() {
        if (doShowCard) return QuizTypeEnum.SHOW_CARD;

        int learnScore = card.getLearnScore();

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
