package io.harry.zealot.helper;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.harry.zealot.BuildConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AnimationHelperTest {

    private AnimationHelper subject;

    @Before
    public void setUp() throws Exception {
        subject = new AnimationHelper(mock(Context.class));
    }

    @Test
    public void startAnimation_startsAnimationOnPassedView() throws Exception {
        View mockView = mock(View.class);
        Animation mockAnimation = mock(Animation.class);

        subject.startAnimation(mockView, mockAnimation);

        verify(mockView).startAnimation(mockAnimation);
    }

    @Test
    public void getValueIncreaseAnimation_returnsValueAnimatorWithValueRangeAndDuration() throws Exception {
        ValueAnimator valueIncreaseAnimator = subject.getValueIncreaseAnimator(67, 2000);

        assertThat(valueIncreaseAnimator.getDuration()).isEqualTo(2000);

        PropertyValuesHolder[] values = valueIncreaseAnimator.getValues();

        assertThat(valueIncreaseAnimator.getAnimatedValue()).isEqualTo(null);
        //this is how animate values are stored
        assertThat(values[0].toString()).isEqualTo(":  0  67  ");
    }
}