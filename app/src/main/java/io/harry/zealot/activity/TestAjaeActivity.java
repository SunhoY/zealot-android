package io.harry.zealot.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.R;
import io.harry.zealot.adapter.GagPagerAdapter;
import io.harry.zealot.dialog.DialogService;
import io.harry.zealot.helper.AnimationHelper;
import io.harry.zealot.listener.FaceListener;
import io.harry.zealot.model.Gag;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.service.GagService;
import io.harry.zealot.service.ServiceCallback;
import io.harry.zealot.view.AjaeGauge;
import io.harry.zealot.view.NavigationBar;
import io.harry.zealot.view.TestAjaePreview;
import io.harry.zealot.viewpager.OnSwipeListener;
import io.harry.zealot.viewpager.ZealotViewPager;
import io.harry.zealot.vision.ZealotFaceFactory;
import io.harry.zealot.vision.wrapper.ZealotCameraSourceWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceDetectorWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceFactoryWrapper;
import io.harry.zealot.vision.wrapper.ZealotMultiProcessorWrapper;
import io.harry.zealot.wrapper.GagPagerAdapterWrapper;

public class TestAjaeActivity extends ZealotBaseActivity
        implements FaceListener, OnSwipeListener, ViewPager.OnPageChangeListener, NavigationBar.NavigateListener {

    private final float AJAE_POWER_UNIT = 1.0f;
    private final float MAX_POWER = 100.f;

    @BindView(R.id.gag_pager)
    ZealotViewPager gagPager;
    @BindView(R.id.test_ajae_preview)
    TestAjaePreview testAjaePreview;
    @BindView(R.id.navigation_bar)
    NavigationBar navigationBar;
    @BindView(R.id.ajae_gauge)
    AjaeGauge ajaeGauge;

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
    @Inject
    DialogService dialogService;

    private GagPagerAdapter gagPagerAdapter;
    private FaceDetector faceDetector;
    private MultiProcessor<Face> faceProcessor;
    private ZealotFaceFactory faceFactory;
    private CameraSource cameraSource;
    private ProgressDialog progressDialog;

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

        progressDialog = dialogService.getProgressDialog(this, getString(R.string.gaining_ajae_power));
        progressDialog.show();

        int requestCount = getResources().getInteger(R.integer.gag_count);
        gagService.getGags(requestCount, true, new ServiceCallback<List<Gag>>() {
            @Override
            public void onSuccess(List<Gag> result) {
                getGagImageURLsWithGags(result, new ServiceCallback<List<Uri>>() {
                    @Override
                    public void onSuccess(List<Uri> result) {
                        gagPagerAdapter = gagPagerAdapterWrapper.getGagPagerAdapter(
                                getSupportFragmentManager(), result);
                        gagPager.setAdapter(gagPagerAdapter);
                        progressDialog.dismiss();
                    }
                });
            }
        });

        navigationBar.setSize(requestCount);
        navigationBar.setNavigateListener(this);
    }

    @Override
    public void onFaceDetect(Face face) {
        if(ajaePower >= MAX_POWER) {
            return;
        }

        final float smile = face.getIsSmilingProbability();

        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (smile > .30f) {
                    ajaePower += AJAE_POWER_UNIT;
                    ajaeGauge.setGaugeValue(ajaePower);
                }
            }
        });
    }

    private void launchResultActivity(float ajaePower) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("ajaeScore", (int) ajaePower);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }

    private void getGagImageURLsWithGags(List<Gag> gags, ServiceCallback<List<Uri>> serviceCallback) {
        List<String> imageNames = new ArrayList<>();
        for(Gag gag : gags) {
            imageNames.add(gag.fileName);
        }

        gagService.getGagImageUris(imageNames, serviceCallback);
    }

    @Override
    public void onAttemptedOnLastPage() {
        launchResultActivity(ajaeGauge.getGaugeValue());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if(position != navigationBar.getCurrentIndex()) {
            navigationBar.setCurrentIndex(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gagPager.clearOnPageChangeListeners();
    }

    @Override
    public void onNext() {
        int currentItem = gagPager.getCurrentItem();

        if (currentItem == gagPager.getAdapter().getCount() - 1) {
            launchResultActivity(ajaeGauge.getGaugeValue());
            finish();
        } else {
            gagPager.setCurrentItem(currentItem + 1);
        }
    }

    @Override
    public void onPrevious() {
        int currentItem = gagPager.getCurrentItem();

        if (currentItem == 0) {
            finish();
        } else {
            gagPager.setCurrentItem(currentItem - 1);
        }
    }
}
