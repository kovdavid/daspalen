package com.github.davsx.llearn.service.WordOfTheDay;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.github.davsx.llearn.LLearnConstants;

import java.util.Calendar;
import java.util.TimeZone;

public class WordOfTheDayAlarmService {

    private static boolean hasPendingIntent(Context context) {
        Intent intent = new Intent(LLearnConstants.WORD_OF_THE_DAY_INTENT);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(LLearnConstants.WORD_OF_THE_DAY_INTENT);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void setAlarm(Context context, SharedPreferences sharedPreferences) {
        WordOfTheDaySettingsService settings = new WordOfTheDaySettingsService(sharedPreferences);

        if (hasPendingIntent(context)) {
            return;
        }

        PendingIntent intent = getPendingIntent(context);

        long now = System.currentTimeMillis();
        TimeZone timeZone = TimeZone.getDefault();

//        TODO

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
}
