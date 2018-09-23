package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.CardExport.CardExportService;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.CardImport.CardImportService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {CardRepositoryModule.class, CardImageModule.class})
public class CardImportModule {
    @Provides
    CardImportService provideCardExportModule(CardRepository cardRepository, CardImageService cardImageService) {
        return new CardImportService(cardRepository, cardImageService);
    }

}

