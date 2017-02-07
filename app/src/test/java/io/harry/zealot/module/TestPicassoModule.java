package io.harry.zealot.module;

import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestPicassoModule {
    @Provides @Singleton
    public Picasso providePicasso() {
        return mock(Picasso.class);
    }
}