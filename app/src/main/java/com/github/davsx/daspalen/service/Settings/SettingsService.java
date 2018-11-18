package com.github.davsx.daspalen.service.Settings;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class SettingsService {

    private static final String CARD_NOTIFICATION_ENABLED = "CARD_NOTIFICATION_ENABLED";
    private static final String CARD_NOTIFICATION_FROM_HOUR = "CARD_NOTIFICATION_FROM_HOUR";
    private static final String CARD_NOTIFICATION_FROM_MINUTE = "CARD_NOTIFICATION_FROM_MINUTE";
    private static final String CARD_NOTIFICATION_TO_HOUR = "CARD_NOTIFICATION_TO_HOUR";
    private static final String CARD_NOTIFICATION_TO_MINUTE = "CARD_NOTIFICATION_TO_MINUTE";
    private static final String CARD_NOTIFICATION_INTERVAL_HOUR = "CARD_NOTIFICATION_INTERVAL_HOUR";
    private static final String CARD_NOTIFICATION_INTERVAL_MINUTE = "CARD_NOTIFICATION_INTERVAL_MINUTE";
    private static final String IMAGE_SEARCH_API_KEY = "IMAGE_SEARCH_API_KEY";
    private static final String IMAGE_SEARCH_CX_KEY = "IMAGE_SEARCH_CX_KEY";
    private static final String SYNC_SERVER_URL = "SYNC_SERVER_URL";

    private SharedPreferences sharedPreferences;

    public SettingsService(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setCardNotificationFrom(int hour, int minute) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CARD_NOTIFICATION_FROM_HOUR, hour);
        editor.putInt(CARD_NOTIFICATION_FROM_MINUTE, minute);
        editor.commit();
    }

    public void setCardNotificationTo(int hour, int minute) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CARD_NOTIFICATION_TO_HOUR, hour);
        editor.putInt(CARD_NOTIFICATION_TO_MINUTE, minute);
        editor.commit();
    }

    public void setCardNotificationInterval(int hour, int minute) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CARD_NOTIFICATION_INTERVAL_HOUR, hour);
        editor.putInt(CARD_NOTIFICATION_INTERVAL_MINUTE, minute);
        editor.commit();
    }

    public String toJson() {
        Map<String, ?> preferences = sharedPreferences.getAll();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(preferences);
    }

    public void fromJson(String json) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Type typeOfHashMap = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map = gson.fromJson(json, typeOfHashMap);

        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            switch (entry.getKey()) {
                case CARD_NOTIFICATION_ENABLED:
                    editor.putBoolean(entry.getKey(), Boolean.valueOf(entry.getValue()));
                    break;
                case CARD_NOTIFICATION_FROM_HOUR:
                case CARD_NOTIFICATION_FROM_MINUTE:
                case CARD_NOTIFICATION_TO_HOUR:
                case CARD_NOTIFICATION_TO_MINUTE:
                case CARD_NOTIFICATION_INTERVAL_HOUR:
                case CARD_NOTIFICATION_INTERVAL_MINUTE:
                    editor.putInt(entry.getKey(), Integer.valueOf(entry.getValue()));
                    break;
                case IMAGE_SEARCH_API_KEY:
                case IMAGE_SEARCH_CX_KEY:
                case SYNC_SERVER_URL:
                    editor.putString(entry.getKey(), entry.getValue());
                    break;
            }
        }

        editor.commit();
    }

    public void setImageSearchKeys(String apiKey, String cxKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE_SEARCH_API_KEY, apiKey);
        editor.putString(IMAGE_SEARCH_CX_KEY, cxKey);
        editor.commit();
    }

    public Boolean getCardNotificationEnabled() {
        return sharedPreferences.getBoolean(CARD_NOTIFICATION_ENABLED, false);
    }

    public void setCardNotificationEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CARD_NOTIFICATION_ENABLED, enabled);
        editor.commit();
    }

    public Integer getCardNotificationFromHour() {
        return sharedPreferences.getInt(CARD_NOTIFICATION_FROM_HOUR, 10);
    }

    public Integer getCardNotificationFromMinute() {
        return sharedPreferences.getInt(CARD_NOTIFICATION_FROM_MINUTE, 0);
    }

    public Integer getCardNotificationIntervalHour() {
        return sharedPreferences.getInt(CARD_NOTIFICATION_INTERVAL_HOUR, 1);
    }

    public Integer getCardNotificationIntervalMinute() {
        return sharedPreferences.getInt(CARD_NOTIFICATION_INTERVAL_MINUTE, 0);
    }

    public Integer getCardNotificationToHour() {
        return sharedPreferences.getInt(CARD_NOTIFICATION_TO_HOUR, 16);
    }

    public Integer getCardNotificationToMinute() {
        return sharedPreferences.getInt(CARD_NOTIFICATION_TO_MINUTE, 0);
    }

    public String getImageSearchApiKey() {
        return sharedPreferences.getString(IMAGE_SEARCH_API_KEY, null);
    }

    public String getImageSearchCxKey() {
        return sharedPreferences.getString(IMAGE_SEARCH_CX_KEY, null);
    }

    public String getSyncServerUrl() {
        return sharedPreferences.getString(SYNC_SERVER_URL, "");
    }

    public void setSyncServerUrl(String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SYNC_SERVER_URL, url);
        editor.commit();
    }

}