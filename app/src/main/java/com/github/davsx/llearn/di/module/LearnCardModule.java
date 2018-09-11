package com.github.davsx.llearn.di.module;

import android.util.Log;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = CardRepositoryModule.class)
public class LearnCardModule {

    private static final String TAG = "LearnCardModule";

    @Provides
    LearnQuizService provideLearnCardService(CardRepository cardRepository) {
        Log.d(TAG, "provideLearnCardService");
        return new LearnQuizService(cardRepository);
    }

}
