package com.github.davsx.daspalen.di.module;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import com.github.davsx.daspalen.service.Sync.SyncService;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module(includes = {
        DaspalenRepositoryModule.class,
        CardImageServiceModule.class,
        OkHttpModule.class,
        SettingsServiceModule.class
})
public class SyncServiceModule {

    @Provides
    SyncService provide(DaspalenRepository repository,
                        CardImageService cardImageService,
                        OkHttpClient httpClient,
                        SettingsService settingsService) {
        return new SyncService(repository, cardImageService, httpClient, settingsService);
    }

}

