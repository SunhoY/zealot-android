package io.harry.zealot.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.R;

public class NavigationBar extends RelativeLayout {
    private int index = 0;
    private int size = 10;
    private String[] ordinalNumbers;

    @BindView(R.id.next)
    TextView next;
    @BindView(R.id.previous)
    TextView previous;
    @BindView(R.id.current)
    TextView current;

    private OnClickListener nextListener;
    private OnClickListener previousListener;
    private NavigateListener navigateListener;

    public interface NavigateListener {
        void onNext();
        void onPrevious();
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = inflate(context, R.layout.navigation_bar, this);

        ButterKnife.bind(this, view);

        ordinalNumbers = getResources().getStringArray(R.array.ordinal_numbers);

        previousListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index > 0) {
                    setCurrentIndex(--index);
                }
                if(navigateListener != null) {
                    navigateListener.onPrevious();
                }
            }
        };

        nextListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index < size - 1) {
                    setCurrentIndex(++index);
                }
                if(navigateListener != null) {
                    navigateListener.onNext();
                }
            }
        };

        previous.setOnClickListener(previousListener);
        next.setOnClickListener(nextListener);
    }

    public int getCurrentIndex() {
        return index;
    }

    public void setNavigateListener(final NavigateListener navigateListener) {
        this.navigateListener = navigateListener;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setCurrentIndex(int index) {
        if(index < 0 || index >= size)
            return;

        this.index = index;

        if(index == 0)  {
            previous.setText(R.string.to_home);
        } else {
            previous.setText(R.string.previous);
        }

        if (index == size - 1) {
            next.setText(R.string.result);
        } else {
            next.setText(R.string.next);
        }

        current.setText(ordinalNumbers[index]);
    }
}
