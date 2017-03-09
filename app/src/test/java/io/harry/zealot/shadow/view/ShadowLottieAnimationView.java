package io.harry.zealot.shadow.view;

import android.animation.ValueAnimator;
import android.support.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowImageView;

@Implements(LottieAnimationView.class)
public class ShadowLottieAnimationView extends ShadowImageView {
    private String imageAssetsFolder;
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener;

    @Implementation
    public void setImageAssetsFolder(@Nullable String imageAssetsFolder) {
        this.imageAssetsFolder = imageAssetsFolder;
    }

    public void addAnimatorUpdateListener(ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        this.animatorUpdateListener = animatorUpdateListener;
    }

    public String getImageAssetsFolder() {
        return imageAssetsFolder;
    }

    public ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener() {
        return animatorUpdateListener;
    }
}
