package com.github.davsx.llearn.di.module;

import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;

@Module
public class OkHttpModule {

    @Provides
    public OkHttpClient provide() {
        return new OkHttpClient();
    }

}