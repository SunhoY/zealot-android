package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.dialog.DialogService;

@Module
public class DialogModule {
    @Provides
    @Singleton
    DialogService provideDialogService() {
        return new DialogService();
    }
}
