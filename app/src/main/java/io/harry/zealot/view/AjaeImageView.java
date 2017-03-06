package io.harry.zealot.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

import io.harry.zealot.model.Ajae;

public class AjaeImageView extends AppCompatImageView implements AjaeAware {
    public AjaeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAjae(Ajae ajae) {
        setImageResource(ajae.getImage());
    }
}
