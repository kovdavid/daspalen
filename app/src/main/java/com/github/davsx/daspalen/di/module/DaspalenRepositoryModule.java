package com.github.davsx.daspalen.di.module;

import com.github.davsx.daspalen.persistence.DaspalenDatabase;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = DaspalenDatabaseModule.class)
public class DaspalenRepositoryModule {

    @Singleton
    @Provides
    DaspalenRepository provide(DaspalenDatabase database) {
        return new DaspalenRepository(database.daspalenDao());
    }

}
