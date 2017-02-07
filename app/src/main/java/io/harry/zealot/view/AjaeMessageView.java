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
            case FULL:
                setText(R.string.real_ajae_message);
                setTextColor(ContextCompat.getColor(getContext(), R.color.full_ajae));
                break;
            case MEDIUM:
                setText(R.string.medium_ajae_message);
                setTextColor(ContextCompat.getColor(getContext(), R.color.medium_ajae));
                break;
            case NO:
                setText(R.string.not_ajae_message);
                setTextColor(ContextCompat.getColor(getContext(), R.color.not_ajae));
                break;
        }
    }
}
