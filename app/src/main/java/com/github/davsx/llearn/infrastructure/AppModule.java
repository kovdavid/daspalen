package com.github.davsx.llearn.infrastructure;

import android.app.Application;
import android.arch.persistence.room.Room;
import com.github.davsx.llearn.domain.persistence.CardRepository;
import com.github.davsx.llearn.infrastructure.persistence.Database;
import com.github.davsx.llearn.infrastructure.persistence.dao.CardDao;
import com.github.davsx.llearn.infrastructure.persistence.repository.CardRepositoryImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Module
public class AppModule {
    @Provides
    @Singleton
    Database provideDatabase(Application application) {
        return Room.databaseBuilder(application.getApplicationContext(), Database.class, "llearn").build();
    }

    @Provides
    @Singleton
    CardDao provideCardDao(Database database) {
        return database.cardDao();
    }

    @Provides
    Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Singleton
    CardRepository provideCardRepository(CardDao cardDao, Executor executor) {
        return new CardRepositoryImpl(cardDao, executor);
    }

}
