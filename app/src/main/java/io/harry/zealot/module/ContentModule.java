package io.harry.zealot.module;

import android.content.ContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.helper.BitmapHelper;

@Module(
        includes = ContextModule.class
)
public class ContentModule {
    @Provides @Singleton
    BitmapHelper provideBitmapHelper(ContentResolver contentResolver) {
        return new BitmapHelper(contentResolver);
    }
}
