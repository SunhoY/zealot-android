package io.harry.zealot.shadow;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowRelativeLayout;

import io.harry.zealot.view.NavigationBar;
import lombok.Getter;

@Implements(NavigationBar.class)
public class ShadowNavigationBar extends ShadowRelativeLayout {
    @Getter
    private int index;
    @Getter
    private int size;
    @Getter
    private NavigationBar.NavigateListener listener;

    @Implementation
    public void setCurrentIndex(int index) {
        this.index = index;
    }

    @Implementation
    public void setSize(int size) {
        this.size = size;
    }

    @Implementation
    public void setNavigateListener(NavigationBar.NavigateListener listener) {
        this.listener = listener;
    }
}
