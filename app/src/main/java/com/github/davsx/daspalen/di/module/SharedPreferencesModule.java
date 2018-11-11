package com.github.davsx.daspalen.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = ContextModule.class)
public class SharedPreferencesModule {

    @Singleton
    @Provides
    SharedPreferences provide(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
