package com.github.davsx.llearn.di.module;

import android.content.SharedPreferences;
import com.github.davsx.llearn.service.Settings.SettingsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {SharedPreferencesModule.class})
public class SettingsModule {

    @Provides
    SettingsService provide(SharedPreferences sharedPreferences) {
        return new SettingsService(sharedPreferences);
    }

}