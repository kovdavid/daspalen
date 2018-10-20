package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        CardRepositoryModule.class,
        CardImageModule.class
})
public class LearnQuizModule {
    @Provides
    LearnQuizService provideLearnQuizService(CardRepository cardRepository, CardImageService cardImageService) {
        return new LearnQuizService(cardRepository, cardImageService);
    }
}