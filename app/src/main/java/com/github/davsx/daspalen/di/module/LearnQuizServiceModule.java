package com.github.davsx.daspalen.di.module;

import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.LearnQuiz.LearnQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        DaspalenRepositoryModule.class,
        CardImageServiceModule.class
})
public class LearnQuizServiceModule {

    @Provides
    LearnQuizService provide(DaspalenRepository repository, CardImageService cardImageService) {
        return new LearnQuizService(repository, cardImageService);
    }

}