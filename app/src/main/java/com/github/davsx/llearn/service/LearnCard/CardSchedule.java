package com.github.davsx.llearn.service.LearnCard;

import android.util.SparseArray;

public class CardSchedule {
    private SparseArray<SchedulableCard> schedule;
    private int currentTick;
    private int maxTick;
    private int roundCounter;

    public CardSchedule() {
        schedule = new SparseArray<>();
        currentTick = 0;
        maxTick = 0;
        roundCounter = 0;
    }

    public void scheduleCard(SchedulableCard card, int tick) {
        while (schedule.get(tick) != null) {
            tick++; // Find the first free tick number
        }
        if (tick > maxTick) {
            maxTick = tick;
        }
        schedule.put(tick, card);
    }

    public SchedulableCard nextCard() {
        if (currentTick >= maxTick) {
            return null;
        }

        currentTick++;

        SchedulableCard card = schedule.get(currentTick);

        if (card != null) {
            roundCounter++;
        }

        return card;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public int getRoundCounter() {
        return roundCounter;
    }
}
