package com.github.davsx.llearn.activities.ReviewQuiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.activities.BaseQuiz.BaseQuizActivity;
import com.github.davsx.llearn.service.ReviewQuiz.ReviewQuizService;
import com.github.davsx.llearn.service.Speaker.SpeakerService;

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

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        setProgressBar((ProgressBar) findViewById(R.id.progress_bar));
        setSpeakerService(speakerService);
        setCardQuizService(reviewQuizService);

        startQuiz();
    }

}
