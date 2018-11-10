package com.github.davsx.llearn.di.module;

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