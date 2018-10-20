package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.ReviewQuiz.ReviewQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        CardRepositoryModule.class,
        CardImageModule.class
})
public class ReviewQuizModule {
    @Provides
    ReviewQuizService provideReviewQuizService(CardRepository cardRepository, CardImageService cardImageService) {
        return new ReviewQuizService(cardRepository, cardImageService);
    }
}