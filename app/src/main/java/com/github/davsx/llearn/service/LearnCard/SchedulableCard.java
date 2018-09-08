package com.github.davsx.llearn.service.LearnCard;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;

import java.util.Comparator;
import java.util.Random;

public class SchedulableCard {

    private CardEntity cardEntity;
    private Boolean doShowCard;
    private Integer roundsCounter;
    private Integer plannedRounds;
    private Random rng;

    public SchedulableCard(CardEntity cardEntity) {
        this.cardEntity = cardEntity;
        this.doShowCard = cardEntity.getLearnScore() == 0;
        this.roundsCounter = 0;
        this.rng = new Random(System.currentTimeMillis());

        this.plannedRounds = LLearnConstants.LEARN_CARD_PLANNED_USAGES.get(cardEntity.getLearnScore());
    }

    public void handleAnswer(String answer) {
        if (doShowCard) {
            doShowCard = false; // showCard does not count as a round
        } else {
            roundsCounter++;
        }
    }

    public Integer calculateScheduleOffset() {
        if (roundsCounter >= plannedRounds) {
            return 0;
        }

        if (roundsCounter == 0) {
            return 2;
        } else if (roundsCounter == 1) {
            return Math.round(4 + rng.nextInt(1));
        } else {
            return Math.round(8 + rng.nextInt(8));
        }
    }

    public CardTypeEnum getCardType() {
        if (cardEntity.getLearnScore() == 0) {
            if (roundsCounter == 0) {
                return CardTypeEnum.SHOW_CARD;
            } else {
                return CardTypeEnum.CHOICE_1of4;
            }
        }
        if (cardEntity.getLearnScore() % 3 == 0) return CardTypeEnum.CHOICE_1of4;
        if (cardEntity.getLearnScore() % 3 == 1) return CardTypeEnum.CHOICE_1of4_REVERSE;
        if (cardEntity.getLearnScore() % 3 == 2) return CardTypeEnum.KEYBOARD_INPUT;
        return CardTypeEnum.NONE;
    }

    public Integer getPlannedRounds() {
        return plannedRounds;
    }

    public static class SchedulableCardComparator implements Comparator<SchedulableCard> {
        @Override
        public int compare(SchedulableCard c1, SchedulableCard c2) {
            int c = c1.doShowCard.compareTo(c2.doShowCard);
            if (c == 0) {
                c = c1.plannedRounds.compareTo(c2.plannedRounds);
            }
            return c;
        }
    }
}
