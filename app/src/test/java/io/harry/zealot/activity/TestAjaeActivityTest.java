package io.harry.zealot.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.BuildConfig;
import io.harry.zealot.R;
import io.harry.zealot.TestZealotApplication;
import io.harry.zealot.adapter.GagPagerAdapter;
import io.harry.zealot.helper.AnimationHelper;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.service.GagService;
import io.harry.zealot.service.ServiceCallback;
import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.TestAjaePreview;
import io.harry.zealot.viewpager.ZealotViewPager;
import io.harry.zealot.vision.wrapper.ZealotCameraSourceWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceDetectorWrapper;
import io.harry.zealot.wrapper.GagPagerAdapterWrapper;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class TestAjaeActivityTest {
    private static final int GAG_PAGE_COUNT = 4;

    private TestAjaeActivity subject;
    private Animation mockScaleXYAnimation;
    private FaceDetector testFaceDetector;

    @BindView(R.id.gag_pager)
    ZealotViewPager gagPager;
    @BindView(R.id.test_ajae_preview)
    TestAjaePreview testAjaePreview;
    @BindView(R.id.progress)
    RoundCornerProgressBar progress;
    @BindView(R.id.ajae_power_percentage)
    TextView ajaePowerPercentage;
    @BindView(R.id.previous_gag)
    TextView previousGag;
    @BindView(R.id.next_gag)
    TextView nextGag;
    @BindView(R.id.current_gag)
    TextView currentGag;

    @Inject
    GagPagerAdapterWrapper mockGagPagerAdapterWrapper;
    @Inject
    GagService mockGagService;
    @Inject
    ZealotFaceDetectorWrapper mockFaceDetectorWrapper;
    @Inject
    ZealotCameraSourceWrapper mockCameraSourceWrapper;
    @Inject
    AnimationHelper mockAnimationHelper;
    @Inject
    AjaeScoreRange mockAjaeScoreRange;

    @Mock
    GagPagerAdapter mockGagPagerAdapter;
    @Mock
    com.google.android.gms.vision.CameraSource mockCameraSource;

    @Captor
    ArgumentCaptor<ServiceCallback<List<String>>> stringListServiceCallbackCaptor;
    @Captor
    ArgumentCaptor<ServiceCallback<List<Uri>>> uriListServiceCallbackCaptor;


    @Before
    public void setUp() throws Exception {
        ((TestZealotApplication) application).getZealotComponent().inject(this);
        MockitoAnnotations.initMocks(this);

        testFaceDetector = new FaceDetector.Builder(application).build();
        mockScaleXYAnimation = mock(Animation.class);

        //TODO: has to be realistic
        when(mockAjaeScoreRange.getRange(anyInt())).thenReturn(AjaePower.FULL);
        when(mockGagPagerAdapterWrapper.getGagPagerAdapter(any(FragmentManager.class), anyListOf(Uri.class)))
            .thenReturn(mockGagPagerAdapter);
        when(mockGagPagerAdapter.getCount()).thenReturn(GAG_PAGE_COUNT);
        when(mockFaceDetectorWrapper.getFaceDetector(any(Context.class))).thenReturn(testFaceDetector);
        when(mockCameraSourceWrapper.getCameraSource(any(Context.class), eq(testFaceDetector))).thenReturn(mockCameraSource);
        when(mockAnimationHelper.loadAnimation(R.animator.scale_xy)).thenReturn(mockScaleXYAnimation);

        subject = Robolectric.buildActivity(TestAjaeActivity.class).create().get();
        subject.gagPager.setAdapter(mockGagPagerAdapter);

        ButterKnife.bind(this, subject);
    }

    @Test
    public void onCreate_setsCameraSourceOnAjaePreview() throws Exception {
        Field cameraSourceField = TestAjaePreview.class.getDeclaredField("cameraSource");
        cameraSourceField.setAccessible(true);
        CameraSource cameraSource = (CameraSource) cameraSourceField.get(testAjaePreview);
        cameraSourceField.setAccessible(false);

        assertThat(cameraSource).isEqualTo(mockCameraSource);
    }

    @Test
    public void onCreate_setsOnSwipeAttemptedOnLastPageListenerOnViewPager() throws Exception {
        assertThat(gagPager.getOnSwipeListener()).isEqualTo(subject);
    }

    @Test
    public void onCreate_callGagServiceToFetchGagImageFileNames() throws Exception {
        verify(mockGagService).getGagImageFileNames(Matchers.<ServiceCallback<List<String>>>any());
    }

    @Test
    public void afterFetchingGagImageFileNames_callsGagServiceToGetImageURLs() throws Exception {
        verify(mockGagService).getGagImageFileNames(stringListServiceCallbackCaptor.capture());

        stringListServiceCallbackCaptor.getValue().onSuccess(Arrays.asList("gag1.png", "gag2.png"));

        verify(mockGagService).getGagImageUris(eq(Arrays.asList("gag1.png", "gag2.png")),
                Matchers.<ServiceCallback<List<Uri>>> any());
    }

    @Test
    public void onCreate_getPagerAdapterWithRequestSizeOfFragments() throws Exception {
        verify(mockGagService).getGagImageFileNames(stringListServiceCallbackCaptor.capture());

        stringListServiceCallbackCaptor.getValue().onSuccess(Arrays.asList("gag1.png", "gag2.png"));

        verify(mockGagService).getGagImageUris(eq(Arrays.asList("gag1.png", "gag2.png")),
                uriListServiceCallbackCaptor.capture());

        List<Uri> actualUris = Arrays.asList(Uri.parse("http://gag1.png"),
                Uri.parse("http://gag2.png"), Uri.parse("http://gag3.png"));

        uriListServiceCallbackCaptor.getValue().onSuccess(actualUris);

        List<Uri> expectedUris = Arrays.asList(Uri.parse("http://gag1.png"),
                Uri.parse("http://gag2.png"), Uri.parse("http://gag3.png"));

        verify(mockGagPagerAdapterWrapper).getGagPagerAdapter(subject.getSupportFragmentManager(),
                expectedUris);
    }

    @Test
    public void onCreate_setAdapterOnViewPager() throws Exception {
        verify(mockGagService).getGagImageFileNames(stringListServiceCallbackCaptor.capture());

        stringListServiceCallbackCaptor.getValue().onSuccess(Arrays.asList("gag1.png", "gag2.png"));

        verify(mockGagService).getGagImageUris(eq(Arrays.asList("gag1.png", "gag2.png")),
                uriListServiceCallbackCaptor.capture());

        List<Uri> actualUris = Arrays.asList(Uri.parse("http://gag1.png"),
                Uri.parse("http://gag2.png"), Uri.parse("http://gag3.png"));

        when(mockGagPagerAdapterWrapper.getGagPagerAdapter(any(FragmentManager.class), anyListOf(Uri.class)))
                .thenReturn(mockGagPagerAdapter);

        uriListServiceCallbackCaptor.getValue().onSuccess(actualUris);

        assertThat(gagPager.getAdapter()).isEqualTo(mockGagPagerAdapter);
    }

    @Test
    public void onFaceDetect_fillsProgressBar_whenFaceIsSmiling() throws Exception {
        faceDetectsWithSmileyProbability(.40f);

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        assertThat(progress.getProgress()).isEqualTo(10);
    }

    @Test
    public void onFaceDetect_doesNotFillProgressBar_whenFaceIsNotSmiling() throws Exception {
        faceDetectsWithSmileyProbability(.20f);

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        assertThat(progress.getProgress()).isEqualTo(.0f);
    }

    @Test
    public void onAjaePowerChanged_changesAjaePowerPercentage() throws Exception {
        progress.setProgress(500.0f);

        assertThat(ajaePowerPercentage.getText()).isEqualTo("50 %");

        progress.setProgress(600.0f);

        assertThat(ajaePowerPercentage.getText()).isEqualTo("60 %");
    }

    @Test
    public void onAjaePowerChanged_setProgressColorAsOrange_whenAjaeLevelExceedsCaution() throws Exception {
        progress.setProgress(500.f);

        assertThat(progress.getProgressColor()).isEqualTo(ContextCompat.getColor(application, R.color.orange));
    }

    @Test
    public void onAjaePowerChanged_setProgressColorAsHotPink_whenAjaeLevelExceedsDanger() throws Exception {
        progress.setProgress(700.f);

        assertThat(progress.getProgressColor()).isEqualTo(ContextCompat.getColor(application, R.color.hot_pink));
    }

    @Test
    public void onAjaePowerChanged_setProgressColorAsRed_whenAjaeLevelReachesRealAjae() throws Exception {
        progress.setProgress(900.f);

        assertThat(progress.getProgressColor()).isEqualTo(ContextCompat.getColor(application, R.color.red));
    }

    @Test
    public void onAjaePowerChanged_launchesResultActivity_whenAjaePowerFullyCharged() throws Exception {
        progress.setProgress(1000.f);

        Intent actual = shadowOf(subject).getNextStartedActivity();

        IntentAssert intentAssert = new IntentAssert(actual);
        intentAssert.hasComponent(application, ResultActivity.class);
        intentAssert.hasExtra("ajaeScore", 100);
    }

    @Test
    public void onSwipeAttemptedOnLastPage_launchesResultActivity() throws Exception {
        progress.setProgress(699.f);

        subject.onAttemptedOnLastPage();

        assertResultActivityIsLaunched(69);
    }

    @Test
    public void launchingResultActivity_finishesActivity() throws Exception {
        subject.onAttemptedOnLastPage();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void showsToHome_onLeftTopText_whenPageIsFirstPage() throws Exception {
        subject.onPageSelected(0);

        assertThat(previousGag.getText()).isEqualTo("집으로");
    }

    @Test
    public void showsPrevious_onLeftTopText_whenPageIsNotFirstPage() throws Exception {
        subject.onPageSelected(1);

        assertThat(previousGag.getText()).isEqualTo("이전");

        subject.onPageSelected(2);

        assertThat(previousGag.getText()).isEqualTo("이전");
    }

    @Test
    public void showsToNext_onRightTopText_whenPageIsNotLastPage() throws Exception {
        subject.onPageSelected(GAG_PAGE_COUNT - 3);

        assertThat(nextGag.getText()).isEqualTo("다음");

        subject.onPageSelected(GAG_PAGE_COUNT - 2);

        assertThat(nextGag.getText()).isEqualTo("다음");
    }

    @Test
    public void showsToResult_onRightTopText_whenPageIsLastPage() throws Exception {
        subject.onPageSelected(GAG_PAGE_COUNT - 1);

        assertThat(nextGag.getText()).isEqualTo("결과");
    }

    @Test
    public void showsOrdinalNumber_onCenterTopText_accordingToPageNumber() throws Exception {
        subject.onPageSelected(0);

        assertThat(currentGag.getText()).isEqualTo("첫번째");

        subject.onPageSelected(3);

        assertThat(currentGag.getText()).isEqualTo("네번째");
    }

    @Test
    public void clickOnPrevious_finishes_whenPageIsFirstPage() throws Exception {
        gagPager.setCurrentItem(0);

        previousGag.performClick();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void clickOnPrevious_showsPreviousPage_whenPageIsNotFirstPage() throws Exception {
        gagPager.setCurrentItem(2);

        previousGag.performClick();

        assertThat(gagPager.getCurrentItem()).isEqualTo(1);
    }

    @Test
    public void clickOnNext_finishes_whenPageIsLastPage() throws Exception {
        gagPager.setCurrentItem(GAG_PAGE_COUNT - 1);

        nextGag.performClick();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void clickOnText_launchesResultActivity() throws Exception {
        gagPager.setCurrentItem(GAG_PAGE_COUNT - 1);

        progress.setProgress(800.f);

        nextGag.performClick();

        assertResultActivityIsLaunched(80);
    }

    private void assertResultActivityIsLaunched(int expectedScore) {
        Intent actual = shadowOf(subject).getNextStartedActivity();

        IntentAssert intentAssert = new IntentAssert(actual);
        intentAssert.hasComponent(application, ResultActivity.class);
        intentAssert.hasExtra("ajaeScore", expectedScore);
        intentAssert.hasFlags(FLAG_ACTIVITY_SINGLE_TOP);
    }

    @Test
    public void clickOnNext_showsNextPage_whenPageIsNotLastPage() throws Exception {
        gagPager.setCurrentItem(GAG_PAGE_COUNT - 2);

        nextGag.performClick();

        assertThat(gagPager.getCurrentItem()).isEqualTo(GAG_PAGE_COUNT - 1);
    }

    private void faceDetectsWithSmileyProbability(float smileyProbability) {
        Face mockFace = mock(Face.class);
        when(mockFace.getIsSmilingProbability()).thenReturn(smileyProbability);

        subject.onFaceDetect(mockFace);
    }
}