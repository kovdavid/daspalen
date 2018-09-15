package com.github.davsx.llearn.service.LearnQuiz;

public interface LearnQuizCardScheduler<T> {
    void scheduleAfterOffset(int offset, T elem);

    void scheduleToExactOffset(int offset, T elem);
}
