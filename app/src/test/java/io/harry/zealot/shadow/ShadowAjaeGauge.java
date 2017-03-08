package io.harry.zealot.shadow;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowRelativeLayout;

import io.harry.zealot.view.AjaeGauge;

@Implements(AjaeGauge.class)
public class ShadowAjaeGauge extends ShadowRelativeLayout {
    private float value;

    @Implementation
    public void setGaugeValue(float value) {
        this.value = value;
    }

    @Implementation
    public float getGaugeValue() {
        return value;
    }
}
