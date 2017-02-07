package io.harry.zealot.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import io.harry.zealot.R;
import io.harry.zealot.state.AjaePower;

public class AjaeImageView extends ImageView implements AjaeAware {
    private AjaePower ajaePower;

    public AjaeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAjaePower(AjaePower ajaePower) {
        this.ajaePower = ajaePower;

        switch (ajaePower) {
            case NO: {
                setImageResource(R.drawable.not_ajae);
                break;
            }
            case MEDIUM: {
                setImageResource(R.drawable.medium_ajae);
                break;
            }
            case FULL: {
                setImageResource(R.drawable.full_ajae);
                break;
            }
        }
    }
}
