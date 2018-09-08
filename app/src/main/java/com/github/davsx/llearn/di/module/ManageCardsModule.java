package com.github.davsx.llearn.di.module;

import android.util.Log;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;
import dagger.Module;
import dagger.Provides;


@Module(includes = CardRepositoryModule.class)
public class ManageCardsModule {

    private static final String TAG = "ManageCardsModule";

    @Provides
    ManageCardsService provideManageCardsService(CardRepository cardRepository) {
        Log.d(TAG, "provideManageCardsService");
        return new ManageCardsService(cardRepository);
    }

}

