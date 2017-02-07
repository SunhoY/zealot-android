package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.helper.AnimationHelper;

import static org.mockito.Mockito.mock;

@Module
public class TestAnimationModule {
    @Singleton @Provides
    public AnimationHelper provideAnimationWrapper() {
        return mock(AnimationHelper.class);
    }
}
