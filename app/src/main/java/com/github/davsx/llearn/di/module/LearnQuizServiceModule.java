package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.LLearnRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        LLearnRepositoryModule.class,
        CardImageServiceModule.class
})
public class LearnQuizServiceModule {

    @Provides
    LearnQuizService provide(LLearnRepository repository, CardImageService cardImageService) {
        return new LearnQuizService(repository, cardImageService);
    }

}