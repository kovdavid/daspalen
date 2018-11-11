package com.github.davsx.daspalen.di.module;

import android.content.Context;
import com.github.davsx.daspalen.service.Speaker.SpeakerService;
import com.github.davsx.daspalen.service.Speaker.SpeakerServiceImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = ContextModule.class)
public class SpeakerModule {

    @Singleton
    @Provides
    SpeakerService provide(Context context) {
        return new SpeakerServiceImpl(context);
    }

}
