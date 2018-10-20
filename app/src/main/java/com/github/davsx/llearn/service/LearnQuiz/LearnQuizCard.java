package com.github.davsx.llearn.service.LearnQuiz;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.BaseQuiz.BaseQuizCardScheduler;
import com.github.davsx.llearn.service.BaseQuiz.QuizData;
import com.github.davsx.llearn.service.BaseQuiz.QuizTypeEnum;
import com.github.davsx.llearn.service.CardImage.CardImageService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class LearnQuizCard {

    private CardRepository cardRepository;
    private CardImageService cardImageService;
    private CardEntity cardEntity;
    private Boolean doShowCard;
    private Integer completedRounds;
    private Integer plannedRounds;
    private Random rng;

    private Boolean gotBadAnswer = false;

    LearnQuizCard(CardRepository cardRepository, CardImageService cardImageService, CardEntity cardEntity) {
        this.cardRepository = cardRepository;
        this.cardImageService = cardImageService;
        this.cardEntity = cardEntity;
        this.doShowCard = cardEntity.getLearnScore() == 0;
        this.completedRounds = 0;
        this.plannedRounds = calculatePlannedRounds();

        this.rng = new Random(System.currentTimeMillis());
    }

    void handleAnswer(BaseQuizCardScheduler<LearnQuizCard> scheduler, String answer) {
        boolean isCorrectAnswer = evaluateAnswer(answer);

        if (isCorrectAnswer) {
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
            gotBadAnswer = true;
            doShowCard = true;
            scheduler.scheduleToExactOffset(1, this);
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

        if (cardEntity.getLearnScore() < limit1) return 3;
        if (cardEntity.getLearnScore() < limit2) return 2;
        return 1;
    }

    private boolean evaluateAnswer(String answer) {
        QuizTypeEnum type = getCardType();
        if (type.equals(QuizTypeEnum.SHOW_CARD)) {
            return true;
        }
        if (type.equals(QuizTypeEnum.CHOICE_1of4) || type.equals(QuizTypeEnum.KEYBOARD_INPUT)) {
            return answer.equals(cardEntity.getBack());
        }
        if (type.equals(QuizTypeEnum.CHOICE_1of4_REVERSE)) {
            return answer.equals(cardEntity.getFront());
        }
        return true;
    }

    QuizData buildQuizData(List<CardEntity> randomCards) {
        return QuizData.build(getCardType(), cardImageService, cardEntity, randomCards);
    }

    private QuizTypeEnum getCardType() {
        if (doShowCard) return QuizTypeEnum.SHOW_CARD;

        if (gotBadAnswer || cardEntity.getLearnScore() == 0) {
            return QuizTypeEnum.CHOICE_1of4;
        }

        List<QuizTypeEnum> types = Arrays.asList(
                QuizTypeEnum.CHOICE_1of4,
                QuizTypeEnum.CHOICE_1of4,
                QuizTypeEnum.CHOICE_1of4,
                QuizTypeEnum.KEYBOARD_INPUT,
                QuizTypeEnum.KEYBOARD_INPUT,
                QuizTypeEnum.CHOICE_1of4_REVERSE
        );

        int typeIndex = new Random(System.currentTimeMillis()).nextInt(types.size());

        return types.get(typeIndex);
    }

    Integer getCompletedRounds() {
        return completedRounds;
    }

    Integer getPlannedRounds() {
        return plannedRounds;
    }

    public static class LearnQuizCardComparator implements Comparator<LearnQuizCard> {
        @Override
        public int compare(LearnQuizCard c1, LearnQuizCard c2) {
            int c = c1.doShowCard.compareTo(c2.doShowCard);
            if (c == 0) {
                c = c1.plannedRounds.compareTo(c2.plannedRounds);
            }
            return c;
        }
    }
}
