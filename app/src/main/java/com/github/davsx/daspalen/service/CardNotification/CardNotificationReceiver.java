package com.github.davsx.daspalen.service.CardNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.github.davsx.daspalen.DaspalenApplication;

import javax.inject.Inject;

public class CardNotificationReceiver extends BroadcastReceiver {

    @Inject
    CardNotificationService cardNotificationService;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("DASPALEN", "got onReceive");

        ((DaspalenApplication) context.getApplicationContext()).getApplicationComponent().inject(this);
        cardNotificationService.showNotification(context);
    }
}
