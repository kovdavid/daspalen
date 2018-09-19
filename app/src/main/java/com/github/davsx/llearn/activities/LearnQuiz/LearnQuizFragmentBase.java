package com.github.davsx.llearn.activities.LearnQuiz;

import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizData;
import com.github.davsx.llearn.service.Speaker.SpeakerService;

abstract class LearnQuizFragmentBase extends Fragment {
    LearnQuizData learnQuizData;
    AnswerReceiver answerReceiver;
    SpeakerService speakerService;

    void setAnswerReceiver(AnswerReceiver answerReceiver) {
        this.answerReceiver = answerReceiver;
    }

    void setData(LearnQuizData data) {
        this.learnQuizData = data;
    }

    void setSpeakerService(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }
}
