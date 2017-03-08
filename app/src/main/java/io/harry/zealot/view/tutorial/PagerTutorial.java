package io.harry.zealot.view.tutorial;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import io.harry.zealot.R;

public class PagerTutorial extends LinearLayout {
    public PagerTutorial(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.pager_tutorial, this);
    }
}
