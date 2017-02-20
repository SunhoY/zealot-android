package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.api.UrlShortenerApi;
import retrofit2.Retrofit;

import static org.mockito.Mockito.mock;

@Module
public class TestGoogleApiModule {
    @Provides
    @Singleton
    Retrofit provideGoogleApiRetrofit() {
        return mock(Retrofit.class);
    }

    @Provides
    @Singleton
    UrlShortenerApi provideUrlShortenerApi() {
        return mock(UrlShortenerApi.class);
    }
}