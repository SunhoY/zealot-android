package io.harry.zealot.activity;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import com.google.common.collect.ImmutableMap;

import org.assertj.android.api.content.IntentAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.BuildConfig;
import io.harry.zealot.R;
import io.harry.zealot.TestZealotApplication;
import io.harry.zealot.api.UrlShortenApi;
import io.harry.zealot.dialog.DialogService;
import io.harry.zealot.dialog.DialogService.InputDialogListener;
import io.harry.zealot.helper.AnimationHelper;
import io.harry.zealot.model.Ajae;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.shadow.view.ShadowAjaeImageView;
import io.harry.zealot.shadow.view.ShadowAjaeMessageView;
import io.harry.zealot.shadow.view.ShadowAjaePercentageView;
import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.AjaeImageView;
import io.harry.zealot.view.AjaeMessageView;
import io.harry.zealot.view.AjaePercentageView;
import retrofit2.Call;
import retrofit2.Response;

import static io.harry.zealot.state.AjaePower.BURNT;
import static io.harry.zealot.state.AjaePower.MEDIUM;
import static io.harry.zealot.state.AjaePower.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowAjaeImageView.class, ShadowAjaeMessageView.class, ShadowAjaePercentageView.class})
public class ResultActivityTest {
    private static final int SCORE_NO_MATTER = 80;
    private static final AjaePower POWER_NO_MATTER = BURNT;
    private static final String NICK_NAME_NO_MATTER = "nick name does not matter";
    private ResultActivity subject;

    @BindView(R.id.ajae_percentage)
    AjaePercentageView ajaePercentage;
    @BindView(R.id.ajae_message)
    AjaeMessageView ajaeMessage;
    @BindView(R.id.ajae_image)
    AjaeImageView ajaeImage;
    @BindView(R.id.share_sns)
    Button share;
    @BindView(R.id.test_again)
    Button testAgain;

    @Inject
    AjaeScoreRange mockAjaeScoreRange;
    @Inject
    DialogService mockDialogService;
    @Inject
    UrlShortenApi mockUrlShortenApi;
    @Inject
    AnimationHelper mockAnimationHelper;

    @Mock
    AlertDialog mockInputDialog;
    @Mock
    Call<Map<String, Object>> mockMapCall;
    @Mock
    ProgressDialog mockProgressDialog;
    @Mock
    ValueAnimator mockValueIncreaseAnimation;

    @Captor
    ArgumentCaptor<InputDialogListener> inputDialogListenerCaptor;
    @Captor
    ArgumentCaptor<AnimatorUpdateListener> animatorUpdateListenerCaptor;

