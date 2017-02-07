package io.harry.zealot.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class ZealotViewPager extends ViewPager {
    private OnSwipeListener onSwipeListener;
    boolean lastPagedReached = false;
    boolean swipeListenerCalled = false;

    public ZealotViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        if(lastPagedReached && offset == 0 && !swipeListenerCalled) {
            onSwipeListener.onAttemptedOnLastPage();
            swipeListenerCalled = true;
        }

        lastPagedReached = position + 1 == getAdapter().getCount();

        super.onPageScrolled(position, offset, offsetPixels);
    }

    public OnSwipeListener getOnSwipeListener() {
        return onSwipeListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }
}
