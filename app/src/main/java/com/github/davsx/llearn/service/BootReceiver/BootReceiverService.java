package com.github.davsx.llearn.service.BootReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.service.WordOfTheDay.WordOfTheDayAlarmService;

import javax.inject.Inject;

public class BootReceiverService extends BroadcastReceiver {

    @Inject
    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((LLearnApplication) context).getApplicationComponent().inject(this);
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            WordOfTheDayAlarmService.setNextAlarm(context, sharedPreferences);
        }

    }
}
