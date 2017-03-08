package io.harry.zealot.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.assertj.android.api.content.IntentAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.BuildConfig;
import io.harry.zealot.R;
import io.harry.zealot.TestZealotApplication;
import io.harry.zealot.adapter.GagPagerAdapter;
import io.harry.zealot.listener.FaceListener;
import io.harry.zealot.shadow.ShadowAjaeGauge;
import io.harry.zealot.shadow.ShadowNavigationBar;
import io.harry.zealot.shadow.ShadowViewPager;
import io.harry.zealot.view.AjaeGauge;
import io.harry.zealot.view.NavigationBar;
import io.harry.zealot.vision.ZealotFaceFactory;
import io.harry.zealot.vision.wrapper.ZealotFaceDetectorWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceFactoryWrapper;
import io.harry.zealot.vision.wrapper.ZealotMultiProcessorWrapper;
import io.harry.zealot.wrapper.GagPagerAdapterWrapper;
import io.harry.zealot.wrapper.SharedPreferencesWrapper;

import static io.harry.zealot.wrapper.SharedPreferencesWrapper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        shadows = {ShadowNavigationBar.class, ShadowViewPager.class, ShadowAjaeGauge.class})
public class TutorialActivityTest {

    private static final int PAGER_ADAPTER_SIZE = 3;
    private TutorialActivity subject;

    private FaceDetector faceDetector;

    @BindView(R.id.tutorial_pager)
    ViewPager tutorialPager;
    @BindView(R.id.camera_tutorial_next)
    Button cameraNext;
    @BindView(R.id.smile_tutorial_next)
    Button smileNext;
    @BindView(R.id.pager_tutorial_next)
    Button pagerNext;
    @BindView(R.id.navigation_tutorial_next)
    Button navigationNext;
    @BindView(R.id.camera_tutorial)
    LinearLayout cameraTutorial;
    @BindView(R.id.smile_tutorial)
    LinearLayout smileTutorial;
    @BindView(R.id.pager_tutorial)
    LinearLayout pagerTutorial;
    @BindView(R.id.navigation_tutorial)
    LinearLayout navigationTutorial;
    @BindView(R.id.navigation_bar)
    NavigationBar navigationBar;
    @BindView(R.id.ajae_gauge)
    AjaeGauge ajaeGauge;

    @Inject
    ZealotFaceDetectorWrapper mockFaceDetectorWrapper;
    @Inject
    GagPagerAdapterWrapper mockGagPagerAdapterWrapper;
    @Inject
    ZealotFaceFactoryWrapper mockFaceFactoryWrapper;
    @Inject
    ZealotMultiProcessorWrapper mockMultiProcessorWrapper;
    @Inject
    SharedPreferencesWrapper mockSharedPreferencesWrapper;

    @Mock
    private GagPagerAdapter mockGagPagerAdapter;
    @Mock
    private ZealotFaceFactory mockFaceFactory;
    @Mock
    private SharedPreferences mockSharedPreferences;
    @Mock
    private SharedPreferences.Editor mockEditor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ((TestZealotApplication) application).getZealotComponent().inject(this);
        faceDetector = new FaceDetector.Builder(application).build();

