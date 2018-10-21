package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.MemriseImport.MemriseImportService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {CardRepositoryModule.class})
public class MemriseImportModule {

    @Provides
    MemriseImportService provideMemriseImportService(CardRepository cardRepository) {
        return new MemriseImportService(cardRepository);
    }

}

