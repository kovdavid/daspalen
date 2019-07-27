package com.github.davsx.daspalen.service.CardNotification;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;

import javax.inject.Inject;

public class CardNotificationReceiver extends BroadcastReceiver {

    private final static String INTENT_ACTION_KEY = "EXTRA_ACTION";
    private final static String EXTRA_NEW_NOTIFICATION = "NEW_NOTIFICATION";
    private final static String EXTRA_DISABLE_NOTIFICATION = "DISABLE_NOTIFICATION";
    private static final String EXTRA_CARD_ID = "EXTRA_CARD_ID";

    @Inject
    CardNotificationService cardNotificationService;
    @Inject
    DaspalenRepository repository;

    static PendingIntent getNewNotificationIntent(Context context) {
        Intent intent = new Intent(context, CardNotificationReceiver.class);
        intent.putExtra(INTENT_ACTION_KEY, EXTRA_NEW_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    static PendingIntent getDisableNotificationIntent(Context context, Card card) {
        Intent intent = new Intent(context, CardNotificationReceiver.class);
        intent.putExtra(INTENT_ACTION_KEY, EXTRA_DISABLE_NOTIFICATION);
        intent.putExtra(EXTRA_CARD_ID, card.getCardId());
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ((DaspalenApplication) context.getApplicationContext()).getApplicationComponent().inject(this);

        if (intent.getStringExtra(INTENT_ACTION_KEY).equals(EXTRA_DISABLE_NOTIFICATION)) {
            long cardId = intent.getLongExtra(EXTRA_CARD_ID, 0L);
            if (cardId > 0L) {
                Card card = repository.getCardWithId(cardId);
                card.setNotificationEnabled(false);
                repository.updateCard(card);
            }
        }

        cardNotificationService.showNotification(context);
    }
}
