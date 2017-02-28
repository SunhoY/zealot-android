package io.harry.zealot.shadow.view;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowTextView;

import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.AjaeMessageView;

@Implements(AjaeMessageView.class)
public class ShadowAjaeMessageView extends ShadowTextView {
    private AjaePower ajaePower;

    @Implementation
    public void setAjaePower(AjaePower ajaePower) {
        this.ajaePower = ajaePower;
    }

    public AjaePower getAjaePower() {
        return ajaePower;
    }
}
