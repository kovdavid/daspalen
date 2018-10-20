package com.github.davsx.llearn.service.PeriodicNotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class PeriodicNotificationService {
    private Context context;

    public void setAlarm() {
        if (hasPendingIntent()) {
            return;
        }

        PendingIntent intent = getPendingIntent();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, 10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long startTime = calendar.getTimeInMillis();
        long repeatInterval = (long) (1000 * 60 * 60 * 24); // 1 day

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, repeatInterval, intent);
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent("com.github.davsx.llearn.PERIODIC_NOTIFICATION");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private boolean hasPendingIntent() {
        Intent intent = new Intent("com.github.davsx.llearn.PERIODIC_NOTIFICATION");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}
