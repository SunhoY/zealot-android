package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.dialog.DialogService;

import static org.mockito.Mockito.mock;

@Module
public class TestDialogModule {
    @Provides
    @Singleton
    DialogService provideDialogService() {
        return mock(DialogService.class);
    }
}
