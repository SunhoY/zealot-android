package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.service.GagService;
import io.harry.zealot.wrapper.GagPagerAdapterWrapper;

import static org.mockito.Mockito.mock;

@Module
public class TestGagTestModule {
    @Provides @Singleton
    public GagPagerAdapterWrapper provideGagPagerAdapterWrapper() {
        return mock(GagPagerAdapterWrapper.class);
    }

    @Provides @Singleton
    public GagService provideGagService() {
        return mock(GagService.class);
    }
}
