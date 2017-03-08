package io.harry.zealot.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.animation.Animation;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.BuildConfig;
import io.harry.zealot.R;
import io.harry.zealot.TestZealotApplication;
import io.harry.zealot.adapter.GagPagerAdapter;
import io.harry.zealot.dialog.DialogService;
import io.harry.zealot.helper.AnimationHelper;
import io.harry.zealot.model.Gag;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.service.GagService;
import io.harry.zealot.service.ServiceCallback;
import io.harry.zealot.shadow.ShadowAjaeGauge;
import io.harry.zealot.shadow.ShadowNavigationBar;
import io.harry.zealot.shadow.ShadowViewPager;
import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.AjaeGauge;
import io.harry.zealot.view.NavigationBar;
import io.harry.zealot.view.TestAjaePreview;
import io.harry.zealot.viewpager.ZealotViewPager;
import io.harry.zealot.vision.wrapper.ZealotCameraSourceWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceDetectorWrapper;
import io.harry.zealot.wrapper.GagPagerAdapterWrapper;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        shadows = {ShadowNavigationBar.class, ShadowViewPager.class, ShadowAjaeGauge.class})
public class TestAjaeActivityTest {
    private static final int GAG_PAGE_COUNT = 4;

    private TestAjaeActivity subject;
    private Animation mockScaleXYAnimation;
    private FaceDetector testFaceDetector;

    @BindView(R.id.gag_pager)
    ZealotViewPager gagPager;
    @BindView(R.id.test_ajae_preview)
    TestAjaePreview testAjaePreview;
    @BindView(R.id.navigation_bar)
    NavigationBar navigationBar;
    @BindView(R.id.ajae_gauge)
    AjaeGauge ajaeGauge;

    @Inject
    GagPagerAdapterWrapper mockGagPagerAdapterWrapper;
    @Inject
    DialogService mockDialogService;
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
    ProgressDialog mockProgressDialog;
    @Mock
    CameraSource mockCameraSource;

    @Captor
    ArgumentCaptor<ServiceCallback<List<Gag>>> gagListServiceCallbackCaptor;
    @Captor
    ArgumentCaptor<ServiceCallback<List<Uri>>> uriListServiceCallbackCaptor;
    private ShadowNavigationBar shadowNavigationBar;

