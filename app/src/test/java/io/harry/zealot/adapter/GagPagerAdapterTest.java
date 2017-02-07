package io.harry.zealot.adapter;

import android.net.Uri;
import android.support.v4.app.FragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import io.harry.zealot.BuildConfig;
import io.harry.zealot.activity.SplashActivity;
import io.harry.zealot.fragment.GagFragment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GagPagerAdapterTest {

    private GagPagerAdapter subject;

    @Before
    public void setUp() throws Exception {
        FragmentManager supportFragmentManager = Robolectric.buildActivity(SplashActivity.class)
                .create().get().getSupportFragmentManager();

        List<Uri> uris = Arrays.asList(
                Uri.parse("http://gag1.png"),
                Uri.parse("http://gag2.png"),
                Uri.parse("http://gag3.png"));

        subject = new GagPagerAdapter(supportFragmentManager, uris);
    }

    @Test
    public void getItem_returnsFragmentAtRequestedPosition() throws Exception {
        GagFragment firstFragment = (GagFragment) subject.getItem(0);

        assertThat(firstFragment.getArguments().get("gagImageUri"))
                .isEqualTo(Uri.parse("http://gag1.png"));

        GagFragment secondFragment = (GagFragment) subject.getItem(1);

        assertThat(secondFragment.getArguments().get("gagImageUri"))
                .isEqualTo(Uri.parse("http://gag2.png"));

        GagFragment thirdFragment = (GagFragment) subject.getItem(2);

        assertThat(thirdFragment.getArguments().get("gagImageUri"))
                .isEqualTo(Uri.parse("http://gag3.png"));
    }

    @Test
    public void getCount_returnsNumberOfURLs() throws Exception {
        assertThat(subject.getCount()).isEqualTo(3);
    }
}