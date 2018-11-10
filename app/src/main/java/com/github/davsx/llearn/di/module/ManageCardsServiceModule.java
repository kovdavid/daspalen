package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.LLearnRepository;
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = LLearnRepositoryModule.class)
public class ManageCardsServiceModule {

    @Provides
    ManageCardsService provide(LLearnRepository repository) {
        return new ManageCardsService(repository);
    }

}

