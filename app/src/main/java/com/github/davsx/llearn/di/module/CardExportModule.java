package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import com.github.davsx.llearn.service.CardExport.CardExportService;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.Settings.SettingsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        CardRepositoryModule.class,
        JournalRepositoryModule.class,
        CardImageModule.class,
        SettingsModule.class
})
public class CardExportModule {
    @Provides
    CardExportService provide(CardRepository cardRepository,
                                               JournalRepository journalRepository,
                                               CardImageService cardImageService,
                                               SettingsService settingsService) {
        return new CardExportService(cardRepository, journalRepository, cardImageService, settingsService);
    }
}

