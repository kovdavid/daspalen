package com.github.davsx.llearn.service.LearnQuiz;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import java.util.Comparator;
import java.util.Random;

public class LearnQuizCard {

    private CardRepository cardRepository;
    private CardEntity cardEntity;
    private Boolean doShowCard;
    private Integer completedRounds;
    private Integer plannedRounds;
    private Random rng;

    private Boolean gotBadAnswer = false;

    public LearnQuizCard(CardRepository cardRepository, CardEntity cardEntity) {
        this.cardRepository = cardRepository;
        this.cardEntity = cardEntity;
        this.doShowCard = cardEntity.getLearnScore() == 0;
        this.completedRounds = 0;
        this.plannedRounds = calculatePlannedRounds();

        this.rng = new Random(System.currentTimeMillis());
    }

    public void handleAnswer(LearnQuizCardScheduler scheduler, String answer) {
        boolean isCorrectAnswer = evaluateAnswer(answer);

        if (isCorrectAnswer) {
            if (!gotBadAnswer) {
                if (doShowCard) {
                    doShowCard = false;
                } else {
                    completedRounds++;
                    cardEntity.handleCorrectLearnQuizAnswer();
                    cardRepository.save(cardEntity);
                }
                Integer scheduleOffset = calculateScheduleOffset();
                if (scheduleOffset > 0) {
                    scheduler.scheduleAfterOffset(this, scheduleOffset);
                }
            } else {
                if (doShowCard) {
                    doShowCard = false;
                } else {
                    gotBadAnswer = false;
                }
                Integer scheduleOffset = calculateScheduleOffset();
                if (scheduleOffset > 0) {
                    scheduler.scheduleToExactOffset(this, scheduleOffset);
                }
            }
        } else {
            gotBadAnswer = true;
            doShowCard = true;
            scheduler.scheduleToExactOffset(this, 1);
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
        LearnQuizType type = getCardType();
        if (type.equals(LearnQuizType.SHOW_CARD) || type.equals(LearnQuizType.SHOW_CARD_WITH_IMAGE)) {
            return true;
        }
        if (type.equals(LearnQuizType.CHOICE_1of4) || type.equals(LearnQuizType.KEYBOARD_INPUT)) {
            return answer.equals(cardEntity.getBack());
        }
        if (type.equals(LearnQuizType.CHOICE_1of4_REVERSE)) {
            return answer.equals(cardEntity.getFront());
        }
        return true;
    }

    private LearnQuizType getCardType() {
        if (doShowCard) return LearnQuizType.SHOW_CARD;
        if (gotBadAnswer) return LearnQuizType.CHOICE_1of4;

        if (cardEntity.getLearnScore() == 0) return LearnQuizType.CHOICE_1of4;
        if (cardEntity.getLearnScore() == 1) return LearnQuizType.CHOICE_1of4_REVERSE;
        if (cardEntity.getLearnScore() == 2) return LearnQuizType.KEYBOARD_INPUT;
        if (cardEntity.getLearnScore() == 3) return LearnQuizType.CHOICE_1of4;
        if (cardEntity.getLearnScore() == 4) return LearnQuizType.KEYBOARD_INPUT;
        if (cardEntity.getLearnScore() == 5) return LearnQuizType.CHOICE_1of4_REVERSE;
        if (cardEntity.getLearnScore() == 6) return LearnQuizType.CHOICE_1of4;
        if (cardEntity.getLearnScore() == 7) return LearnQuizType.KEYBOARD_INPUT;
        if (cardEntity.getLearnScore() == 8) return LearnQuizType.CHOICE_1of4;
        if (cardEntity.getLearnScore() >= 9) return LearnQuizType.KEYBOARD_INPUT;

        return LearnQuizType.NONE;
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
