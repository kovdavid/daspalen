package com.github.davsx.llearn.service.WordOfTheDay;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.github.davsx.llearn.LLearnConstants;

import java.util.Calendar;
import java.util.TimeZone;

public class WordOfTheDayAlarmService {

    private static PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(LLearnConstants.WORD_OF_THE_DAY_INTENT);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(LLearnConstants.WORD_OF_THE_DAY_INTENT);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
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
            alarmManager.set(AlarmManager.RTC_WAKEUP, getNextAlarmTimestamp(settings), createPendingIntent(context));
            Toast.makeText(context, "Created alarm", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean validateSettings(WordOfTheDaySettingsService settings) {
        Integer fromHour = settings.getNotificationFromHour();
        Integer fromMinute = settings.getNotificationFromMinute();
        Integer toHour = settings.getNotificationToHour();
        Integer toMinute = settings.getNotificationToMinute();

        return fromHour < toHour || (fromHour.equals(toHour) && toMinute - fromMinute > 10);
    }

    private static long getNextAlarmTimestamp(WordOfTheDaySettingsService settings) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
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

        return calendar.getTimeInMillis();
    }

    private static boolean isWithinLimits(Calendar calendar, Integer fromHour, Integer fromMinute, Integer toHour,
                                          Integer toMinute) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return ((hour > fromHour) || (hour == fromHour && minute >= fromMinute))
                && ((hour < toHour) || (hour == toHour && minute <= toMinute));

    }
}
