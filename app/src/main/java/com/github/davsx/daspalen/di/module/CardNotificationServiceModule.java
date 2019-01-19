package com.github.davsx.daspalen.di.module;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardNotification.CardNotificationService;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        DaspalenRepositoryModule.class,
        SettingsServiceModule.class
})
public class CardNotificationServiceModule {

    @Provides
    CardNotificationService provide(DaspalenRepository repository, SettingsService service) {
        return new CardNotificationService(repository, service);
    }

}
