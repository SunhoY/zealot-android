package io.harry.zealot.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import io.harry.zealot.model.Ajae;

public class AjaePercentageView extends AppCompatTextView implements AjaeAware {
    public AjaePercentageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAjae(Ajae ajae) {
        setTextColor(ContextCompat.getColor(getContext(), ajae.getColor()));
    }
}
