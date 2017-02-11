package io.harry.zealot.activity;

import android.content.Intent;
import android.widget.EditText;

import org.assertj.android.api.content.IntentAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.BuildConfig;
import io.harry.zealot.R;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CheckAdminActivityTest {
    @BindView(R.id.admin_code)
    EditText adminCode;

    private CheckAdminActivity subject;

    @Before
    public void setUp() throws Exception {
        subject = Robolectric.setupActivity(CheckAdminActivity.class);

        ButterKnife.bind(this, subject);
    }

    @Test
    public void onConfirmClick_finishesActivity() throws Exception {
        String passCode = "pass code does not matter";

        adminCode.setText(passCode);

        subject.onConfirmClick();

        assertThat(subject.isFinishing()).isTrue();

    }

    @Test
    public void onConfirmClick_launchesVerificationActivity_whenPassCodeIsCorrect() throws Exception {
        //todo: make it more realistic
        //using firebase or encrypted something
        String passCode = application.getResources().getString(R.string.admin_pass_code);

        adminCode.setText(passCode);

        subject.onConfirmClick();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();

        IntentAssert intentAssert = new IntentAssert(nextStartedActivity);

        intentAssert.hasComponent(application, VerificationActivity.class);
    }
}