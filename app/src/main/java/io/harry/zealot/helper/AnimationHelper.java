package io.harry.zealot.helper;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationHelper {
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
}
