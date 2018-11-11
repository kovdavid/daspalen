package com.github.davsx.daspalen.di.module;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provide() {
        return context.getApplicationContext();
    }

}