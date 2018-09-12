package com.github.davsx.llearn.service.LearnQuiz;

public interface LearnQuizCardScheduler {
    void scheduleAfterOffset(LearnQuizCard card, int offset);

    void scheduleToExactOffset(LearnQuizCard card, int offset);
}
