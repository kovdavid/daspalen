package com.github.davsx.llearn.service.Settings;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsService {

    private static final String WORD_OF_THE_DAY_NOTIFICATION_ENABLED = "WORD_OF_THE_DAY_NOTIFICATION_ENABLED";
    private static final String WORD_OF_THE_DAY_NOTIFICATION_FROM_HOUR = "WORD_OF_THE_DAY_NOTIFICATION_FROM_HOUR";
    private static final String WORD_OF_THE_DAY_NOTIFICATION_FROM_MINUTE = "WORD_OF_THE_DAY_NOTIFICATION_FROM_MINUTE";
    private static final String WORD_OF_THE_DAY_NOTIFICATION_TO_HOUR = "WORD_OF_THE_DAY_NOTIFICATION_TO_HOUR";
    private static final String WORD_OF_THE_DAY_NOTIFICATION_TO_MINUTE = "WORD_OF_THE_DAY_NOTIFICATION_TO_MINUTE";
    private static final String WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_HOUR =
            "WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_HOUR";
    private static final String WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_MINUTE =
            "WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_MINUTE";

    private SharedPreferences sharedPreferences;

    public SettingsService(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setNotificationFrom(int hour, int minute) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(WORD_OF_THE_DAY_NOTIFICATION_FROM_HOUR, hour);
        editor.putInt(WORD_OF_THE_DAY_NOTIFICATION_FROM_MINUTE, minute);
        editor.commit();
    }

    public void setNotificationTo(int hour, int minute) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(WORD_OF_THE_DAY_NOTIFICATION_TO_HOUR, hour);
        editor.putInt(WORD_OF_THE_DAY_NOTIFICATION_TO_MINUTE, minute);
        editor.commit();
    }

    public void setNotificationInterval(int hour, int minute) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_HOUR, hour);
        editor.putInt(WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_MINUTE, minute);
        editor.commit();
    }

    public Boolean getNotificationEnabled() {
        return sharedPreferences.getBoolean(WORD_OF_THE_DAY_NOTIFICATION_ENABLED, false);
    }

    public void setNotificationEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(WORD_OF_THE_DAY_NOTIFICATION_ENABLED, enabled);
        editor.commit();
    }

    public Integer getNotificationFromHour() {
        return sharedPreferences.getInt(WORD_OF_THE_DAY_NOTIFICATION_FROM_HOUR, 10);
    }

    public Integer getNotificationFromMinute() {
        return sharedPreferences.getInt(WORD_OF_THE_DAY_NOTIFICATION_FROM_MINUTE, 0);
    }

    public Integer getNotificationIntervalHour() {
        return sharedPreferences.getInt(WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_HOUR, 1);
    }

    public Integer getNotificationIntervalMinute() {
        return sharedPreferences.getInt(WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_MINUTE, 0);
    }

    public Integer getNotificationToHour() {
        return sharedPreferences.getInt(WORD_OF_THE_DAY_NOTIFICATION_TO_HOUR, 16);
    }

    public Integer getNotificationToMinute() {
        return sharedPreferences.getInt(WORD_OF_THE_DAY_NOTIFICATION_TO_MINUTE, 0);
    }

    public List<String[]> toCsvDataV1() {
        return new ArrayList<>(
                Arrays.asList(
                        new String[]{
                                WORD_OF_THE_DAY_NOTIFICATION_ENABLED,
                                Boolean.toString(getNotificationEnabled())
                        },
                        new String[]{
                                WORD_OF_THE_DAY_NOTIFICATION_FROM_HOUR,
                                Integer.toString(getNotificationFromHour())
                        },
                        new String[]{
                                WORD_OF_THE_DAY_NOTIFICATION_FROM_MINUTE,
                                Integer.toString(getNotificationFromMinute())
                        },
                        new String[]{
                                WORD_OF_THE_DAY_NOTIFICATION_TO_HOUR,
                                Integer.toString(getNotificationToHour())
                        },
                        new String[]{
                                WORD_OF_THE_DAY_NOTIFICATION_TO_MINUTE,
                                Integer.toString(getNotificationToMinute())
                        },
                        new String[]{
                                WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_HOUR,
                                Integer.toString(getNotificationIntervalHour())
                        },
                        new String[]{
                                WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_MINUTE,
                                Integer.toString(getNotificationIntervalMinute())
                        }
                )
        );
    }

    public void fromCsvDataV1(List<String[]> settings) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (String[] setting : settings) {
            String name = setting[0];
            String value = setting[1];

            switch (name) {
                case WORD_OF_THE_DAY_NOTIFICATION_ENABLED:
                    editor.putBoolean(name, Boolean.valueOf(value));
                    break;
                case WORD_OF_THE_DAY_NOTIFICATION_FROM_HOUR:
                case WORD_OF_THE_DAY_NOTIFICATION_FROM_MINUTE:
                case WORD_OF_THE_DAY_NOTIFICATION_TO_HOUR:
                case WORD_OF_THE_DAY_NOTIFICATION_TO_MINUTE:
                case WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_HOUR:
                case WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_MINUTE:
                    editor.putInt(name, Integer.valueOf(value));
                    break;
            }
        }

        editor.commit();
    }

}
