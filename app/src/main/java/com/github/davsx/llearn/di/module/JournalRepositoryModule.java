package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.LLearnDatabase;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = LLearnDatabaseModule.class)
public class JournalRepositoryModule {

    @Singleton
    @Provides
    JournalRepository provide(LLearnDatabase database) {
        return new JournalRepository(database.journalDao());
    }

}
