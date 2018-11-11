package com.github.davsx.daspalen.di.module;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.MainActivity.MainActivityService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        DaspalenRepositoryModule.class,
        CardImageServiceModule.class
})
public class MainActivityServiceModule {

    @Provides
    MainActivityService provide(DaspalenRepository repository, CardImageService cardImageService) {
        return new MainActivityService(repository, cardImageService);
    }

}

