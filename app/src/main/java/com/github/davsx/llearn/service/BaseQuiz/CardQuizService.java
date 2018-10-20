package com.github.davsx.llearn.service.BaseQuiz;

public interface CardQuizService {
    boolean startSession();

    void processAnswer(String answer);

    Integer getCompletedRounds();

    QuizData getNextCardData();

    Integer getTotalRounds();
}
