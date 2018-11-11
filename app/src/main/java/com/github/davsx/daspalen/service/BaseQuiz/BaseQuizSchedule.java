package com.github.davsx.daspalen.service.BaseQuiz;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseQuizSchedule<T> implements BaseQuizCardScheduler<T> {

    private static final String TAG = "BaseQuizSchedule";

    private Map<Integer, T> schedule;
    private List<T> queue;
    private int nextTick;
    private int maxTick;

    public BaseQuizSchedule(List<T> queue) {
        this.queue = queue;
        this.schedule = new HashMap<Integer, T>();
        this.nextTick = 0;
        this.maxTick = 0;

        Log.d(TAG, "initialize schedule with queue size:" + queue.size());
    }

    @Override
    public void scheduleAfterOffset(int offset, T elem) {
        Log.d(TAG, String.format("scheduleAfterOffset offset:%d nextTick:%d maxTick:%d", offset, nextTick, maxTick));

        int tick = nextTick + offset - 1;
        while (schedule.get(tick) != null) {
            tick++; // Find the first free tick number
        }
        if (tick > maxTick) {
            maxTick = tick;
        }
        schedule.put(tick, elem);

        Log.d(TAG, String.format("scheduleAfterOffset tick:%d", tick));
    }

    @Override
    public void scheduleToExactOffset(int offset, T elem) {
        Log.d(TAG, String.format("scheduleToExactOffset offset:%d nextTick:%d maxTick:%d", offset, nextTick, maxTick));

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

        schedule.put(targetTick, elem);

        Log.d(TAG, String.format("scheduleToExactOffset tick:%d", targetTick));
    }

    @Override
    public void scheduleToEnd(T elem) {
        Log.d(TAG, String.format("scheduleToEnd nextTick:%d maxTick:%d", nextTick, maxTick));
        maxTick = maxTick + 1 + queue.size();
        schedule.put(maxTick, elem);
    }

    public T nextElem() {
        if (queue.size() == 0 && nextTick > maxTick) {
            return null;
        }

        int currentTick = nextTick;

        T elem = schedule.get(currentTick);
        if (elem != null) {
            Log.d(TAG, String.format("nextElem serving from schedule tick:%d", currentTick));
            nextTick++;
            return elem;
        } else {
            if (queue.size() > 0) {
                Log.d(TAG, String.format("nextElem serving from queue size:%d", queue.size()));
                nextTick++;
                return queue.remove(0);
            } else {
                for (int t = currentTick; t <= maxTick; t++) {
                    if (schedule.get(t) != null) {
                        nextTick = t + 1;
                        Log.d(TAG, String.format("nextElem serving from schedule after skipping empty ticks " +
                                "nextTick:%d tick:%d", currentTick, t));
                        return schedule.get(t);
                    }
                }
            }
        }

        return null;
    }
}