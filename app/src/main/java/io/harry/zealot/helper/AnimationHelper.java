package io.harry.zealot.helper;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationHelper {
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
}
