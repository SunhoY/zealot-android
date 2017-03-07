package io.harry.zealot.module;

import android.content.ContentResolver;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.helper.PermissionHelper;
import io.harry.zealot.wrapper.SharedPreferencesWrapper;

@Module
@Singleton
public class ContextModule {
    private Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }

    @Provides
    ContentResolver provideContentResolver(Context context) {
        return context.getContentResolver();
    }

    @Provides @Singleton
    PermissionHelper providePermissionHelper(Context context) {
        return new PermissionHelper(context);
    }

    @Provides @Singleton
    SharedPreferencesWrapper provideSharedPreferencesWrapper(Context context) {
        return new SharedPreferencesWrapper(context);
    }
}
