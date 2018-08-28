package com.github.davsx.llearn.infrastructure;

import android.app.Application;
import com.github.davsx.llearn.LLearnApplication;
import dagger.BindsInstance;
import dagger.Component;

@Component
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(LLearnApplication application);

}
