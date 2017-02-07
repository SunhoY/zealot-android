package io.harry.zealot.viewpager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.harry.zealot.BuildConfig;
import io.harry.zealot.adapter.GagPagerAdapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ZealotViewPagerTest {
    private ZealotViewPager subject;

    @Mock
    GagPagerAdapter mockGagPager;
    @Mock
    OnSwipeListener mockOnSwipeListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Robolectric.AttributeSetBuilder attributeSetBuilder = Robolectric.buildAttributeSet();

        when(mockGagPager.getCount()).thenReturn(4);

        subject = new ZealotViewPager(application, attributeSetBuilder.build());
        subject.setAdapter(mockGagPager);
        subject.setOnSwipeListener(mockOnSwipeListener);
    }

    @Test
    public void onPageScroll_changesIndicator_whenPagerReachesLastPage() throws Exception {
        subject.onPageScrolled(1, 0, 0);

        assertThat(subject.lastPagedReached).isFalse();

        subject.onPageScrolled(3, 0, 0);

        assertThat(subject.lastPagedReached).isTrue();
    }

    @Test
    public void onPageScroll_runsOnSwipeListener_whenAttemptToGoNextPageOnLastPage() throws Exception {
        subject.onPageScrolled(3, 0, 0);

        verify(mockOnSwipeListener, never()).onAttemptedOnLastPage();

        subject.onPageScrolled(3, 0, 0);
        subject.onPageScrolled(3, 0, 0);
        subject.onPageScrolled(3, 0, 0);

        verify(mockOnSwipeListener, times(1)).onAttemptedOnLastPage();
    }

    @Test
    public void onPageScroll_doesNotRunOnSwipeListener_whenAttemptToGoPreviousPageOnLastPage() throws Exception {
        subject.onPageScrolled(3, 0, 0);

        subject.onPageScrolled(2, 0.9f, 0);

        verify(mockOnSwipeListener, never()).onAttemptedOnLastPage();
        assertThat(subject.lastPagedReached).isFalse();
    }
}