package com.github.davsx.llearn.gui.activities.LearnCard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.data.LearnCard.CardTypeEnum;
import com.github.davsx.llearn.data.LearnCard.ShowCardData;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.LearnCard.LearnCardService;

public class LearnCardActivity extends FragmentActivity implements AnswerReceiver {

    private FragmentManager fragmentManager;
    private LearnCardService learnCardService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_card);

        fragmentManager = getSupportFragmentManager();

        CardRepository cardRepository = CardRepository.getInstance(this);
        learnCardService = new LearnCardService(cardRepository);

        boolean sessionStarted = learnCardService.startSession();

        if (sessionStarted) {
            showNextFragment();
        } else {
            finish();
        }
    }

    private void showNextFragment() {
        CardTypeEnum currentCardType = learnCardService.getCurrentCardType();
        if (currentCardType.equals(CardTypeEnum.NONE)) {
            finish();
        } else if (currentCardType.equals(CardTypeEnum.SHOW_CARD)) {
            ShowCardData data = learnCardService.getShowCardData();
            FragmentShowCard fragment = new FragmentShowCard()
                    .setAnswerReceiver(this)
                    .setData(data);
            renderFragment(fragment);
        } else {
            finish();
        }
    }

    private void renderFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_card, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onAnswer(String answer) {
        learnCardService.processAnswer(answer);
        showNextFragment();
    }
}
