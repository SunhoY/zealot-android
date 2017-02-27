package io.harry.zealot.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import io.harry.zealot.R;
import io.harry.zealot.state.AjaePower;

public class AjaeMessageView extends TextView implements AjaeAware {
    private AjaePower ajaePower;

    public AjaeMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAjaePower(AjaePower ajaePower) {
        this.ajaePower = ajaePower;

        switch (ajaePower) {
            case BURNT:
                setText(R.string.burnt_ajae_message);
                setTextColor(ContextCompat.getColor(getContext(), R.color.burnt_ajae));
                break;
            case WELL_DONE:
                setText(R.string.well_done_ajae_message);
                setTextColor(ContextCompat.getColor(getContext(), R.color.well_done_ajae));
                break;
            case MEDIUM:
                setText(R.string.medium_ajae_message);
                setTextColor(ContextCompat.getColor(getContext(), R.color.medium_ajae));
                break;
            case RARE:
                setText(R.string.rare_ajae_message);
                setTextColor(ContextCompat.getColor(getContext(), R.color.rare_ajae));
                break;
            case NONE:
                setText(R.string.none_ajae_message);
                setTextColor(ContextCompat.getColor(getContext(), R.color.none_ajae));
                break;
        }
    }
}
