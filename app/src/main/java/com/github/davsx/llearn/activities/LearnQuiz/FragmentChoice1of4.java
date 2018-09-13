package com.github.davsx.llearn.activities.LearnQuiz;

import android.view.View;
import com.github.davsx.llearn.R;

public class FragmentChoice1of4 extends FragmentChoiceBase {
    @Override
    public void childViewInit(View view) {
    }

    @Override
    public String getCorrectAnswerString() {
        return learnQuizData.getBackText();
    }

    @Override
    public String getQuizString() {
        return learnQuizData.getFrontText();
    }

    @Override
    public int getViewResource() {
        return R.layout.fragment_learn_choice_1of4;
    }
}
