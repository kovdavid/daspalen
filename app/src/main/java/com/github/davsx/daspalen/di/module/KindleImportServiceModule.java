package com.github.davsx.daspalen.di.module;

import android.content.Context;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.KindleImport.KindleImportService;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        DaspalenRepositoryModule.class,
        ContextModule.class
})
public class KindleImportServiceModule {

    @Provides
    KindleImportService provide(DaspalenRepository repository, Context context) {
        return new KindleImportService(repository, context);
    }

}

