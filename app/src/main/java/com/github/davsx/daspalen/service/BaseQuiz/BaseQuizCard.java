package com.github.davsx.daspalen.service.BaseQuiz;

import com.github.davsx.daspalen.persistence.entity.CardEntity;

import java.util.List;

public interface BaseQuizCard {
    void handleAnswer(BaseQuizCardScheduler quizSchedule, String answer);

    QuizData buildQuizData(List<CardEntity> randomCards);

    long getCardId();

    int getCompletedRounds();
}
