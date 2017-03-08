package io.harry.zealot.helper;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationHelper {
    private static final int FROM_VALUE = 0;

    private final Context context;

    public AnimationHelper(Context context) {
        this.context = context;
    }

    public Animation loadAnimation(int animationResourceId) {
        return AnimationUtils.loadAnimation(context, animationResourceId);
    }

    public void startAnimation(View view, Animation animation) {
        view.startAnimation(animation);
    }

    public ValueAnimator getValueIncreaseAnimator(int toValue, int duration) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(duration);
        valueAnimator.setObjectValues(FROM_VALUE, toValue);

        return valueAnimator;
    }
}
