package com.github.davsx.daspalen.activities.ReviewQuiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.activities.BaseQuiz.BaseQuizActivity;
import com.github.davsx.daspalen.service.ReviewQuiz.ReviewQuizService;
import com.github.davsx.daspalen.service.Speaker.SpeakerService;

import javax.inject.Inject;

public class ReviewQuizActivity extends BaseQuizActivity {

    @Inject
    ReviewQuizService reviewQuizService;
    @Inject
    SpeakerService speakerService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_quiz);

        ((DaspalenApplication) getApplication()).getApplicationComponent().inject(this);

        setProgressBar((ProgressBar) findViewById(R.id.progress_bar));
        setSpeakerService(speakerService);
        setCardQuizService(reviewQuizService);

        startQuiz();
    }

}