        when(mockFaceDetectorWrapper.getFaceDetector(any(Context.class))).thenReturn(faceDetector);
        when(mockGagPagerAdapterWrapper.getGagPagerAdapter(any(FragmentManager.class), anyList())).thenReturn(mockGagPagerAdapter);
        when(mockFaceFactoryWrapper.getZealotFaceFactory(any(FaceListener.class))).thenReturn(mockFaceFactory);
        when(mockGagPagerAdapter.getCount()).thenReturn(PAGER_ADAPTER_SIZE);
        when(mockSharedPreferencesWrapper.getSharedPreferences()).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor);

        subject = Robolectric.setupActivity(TutorialActivity.class);

        ButterKnife.bind(this, subject);
    }

    @Test
    public void onCreate_getsPagerAdapterWith3TutorialDrawables() throws Exception {
        verify(mockGagPagerAdapterWrapper).getGagPagerAdapter(subject.getSupportFragmentManager(),
                Arrays.asList(R.drawable.az_tutorial_1, R.drawable.az_tutorial_2, R.drawable.az_tutorial_3));
    }

    @Test
    public void onCreate_setsPagerAdapterOnTutorialPager() throws Exception {
        assertThat(tutorialPager.getAdapter()).isEqualTo(mockGagPagerAdapter);
    }

    @Test
    public void onCreate_addsOnPageChangeListenerOnTutorialPager() throws Exception {
        ShadowViewPager shadowPager = (ShadowViewPager) shadowOf(tutorialPager);

        assertThat(shadowPager.getOnPageChangeListener()).isEqualTo(subject);
    }

    @Test
    public void onCreate_getsFaceFactory_fromFaceFactoryWrapper() throws Exception {
        verify(mockFaceFactoryWrapper).getZealotFaceFactory(subject);
    }

    @Test
    public void onCreate_getsFaceDetector_fromFaceDetectorWrapper() throws Exception {
        verify(mockFaceDetectorWrapper).getFaceDetector(subject);
    }

    @Test
    public void onCreate_getsFaceProcessor_FromMultiProcessorWrapper() throws Exception {
        verify(mockMultiProcessorWrapper).getMultiProcessor(mockFaceFactory);
    }

    @Test
    public void clickOnCameraNext_hidesCameraTutorial_andShowsSmileTutorial() throws Exception {
        cameraNext.performClick();

        assertThat(cameraTutorial.getVisibility()).isEqualTo(View.INVISIBLE);
        assertThat(smileTutorial.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void clickOnSmileNext_hidesSmileTutorial_andShowsPagerTutorial() throws Exception {
        smileNext.performClick();

        assertThat(smileTutorial.getVisibility()).isEqualTo(View.INVISIBLE);
        assertThat(pagerTutorial.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void clickOnPagerNext_hidesPagerTutorial_andShowsNavigationTutorial() throws Exception {
        pagerNext.performClick();

        assertThat(pagerTutorial.getVisibility()).isEqualTo(View.INVISIBLE);
        assertThat(navigationTutorial.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void clickOnNavigationNext_putSharedPreferenceTutorialSeenAsTrue() throws Exception {
        navigationNext.performClick();

        verify(mockSharedPreferencesWrapper).getSharedPreferences();
        verify(mockSharedPreferences).edit();
        verify(mockEditor).putBoolean(TUTORIAL_SEEN, true);
        verify(mockEditor).apply();
    }

    @Test
    public void clickOnNavigationNext_finishesActivity() throws Exception {
        navigationNext.performClick();

        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void clickOnNavigationNext_startsTestAjaeActivity() throws Exception {
        navigationNext.performClick();

        Intent nextStartedActivity = shadowOf(application).getNextStartedActivity();
        IntentAssert intentAssert = new IntentAssert(nextStartedActivity);

        intentAssert.hasComponent(application, TestAjaeActivity.class);
    }

    @Test
    public void clickOnNextGag_showsNextPageOnViewPager() throws Exception {
        assertThat(tutorialPager.getCurrentItem()).isEqualTo(0);

        subject.onNext();

        assertThat(tutorialPager.getCurrentItem()).isEqualTo(1);
    }

    @Test
    public void clickOnPreviousGag_showsPreviousPageOnViewPager() throws Exception {
        tutorialPager.setCurrentItem(2);

        subject.onPrevious();

        assertThat(tutorialPager.getCurrentItem()).isEqualTo(1);
    }

    @Test
    public void onCameraPhase_progressBarDoesNotIncrease() throws Exception {
        Face face = mock(Face.class);
        when(face.getIsSmilingProbability()).thenReturn(.31f);

        subject.onFaceDetect(face);

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        assertThat(ajaeGauge.getGaugeValue()).isEqualTo(0.f);
    }

    @Test
    public void onFaceDetect_doesNotIncreaseOrSetAjaePower_whenAjaePowerExceed_100() throws Exception {
        Field ajaePowerField = TutorialActivity.class.getDeclaredField("ajaePower");
        ajaePowerField.setAccessible(true);
        ajaePowerField.set(subject, 99.f);
        ajaePowerField.setAccessible(false);

        Face face = mock(Face.class);
        when(face.getIsSmilingProbability()).thenReturn(.31f);

        cameraNext.performClick();
        subject.onFaceDetect(face);
        subject.onFaceDetect(face);

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        assertThat(ajaeGauge.getGaugeValue()).isEqualTo(100.f);
    }

    @Test
    public void onSmilePhase_progressBarIncrease_whenFaceIsSmiley() throws Exception {
        cameraNext.performClick();

        Face face = mock(Face.class);
        when(face.getIsSmilingProbability()).thenReturn(.31f);

        subject.onFaceDetect(face);

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        assertThat(ajaeGauge.getGaugeValue()).isEqualTo(1.f);
    }

    @Test
    public void onPageSelected_setsIndexOnNavigationBar() throws Exception {
        subject.onPageSelected(2);

        assertThat(navigationBar.getCurrentIndex()).isEqualTo(2);
    }
}