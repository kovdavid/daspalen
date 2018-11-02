package com.github.davsx.llearn.service.WordOfTheDay;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.github.davsx.llearn.service.Settings.SettingsService;

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

    public static void resetAlarm(Context context, SettingsService settingsService) {
        PendingIntent pi = getPendingIntent(context);
        if (pi != null) {
            pi.cancel();
        }
        setNextAlarm(context, settingsService);
    }

    public static void setNextAlarm(Context context, SettingsService settingsService) {

        Boolean notificationEnabled = settingsService.getNotificationEnabled();

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
        if (!validateSettings(settingsService)) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Calendar cal = getNextAlarm(settingsService);
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), createPendingIntent(context));
        }
    }

    private static boolean validateSettings(SettingsService settingsService) {
        Integer fromHour = settingsService.getNotificationFromHour();
        Integer fromMinute = settingsService.getNotificationFromMinute();
        Integer toHour = settingsService.getNotificationToHour();
        Integer toMinute = settingsService.getNotificationToMinute();

        return fromHour < toHour || (fromHour.equals(toHour) && toMinute - fromMinute > 10);
    }

    private static Calendar getNextAlarm(SettingsService settingsService) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.HOUR, settingsService.getNotificationIntervalHour());
        calendar.add(Calendar.MINUTE, settingsService.getNotificationIntervalMinute());

        Integer fromHour = settingsService.getNotificationFromHour();
        Integer fromMinute = settingsService.getNotificationFromMinute();
        Integer toHour = settingsService.getNotificationToHour();
        Integer toMinute = settingsService.getNotificationToMinute();

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
