package io.harry.zealot.shadow.view;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowTextView;

import io.harry.zealot.model.Ajae;
import io.harry.zealot.view.AjaeMessageView;

@Implements(AjaeMessageView.class)
public class ShadowAjaeMessageView extends ShadowTextView {
    private Ajae ajae;

    @Implementation
    public void setAjae(Ajae ajae) {
        this.ajae = ajae;
    }

    public Ajae getAjae() {
        return ajae;
    }
}
