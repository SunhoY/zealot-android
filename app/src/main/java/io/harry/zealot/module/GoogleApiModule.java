package io.harry.zealot.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.R;
import io.harry.zealot.api.UrlShortenerApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ContentModule.class)
public class GoogleApiModule {
    @Provides
    Retrofit provideGoogleApiRetrofit(Context context) {
        return new Retrofit.Builder()
                .baseUrl(context.getString(R.string.google_api_backend))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    @Provides
    UrlShortenerApi provideUrlshortenerApi(Retrofit retrofit) {
        return retrofit.create(UrlShortenerApi.class);
    }
}
