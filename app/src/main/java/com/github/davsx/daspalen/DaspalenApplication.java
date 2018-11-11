package com.github.davsx.daspalen;

import android.app.Activity;
import android.app.Application;
import com.github.davsx.daspalen.di.component.ApplicationComponent;
import com.github.davsx.daspalen.di.component.DaggerApplicationComponent;
import com.github.davsx.daspalen.di.module.ContextModule;
import com.github.davsx.daspalen.service.CardNotification.CardNotificationAlarmService;

public class DaspalenApplication extends Application {

    private ApplicationComponent applicationComponent;

    public static DaspalenApplication get(Activity activity) {
        return (DaspalenApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        CardNotificationAlarmService.setNextAlarm(getApplicationContext(), applicationComponent.getSettingsService());
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
