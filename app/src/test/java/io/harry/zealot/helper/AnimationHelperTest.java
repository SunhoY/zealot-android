package io.harry.zealot.helper;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AnimationHelperTest {
    @Test
    public void startAnimation_startsAnimationOnPassedView() throws Exception {
        AnimationHelper subject = new AnimationHelper(mock(Context.class));

        View mockView = mock(View.class);
        Animation mockAnimation = mock(Animation.class);

        subject.startAnimation(mockView, mockAnimation);

        verify(mockView).startAnimation(mockAnimation);
    }
}