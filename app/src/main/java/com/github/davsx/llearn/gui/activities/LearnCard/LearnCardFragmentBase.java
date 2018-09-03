package com.github.davsx.llearn.gui.activities.LearnCard;

import android.support.v4.app.Fragment;
import com.github.davsx.llearn.data.LearnCard.LearnCardData;

public abstract class LearnCardFragmentBase extends Fragment {
    LearnCardData learnCardData;
    AnswerReceiver answerReceiver;

    LearnCardFragmentBase setAnswerReceiver(AnswerReceiver answerReceiver) {
        this.answerReceiver = answerReceiver;
        return this;
    }

    public LearnCardFragmentBase setData(LearnCardData data) {
        this.learnCardData = data;
        return this;
    }
}
