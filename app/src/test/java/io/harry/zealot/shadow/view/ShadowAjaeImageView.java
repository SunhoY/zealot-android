package io.harry.zealot.shadow.view;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowImageView;

import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.AjaeImageView;

@Implements(AjaeImageView.class)
public class ShadowAjaeImageView extends ShadowImageView {
    private AjaePower ajaePower;

    @Implementation
    public void setAjaePower(AjaePower ajaePower) {
        this.ajaePower = ajaePower;
    }

    public AjaePower getAjaePower() {
        return this.ajaePower;
    }
}
