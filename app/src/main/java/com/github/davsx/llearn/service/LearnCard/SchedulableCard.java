package com.github.davsx.llearn.service.LearnCard;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;

import java.util.Comparator;

public class SchedulableCard {
    private CardEntity cardEntity;
    private Boolean doShowCard;
    private Integer usageCounter;
    private Integer plannedRounds;

    public SchedulableCard(CardEntity cardEntity) {
        this.cardEntity = cardEntity;
        this.doShowCard = cardEntity.getLearnScore() == 0;
        this.usageCounter = 0;

        this.plannedRounds = LLearnConstants.LEARN_CARD_PLANNED_USAGES.get(cardEntity.getLearnScore());
    }

    public Integer getPlannedRounds() {
        return plannedRounds;
    }

    static class SchedulableCardComparator implements Comparator<SchedulableCard> {
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
