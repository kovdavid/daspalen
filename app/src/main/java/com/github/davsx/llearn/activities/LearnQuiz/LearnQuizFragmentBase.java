package com.github.davsx.llearn.activities.LearnQuiz;

import android.support.v4.app.Fragment;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizData;

public abstract class LearnQuizFragmentBase extends Fragment {
    LearnQuizData learnQuizData;
    AnswerReceiver answerReceiver;

    LearnQuizFragmentBase setAnswerReceiver(AnswerReceiver answerReceiver) {
        this.answerReceiver = answerReceiver;
        return this;
    }

    public LearnQuizFragmentBase setData(LearnQuizData data) {
        this.learnQuizData = data;
        return this;
    }
}
