package io.harry.zealot.wrapper;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesWrapper {
    public static final String TUTORIAL_SEEN = "tutorial_seen";

    private Context context;

    public SharedPreferencesWrapper(Context context) {
        this.context = context;
    }

    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("AZ_TEST", MODE_PRIVATE);
    }
}
