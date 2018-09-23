package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.CardExport.CardExportService;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {CardRepositoryModule.class, CardImageModule.class})
public class CardExportModule {
    @Provides
    CardExportService provideCardExportModule(CardRepository cardRepository, CardImageService cardImageService) {
        return new CardExportService(cardRepository, cardImageService);
    }

}

