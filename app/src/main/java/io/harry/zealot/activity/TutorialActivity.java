package io.harry.zealot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.common.BaseRoundCornerProgressBar.OnProgressChangedListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.harry.zealot.R;
import io.harry.zealot.adapter.GagPagerAdapter;
import io.harry.zealot.listener.FaceListener;
import io.harry.zealot.model.Ajae;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.AjaePercentageView;
import io.harry.zealot.view.NavigationBar;
import io.harry.zealot.view.NavigationBar.NavigateListener;
import io.harry.zealot.view.TestAjaePreview;
import io.harry.zealot.viewpager.ZealotViewPager;
import io.harry.zealot.vision.ZealotFaceFactory;
import io.harry.zealot.vision.wrapper.ZealotCameraSourceWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceDetectorWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceFactoryWrapper;
import io.harry.zealot.vision.wrapper.ZealotMultiProcessorWrapper;
import io.harry.zealot.wrapper.GagPagerAdapterWrapper;
import io.harry.zealot.wrapper.SharedPreferencesWrapper;

import static io.harry.zealot.wrapper.SharedPreferencesWrapper.TUTORIAL_SEEN;

public class TutorialActivity extends ZealotBaseActivity
        implements FaceListener, OnProgressChangedListener, NavigateListener,
        ViewPager.OnPageChangeListener {

    public static final int TUTORIAL_GAG_SIZE = 3;
    private final float AJAE_POWER_UNIT = 10.0f;

    @BindView(R.id.tutorial_pager)
    ZealotViewPager tutorialPager;
    @BindView(R.id.test_ajae_preview)
    TestAjaePreview testAjaePreview;
    @BindView(R.id.progress)
    RoundCornerProgressBar ajaePowerProgress;
    @BindView(R.id.ajae_power_percentage)
    AjaePercentageView ajaePowerPercentage;
    @BindView(R.id.navigation_bar)
    NavigationBar navigationBar;
    @BindView(R.id.camera_tutorial)
    LinearLayout cameraTutorial;
    @BindView(R.id.smile_tutorial)
    LinearLayout smileTutorial;
    @BindView(R.id.pager_tutorial)
    LinearLayout pagerTutorial;
    @BindView(R.id.navigation_tutorial)
    LinearLayout navigationTutorial;

    @Inject
    GagPagerAdapterWrapper gagPagerAdapterWrapper;
    @Inject
    ZealotFaceDetectorWrapper faceDetectorWrapper;
    @Inject
    ZealotMultiProcessorWrapper multiProcessorWrapper;
    @Inject
    ZealotFaceFactoryWrapper faceFactoryWrapper;
    @Inject
    ZealotCameraSourceWrapper cameraSourceWrapper;
    @Inject
    AjaeScoreRange ajaeScoreRange;
    @Inject
    SharedPreferencesWrapper sharePreferenceWrapper;

    private GagPagerAdapter gagPagerAdapter;
    private FaceDetector faceDetector;
    private MultiProcessor<Face> faceProcessor;
    private ZealotFaceFactory faceFactory;
    private CameraSource cameraSource;
    private int tutorialPhase = 0;
    private int ajaePower = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);

        ButterKnife.bind(this);
        zealotComponent.inject(this);

        faceFactory = faceFactoryWrapper.getZealotFaceFactory(this);
        faceDetector = faceDetectorWrapper.getFaceDetector(this);
        cameraSource = cameraSourceWrapper.getCameraSource(this, faceDetector);
        faceProcessor = multiProcessorWrapper.getMultiProcessor(faceFactory);
        faceDetector.setProcessor(faceProcessor);

        testAjaePreview.setCameraSource(cameraSource);

        List<Integer> gags = Arrays.asList(R.drawable.az_tutorial_1, R.drawable.az_tutorial_2, R.drawable.az_tutorial_3);
        gagPagerAdapter = gagPagerAdapterWrapper.getGagPagerAdapter(getSupportFragmentManager(), gags);

        tutorialPager.setAdapter(gagPagerAdapter);
        tutorialPager.addOnPageChangeListener(this);

        navigationBar.setSize(TUTORIAL_GAG_SIZE);
        navigationBar.setNavigateListener(this);
    }

    @OnClick(R.id.camera_tutorial_next)
    public void onCameraNextClick() {
        cameraTutorial.setVisibility(View.INVISIBLE);
        smileTutorial.setVisibility(View.VISIBLE);

        tutorialPhase += 1;
    }

    @OnClick(R.id.smile_tutorial_next)
    public void onSmileNextClick() {
        smileTutorial.setVisibility(View.INVISIBLE);
        pagerTutorial.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.pager_tutorial_next)
    public void onPagerNextClick() {
        pagerTutorial.setVisibility(View.INVISIBLE);
        navigationTutorial.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.navigation_tutorial_next)
    public void onNavigationNextClick() {
        sharePreferenceWrapper.getSharedPreferences().edit().putBoolean(TUTORIAL_SEEN, true).apply();
        startActivity(new Intent(this, TestAjaeActivity.class));
        finish();
    }

    @Override
    public void onFaceDetect(Face face) {
        if (tutorialPhase == 0) {
            return;
        }

        final float smile = face.getIsSmilingProbability();

        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (smile > .30f) {
                    ajaePower += AJAE_POWER_UNIT;
                    ajaePowerProgress.setProgress(ajaePower);
                }
            }
        });
    }

    @Override
    public void onProgressChanged(int viewId, float progress, boolean isPrimaryProgress, boolean isSecondaryProgress) {
        int ajaePercentage = (int) (progress / 10);

        AjaePower ajaePower = ajaeScoreRange.getAjaePower(ajaePercentage);
        Ajae ajae = new Ajae(ajaePower);

        //TODO extract this into customView.
        ajaePowerProgress.setProgressColor(ContextCompat.getColor(TutorialActivity.this, ajae.getColor()));
        ajaePowerPercentage.setText(getString(R.string.x_percentage, ajaePercentage));
        ajaePowerPercentage.setAjae(ajae);
    }

    @Override
    public void onNext() {
        int currentItem = tutorialPager.getCurrentItem();
        tutorialPager.setCurrentItem(currentItem + 1);
    }

    @Override
    public void onPrevious() {
        int currentItem = tutorialPager.getCurrentItem();
        tutorialPager.setCurrentItem(currentItem - 1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        navigationBar.setCurrentIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) { }
}
