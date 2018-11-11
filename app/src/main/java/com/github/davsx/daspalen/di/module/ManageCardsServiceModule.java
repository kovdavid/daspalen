package com.github.davsx.daspalen.di.module;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.ManageCards.ManageCardsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = DaspalenRepositoryModule.class)
public class ManageCardsServiceModule {

    @Provides
    ManageCardsService provide(DaspalenRepository repository) {
        return new ManageCardsService(repository);
    }

}

