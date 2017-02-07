package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.helper.PermissionHelper;

import static org.mockito.Mockito.mock;

@Module
public class TestContextModule {
    @Provides @Singleton
    PermissionHelper providePermissionHelper() {
        return mock(PermissionHelper.class);
    }
}
