package io.harry.zealot.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.harry.zealot.R;
import io.harry.zealot.adapter.GagPagerAdapter;
import io.harry.zealot.model.Gag;
import io.harry.zealot.service.GagService;
import io.harry.zealot.service.ServiceCallback;
import io.harry.zealot.wrapper.GagPagerAdapterWrapper;

public class VerificationActivity extends ZealotBaseActivity {
    @Inject
    GagService gagService;
    @Inject
    GagPagerAdapterWrapper gagPagerAdapterWrapper;

    @BindView(R.id.verification_pager)
    ViewPager verificationPager;

    private List<Gag> gagList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zealotComponent.inject(this);

        setContentView(R.layout.activity_verification);
        ButterKnife.bind(this);

        int chunkSize = getResources().getInteger(R.integer.verification_chunk_size);
        gagService.getGags(chunkSize, false, new ServiceCallback<List<Gag>>() {
            @Override
            public void onSuccess(List<Gag> result) {
                gagList = result;
                getGagImageUris(result);
            }
        });
    }

    private void getGagImageUris(List<Gag> gags) {
        List<String> imageFileNames = new ArrayList<>();
        for(Gag gag : gags) {
            imageFileNames.add(gag.fileName);
        }

        gagService.getGagImageUris(imageFileNames, new ServiceCallback<List<Uri>>() {
            @Override
            public void onSuccess(List<Uri> result) {
                GagPagerAdapter gagPagerAdapter = gagPagerAdapterWrapper.getGagPagerAdapter(getSupportFragmentManager(), result);
                verificationPager.setAdapter(gagPagerAdapter);
            }
        });
    }

    @OnClick(R.id.verify)
    public void onVerifyClick() {
        int currentItem = verificationPager.getCurrentItem();

        gagService.verifyGag(gagList.get(currentItem));
    }
}
