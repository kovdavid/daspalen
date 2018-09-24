package com.github.davsx.llearn.di.component;

import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.activities.CardEditor.CardEditorActivity;
import com.github.davsx.llearn.activities.CardExport.CardExportActivity;
import com.github.davsx.llearn.activities.CardImport.CardImportActivity;
import com.github.davsx.llearn.activities.KindleImport.KindleImportActivity;
import com.github.davsx.llearn.activities.LearnQuiz.LearnQuizActivity;
import com.github.davsx.llearn.activities.Main.MainActivity;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.llearn.di.module.*;
import com.github.davsx.llearn.service.CardExport.CardExportService;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.CardImport.CardImportService;
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
        SpeakerModule.class,
        CardExportModule.class,
        CardImportModule.class
})
public interface ApplicationComponent {

    void inject(LLearnApplication app);

    void inject(MainActivity activity);

    void inject(ManageCardsActivity activity);

    void inject(LearnQuizActivity activity);

    void inject(KindleImportActivity activity);

    void inject(CardEditorActivity activity);

    void inject(CardExportActivity activity);

    void inject(CardImportActivity activity);

    CardExportService getCardExportService();

    CardImportService getCardImportService();

    CardImageService getCardImageService();

    LearnQuizService getLearnCardService();

    ManageCardsService getManageCardsService();

    SpeakerService getSpeakerService();

}
