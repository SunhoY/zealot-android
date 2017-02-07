package io.harry.zealot.module;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.helper.FirebaseHelper;

@Module
public class FirebaseModule {
    @Provides
    public FirebaseHelper provideFirebaseHelper() {
        return new FirebaseHelper();
    }
}
