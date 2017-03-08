package io.harry.zealot.view;

import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.BuildConfig;
import io.harry.zealot.R;
import io.harry.zealot.TestZealotApplication;
import io.harry.zealot.model.Ajae;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.shadow.view.ShadowAjaePercentageView;
import io.harry.zealot.state.AjaePower;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowAjaePercentageView.class})
public class AjaeGaugeTest {
    private static final float VALUE_NO_MATTER = 66.f;
    private AjaeGauge subject;

    @BindView(R.id.percentage)
    AjaePercentageView percentage;
    @BindView(R.id.progress)
    RoundCornerProgressBar progressBar;

    @Inject
    AjaeScoreRange mockScoreRange;

    @Before
    public void setUp() throws Exception {
        AttributeSet attributes = Robolectric.buildAttributeSet().build();

        ((TestZealotApplication) application).getZealotComponent().inject(this);
        subject = new AjaeGauge(application, attributes);

        ButterKnife.bind(this, subject);

        when(mockScoreRange.getAjaePower(anyInt())).thenReturn(AjaePower.RARE);
    }

    @Test
    public void setGaugeValue_setsProgressOnProgressBar() throws Exception {
        subject.setGaugeValue(70.f);

        assertThat(progressBar.getProgress()).isEqualTo(70.f);
    }

    @Test
    public void setGaugeValue_callsScoreRangeToFigureAjaePowerOut() throws Exception {
        subject.setGaugeValue(60.f);

        verify(mockScoreRange).getAjaePower(60);
    }

    @Test
    public void setGaugeValue_setsProgressColor_accordingToAjaePower() throws Exception {
        when(mockScoreRange.getAjaePower(anyInt())).thenReturn(AjaePower.BURNT);

        subject.setGaugeValue(VALUE_NO_MATTER);

        Ajae expectedAjae = new Ajae(AjaePower.BURNT);

        assertThat(progressBar.getProgressColor()).isEqualTo(ContextCompat.getColor(application, expectedAjae.getColor()));
    }

    @Test
    public void setGaugeValue_setsPercentageValueOnAjaePercentageView() throws Exception {
        subject.setGaugeValue(50.0f);

        assertThat(percentage.getText()).isEqualTo("50 %");

        subject.setGaugeValue(60.0f);

        assertThat(percentage.getText()).isEqualTo("60 %");
    }

    @Test
    public void setGaugeValue_setsAjae_accordingToAjaePower() throws Exception {
        when(mockScoreRange.getAjaePower(anyInt())).thenReturn(AjaePower.MEDIUM);

        subject.setGaugeValue(VALUE_NO_MATTER);

        Ajae expectedAjae = new Ajae(AjaePower.MEDIUM);

        ShadowAjaePercentageView shadowPercentage = (ShadowAjaePercentageView) shadowOf(percentage);
        assertThat(shadowPercentage.getAjae()).isEqualTo(expectedAjae);
    }
}