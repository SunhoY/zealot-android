package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.range.AjaeScoreRange;

import static org.mockito.Mockito.mock;

@Module
public class TestAjaeScoreModule {
    @Provides
    @Singleton
    AjaeScoreRange provideAjaeScoreRange() {
        return mock(AjaeScoreRange.class);
    }
}
