package io.harry.zealot.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.service.GagService;
import io.harry.zealot.wrapper.GagPagerAdapterWrapper;

@Module(includes = ContextModule.class)
public class GagTestModule {
    @Provides @Singleton
    public GagPagerAdapterWrapper provideGagPagerAdapterWrapper() {
        return new GagPagerAdapterWrapper();
    }

    @Provides @Singleton
    public GagService provideGagService(Context context) {
        return new GagService(context);
    }
}
