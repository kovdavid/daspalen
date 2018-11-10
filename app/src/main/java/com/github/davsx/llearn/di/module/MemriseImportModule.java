package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepositoryOld;
import com.github.davsx.llearn.service.MemriseImport.MemriseImportService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {LLearnRepositoryModule.class})
public class MemriseImportModule {

    @Provides
    MemriseImportService provide(CardRepositoryOld cardRepository) {
        return new MemriseImportService(cardRepository);
    }

}

