package io.harry.zealot.shadow.view;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowTextView;

import io.harry.zealot.model.Ajae;
import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.AjaePercentageView;

@Implements(AjaePercentageView.class)
public class ShadowAjaePercentageView extends ShadowTextView {
    private Ajae ajae;

    @Implementation
    public void setAjae(Ajae ajae) {
        this.ajae = ajae;
    }

    public Ajae getAjae() {
        return ajae;
    }
}
