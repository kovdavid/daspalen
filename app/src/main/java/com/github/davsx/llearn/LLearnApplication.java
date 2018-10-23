package com.github.davsx.llearn;

import android.app.Activity;
import android.app.Application;
import com.github.davsx.llearn.di.component.ApplicationComponent;
import com.github.davsx.llearn.di.component.DaggerApplicationComponent;
import com.github.davsx.llearn.di.module.ContextModule;
import com.github.davsx.llearn.service.WordOfTheDay.WordOfTheDayAlarmService;

public class LLearnApplication extends Application {

    private ApplicationComponent applicationComponent;

    public static LLearnApplication get(Activity activity) {
        return (LLearnApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        WordOfTheDayAlarmService.setAlarm(getApplicationContext());

        applicationComponent = DaggerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
