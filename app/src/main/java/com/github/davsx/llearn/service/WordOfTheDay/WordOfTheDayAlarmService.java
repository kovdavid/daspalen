package com.github.davsx.llearn.service.WordOfTheDay;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class WordOfTheDayAlarmService {

    private static PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, WordOfTheDayNotificationService.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, WordOfTheDayNotificationService.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
    }

    public static void resetAlarm(Context context, SharedPreferences sharedPreferences) {
        PendingIntent pi = getPendingIntent(context);
        if (pi != null) {
            pi.cancel();
        }
        setNextAlarm(context, sharedPreferences);
    }

    public static void setNextAlarm(Context context, SharedPreferences sharedPreferences) {
        WordOfTheDaySettingsService settings = new WordOfTheDaySettingsService(sharedPreferences);

        Boolean notificationEnabled = settings.getNotificationEnabled();

        PendingIntent pi = getPendingIntent(context);
        if (pi != null) {
            if (!notificationEnabled) {
                pi.cancel();
            }
            return;
        }

        if (!notificationEnabled) {
            return;
        }
        if (!validateSettings(settings)) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Calendar cal = getNextAlarm(settings);
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), createPendingIntent(context));
        }
    }

    private static boolean validateSettings(WordOfTheDaySettingsService settings) {
        Integer fromHour = settings.getNotificationFromHour();
        Integer fromMinute = settings.getNotificationFromMinute();
        Integer toHour = settings.getNotificationToHour();
        Integer toMinute = settings.getNotificationToMinute();

        return fromHour < toHour || (fromHour.equals(toHour) && toMinute - fromMinute > 10);
    }

    private static Calendar getNextAlarm(WordOfTheDaySettingsService settings) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.HOUR, settings.getNotificationIntervalHour());
        calendar.add(Calendar.MINUTE, settings.getNotificationIntervalMinute());

        Integer fromHour = settings.getNotificationFromHour();
        Integer fromMinute = settings.getNotificationFromMinute();
        Integer toHour = settings.getNotificationToHour();
        Integer toMinute = settings.getNotificationToMinute();

        while (!isWithinLimits(calendar, fromHour, fromMinute, toHour, toMinute)) {
            calendar.add(Calendar.MINUTE, 10);
        }

        return calendar;
    }

    private static boolean isWithinLimits(Calendar calendar, Integer fromHour, Integer fromMinute, Integer toHour,
                                          Integer toMinute) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return ((hour > fromHour) || (hour == fromHour && minute >= fromMinute))
                && ((hour < toHour) || (hour == toHour && minute <= toMinute));

    }
}
