package com.github.davsx.daspalen.service.BaseQuiz;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizScheduler {

    private static final String TAG = "daspalen|QuizScheduler";

    private Map<Integer, BaseQuizCard> schedule;
    private List<BaseQuizCard> queue;
    private int nextTick;
    private int maxTick;

    public QuizScheduler(List<BaseQuizCard> queue) {
        this.queue = queue;
        this.schedule = new HashMap<Integer, BaseQuizCard>();
        this.nextTick = 0;
        this.maxTick = 0;
    }

    public void scheduleAfterOffset(int offset, BaseQuizCard card) {
        Log.i(TAG, String.format("scheduleAfterOffset offset:%d nextTick:%d maxTick:%d cardId:%d",
                offset, nextTick, maxTick, card.getCardId()));

        int tick = nextTick + offset - 1;
        while (schedule.get(tick) != null) {
            tick++; // Find the first free tick number
        }
        if (tick > maxTick) {
            maxTick = tick;
        }
        schedule.put(tick, card);

        Log.i(TAG, String.format("scheduleAfterOffset tick:%d", tick));
        logScheduleState();
    }

    public void scheduleToExactOffset(int offset, BaseQuizCard card) {
        Log.i(TAG, String.format("scheduleToExactOffset offset:%d nextTick:%d maxTick:%d cardId:%d", offset, nextTick
                , maxTick, card.getCardId()));

        int targetTick = nextTick + offset - 1;
        int freeTick = targetTick;
        while (schedule.get(freeTick) != null) {
            freeTick++;
        }

        for (int t = freeTick; t > targetTick; t--) {
            schedule.put(t, schedule.get(t - 1));
        }

        if (freeTick > maxTick) {
            maxTick = freeTick;
        }

        schedule.put(targetTick, card);

        Log.i(TAG, String.format("scheduleToExactOffset targetTick:%d freeTick:%d", targetTick, freeTick));
        logScheduleState();
    }

    public void scheduleToEnd(BaseQuizCard card) {
        Log.i(TAG, String.format("scheduleToEnd nextTick:%d maxTick:%d queue.size:%d cardId:%d",
                nextTick, maxTick, queue.size(), card.getCardId()));

        int baseTick = maxTick > nextTick ? maxTick : nextTick;
        maxTick = baseTick + queue.size() + 1;
        schedule.put(maxTick, card);
        logScheduleState();
    }

    public BaseQuizCard nextCard() {
        if (queue.size() == 0 && nextTick > maxTick) {
            return null;
        }

        int currentTick = nextTick;

        BaseQuizCard card = schedule.get(currentTick);
        if (card != null) {
            Log.i(TAG, String.format("nextCard serving from schedule tick:%d cardId:%d",
                    currentTick, card.getCardId()));
            nextTick++;
            return card;
        } else {
            if (queue.size() > 0) {
                BaseQuizCard queueCard = queue.remove(0);
                Log.i(TAG, String.format("nextCard serving from queue currentTick:%d cardId:%d",
                        currentTick, queueCard.getCardId()));
                nextTick++;
                return queueCard;
            } else {
                for (int t = currentTick; t <= maxTick; t++) {
                    BaseQuizCard c = schedule.get(t);
                    if (c != null) {
                        nextTick = t + 1;
                        Log.i(TAG, String.format("nextCard serving from schedule after skipping empty ticks " +
                                "currentTick:%d tick:%d cardId:%d", currentTick, t, c.getCardId()));
                        return c;
                    }
                }
            }
        }

        return null;
    }

    private void logScheduleState() {
        StringBuilder builder = new StringBuilder();

        for (int tick = nextTick; tick <= maxTick; tick++) {
            BaseQuizCard card = schedule.get(tick);

            if (card == null) {
                builder.append(String.format("%d[0] ", tick));
            } else {
                builder.append(String.format("%d[%d] ", tick, card.getCardId()));
            }
        }

        Log.i(TAG, "scheduleState: " + builder.toString());
    }
}