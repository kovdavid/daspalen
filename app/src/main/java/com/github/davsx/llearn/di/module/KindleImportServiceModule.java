package com.github.davsx.llearn.di.module;

import android.content.Context;
import com.github.davsx.llearn.persistence.repository.LLearnRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.KindleImport.KindleImportService;
import com.github.davsx.llearn.service.MainActivity.MainActivityService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        LLearnRepositoryModule.class,
        Context.class
})
public class KindleImportServiceModule {

    @Provides
    KindleImportService provide(LLearnRepository repository, Context context) {
        return new KindleImportService(repository, context);
    }

}
