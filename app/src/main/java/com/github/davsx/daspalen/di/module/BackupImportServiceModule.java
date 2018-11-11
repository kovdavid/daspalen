package com.github.davsx.daspalen.di.module;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.BackupImport.BackupImportService;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        DaspalenRepositoryModule.class,
        CardImageServiceModule.class,
        SettingsModule.class
})
public class BackupImportServiceModule {

    @Provides
    BackupImportService provide(DaspalenRepository repository,
                                CardImageService cardImageService,
                                SettingsService settingsService) {
        return new BackupImportService(repository, cardImageService, settingsService);
    }

}

