package com.github.davsx.llearn.service.LearnCard;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LearnCardServiceTest {

    private ArrayList<SchedulableCard> schedule;
    private ArrayList<SchedulableCard> cards;
    private int currentSlot;

    @Test
    public void myTest() {
        schedule = new ArrayList<>();
        currentSlot = 0;




        assertEquals(1, 1);
    }

    class SchedulableCard {
        boolean doShowCard;
        int score;
        int targetUsageCount;
        int currentUsageCount;
        int lastUsedSlot;

        public SchedulableCard(boolean doShowCard, int score, int targetUsageCount, int currentUsageCount, int lastUsedSlot) {
            this.doShowCard = doShowCard;
            this.score = score;
            this.targetUsageCount = targetUsageCount;
            this.currentUsageCount = currentUsageCount;
            this.lastUsedSlot = lastUsedSlot;
        }
    }
}