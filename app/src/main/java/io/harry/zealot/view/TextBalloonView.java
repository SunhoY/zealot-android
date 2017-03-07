package io.harry.zealot.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import io.harry.zealot.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TextBalloonView extends RelativeLayout {

    private int balloonTextResourceId;
    private int balloonImageResourceId;
    private ImageView balloon;

    public TextBalloonView(Context context) {
        super(context);
    }

    public TextBalloonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TextBalloonView, 0, 0);

        balloonImageResourceId = typedArray.getInteger(R.styleable.TextBalloonView_balloon_drawable, 0);
        balloonTextResourceId = typedArray.getInteger(R.styleable.TextBalloonView_balloon_message, 0);
    }

    private void init() {
        balloon = new ImageView(getContext());
        RelativeLayout.LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        balloon.setLayoutParams(params);
        balloon.setImageResource(balloonImageResourceId);

        addView(balloon);
    }
}
