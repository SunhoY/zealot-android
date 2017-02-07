package io.harry.zealot.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class PermissionHelper {
    private Context context;

    public PermissionHelper(Context context) {
        this.context = context;
    }

    public boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
