package com.github.davsx.daspalen.service.Sync;

import android.support.annotation.NonNull;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import okhttp3.*;

import java.io.IOException;

public class SyncService implements Callback {

    private DaspalenRepository repository;
    private CardImageService imageService;
    private SettingsService settingsService;
    private OkHttpClient httpClient;
    private String serverUrl;

    public SyncService(DaspalenRepository repository,
                       CardImageService imageService,
                       OkHttpClient httpClient,
                       SettingsService settingsService) {
        this.repository = repository;
        this.imageService = imageService;
        this.httpClient = httpClient;
        this.settingsService = settingsService;
        this.serverUrl = settingsService.getSyncServerUrl();
    }

    public boolean startSync() {
        if (serverUrl == null || serverUrl.equals("")) {
            return false;
        }

        httpClient.newCall(createPingRequest()).enqueue(this);

        // TODO

        return true;
    }

    public void stopSync() {
        httpClient.dispatcher().cancelAll();
    }

    private Request createPingRequest() {
        HttpUrl url = new HttpUrl.Builder().host(serverUrl).addPathSegment("ping").build();
        return new Request.Builder().url(url).build();
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {

    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) {

    }
}
