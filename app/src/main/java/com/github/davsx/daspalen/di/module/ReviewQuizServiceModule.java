package com.github.davsx.daspalen.di.module;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.ReviewQuiz.ReviewQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        DaspalenRepositoryModule.class,
        CardImageServiceModule.class
})
public class ReviewQuizServiceModule {

    @Provides
    ReviewQuizService provide(DaspalenRepository repository, CardImageService cardImageService) {
        return new ReviewQuizService(repository, cardImageService);
    }

}