package com.github.davsx.llearn.activities.LearnCard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizService;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizType;
import com.github.davsx.llearn.service.LearnQuiz.LearnCardData;

import javax.inject.Inject;

public class LearnCardActivity extends FragmentActivity implements AnswerReceiver {
    private static final String TAG = "LearnCardActivity";

    @Inject
    LearnQuizService learnQuizService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_card);

        Log.d(TAG, "onCreate");

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        boolean sessionStarted = learnQuizService.startSession();

        if (sessionStarted) {
            showNextFragment();
        } else {
            finish();
        }
    }

    private void showNextFragment() {
        LearnQuizType currentCardType = learnQuizService.getCurrentCardType();
        LearnCardData data = learnQuizService.getLearnCardData();

        LearnCardFragmentBase fragment = null;
        if (currentCardType.equals(LearnQuizType.NONE)) {
            // Learning session is finished
        } else if (currentCardType.equals(LearnQuizType.SHOW_CARD)) {
            fragment = new FragmentShowCard();
        } else if (currentCardType.equals(LearnQuizType.SHOW_CARD_WITH_IMAGE)) {
            fragment = new FragmentShowCardWithImage();
        } else if (currentCardType.equals(LearnQuizType.KEYBOARD_INPUT)) {
            fragment = new FragmentKeyboardInput();
        } else if (currentCardType.equals(LearnQuizType.CHOICE_1of4)) {
            fragment = new FragmentChoice1of4();
        } else if (currentCardType.equals(LearnQuizType.CHOICE_1of4_REVERSE)) {
            fragment = new FragmentChoice1of4();
        } else {
            // Not yet implemented
        }

        if (fragment == null) {
            finish();
        } else {
            renderFragment(fragment.setAnswerReceiver(this).setData(data));
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
