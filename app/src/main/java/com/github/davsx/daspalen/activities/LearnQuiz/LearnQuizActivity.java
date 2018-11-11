package com.github.davsx.daspalen.activities.LearnQuiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.activities.BaseQuiz.BaseQuizActivity;
import com.github.davsx.daspalen.service.LearnQuiz.LearnQuizService;
import com.github.davsx.daspalen.service.Speaker.SpeakerService;

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

        ((DaspalenApplication) getApplication()).getApplicationComponent().inject(this);

        setProgressBar((ProgressBar) findViewById(R.id.progress_bar));
        setSpeakerService(speakerService);
        setCardQuizService(learnQuizService);

        startQuiz();
    }

}
