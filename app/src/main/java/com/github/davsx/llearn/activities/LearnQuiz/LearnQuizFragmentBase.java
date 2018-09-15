package com.github.davsx.llearn.activities.LearnQuiz;

import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizData;

abstract class LearnQuizFragmentBase extends Fragment {
    LearnQuizData learnQuizData;
    AnswerReceiver answerReceiver;
    Speaker speaker;

    void setAnswerReceiver(AnswerReceiver answerReceiver) {
        this.answerReceiver = answerReceiver;
    }

    void setData(LearnQuizData data) {
        this.learnQuizData = data;
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }
}
