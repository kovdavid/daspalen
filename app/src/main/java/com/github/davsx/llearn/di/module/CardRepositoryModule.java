package com.github.davsx.llearn.di.module;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;
import com.github.davsx.llearn.persistence.LLearnDatabase;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = ContextModule.class)
public class CardRepositoryModule {

    private static final String TAG = "CardRepositoryModule";

    @Singleton
    @Provides
    CardRepository provideCardRepository(LLearnDatabase database) {
        return new CardRepository(database.cardDao());
    }

    @Provides
    LLearnDatabase provideDatabase(Context context) {
        Log.d(TAG, "provideDatabase");
        return Room.databaseBuilder(context, LLearnDatabase.class, "llearn").allowMainThreadQueries().build();
    }

}
