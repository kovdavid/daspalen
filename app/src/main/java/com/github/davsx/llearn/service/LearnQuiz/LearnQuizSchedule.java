package com.github.davsx.llearn.service.LearnQuiz;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class LearnQuizSchedule implements LearnQuizCardScheduler {

    private SparseArray<LearnQuizCard> schedule;
    private List<LearnQuizCard> cardQueue;
    private int nextTick;
    private int maxTick;

    LearnQuizSchedule(ArrayList<LearnQuizCard> cardQueue) {
        this.cardQueue = cardQueue;
        this.schedule = new SparseArray<>();
        this.nextTick = 0;
        this.maxTick = 0;
    }

    @Override
    public void scheduleAfterOffset(LearnQuizCard card, int offset) {
        int tick = nextTick + offset - 1;
        while (schedule.get(tick) != null) {
            tick++; // Find the first free tick number
        }
        if (tick > maxTick) {
            maxTick = tick;
        }
        schedule.put(tick, card);
    }

    @Override
    public void scheduleToExactOffset(LearnQuizCard card, int offset) {
        int tick = nextTick + offset - 1;
        int freeTick = 0;
        for (int t = tick; t <= maxTick + 1; t++) {
            if (schedule.get(t) == null) {
                freeTick = t;
                break;
            }
        }

        if (freeTick > maxTick) {
            maxTick = freeTick;
        }

        for (int t = freeTick; t > tick; t--) {
            schedule.put(t, schedule.get(t - 1));
        }

        schedule.put(tick, card);
    }

    LearnQuizCard nextCard() {
        if (cardQueue.size() == 0 && nextTick > maxTick) {
            return null;
        }

        int currentTick = nextTick;

        LearnQuizCard card = schedule.get(currentTick);
        if (card != null) {
            nextTick++;
            return card;
        } else {
            if (cardQueue.size() > 0) {
                nextTick++;
                return cardQueue.remove(0);
            } else {
                for (int t = currentTick; t <= maxTick; t++) {
                    if (schedule.get(t) != null) {
                        nextTick = t + 1;
                        return schedule.get(t);
                    }
                }
            }
        }

        return null;
    }
}