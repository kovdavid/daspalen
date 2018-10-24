package com.github.davsx.llearn.service.WordOfTheDay;

import android.content.SharedPreferences;

public class WordOfTheDaySettingsService {

    private SharedPreferences sharedPreferences;

    public WordOfTheDaySettingsService(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setNotificationFrom(int hour, int minute) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("WORD_OF_THE_DAY_NOTIFICATION_FROM_HOUR", hour);
        editor.putInt("WORD_OF_THE_DAY_NOTIFICATION_FROM_MINUTE", minute);
        editor.commit();
    }

    public void setNotificationTo(int hour, int minute) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("WORD_OF_THE_DAY_NOTIFICATION_TO_HOUR", hour);
        editor.putInt("WORD_OF_THE_DAY_NOTIFICATION_TO_MINUTE", minute);
        editor.commit();
    }

    public void setNotificationInterval(int hour, int minute) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_HOUR", hour);
        editor.putInt("WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_MINUTE", minute);
        editor.commit();
    }

    public Boolean getNotificationEnabled() {
        return sharedPreferences.getBoolean("WORD_OF_THE_DAY_NOTIFICATION_ENABLED", false);
    }

    public void setNotificationEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("WORD_OF_THE_DAY_NOTIFICATION_ENABLED", enabled);
        editor.commit();
    }

    public Integer getNotificationFromHour() {
        return sharedPreferences.getInt("WORD_OF_THE_DAY_NOTIFICATION_FROM_HOUR", 10);
    }

    public Integer getNotificationFromMinute() {
        return sharedPreferences.getInt("WORD_OF_THE_DAY_NOTIFICATION_FROM_MINUTE", 0);
    }

    public Integer getNotificationIntervalHour() {
        return sharedPreferences.getInt("WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_HOUR", 1);
    }

    public Integer getNotificationIntervalMinute() {
        return sharedPreferences.getInt("WORD_OF_THE_DAY_NOTIFICATION_INTERVAL_MINUTE", 0);
    }

    public Integer getNotificationToHour() {
        return sharedPreferences.getInt("WORD_OF_THE_DAY_NOTIFICATION_TO_HOUR", 16);
    }

    public Integer getNotificationToMinute() {
        return sharedPreferences.getInt("WORD_OF_THE_DAY_NOTIFICATION_TO_MINUTE", 0);
    }
}
