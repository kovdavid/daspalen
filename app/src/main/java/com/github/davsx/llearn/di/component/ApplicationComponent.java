package com.github.davsx.llearn.di.component;

import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.di.module.LearnCardModule;
import com.github.davsx.llearn.di.module.ManageCardsModule;
import com.github.davsx.llearn.activities.KindleImport.KindleImportActivity;
import com.github.davsx.llearn.activities.LearnCard.LearnCardActivity;
import com.github.davsx.llearn.activities.Main.MainActivity;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizService;
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        LearnCardModule.class,
        ManageCardsModule.class
})
public interface ApplicationComponent {

    LearnQuizService getLearnCardService();

    ManageCardsService getManageCardsService();

    void inject(LLearnApplication app);

    void inject(MainActivity activity);

    void inject(ManageCardsActivity activity);

    void inject(LearnCardActivity activity);

    void inject(KindleImportActivity activity);

}
