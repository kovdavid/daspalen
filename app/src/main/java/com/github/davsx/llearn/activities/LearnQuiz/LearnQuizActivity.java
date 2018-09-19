package com.github.davsx.llearn.activities.LearnQuiz;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ProgressBar;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizData;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizService;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizType;
import com.github.davsx.llearn.service.Speaker.SpeakerService;

import javax.inject.Inject;
import java.util.Locale;

public class LearnQuizActivity extends FragmentActivity implements AnswerReceiver {
    private static final String TAG = "LearnQuizActivity";

    @Inject
    LearnQuizService learnQuizService;

    @Inject
    SpeakerService speakerService;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_quiz);

        Log.d(TAG, "onCreate");

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        boolean sessionStarted = learnQuizService.startSession();

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(learnQuizService.getTotalRounds());

        speakerService.setLanguage(new Locale("es", "ES"));

        if (sessionStarted) {
            showNextFragment();
        } else {
            finish();
        }
    }

    private void showNextFragment() {
        progressBar.setProgress(learnQuizService.getCompletedRounds());

        LearnQuizData data = learnQuizService.getNextCardData();
        if (data == null) {
            finish();
            return;
        }

        LearnQuizType learnQuizType = data.getLearnQuizType();

        LearnQuizFragmentBase fragment = null;
        if (learnQuizType.equals(LearnQuizType.QUIZ_FINISHED)) {
            fragment = new FragmentQuizFinished();
        } else if (learnQuizType.equals(LearnQuizType.SHOW_CARD)) {
            fragment = new FragmentShowCard();
        } else if (learnQuizType.equals(LearnQuizType.SHOW_CARD_WITH_IMAGE)) {
            fragment = new FragmentShowCardWithImage();
        } else if (learnQuizType.equals(LearnQuizType.KEYBOARD_INPUT)) {
            fragment = new FragmentKeyboardInput();
        } else if (learnQuizType.equals(LearnQuizType.CHOICE_1of4)) {
            fragment = new FragmentChoice1of4();
        } else if (learnQuizType.equals(LearnQuizType.CHOICE_1of4_REVERSE)) {
            fragment = new FragmentChoice1of4Reverse();
        } else {
            // Not yet implemented
        }

        if (fragment == null) {
            finish();
        } else {
            fragment.setAnswerReceiver(this);
            fragment.setSpeakerService(speakerService);
            fragment.setData(data);
            renderFragment(fragment);
        }
    }

    private void renderFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_card, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onAnswer(String answer) {
        learnQuizService.processAnswer(answer);
        showNextFragment();
    }
}
