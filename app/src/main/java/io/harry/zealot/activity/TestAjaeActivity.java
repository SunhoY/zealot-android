package io.harry.zealot.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.common.BaseRoundCornerProgressBar.OnProgressChangedListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.harry.zealot.R;
import io.harry.zealot.adapter.GagPagerAdapter;
import io.harry.zealot.helper.AnimationHelper;
import io.harry.zealot.listener.FaceListener;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.service.GagService;
import io.harry.zealot.service.ServiceCallback;
import io.harry.zealot.view.AjaePercentageView;
import io.harry.zealot.view.TestAjaePreview;
import io.harry.zealot.viewpager.OnSwipeListener;
import io.harry.zealot.viewpager.ZealotViewPager;
import io.harry.zealot.vision.ZealotFaceFactory;
import io.harry.zealot.vision.wrapper.ZealotCameraSourceWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceDetectorWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceFactoryWrapper;
import io.harry.zealot.vision.wrapper.ZealotMultiProcessorWrapper;
import io.harry.zealot.wrapper.GagPagerAdapterWrapper;

public class TestAjaeActivity extends ZealotBaseActivity implements FaceListener, OnProgressChangedListener, OnSwipeListener, ViewPager.OnPageChangeListener {

    private final float AJAE_POWER_UNIT = 10.0f;

    @BindView(R.id.gag_pager)
    ZealotViewPager gagPager;
    @BindView(R.id.test_ajae_preview)
    TestAjaePreview testAjaePreview;
    @BindView(R.id.progress)
    RoundCornerProgressBar ajaePowerProgress;
    @BindView(R.id.ajae_power_percentage)
    AjaePercentageView ajaePowerPercentage;
    @BindView(R.id.previous_gag)
    TextView previousGag;
    @BindView(R.id.next_gag)
    TextView nextGag;
    @BindView(R.id.current_gag)
    TextView currentGag;

    @Inject
    GagPagerAdapterWrapper gagPagerAdapterWrapper;
    @Inject
    GagService gagService;
    @Inject
    ZealotFaceDetectorWrapper faceDetectorWrapper;
    @Inject
    ZealotMultiProcessorWrapper multiProcessorWrapper;
    @Inject
    ZealotFaceFactoryWrapper faceFactoryWrapper;
    @Inject
    ZealotCameraSourceWrapper cameraSourceWrapper;
    @Inject
    AnimationHelper animationHelper;
    @Inject
    AjaeScoreRange ajaeScoreRange;

    private GagPagerAdapter gagPagerAdapter;
    private FaceDetector faceDetector;
    private MultiProcessor<Face> faceProcessor;
    private ZealotFaceFactory faceFactory;
    private CameraSource cameraSource;

    private float ajaePower = .0f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ajae);

        ButterKnife.bind(this);
        zealotComponent.inject(this);

        gagPager.setOnSwipeListener(this);
        gagPager.addOnPageChangeListener(this);

        faceFactory = faceFactoryWrapper.getZealotFaceFactory(this);
        faceDetector = faceDetectorWrapper.getFaceDetector(this);
        cameraSource = cameraSourceWrapper.getCameraSource(this, faceDetector);
        faceProcessor = multiProcessorWrapper.getMultiProcessor(faceFactory);
        faceDetector.setProcessor(faceProcessor);

        testAjaePreview.setCameraSource(cameraSource);

        gagService.getGagImageFileNames(new ServiceCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                getGagImageURLsWithImageNames(result, new ServiceCallback<List<Uri>>() {
                    @Override
                    public void onSuccess(List<Uri> result) {
                        gagPagerAdapter = gagPagerAdapterWrapper.getGagPagerAdapter(
                                getSupportFragmentManager(), result);
                        gagPager.setAdapter(gagPagerAdapter);
                    }
                });
            }
        });

        ajaePowerProgress.setOnProgressChangedListener(this);
    }

    @Override
    public void onFaceDetect(Face face) {
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
        final int ajaeFullPower = getResources().getInteger(R.integer.ajae_full_power);

        int severityColorId = getAjaeSeverityLevel(progress);
        int ajaePercentage = (int) (progress / 10);

        ajaePowerProgress.setProgressColor(ContextCompat.getColor(TestAjaeActivity.this, severityColorId));
        ajaePowerPercentage.setText(getString(R.string.x_percentage, ajaePercentage));
        ajaePowerPercentage.setAjaePower(ajaeScoreRange.getRange(ajaePercentage));

        if (progress == ajaeFullPower) {
            launchResultActivity(ajaeFullPower);
        }
    }

    private void launchResultActivity(float ajaePower) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("ajaeScore", (int) (ajaePower / 10.f));
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }

    private void getGagImageURLsWithImageNames(List<String> urls, ServiceCallback<List<Uri>> serviceCallback) {
        gagService.getGagImageUris(urls, serviceCallback);
    }

    private int getAjaeSeverityLevel(float ajaePower) {
        //todo: change with range
        if (ajaePower >= 500 && ajaePower < 700) {
            return R.color.orange;
        } else if (ajaePower >= 700 && ajaePower < 900) {
            return R.color.hot_pink;
        } else if (ajaePower >= 900 && ajaePower <= 1000) {
            return R.color.red;
        } else {
            return R.color.light_green;
        }
    }

    @Override
    public void onAttemptedOnLastPage() {
        launchResultActivity(ajaePowerProgress.getProgress());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            previousGag.setText(R.string.to_home);
        } else {
            previousGag.setText(R.string.previous);
        }

        if (position == gagPager.getAdapter().getCount() - 1) {
            nextGag.setText(R.string.result);
        } else {
            nextGag.setText(R.string.next);
        }

        String[] ordinalNumbers = getResources().getStringArray(R.array.ordinal_numbers);
        currentGag.setText(ordinalNumbers[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gagPager.clearOnPageChangeListeners();
    }

    @OnClick(R.id.previous_gag)
    public void onPreviousGagClick() {
        int currentItem = gagPager.getCurrentItem();

        if (currentItem == 0) {
            finish();
        } else {
            gagPager.setCurrentItem(currentItem - 1);
        }
    }

    @OnClick(R.id.next_gag)
    public void onNextGagClick() {
        int currentItem = gagPager.getCurrentItem();

        if (currentItem == gagPager.getAdapter().getCount() - 1) {
            launchResultActivity(ajaePowerProgress.getProgress());
            finish();
        } else {
            gagPager.setCurrentItem(currentItem + 1);
        }
    }
}
