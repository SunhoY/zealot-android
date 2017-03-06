package io.harry.zealot.shadow.view;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowImageView;

import io.harry.zealot.model.Ajae;
import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.AjaeImageView;

@Implements(AjaeImageView.class)
public class ShadowAjaeImageView extends ShadowImageView {
    private Ajae ajae;

    @Implementation
    public void setAjae(Ajae ajae) {
        this.ajae = ajae;
    }

    public Ajae getAjae() {
        return this.ajae;
    }
}
