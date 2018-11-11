package com.github.davsx.daspalen.di.module;

import android.arch.persistence.room.Room;
import android.content.Context;
import com.github.davsx.daspalen.persistence.DaspalenDatabase;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = ContextModule.class)
public class DaspalenDatabaseModule {

    @Singleton
    @Provides
    DaspalenDatabase provide(Context context) {
        return Room.databaseBuilder(context, DaspalenDatabase.class, "daspalen")
                .allowMainThreadQueries()
                .build();
    }

}
