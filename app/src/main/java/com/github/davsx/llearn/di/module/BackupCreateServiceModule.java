package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.LLearnRepository;
import com.github.davsx.llearn.service.BackupCreate.BackupCreateService;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.Settings.SettingsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        LLearnRepositoryModule.class,
        CardImageServiceModule.class,
        SettingsModule.class
})
public class BackupCreateServiceModule {

    @Provides
    BackupCreateService provide(LLearnRepository repository,
                                CardImageService cardImageService,
                                SettingsService settingsService) {
        return new BackupCreateService(repository, cardImageService, settingsService);
    }

}

