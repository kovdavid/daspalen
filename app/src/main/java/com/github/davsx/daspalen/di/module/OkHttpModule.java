package com.github.davsx.daspalen.di.module;

import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class OkHttpModule {

    @Singleton
    @Provides
    public OkHttpClient provide() {
        return new OkHttpClient();
    }

}