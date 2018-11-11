package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.LLearnRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.ReviewQuiz.ReviewQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        LLearnRepositoryModule.class,
        CardImageServiceModule.class
})
public class ReviewQuizServiceModule {

    @Provides
    ReviewQuizService provide(LLearnRepository repository, CardImageService cardImageService) {
        return new ReviewQuizService(repository, cardImageService);
    }

}