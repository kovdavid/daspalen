package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import com.github.davsx.llearn.service.CardExport.CardExportService;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        CardRepositoryModule.class,
        JournalRepositoryModule.class,
        CardImageModule.class
})
public class CardExportModule {
    @Provides
    CardExportService provideCardExportService(CardRepository cardRepository,
                                               JournalRepository journalRepository,
                                               CardImageService cardImageService) {
        return new CardExportService(cardRepository, journalRepository, cardImageService);
    }
}

