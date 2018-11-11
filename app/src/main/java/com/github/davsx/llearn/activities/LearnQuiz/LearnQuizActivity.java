package com.github.davsx.llearn.activities.LearnQuiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.activities.BaseQuiz.BaseQuizActivity;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizService;
import com.github.davsx.llearn.service.Speaker.SpeakerService;

import javax.inject.Inject;

public class LearnQuizActivity extends BaseQuizActivity {

    @Inject
    LearnQuizService learnQuizService;
    @Inject
    SpeakerService speakerService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_quiz);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        setProgressBar((ProgressBar) findViewById(R.id.progress_bar));
        setSpeakerService(speakerService);
        setCardQuizService(learnQuizService);

        startQuiz();
    }

}
