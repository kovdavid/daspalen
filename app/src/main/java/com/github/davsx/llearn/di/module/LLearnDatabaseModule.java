package com.github.davsx.llearn.di.module;

import android.arch.persistence.room.Room;
import android.content.Context;
import com.github.davsx.llearn.persistence.LLearnDatabase;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = ContextModule.class)
public class LLearnDatabaseModule {

    @Singleton
    @Provides
    LLearnDatabase provide(Context context) {
        return Room.databaseBuilder(context, LLearnDatabase.class, "llearn")
                .allowMainThreadQueries()
                .addMigrations(LLearnDatabase.MIGRATION_1_2)
                .build();
    }

}
