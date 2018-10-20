package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.ReviewQuiz.ReviewQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = CardRepositoryModule.class)
public class ReviewQuizModule {
    @Provides
    ReviewQuizService provideReviewQuizService(CardRepository cardRepository) {
        return new ReviewQuizService(cardRepository);
    }
}