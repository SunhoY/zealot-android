package io.harry.zealot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.R;
import io.harry.zealot.helper.AnimationHelper;

public class SplashActivity extends ZealotBaseActivity implements Animation.AnimationListener{

    @BindView(R.id.view_container)
    View viewContainer;

    @Inject
    AnimationHelper animationHelper;

    private Animation fadeOutAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);
        zealotComponent.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fadeOutAnimation = animationHelper.loadAnimation(R.animator.fade_out);
        fadeOutAnimation.setAnimationListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationHelper.startAnimation(viewContainer, fadeOutAnimation);
            }
        }, 2000);

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        viewContainer.setVisibility(View.GONE);
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
