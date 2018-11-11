package com.github.davsx.daspalen.activities.BaseQuiz;

import android.support.v4.app.Fragment;
import com.github.davsx.daspalen.service.BaseQuiz.AnswerReceiver;
import com.github.davsx.daspalen.service.BaseQuiz.QuizData;
import com.github.davsx.daspalen.service.Speaker.SpeakerService;

abstract class BaseQuizFragment extends Fragment {
    QuizData quizData;
    AnswerReceiver answerReceiver;
    SpeakerService speakerService;

    void setAnswerReceiver(AnswerReceiver answerReceiver) {
        this.answerReceiver = answerReceiver;
    }

    void setData(QuizData data) {
        this.quizData = data;
    }

    void setSpeakerService(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }
}
