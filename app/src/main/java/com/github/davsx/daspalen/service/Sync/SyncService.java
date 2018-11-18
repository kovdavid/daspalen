package com.github.davsx.daspalen.service.Sync;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import okhttp3.OkHttpClient;

public class SyncService {

    private DaspalenRepository repository;
    private CardImageService imageService;
    private SettingsService settingsService;
    private OkHttpClient httpClient;

    public SyncService(DaspalenRepository repository,
                       CardImageService imageService,
                       OkHttpClient httpClient,
                       SettingsService settingsService) {
        this.repository = repository;
        this.imageService = imageService;
        this.httpClient = httpClient;
        this.settingsService = settingsService;
    }

    public boolean startSync() {
        if (settingsService.getSyncServerUrl() == null || settingsService.getSyncServerUrl().equals("")) {
            return false;
        }

        return true;
    }

}
