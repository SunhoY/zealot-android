package io.harry.zealot.shadow;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowViewGroup;

import lombok.Getter;

@Implements(ViewPager.class)
public class ShadowViewPager extends ShadowViewGroup {
    @Getter
    private OnPageChangeListener onPageChangeListener;

    @Implementation
    public void addOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }
}
