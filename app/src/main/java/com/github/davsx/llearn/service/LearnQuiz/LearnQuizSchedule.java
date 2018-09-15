package com.github.davsx.llearn.service.LearnQuiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearnQuizSchedule<T> implements LearnQuizCardScheduler<T> {

    private Map<Integer, T> schedule;
    private List<T> queue;
    private int nextTick;
    private int maxTick;

    LearnQuizSchedule(List<T> queue) {
        this.queue = queue;
        this.schedule = new HashMap<Integer, T>();
        this.nextTick = 0;
        this.maxTick = 0;
    }

    @Override
    public void scheduleAfterOffset(int offset, T elem) {
        int tick = nextTick + offset - 1;
        while (schedule.get(tick) != null) {
            tick++; // Find the first free tick number
        }
        if (tick > maxTick) {
            maxTick = tick;
        }
        schedule.put(tick, elem);
    }

    @Override
    public void scheduleToExactOffset(int offset, T elem) {
        int targetTick = nextTick + offset - 1;
        int freeTick = targetTick;
        while (schedule.get(freeTick) != null) {
            freeTick++;
        }

        for (int t = freeTick; t > targetTick; t--) {
            schedule.put(t, schedule.get(t-1));
        }

        if (freeTick > maxTick) {
            maxTick = freeTick;
        }

        schedule.put(targetTick, elem);
    }

    T nextElem() {
        if (queue.size() == 0 && nextTick > maxTick) {
            return null;
        }

        int currentTick = nextTick;

        T elem = schedule.get(currentTick);
        if (elem != null) {
            nextTick++;
            return elem;
        } else {
            if (queue.size() > 0) {
                nextTick++;
                return queue.remove(0);
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