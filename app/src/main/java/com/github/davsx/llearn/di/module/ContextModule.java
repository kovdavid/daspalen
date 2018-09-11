package com.github.davsx.llearn.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    Context context;
    SharedPreferences sharedPreferences;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context context() {
        return context.getApplicationContext();
    }

    @Provides
    public SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("llearn", Context.MODE_PRIVATE);
    }
}