package io.harry.zealot.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.R;
import io.harry.zealot.ZealotApplication;
import io.harry.zealot.model.Ajae;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.state.AjaePower;

public class AjaeGauge extends RelativeLayout {

    private final Context context;

    @BindView(R.id.progress)
    RoundCornerProgressBar progressBar;
    @BindView(R.id.percentage)
    AjaePercentageView percentage;
    @BindView(R.id.taunt_message)
    TextView tauntMessage;

    @Inject
    AjaeScoreRange ajaeScoreRange;

    public AjaeGauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        View view = inflate(context, R.layout.ajae_gauge, this);

        ButterKnife.bind(this, view);

        ((ZealotApplication) context.getApplicationContext()).getZealotComponent().inject(this);
    }

    public void setGaugeValue(float value) {
        AjaePower ajaePower = ajaeScoreRange.getAjaePower((int) value);
        Ajae ajae = new Ajae(ajaePower);

        progressBar.setProgress(value);
        progressBar.setProgressColor(ContextCompat.getColor(context, ajae.getColor()));
        percentage.setText(context.getString(R.string.x_percentage, (int) value));
        percentage.setAjae(ajae);
    }

    public float getGaugeValue() {
        return progressBar.getProgress();
    }
}
