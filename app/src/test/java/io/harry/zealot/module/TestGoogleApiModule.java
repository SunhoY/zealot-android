package io.harry.zealot.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.api.UrlShortenApi;
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
    UrlShortenApi provideUrlShortenApi() {
        return mock(UrlShortenApi.class);
    }
}