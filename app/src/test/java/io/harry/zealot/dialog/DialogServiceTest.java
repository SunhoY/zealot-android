package io.harry.zealot.dialog;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowProgressDialog;

import io.harry.zealot.BuildConfig;
import io.harry.zealot.R;
import io.harry.zealot.dialog.DialogService.InputDialogListener;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DialogServiceTest {
    private AppCompatActivity testActivity;
    private DialogService subject;

    @Mock
    InputDialogListener mockInputDialogListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        testActivity = Robolectric.setupActivity(AppCompatActivity.class);
        subject = new DialogService();
    }

    @Test
    public void getProgressDialog_createsProgressDialog_withPassedContextAndMessage() throws Exception {
        ProgressDialog progressDialog = subject.getProgressDialog(testActivity, "this is message");
        progressDialog.show();

        ProgressDialog latestDialog = (ProgressDialog) ShadowDialog.getLatestDialog();
        ShadowProgressDialog shadowAlertDialog = shadowOf(latestDialog);
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("this is message");
    }

    @Test
    public void getProgressDialog_disablesTouchOutsideOfDialog() throws Exception {
        ProgressDialog progressDialog = subject.getProgressDialog(testActivity, "this is message");
        progressDialog.show();

        ProgressDialog latestDialog = (ProgressDialog) ShadowDialog.getLatestDialog();
        ShadowProgressDialog shadowAlertDialog = shadowOf(latestDialog);

        assertThat(shadowAlertDialog.isCancelable()).isFalse();
        assertThat(shadowAlertDialog.isCancelableOnTouchOutside()).isFalse();
    }

    @Test
    @Ignore
    public void getProgressDialog_usesSupportV7DialogStyle() throws Exception {
        ProgressDialog progressDialog = subject.getProgressDialog(testActivity, "this is message");
        progressDialog.show();

        ProgressDialog latestDialog = (ProgressDialog) ShadowDialog.getLatestDialog();
        ShadowProgressDialog shadowAlertDialog = shadowOf(latestDialog);

        assertThat(shadowAlertDialog).isEqualTo(Theme_AppCompat_Light_Dialog_Alert);
    }

    @Test
    public void getInputDialog_onNegativeClick_dismissesDialog() throws Exception {
        AlertDialog alertDialog = subject.getInputDialog(testActivity, mockInputDialogListener);
        alertDialog.show();

        alertDialog.getButton(BUTTON_NEGATIVE).performClick();

        assertThat(alertDialog.isShowing()).isFalse();
    }

    @Test
    public void getInputDialog_onPositiveClick_runsConfirmListenerWithEnteredNickName() throws Exception {
        AlertDialog alertDialog = subject.getInputDialog(testActivity, mockInputDialogListener);
        alertDialog.show();

        EditText nickName = (EditText) alertDialog.findViewById(R.id.nick_name);
        nickName.setText("entered value");

        alertDialog.getButton(BUTTON_POSITIVE).performClick();

        verify(mockInputDialogListener).onConfirm("entered value");
    }
}