package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = CardRepositoryModule.class)
public class ManageCardsModule {
    @Provides
    ManageCardsService provideManageCardsService(CardRepository cardRepository) {
        return new ManageCardsService(cardRepository);
    }

}

