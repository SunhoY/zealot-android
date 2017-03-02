package io.harry.zealot.helper;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationHelper {
    private static final int FROM_VALUE = 0;

    //TODO: not using this class from anywhere.
    //TODO: 안쓰는 애니메이션도 정리할 것
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
