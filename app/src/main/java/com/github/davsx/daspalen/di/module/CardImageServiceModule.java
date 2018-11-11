package com.github.davsx.daspalen.di.module;

import android.content.Context;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.io.File;

@Module(includes = ContextModule.class)
public class CardImageServiceModule {

    @Singleton
    @Provides
    CardImageService provide(Context context) {
        File imageDir = new File(context.getExternalFilesDir(null) + File.separator + "images");
        imageDir.mkdir();
        return new CardImageService(imageDir);
    }

}
