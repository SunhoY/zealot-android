package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.helper.BitmapHelper;

import static org.mockito.Mockito.mock;

@Module
public class TestContentModule {
    @Provides @Singleton
    BitmapHelper provideBitmapHelper() {
        return mock(BitmapHelper.class);
    }
}
