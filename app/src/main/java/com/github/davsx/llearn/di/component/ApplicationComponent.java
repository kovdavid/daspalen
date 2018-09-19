package com.github.davsx.llearn.di.component;

import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.activities.EditCard.EditCardActivity;
import com.github.davsx.llearn.activities.KindleImport.KindleImportActivity;
import com.github.davsx.llearn.activities.LearnQuiz.LearnQuizActivity;
import com.github.davsx.llearn.activities.Main.MainActivity;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.llearn.di.module.CardImageModule;
import com.github.davsx.llearn.di.module.LearnCardModule;
import com.github.davsx.llearn.di.module.ManageCardsModule;
import com.github.davsx.llearn.di.module.SpeakerModule;
import com.github.davsx.llearn.service.FileService.CardImageService;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizService;
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;
import com.github.davsx.llearn.service.Speaker.SpeakerService;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        LearnCardModule.class,
        ManageCardsModule.class,
        CardImageModule.class,
        SpeakerModule.class
})
public interface ApplicationComponent {

    void inject(LLearnApplication app);

    void inject(MainActivity activity);

    void inject(ManageCardsActivity activity);

    void inject(LearnQuizActivity activity);

    void inject(KindleImportActivity activity);

    void inject(EditCardActivity activity);

    CardImageService getCardImageService();

    LearnQuizService getLearnCardService();

    ManageCardsService getManageCardsService();

    SpeakerService getSpeakerService();

}
