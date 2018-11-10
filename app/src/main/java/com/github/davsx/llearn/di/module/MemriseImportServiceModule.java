package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.LLearnRepository;
import com.github.davsx.llearn.service.MemriseImport.MemriseImportService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {LLearnRepositoryModule.class})
public class MemriseImportServiceModule {

    @Provides
    MemriseImportService provide(LLearnRepository repository) {
        return new MemriseImportService(repository);
    }

}