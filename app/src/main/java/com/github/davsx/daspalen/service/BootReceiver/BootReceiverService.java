package com.github.davsx.daspalen.service.BootReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.service.CardNotification.CardNotificationAlarmService;
import com.github.davsx.daspalen.service.Settings.SettingsService;

import javax.inject.Inject;

public class BootReceiverService extends BroadcastReceiver {

    @Inject
    SettingsService settingsService;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((DaspalenApplication) context).getApplicationComponent().inject(this);
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            CardNotificationAlarmService.setNextAlarm(context, settingsService);
        }

    }
}
