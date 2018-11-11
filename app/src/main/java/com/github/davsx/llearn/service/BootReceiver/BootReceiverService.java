package com.github.davsx.llearn.service.BootReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.service.CardNotification.CardNotificationAlarmService;
import com.github.davsx.llearn.service.Settings.SettingsService;

import javax.inject.Inject;

public class BootReceiverService extends BroadcastReceiver {

    @Inject
    SettingsService settingsService;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((LLearnApplication) context).getApplicationComponent().inject(this);
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            CardNotificationAlarmService.setNextAlarm(context, settingsService);
        }

    }
}
