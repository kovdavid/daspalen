package com.github.davsx.daspalen.di.module;

import android.content.SharedPreferences;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {SharedPreferencesModule.class})
public class SettingsServiceModule {

    @Provides
    SettingsService provide(SharedPreferences sharedPreferences) {
        return new SettingsService(sharedPreferences);
    }

}