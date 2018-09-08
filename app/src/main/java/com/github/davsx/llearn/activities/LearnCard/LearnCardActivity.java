package com.github.davsx.llearn.activities.LearnCard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.LearnCard.CardTypeEnum;
import com.github.davsx.llearn.service.LearnCard.LearnCardData;
import com.github.davsx.llearn.service.LearnCard.LearnCardService;

import javax.inject.Inject;

public class LearnCardActivity extends FragmentActivity implements AnswerReceiver {
    private static final String TAG = "LearnCardActivity";

    @Inject
    LearnCardService learnCardService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_card);

        Log.d(TAG, "onCreate");

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        boolean sessionStarted = learnCardService.startSession();

        if (sessionStarted) {
            showNextFragment();
        } else {
            finish();
        }
    }

    private void showNextFragment() {
        CardTypeEnum currentCardType = learnCardService.getCurrentCardType();
        LearnCardData data = learnCardService.getLearnCardData();
        LearnCardFragmentBase fragment = null;
        if (currentCardType.equals(CardTypeEnum.NONE)) {
            // Learning session is finished
        } else if (currentCardType.equals(CardTypeEnum.SHOW_CARD)) {
            fragment = new FragmentShowCard();
        } else if (currentCardType.equals(CardTypeEnum.SHOW_CARD_WITH_IMAGE)) {
            fragment = new FragmentShowCardWithImage();
        } else if (currentCardType.equals(CardTypeEnum.KEYBOARD_INPUT)) {
            fragment = new FragmentKeyboardInput();
        } else if (currentCardType.equals(CardTypeEnum.CHOICE_1of4)) {
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
        learnCardService.processAnswer(answer);
        showNextFragment();
    }
}
