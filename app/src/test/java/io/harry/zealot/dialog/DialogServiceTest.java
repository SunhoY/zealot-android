package io.harry.zealot.dialog;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowProgressDialog;

import io.harry.zealot.BuildConfig;

import static android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert;
import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DialogServiceTest {
    private AppCompatActivity testActivity;
    private DialogService subject;

    @Before
    public void setUp() throws Exception {
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

        assertThat(shadowAlertDialog.getProgressStyle()).isEqualTo(Theme_AppCompat_Light_Dialog_Alert);
    }
}