package com.github.davsx.daspalen.service.BaseQuiz;

public interface BaseQuizCardScheduler {
    void scheduleAfterOffset(int offset, BaseQuizCard elem);

    void scheduleToExactOffset(int offset, BaseQuizCard elem);

    void scheduleToEnd(BaseQuizCard elem);
}
