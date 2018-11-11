package com.github.davsx.daspalen.di.module;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.MemriseImport.MemriseImportService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {DaspalenRepositoryModule.class})
public class MemriseImportServiceModule {

    @Provides
    MemriseImportService provide(DaspalenRepository repository) {
        return new MemriseImportService(repository);
    }

}