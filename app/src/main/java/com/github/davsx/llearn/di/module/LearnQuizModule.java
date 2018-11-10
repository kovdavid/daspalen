package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepositoryOld;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        LLearnRepositoryModule.class,
        JournalRepositoryModule.class,
        CardImageServiceModule.class
})
public class LearnQuizModule {
    @Provides
    LearnQuizService provide(CardRepositoryOld cardRepository,
                             JournalRepository journalRepository,
                             CardImageService cardImageService) {
        return new LearnQuizService(cardRepository, journalRepository, cardImageService);
    }
}