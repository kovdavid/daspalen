package com.github.davsx.llearn.service.BootReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.github.davsx.llearn.service.WordOfTheDay.WordOfTheDayAlarmService;

public class BootReceiverService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            WordOfTheDayAlarmService.setAlarm(context);
        }

    }
}
