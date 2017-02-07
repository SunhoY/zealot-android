package io.harry.zealot.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import io.harry.zealot.R;
import io.harry.zealot.state.AjaePower;

public class AjaePercentageView extends TextView implements AjaeAware {
    private AjaePower ajaePower;

    public AjaePercentageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAjaePower(AjaePower ajaePower) {
        this.ajaePower = ajaePower;

        switch (ajaePower) {
            case FULL:
                setTextColor(ContextCompat.getColor(getContext(), R.color.full_ajae));
                break;
            case MEDIUM:
                setTextColor(ContextCompat.getColor(getContext(), R.color.medium_ajae));
                break;
            case NO:
                setTextColor(ContextCompat.getColor(getContext(), R.color.not_ajae));
                break;
        }
    }
}
