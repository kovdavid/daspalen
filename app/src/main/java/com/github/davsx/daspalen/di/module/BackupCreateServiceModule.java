package com.github.davsx.daspalen.di.module;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.BackupCreate.BackupCreateService;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        DaspalenRepositoryModule.class,
        CardImageServiceModule.class,
        SettingsModule.class
})
public class BackupCreateServiceModule {

    @Provides
    BackupCreateService provide(DaspalenRepository repository,
                                CardImageService cardImageService,
                                SettingsService settingsService) {
        return new BackupCreateService(repository, cardImageService, settingsService);
    }

}

