package com.github.davsx.llearn.service.LearnQuiz;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LearnQuizScheduleTest {

    @Test
    public void testScheduleFromQueue() {
        List<Integer> queue = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

        LearnQuizSchedule<Integer> schedule = new LearnQuizSchedule<>(queue);

        assertEquals(1, (int) schedule.nextElem());
        assertEquals(2, (int) schedule.nextElem());
        assertEquals(3, (int) schedule.nextElem());
        assertEquals(4, (int) schedule.nextElem());
        assertNull(schedule.nextElem());
    }

    @Test
    public void testScheduleAfterOffset() {
        List<Integer> queue = new ArrayList<>();
        LearnQuizSchedule<Integer> schedule = new LearnQuizSchedule<>(queue);

        schedule.scheduleAfterOffset(1, 1);
        schedule.scheduleAfterOffset(1, 2);
        schedule.scheduleAfterOffset(1, 3);
        schedule.scheduleAfterOffset(1, 4);

        assertEquals(1, (int) schedule.nextElem());
        assertEquals(2, (int) schedule.nextElem());
        assertEquals(3, (int) schedule.nextElem());
        assertEquals(4, (int) schedule.nextElem());
        assertNull(schedule.nextElem());
    }

    @Test
    public void testScheduleAfterOffsetWithQueue() {
        List<Integer> queue = new ArrayList<Integer>(Arrays.asList(4, 5));
        LearnQuizSchedule<Integer> schedule = new LearnQuizSchedule<>(queue);

        schedule.scheduleAfterOffset(1, 1);
        schedule.scheduleAfterOffset(5, 2);

        assertEquals(1, (int) schedule.nextElem());
        assertEquals(4, (int) schedule.nextElem());
        assertEquals(5, (int) schedule.nextElem());
        assertEquals(2, (int) schedule.nextElem());
        assertNull(schedule.nextElem());
    }

    @Test
    public void testScheduleExactOffset() {
        List<Integer> queue = new ArrayList<>();
        LearnQuizSchedule<Integer> schedule = new LearnQuizSchedule<>(queue);

        schedule.scheduleAfterOffset(1, 1);
        schedule.scheduleAfterOffset(1, 2);
        schedule.scheduleAfterOffset(1, 3);
        schedule.scheduleToExactOffset(2, 4);
        schedule.scheduleToExactOffset(10, 5);

        assertEquals(1, (int) schedule.nextElem());
        assertEquals(4, (int) schedule.nextElem());
        assertEquals(2, (int) schedule.nextElem());
        assertEquals(3, (int) schedule.nextElem());
        assertEquals(5, (int) schedule.nextElem());
        assertNull(schedule.nextElem());
    }
}