    private ShadowAjaeImageView shadowAjaeImageView;
    private ShadowAjaePercentageView shadowAjaePercentageView;
    private ShadowAjaeMessageView shadowAjaeMessageView;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ((TestZealotApplication)application).getZealotComponent().inject(this);
    }

    public void setUp(int score, AjaePower ajaePower) throws Exception {
        Intent intent = new Intent();
        intent.putExtra("ajaeScore", score);

        when(mockAjaeScoreRange.getAjaePower(anyInt())).thenReturn(ajaePower);
        when(mockDialogService.getInputDialog(any(Context.class), any(InputDialogListener.class)))
                .thenReturn(mockInputDialog);
        when(mockAnimationHelper.getValueIncreaseAnimator(anyInt(), anyInt())).thenReturn(mockValueIncreaseAnimation);

        subject = Robolectric.buildActivity(ResultActivity.class).withIntent(intent).create().get();
        ButterKnife.bind(this, subject);

        shadowAjaeImageView = (ShadowAjaeImageView) shadowOf(ajaeImage);
        shadowAjaeMessageView = (ShadowAjaeMessageView) shadowOf(ajaeMessage);
        shadowAjaePercentageView = (ShadowAjaePercentageView) shadowOf(ajaePercentage);
    }

    @Test
    public void onCreate_getsValueIncreasingAnimationFromAnimationHelperWithDuration2000() throws Exception {
        setUp(95, POWER_NO_MATTER);

        verify(mockAnimationHelper).getValueIncreaseAnimator(95, 2000);
    }

    @Test
    public void onCreate_addsAnimatorListenerOnValueIncreaseAnimator_toChangePercentageValuesWithAnimation() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        verify(mockValueIncreaseAnimation).addUpdateListener(any(AnimatorUpdateListener.class));
    }

    @Test
    public void onCreate_startsAnimation() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        verify(mockValueIncreaseAnimation).start();
    }

    @Test
    public void valueUpdateListener_changesPercentageText() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        verify(mockValueIncreaseAnimation).addUpdateListener(animatorUpdateListenerCaptor.capture());

        AnimatorUpdateListener animatorUpdateListener = animatorUpdateListenerCaptor.getValue();

        ValueAnimator mockAnimator = mock(ValueAnimator.class);
        when(mockAnimator.getAnimatedValue()).thenReturn(20);

        animatorUpdateListener.onAnimationUpdate(mockAnimator);

        assertThat(ajaePercentage.getText()).isEqualTo("20 %");

        when(mockAnimator.getAnimatedValue()).thenReturn(30);

        animatorUpdateListener.onAnimationUpdate(mockAnimator);

        assertThat(ajaePercentage.getText()).isEqualTo("30 %");
    }

    @Test
    public void onCreate_setsAjaePowerOnCustomViews() throws Exception {
        setUp(SCORE_NO_MATTER, NONE);

        assertThat(shadowAjaeMessageView.getAjae()).isEqualTo(new Ajae(NONE));
        assertThat(shadowAjaePercentageView.getAjae()).isEqualTo(new Ajae(NONE));
        assertThat(shadowAjaeImageView.getAjae()).isEqualTo(new Ajae(NONE));

        setUp(SCORE_NO_MATTER, MEDIUM);

        assertThat(shadowAjaeMessageView.getAjae()).isEqualTo(new Ajae(MEDIUM));
        assertThat(shadowAjaePercentageView.getAjae()).isEqualTo(new Ajae(MEDIUM));
        assertThat(shadowAjaeImageView.getAjae()).isEqualTo(new Ajae(MEDIUM));
    }

    @Test
    public void clickOnTestAgain_finishesActivity() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        testAgain.performClick();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void clickOnShare_getsInputDialogFromDialogService() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        share.performClick();

        verify(mockDialogService).getInputDialog(eq(subject), eq(subject));
    }

    @Test
    public void clickOnShare_showsInputDialog() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        share.performClick();

        verify(mockInputDialog).show();
    }

    @Test
    public void onConfirmClick_launchesShareIntentWithNickNameAndScore() throws Exception {
        setUp(80, POWER_NO_MATTER);

        setUpNickNameConfirmed(mockMapCall, mockProgressDialog, "진성아재");

        verify(mockUrlShortenApi).shortenedUrl(ImmutableMap.of("longUrl", "https://sunhoY.github.io/zealot-react?score=80&nickName=진성아재"),
                application.getString(R.string.google_api_key));
    }

    @Test
    public void onConfirmClick_enqueuesPostCall() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        setUpNickNameConfirmed(mockMapCall, mockProgressDialog, NICK_NAME_NO_MATTER);

        verify(mockMapCall).enqueue(subject);
    }

    private void setUpNickNameConfirmed(Call<Map<String, Object>> mockMapCall, ProgressDialog mockProgressDialog, String nickNameNoMatter) {
        when(mockUrlShortenApi.shortenedUrl(Matchers.<Map<String, String>>any(), anyString())).thenReturn(mockMapCall);
        when(mockDialogService.getProgressDialog(any(Context.class), anyString())).thenReturn(mockProgressDialog);

        subject.onConfirm(nickNameNoMatter);
    }

    @Test
    public void onConfirmClick_showsProgressDialog_withMessage() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        setUpNickNameConfirmed(mockMapCall, mockProgressDialog, "진성아재");

        verify(mockDialogService).getProgressDialog(subject, "진성아재 님의 아재력을 포장하고 있습니다.");
        verify(mockProgressDialog).show();
    }

    @Test
    public void onUrlShortenerResponse_launchesSendIntent() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        Map<String, Object> body = ImmutableMap.<String, Object>of("id", "https://goo.gl");
        Response<Map<String, Object>> response = Response.success(body);

        subject.onResponse(mockMapCall, response);

        Intent chooser = shadowOf(subject).getNextStartedActivity();

        IntentAssert chooserIntentAssert = new IntentAssert(chooser);

        assertThat(chooserIntentAssert.hasAction(Intent.ACTION_CHOOSER));
        assertThat(chooserIntentAssert.hasExtra(Intent.EXTRA_TITLE, "아재력 알리기"));

        Intent originalIntent = chooser.getParcelableExtra(Intent.EXTRA_INTENT);

        IntentAssert originalIntentAssert = new IntentAssert(originalIntent);

        assertThat(originalIntentAssert.hasAction(Intent.ACTION_SEND));
        assertThat(originalIntentAssert.hasExtra(Intent.EXTRA_TEXT, "https://goo.gl"));
        assertThat(originalIntentAssert.hasType("text/plain"));
    }

    @Test
    public void onUrlShortenerResponse_hidesProgressDialog_whenDialogIsNotNull() throws Exception {
        setUp(SCORE_NO_MATTER, POWER_NO_MATTER);

        Map<String, Object> body = ImmutableMap.<String, Object>of("id", "does not matter");
        Response<Map<String, Object>> response = Response.success(body);

        subject.onResponse(mockMapCall, response);

        verify(mockProgressDialog, never()).hide();

        setUpNickNameConfirmed(mockMapCall, mockProgressDialog, NICK_NAME_NO_MATTER);

        subject.onResponse(mockMapCall, response);

        verify(mockProgressDialog).hide();
    }
}