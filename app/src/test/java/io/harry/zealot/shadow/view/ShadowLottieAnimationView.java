package io.harry.zealot.shadow.view;

import android.animation.Animator;
import android.support.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowImageView;

@Implements(LottieAnimationView.class)
public class ShadowLottieAnimationView extends ShadowImageView {
    private String imageAssetsFolder;
    private Animator.AnimatorListener animationListener;

    @Implementation
    public void setImageAssetsFolder(@Nullable String imageAssetsFolder) {
        this.imageAssetsFolder = imageAssetsFolder;
    }

    public void addAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.animationListener = animatorListener;
    }

    public String getImageAssetsFolder() {
        return imageAssetsFolder;
    }

    public Animator.AnimatorListener getAnimatorListener() {
        return animationListener;
    }
}
