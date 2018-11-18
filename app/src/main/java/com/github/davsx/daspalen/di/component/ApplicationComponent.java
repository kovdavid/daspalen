package com.github.davsx.daspalen.di.component;

import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.activities.BackupCreate.BackupCreateActivity;
import com.github.davsx.daspalen.activities.BackupImport.BackupImportActivity;
import com.github.davsx.daspalen.activities.CardEditor.CardEditorActivity;
import com.github.davsx.daspalen.activities.KindleImport.KindleImportActivity;
import com.github.davsx.daspalen.activities.LearnQuiz.LearnQuizActivity;
import com.github.davsx.daspalen.activities.Main.MainActivity;
import com.github.davsx.daspalen.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.daspalen.activities.MemriseImport.MemriseImportActivity;
import com.github.davsx.daspalen.activities.ReviewQuiz.ReviewQuizActivity;
import com.github.davsx.daspalen.activities.Settings.SettingsActivity;
import com.github.davsx.daspalen.di.module.*;
import com.github.davsx.daspalen.service.BootReceiver.BootReceiverService;
import com.github.davsx.daspalen.service.CardNotification.CardNotificationService;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        MainActivityServiceModule.class,
        KindleImportServiceModule.class,
        LearnQuizServiceModule.class,
        ReviewQuizServiceModule.class,
        ManageCardsServiceModule.class,
        CardImageServiceModule.class,
        SpeakerModule.class,
        BackupCreateServiceModule.class,
        MemriseImportServiceModule.class,
        SettingsModule.class,
        OkHttpModule.class,
        BackupImportServiceModule.class
})
public interface ApplicationComponent {

    void inject(DaspalenApplication app);

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

    void inject(CardNotificationService service);

    void inject(BootReceiverService service);

    SettingsService getSettingsService();

}
