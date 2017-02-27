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
            case BURNT:
                setImageResource(R.drawable.burnt_ajae);
                break;
            case WELL_DONE:
                setImageResource(R.drawable.well_done_ajae);
                break;
            case MEDIUM:
                setImageResource(R.drawable.medium_ajae);
                break;
            case RARE:
                setImageResource(R.drawable.rare_ajae);
                break;
            case NONE:
                setImageResource(R.drawable.none_ajae);
                break;
        }
    }
}
