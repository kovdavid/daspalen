package com.github.davsx.llearn.di.component;

import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.activities.BackupCreate.BackupCreateActivity;
import com.github.davsx.llearn.activities.BackupImport.BackupImportActivity;
import com.github.davsx.llearn.activities.CardEditor.CardEditorActivity;
import com.github.davsx.llearn.activities.KindleImport.KindleImportActivity;
import com.github.davsx.llearn.activities.LearnQuiz.LearnQuizActivity;
import com.github.davsx.llearn.activities.Main.MainActivity;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.llearn.activities.MemriseImport.MemriseImportActivity;
import com.github.davsx.llearn.activities.ResetImages.ResetImagesActivity;
import com.github.davsx.llearn.activities.ReviewQuiz.ReviewQuizActivity;
import com.github.davsx.llearn.activities.Settings.SettingsActivity;
import com.github.davsx.llearn.di.module.*;
import com.github.davsx.llearn.service.BootReceiver.BootReceiverService;
import com.github.davsx.llearn.service.Settings.SettingsService;
import com.github.davsx.llearn.service.WordOfTheDay.WordOfTheDayNotificationService;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        LearnQuizServiceModule.class,
        ReviewQuizServiceModule.class,
        ManageCardsServiceModule.class,
        CardImageServiceModule.class,
        SpeakerModule.class,
        BackupCreateServiceModule.class,
        MemriseImportServiceModule.class,
        SettingsModule.class,
        BackupImportServiceModule.class
})
public interface ApplicationComponent {

    void inject(LLearnApplication app);

    void inject(MainActivity activity);

    void inject(ManageCardsActivity activity);

    void inject(LearnQuizActivity activity);

    void inject(KindleImportActivity activity);

    void inject(MemriseImportActivity activity);

    void inject(CardEditorActivity activity);

    void inject(BackupCreateActivity activity);

    void inject(BackupImportActivity activity);

    void inject(ReviewQuizActivity activity);

    void inject(SettingsActivity activity);

    void inject(ResetImagesActivity activity);

    void inject(WordOfTheDayNotificationService service);

    void inject(BootReceiverService service);

    SettingsService getSettingsService();

}
