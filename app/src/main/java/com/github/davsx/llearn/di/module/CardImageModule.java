package com.github.davsx.llearn.di.module;

import android.content.Context;
import com.github.davsx.llearn.service.FileService.CardImageService;
import com.github.davsx.llearn.service.FileService.CardImageServiceImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.io.File;

@Module(includes = ContextModule.class)
public class CardImageModule {

    @Singleton
    @Provides
    CardImageService provideCardImageService(Context context) {
        File imageDir = new File(context.getExternalFilesDir(null) + File.separator + "images");
        imageDir.mkdir();
        return new CardImageServiceImpl(imageDir);
    }

}