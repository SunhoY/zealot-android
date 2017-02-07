package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.helper.FirebaseHelper;

import static org.mockito.Mockito.mock;

@Module
public class TestFirebaseModule {
    @Provides @Singleton
    public FirebaseHelper provideFirebaseHelper() {
        return mock(FirebaseHelper.class);
    }
}