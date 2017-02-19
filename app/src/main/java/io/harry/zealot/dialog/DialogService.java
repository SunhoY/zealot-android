package io.harry.zealot.dialog;

import android.app.ProgressDialog;
import android.content.Context;

import static android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert;

public class DialogService {
    public ProgressDialog getProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context, Theme_AppCompat_Light_Dialog_Alert);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        return progressDialog;
    }
}
