package com.github.davsx.llearn;

import android.app.Activity;
import android.app.Application;
import com.github.davsx.llearn.di.component.ApplicationComponent;
import com.github.davsx.llearn.di.component.DaggerApplicationComponent;
import com.github.davsx.llearn.di.module.ContextModule;
import com.github.davsx.llearn.service.CardNotification.CardNotificationAlarmService;

public class LLearnApplication extends Application {

    private ApplicationComponent applicationComponent;

    public static LLearnApplication get(Activity activity) {
        return (LLearnApplication) activity.getApplication();
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
