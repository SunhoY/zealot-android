package io.harry.zealot.shadow.view;

import android.support.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowImageView;

@Implements(LottieAnimationView.class)
public class ShadowLottieAnimationView extends ShadowImageView {
    private String imageAssetsFolder;

    @Implementation
    public void setImageAssetsFolder(@Nullable String imageAssetsFolder) {
        this.imageAssetsFolder = imageAssetsFolder;
    }

    public String getImageAssetsFolder() {
        return imageAssetsFolder;
    }
}
