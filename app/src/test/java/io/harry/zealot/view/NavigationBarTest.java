package io.harry.zealot.view;

import android.util.AttributeSet;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.BuildConfig;
import io.harry.zealot.R;
import io.harry.zealot.view.NavigationBar.NavigateListener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NavigationBarTest {
    private static final int NAVIGATE_SIZE = 5;
    private NavigationBar subject;

    @BindView(R.id.previous)
    TextView previous;
    @BindView(R.id.next)
    TextView next;
    @BindView(R.id.current)
    TextView current;

    @Mock
    private NavigateListener mockNavigateListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        AttributeSet attributeSet = Robolectric.buildAttributeSet().build();

        subject = new NavigationBar(application, attributeSet);
        subject.setSize(NAVIGATE_SIZE);

        ButterKnife.bind(this, subject);
    }

    @Test
    public void setCurrentIndex_changesTextOfPrevious_accordingToCurrentIndex() throws Exception {
        subject.setCurrentIndex(0);

        assertThat(previous.getText()).isEqualTo("집으로");

        subject.setCurrentIndex(1);

        assertThat(previous.getText()).isEqualTo("이전");
    }

    @Test
    public void setCurrentIndex_changesTextOfNext_accordingToCurrentIndex() throws Exception {
        subject.setCurrentIndex(NAVIGATE_SIZE - 2);

        assertThat(next.getText()).isEqualTo("다음");

        subject.setCurrentIndex(NAVIGATE_SIZE - 1);

        assertThat(next.getText()).isEqualTo("결과");
    }

    @Test
    public void showsOrdinalNumber_onCenterTopText_accordingToPageNumber() throws Exception {
        subject.setCurrentIndex(0);

        assertThat(current.getText()).isEqualTo("첫번째");

        subject.setCurrentIndex(3);

        assertThat(current.getText()).isEqualTo("네번째");
    }

    @Test
    public void clickOnNext_increaseIndex_accordingToSize() throws Exception {
        subject.setCurrentIndex(NAVIGATE_SIZE - 2);

        assertThat(current.getText()).isEqualTo("네번째");

        next.performClick();

        assertThat(current.getText()).isEqualTo("다섯번째");

        next.performClick();

        assertThat(current.getText()).isEqualTo("다섯번째");
    }

    @Test
    public void clickOnNext_runsListener_accordingToSize_whenListenerIsSet() throws Exception {
        subject.setNavigateListener(mockNavigateListener);
        subject.setCurrentIndex(NAVIGATE_SIZE - 2);

        next.performClick();

        verify(mockNavigateListener, times(1)).onNext();

        next.performClick();

        verify(mockNavigateListener, times(1)).onNext();
    }

    @Test
    public void clickOnPrevious_decreaseIndex_accordingToSize() throws Exception {
        subject.setCurrentIndex(1);

        assertThat(current.getText()).isEqualTo("두번째");

        previous.performClick();

        assertThat(current.getText()).isEqualTo("첫번째");

        previous.performClick();

        assertThat(current.getText()).isEqualTo("첫번째");
    }

    @Test
    public void clickOnPrevious_runsListener_accordingToSize_whenListenerIsSet() throws Exception {
        subject.setNavigateListener(mockNavigateListener);
        subject.setCurrentIndex(1);

        previous.performClick();

        verify(mockNavigateListener, times(1)).onPrevious();

        previous.performClick();

        verify(mockNavigateListener, times(1)).onPrevious();
    }
}