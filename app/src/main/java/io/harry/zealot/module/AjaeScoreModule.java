package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.range.AjaeScoreRange;

@Module
public class AjaeScoreModule {

    @Provides
    @Singleton
    AjaeScoreRange provideAjaeScoreRange() {
        return new AjaeScoreRange();
    }
}
