package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.LLearnDatabase;
import com.github.davsx.llearn.persistence.repository.LLearnRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = LLearnDatabaseModule.class)
public class LLearnRepositoryModule {

    @Singleton
    @Provides
    LLearnRepository provide(LLearnDatabase database) {
        return new LLearnRepository(database.llearnDao());
    }

}
