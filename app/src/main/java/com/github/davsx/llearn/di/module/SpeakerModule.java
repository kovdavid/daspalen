package com.github.davsx.llearn.di.module;

import android.content.Context;
import com.github.davsx.llearn.service.Speaker.SpeakerService;
import com.github.davsx.llearn.service.Speaker.SpeakerServiceImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = ContextModule.class)
public class SpeakerModule {

    @Singleton
    @Provides
    SpeakerService provideSpeakerService(Context context) {
        return new SpeakerServiceImpl(context);
    }

}
