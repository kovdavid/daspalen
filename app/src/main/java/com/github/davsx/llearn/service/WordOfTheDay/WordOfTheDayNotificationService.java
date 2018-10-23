package com.github.davsx.llearn.service.WordOfTheDay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import javax.inject.Inject;
import java.util.List;

public class WordOfTheDayNotificationService extends BroadcastReceiver {

    @Inject
    CardRepository cardRepository;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(LLearnConstants.WORD_OF_THE_DAY_INTENT)) {
            ((LLearnApplication) context).getApplicationComponent().inject(this);
            showNotification(context);
        }
    }

    private void showNotification(Context context) {
        List<CardEntity> cards = cardRepository.getRandomCards(1);
        if (cards.size() == 0) {
            return;
        }

        CardEntity card = cards.get(0);
    }

}
