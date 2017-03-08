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
import io.harry.zealot.R;
import io.harry.zealot.activity.MenuActivity;
import io.harry.zealot.fragment.GagFragment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GagPagerAdapterTest {

    private GagPagerAdapter subject;
    private FragmentManager supportFragmentManager;

    @Before
    public void setUp() throws Exception {
        supportFragmentManager = Robolectric.buildActivity(MenuActivity.class)
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

    @Test
    public void constructor_createsFragmentListOnlyWhenListTypeIsIntegerOrUri() throws Exception {
        //URI is tested already
        List<Integer> integers = Arrays.asList(R.drawable.az_tutorial_1, R.drawable.az_tutorial_2, R.drawable.az_tutorial_3);

        subject = new GagPagerAdapter(supportFragmentManager, integers);

        assertThat(subject.getCount()).isEqualTo(3);
        assertThat(subject.getItem(1).getArguments().get("gagResourceId")).isEqualTo(R.drawable.az_tutorial_2);

        List<Float> floats = Arrays.asList(1.f, 2.f, 3.f);

        subject = new GagPagerAdapter(supportFragmentManager, floats);

        assertThat(subject.getCount()).isEqualTo(0);
    }
}