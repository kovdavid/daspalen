package com.github.davsx.llearn.di.module;

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
