package io.harry.zealot.activity;

import android.content.Intent;
import android.widget.Button;

import org.assertj.android.api.content.IntentAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.BuildConfig;
import io.harry.zealot.R;
import io.harry.zealot.TestZealotApplication;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.AjaeImageView;
import io.harry.zealot.view.AjaeMessageView;
import io.harry.zealot.view.AjaePercentageView;

import static io.harry.zealot.state.AjaePower.FULL;
import static io.harry.zealot.state.AjaePower.MEDIUM;
import static io.harry.zealot.state.AjaePower.NO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ResultActivityTest {
    private static final int SCORE_NO_MATTER = 80;
    private static final AjaePower POWER_NO_MATTER = AjaePower.FULL;
    private ResultActivity subject;

    @BindView(R.id.ajae_score)
    AjaePercentageView ajaeScore;
    @BindView(R.id.test_again)
    Button testAgain;
    @BindView(R.id.result_message)
    AjaeMessageView resultMessage;
    @BindView(R.id.share_sns)
    Button share;
    @BindView(R.id.result_image)
    AjaeImageView resultImage;

    @Inject
    AjaeScoreRange mockAjaeScoreRange;

    private Field ajaeStateOfPercentageView;
    private Field ajaeStateOfImageView;
    private Field ajaeStateOfMessageView;

    private AjaePower ajaeStateValueOfPercentageView;
    private AjaePower ajaeStateValueOfImageView;
    private AjaePower ajaeStateValueOfMessageView;


    @Before
    public void setUp() throws Exception {
        ajaeStateOfPercentageView = AjaePercentageView.class.getDeclaredField("ajaePower");
        ajaeStateOfPercentageView.setAccessible(true);

        ajaeStateOfImageView = AjaeImageView.class.getDeclaredField("ajaePower");
        ajaeStateOfImageView.setAccessible(true);

        ajaeStateOfMessageView = AjaeMessageView.class.getDeclaredField("ajaePower");
        ajaeStateOfMessageView.setAccessible(true);
    }

    public void setUp(int score, AjaePower ajaePower) throws Exception {
        Intent intent = new Intent();
        intent.putExtra("ajaeScore", score);

        ((TestZealotApplication)application).getZealotComponent().inject(this);

        when(mockAjaeScoreRange.getRange(anyInt())).thenReturn(ajaePower);

        subject = Robolectric.buildActivity(ResultActivity.class).withIntent(intent).create().get();
        ButterKnife.bind(this, subject);

        ajaeStateValueOfPercentageView = (AjaePower) ajaeStateOfPercentageView.get(this.ajaeScore);
        ajaeStateValueOfImageView = (AjaePower) ajaeStateOfImageView.get(resultImage);
        ajaeStateValueOfMessageView = (AjaePower) ajaeStateOfMessageView.get(resultMessage);
    }

    @After
    public void tearDown() throws Exception {
        ajaeStateOfPercentageView.setAccessible(false);
        ajaeStateOfImageView.setAccessible(false);
    }

    @Test
    public void onCreate_setsAjaeScore() throws Exception {
        setUp(95, POWER_NO_MATTER);

        assertThat(ajaeScore.getText()).isEqualTo("95 %");
    }

    @Test
    public void onCreate_changesStateOfScoreAndAjaeImageAsNotAjae_whenScoreIsUnder70() throws Exception {
        setUp(SCORE_NO_MATTER, AjaePower.NO);

        assertThat(ajaeStateValueOfPercentageView).isEqualTo(NO);
        assertThat(ajaeStateValueOfMessageView).isEqualTo(NO);
        assertThat(ajaeStateValueOfImageView).isEqualTo(NO);
    }

    @Test
    public void onCreate_changesStateOfScoreAndAjaeImageAsMediumAjae_whenScoreIsBetween70And90() throws Exception {
        setUp(SCORE_NO_MATTER, AjaePower.MEDIUM);

        assertThat(ajaeStateValueOfPercentageView).isEqualTo(MEDIUM);
        assertThat(ajaeStateValueOfMessageView).isEqualTo(MEDIUM);
        assertThat(ajaeStateValueOfImageView).isEqualTo(MEDIUM);
    }

    @Test
    public void onCreate_changesStateOfScoreAndAjaeImageAsFullAjae_whenScoreIsOver90() throws Exception {
        setUp(SCORE_NO_MATTER, AjaePower.FULL);

        assertThat(ajaeStateValueOfPercentageView).isEqualTo(FULL);
        assertThat(ajaeStateValueOfMessageView).isEqualTo(FULL);
        assertThat(ajaeStateValueOfImageView).isEqualTo(FULL);
    }

    @Test
    public void clickOnTestAgain_finishesActivity() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        testAgain.performClick();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void clickOnShare_sendShareIntentWithLink() throws Exception {
        setUp(80, POWER_NO_MATTER);

        share.performClick();

        Intent chooser = shadowOf(subject).getNextStartedActivity();

        IntentAssert chooserIntentAssert = new IntentAssert(chooser);

        assertThat(chooserIntentAssert.hasAction(Intent.ACTION_CHOOSER));
        assertThat(chooserIntentAssert.hasExtra(Intent.EXTRA_TITLE, "아재력 알리기"));

        Intent originalIntent = chooser.getParcelableExtra(Intent.EXTRA_INTENT);

        IntentAssert originalIntentAssert = new IntentAssert(originalIntent);

        assertThat(originalIntentAssert.hasAction(Intent.ACTION_SEND));
        assertThat(originalIntentAssert.hasExtra(Intent.EXTRA_TEXT, "http://placeholder.com?score=80"));
        assertThat(originalIntentAssert.hasType("text/plain"));
    }
}