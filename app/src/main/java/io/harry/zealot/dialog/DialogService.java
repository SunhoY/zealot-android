package io.harry.zealot.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import io.harry.zealot.R;

import static android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert;

public class DialogService {
    public interface InputDialogListener {
        void onConfirm(String input);
    }

    public ProgressDialog getProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context, Theme_AppCompat_Light_Dialog_Alert);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        return progressDialog;
    }

    public AlertDialog getInputDialog(Context context, final InputDialogListener inputDialogListener) {
        return new AlertDialog.Builder(context, Theme_AppCompat_Light_Dialog_Alert)
                .setView(R.layout.nick_name_input_dialog)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText nickName = (EditText) ((AlertDialog) dialog).findViewById(R.id.nick_name);
                        String enteredNickName = nickName.getText().toString();

                        inputDialogListener.onConfirm(enteredNickName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }
}