    @Before
    public void setUp() throws Exception {
        ((TestZealotApplication) application).getZealotComponent().inject(this);
        MockitoAnnotations.initMocks(this);

        testFaceDetector = new FaceDetector.Builder(application).build();
        mockScaleXYAnimation = mock(Animation.class);

        //TODO: has to be realistic
        when(mockAjaeScoreRange.getAjaePower(anyInt())).thenReturn(AjaePower.BURNT);
        when(mockGagPagerAdapterWrapper.getGagPagerAdapter(any(FragmentManager.class), anyListOf(Uri.class)))
            .thenReturn(mockGagPagerAdapter);
        when(mockGagPagerAdapter.getCount()).thenReturn(GAG_PAGE_COUNT);
        when(mockFaceDetectorWrapper.getFaceDetector(any(Context.class))).thenReturn(testFaceDetector);
        when(mockCameraSourceWrapper.getCameraSource(any(Context.class), eq(testFaceDetector))).thenReturn(mockCameraSource);
        when(mockAnimationHelper.loadAnimation(R.animator.scale_xy)).thenReturn(mockScaleXYAnimation);
        when(mockDialogService.getProgressDialog(any(Context.class), anyString())).thenReturn(mockProgressDialog);

        subject = Robolectric.buildActivity(TestAjaeActivity.class).create().get();
        subject.gagPager.setAdapter(mockGagPagerAdapter);

        ButterKnife.bind(this, subject);

        shadowNavigationBar = (ShadowNavigationBar) shadowOf(navigationBar);
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
    public void onCreate_addsOnPageChangeListenerAsItSelfOnGagPager() throws Exception {
        ShadowViewPager shadowPager = (ShadowViewPager) shadowOf(gagPager);

        assertThat(shadowPager.getOnPageChangeListener()).isEqualTo(subject);
    }

    @Test
    public void onCreate_setsOnSwipeAttemptedOnLastPageListenerOnViewPager() throws Exception {
        assertThat(gagPager.getOnSwipeListener()).isEqualTo(subject);
    }

    @Test
    public void onCreate_callGagServiceToFetchNumberOfGagImageFileNames() throws Exception {
        int requestCount = application.getResources().getInteger(R.integer.gag_count);

        verify(mockGagService).getGags(eq(requestCount), anyBoolean(), Matchers.<ServiceCallback<List<Gag>>>any());
    }

    @Test
    public void onCreate_callGagServiceToFetchVerifiedGagImageFileNames() throws Exception {
        verify(mockGagService).getGags(anyInt(), eq(true), Matchers.<ServiceCallback<List<Gag>>>any());
    }

    @Test
    public void afterFetchingGagImageFileNames_callsGagServiceToGetImageURLs() throws Exception {
        verify(mockGagService).getGags(anyInt(), anyBoolean(), gagListServiceCallbackCaptor.capture());

        gagListServiceCallbackCaptor.getValue().onSuccess(createGagList("gag1.png", "gag2.png"));

        verify(mockGagService).getGagImageUris(eq(Arrays.asList("gag1.png", "gag2.png")),
                Matchers.<ServiceCallback<List<Uri>>> any());
    }

    @Test
    public void onCreate_getPagerAdapterWithRequestSizeOfFragments() throws Exception {
        verify(mockGagService).getGags(anyInt(), anyBoolean(), gagListServiceCallbackCaptor.capture());

        gagListServiceCallbackCaptor.getValue().onSuccess(new ArrayList<Gag>());

        verify(mockGagService).getGagImageUris(anyListOf(String.class),
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
        verify(mockGagService).getGags(anyInt(), anyBoolean(), gagListServiceCallbackCaptor.capture());

        gagListServiceCallbackCaptor.getValue().onSuccess(new ArrayList<Gag>());

        verify(mockGagService).getGagImageUris(anyListOf(String.class),
                uriListServiceCallbackCaptor.capture());

        when(mockGagPagerAdapterWrapper.getGagPagerAdapter(any(FragmentManager.class), anyListOf(Uri.class)))
                .thenReturn(mockGagPagerAdapter);

        uriListServiceCallbackCaptor.getValue().onSuccess(new ArrayList<Uri>());

        assertThat(gagPager.getAdapter()).isEqualTo(mockGagPagerAdapter);
    }

    @Test
    public void afterGettingListOfUri_dismissesProgressDialog() throws Exception {
        verify(mockGagService).getGags(anyInt(), anyBoolean(), gagListServiceCallbackCaptor.capture());

        gagListServiceCallbackCaptor.getValue().onSuccess(new ArrayList<Gag>());

        verify(mockGagService).getGagImageUris(anyListOf(String.class),
                uriListServiceCallbackCaptor.capture());

        when(mockGagPagerAdapterWrapper.getGagPagerAdapter(any(FragmentManager.class), anyListOf(Uri.class)))
                .thenReturn(mockGagPagerAdapter);

        uriListServiceCallbackCaptor.getValue().onSuccess(new ArrayList<Uri>());

        verify(mockProgressDialog).dismiss();
    }

    @Test
    public void onCreate_getsLoadingDialogFromDialogService() throws Exception {
        verify(mockDialogService).getProgressDialog(subject, "아재력을 모으고 있습니다.");
    }

    @Test
    public void onCreate_showsProgressDialog() throws Exception {
        verify(mockProgressDialog).show();
    }

    @Test
    public void onCreate_setsSize_10_onNavigationBar() throws Exception {
        assertThat(shadowNavigationBar.getSize()).isEqualTo(10);
    }

    @Test
    public void onCreate_setsNavigateListenerItSelf() throws Exception {
        assertThat(shadowNavigationBar.getListener()).isEqualTo(subject);
    }

    @Test
    public void onFaceDetect_setsAjaeGaugeValueWithIncreasedValue_whenFaceIsSmiling() throws Exception {
        faceDetectsWithSmileyProbability(.40f);

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        assertThat(ajaeGauge.getGaugeValue()).isEqualTo(1.f);
    }

    @Test
    public void onFaceDetect_doesNotSetAjaeGaugeValue_whenFaceIsNotSmiling() throws Exception {
        faceDetectsWithSmileyProbability(.20f);

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        assertThat(ajaeGauge.getGaugeValue()).isEqualTo(.0f);
    }

    @Test
    public void onSwipeAttemptedOnLastPage_launchesResultActivity() throws Exception {
        ajaeGauge.setGaugeValue(69.f);

        subject.onAttemptedOnLastPage();

        assertResultActivityIsLaunched(69);
    }

    @Test
    public void launchingResultActivity_finishesActivity() throws Exception {
        subject.onAttemptedOnLastPage();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void onPageSelected_callsSetCurrentIndexOfNavigationBar_whenThoseTwoIndexAreDifferent() throws Exception {
        subject.onPageSelected(1);

        assertThat(navigationBar.getCurrentIndex()).isEqualTo(1);

        subject.onPageSelected(3);

        assertThat(navigationBar.getCurrentIndex()).isEqualTo(3);
    }

    @Test
    public void onPrevious_finishes_whenPageIsTheFirstPage() throws Exception {
        gagPager.setCurrentItem(0);

        subject.onPrevious();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void onPrevious_setsPreviousPageOnGagPager_whenPageIsNotTheFirstPage() throws Exception {
        gagPager.setCurrentItem(2);

        subject.onPrevious();

        assertThat(gagPager.getCurrentItem()).isEqualTo(1);
    }

    @Test
    public void onNext_setsNextPageOnGagPager_whenPageIsNotTheLastPage() throws Exception {
        gagPager.setCurrentItem(2);

        subject.onNext();

        assertThat(gagPager.getCurrentItem()).isEqualTo(3);
    }

    @Test
    public void onNext_finishes_whenPageIsTheLastPage() throws Exception {
        gagPager.setCurrentItem(GAG_PAGE_COUNT - 1);

        subject.onNext();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void onNext_launchesResultActivityWithProgressValue_whenPageIsTheLastPage() throws Exception {
        gagPager.setCurrentItem(GAG_PAGE_COUNT - 1);

        ajaeGauge.setGaugeValue(80.f);

        subject.onNext();

        assertResultActivityIsLaunched(80);
    }

    private void assertResultActivityIsLaunched(int expectedScore) {
        Intent actual = shadowOf(subject).getNextStartedActivity();

        IntentAssert intentAssert = new IntentAssert(actual);
        intentAssert.hasComponent(application, ResultActivity.class);
        intentAssert.hasExtra("ajaeScore", expectedScore);
        intentAssert.hasFlags(FLAG_ACTIVITY_SINGLE_TOP);
    }

    private void faceDetectsWithSmileyProbability(float smileyProbability) {
        Face mockFace = mock(Face.class);
        when(mockFace.getIsSmilingProbability()).thenReturn(smileyProbability);

        subject.onFaceDetect(mockFace);
    }

    private List<Gag> createGagList(String... fileNames) {
        List<Gag> result = new ArrayList<>();
        for(String fileName : fileNames) {
            Gag gag = new Gag();
            gag.fileName = fileName;
            result.add(gag);
        }

        return result;
    }
}