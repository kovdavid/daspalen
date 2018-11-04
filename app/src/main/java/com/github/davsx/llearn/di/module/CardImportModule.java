package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.CardImport.CardImportService;
import com.github.davsx.llearn.service.Settings.SettingsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        CardRepositoryModule.class,
        JournalRepositoryModule.class,
        CardImageModule.class,
        SettingsModule.class
})
public class CardImportModule {

    @Provides
    CardImportService provide(CardRepository cardRepository,
                                               JournalRepository journalRepository,
                                               CardImageService cardImageService,
                                               SettingsService settingsService) {
        return new CardImportService(cardRepository, journalRepository, cardImageService, settingsService);
    }

}

