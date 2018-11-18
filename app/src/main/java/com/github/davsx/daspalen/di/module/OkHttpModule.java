package com.github.davsx.daspalen.di.module;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import javax.inject.Singleton;
import java.util.Collections;

@Module
public class OkHttpModule {

    @Singleton
    @Provides
    public OkHttpClient provide() {
        return new OkHttpClient.Builder().protocols(Collections.singletonList(Protocol.HTTP_1_1)).build();
    }

}