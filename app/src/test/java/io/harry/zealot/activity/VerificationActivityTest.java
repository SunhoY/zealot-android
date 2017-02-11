package io.harry.zealot.activity;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

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
import io.harry.zealot.service.GagService;
import io.harry.zealot.service.ServiceCallback;
import io.harry.zealot.wrapper.GagPagerAdapterWrapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class VerificationActivityTest {
    private VerificationActivity subject;

    @Inject
    GagService mockGagService;
    @Inject
    GagPagerAdapterWrapper mockGagPagerAdapterWrapper;

    @Captor
    ArgumentCaptor<ServiceCallback<List<String>>> stringListServiceCallbackCaptor;
    @Captor
    ArgumentCaptor<ServiceCallback<List<Uri>>> uriListServiceCallbackCaptor;

    @Mock
    GagPagerAdapter mockGagPagerAdapter;

    @BindView(R.id.verification_pager)
    ViewPager gagViewPager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = Robolectric.setupActivity(VerificationActivity.class);
        ButterKnife.bind(this, subject);

        ((TestZealotApplication)subject.getApplication()).getZealotComponent().inject(this);

        verify(mockGagService).getGagImageFileNames(anyInt(), anyBoolean(), stringListServiceCallbackCaptor.capture());
    }

    @Test
    public void onCreate_callsGagServiceToFetchUnverifiedGagImages() throws Exception {
        verify(mockGagService).getGagImageFileNames(anyInt(), eq(false), Matchers.<ServiceCallback<List<String>>>any());
    }

    @Test
    public void onCreate_callsGagServiceToFetchNumberOfGagImages() throws Exception {
        int verificationChunkSize = application.getResources().getInteger(R.integer.verification_chunk_size);

        verify(mockGagService).getGagImageFileNames(eq(verificationChunkSize), anyBoolean(), Matchers.<ServiceCallback<List<String>>>any());
    }

    @Test
    public void afterFetchingImages_callsGagServiceToFetchGagURIs() throws Exception {
        stringListServiceCallbackCaptor.getValue().onSuccess(Arrays.asList("first.jpg", "second.jpg"));

        verify(mockGagService).getGagImageUris(eq(Arrays.asList("first.jpg", "second.jpg")),
                Matchers.<ServiceCallback<List<Uri>>>any());
    }

    @Test
    public void afterFetchingURIs_getsViewPagerAdapterFromWrapper() throws Exception {
        ArrayList<String> inputDoesNotMatter = new ArrayList<>();
        stringListServiceCallbackCaptor.getValue().onSuccess(inputDoesNotMatter);

        verify(mockGagService).getGagImageUris(Matchers.<List<String>>any(),
                uriListServiceCallbackCaptor.capture());

        uriListServiceCallbackCaptor.getValue().onSuccess(Arrays.asList(Uri.parse("first.jpg"), Uri.parse("second.jpg")));

        verify(mockGagPagerAdapterWrapper).getGagPagerAdapter(subject.getSupportFragmentManager(), Arrays.asList(Uri.parse("first.jpg"), Uri.parse("second.jpg")));
    }

    @Test
    public void afterGettingViewPagerAdapter_setsOnViewPager() throws Exception {
        ArrayList<String> inputDoesNotMatter = new ArrayList<>();
        stringListServiceCallbackCaptor.getValue().onSuccess(inputDoesNotMatter);

        verify(mockGagService).getGagImageUris(Matchers.<List<String>>any(),
                uriListServiceCallbackCaptor.capture());

        when(mockGagPagerAdapterWrapper.getGagPagerAdapter(any(FragmentManager.class), anyListOf(Uri.class)))
            .thenReturn(mockGagPagerAdapter);

        uriListServiceCallbackCaptor.getValue().onSuccess(Arrays.asList(Uri.parse("first.jpg"), Uri.parse("second.jpg")));

        assertThat(gagViewPager.getAdapter()).isEqualTo(mockGagPagerAdapter);
    }
}