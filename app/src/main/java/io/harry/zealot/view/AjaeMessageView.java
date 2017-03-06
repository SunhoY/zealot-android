package io.harry.zealot.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import io.harry.zealot.model.Ajae;

public class AjaeMessageView extends AppCompatTextView implements AjaeAware {
    public AjaeMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAjae(Ajae ajae) {
        setText(ajae.getMessage());
        setTextColor(ContextCompat.getColor(getContext(), ajae.getColor()));
    }
}
