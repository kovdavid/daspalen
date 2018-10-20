package com.github.davsx.llearn.service.BaseQuiz;

public interface BaseQuizCardScheduler<T> {
    void scheduleAfterOffset(int offset, T elem);

    void scheduleToExactOffset(int offset, T elem);

    void scheduleToEnd(T elem);
}