package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.LLearnDatabase;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = LLearnDatabaseModule.class)
public class CardRepositoryModule {

    @Singleton
    @Provides
    CardRepository provide(LLearnDatabase database) {
        return new CardRepository(database.cardDao());
    }

}
