package com.github.davsx.llearn;

import android.app.Application;
import com.github.davsx.llearn.infrastructure.AppComponent;

public class LLearnApplication extends Application {
    AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
