package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.LLearnRepository;
import com.github.davsx.llearn.service.BackupImport.BackupImportService;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.Settings.SettingsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        LLearnRepositoryModule.class,
        CardImageServiceModule.class,
        SettingsModule.class
})
public class BackupImportServiceModule {

    @Provides
    BackupImportService provide(LLearnRepository repository,
                                CardImageService cardImageService,
                                SettingsService settingsService) {
        return new BackupImportService(repository, cardImageService, settingsService);
    }

}

