package io.harry.zealot.activity;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.harry.zealot.R;
import io.harry.zealot.api.UrlShortenApi;
import io.harry.zealot.dialog.DialogService;
import io.harry.zealot.helper.AnimationHelper;
import io.harry.zealot.model.Ajae;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.AjaeImageView;
import io.harry.zealot.view.AjaeMessageView;
import io.harry.zealot.view.AjaePercentageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends ZealotBaseActivity implements DialogService.InputDialogListener, Callback<Map<String, Object>> {
    private static final String AJAE_SCORE = "ajaeScore";

    private AlertDialog nickNameInputDialog;
    private ProgressDialog progressDialog;
    private int ajaePercentageValue;

    @BindView(R.id.ajae_percentage)
    AjaePercentageView ajaePercentage;
    @BindView(R.id.ajae_image)
    AjaeImageView ajaeImage;
    @BindView(R.id.ajae_message)
    AjaeMessageView ajaeMessage;

    @Inject
    AjaeScoreRange ajaeScoreRange;
    @Inject
    UrlShortenApi urlShortenApi;
    @Inject
    DialogService dialogService;
    @Inject
    AnimationHelper animationHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zealotComponent.inject(this);

        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        ajaePercentageValue = getIntent().getIntExtra(AJAE_SCORE, 100);

        ValueAnimator valueIncreaseAnimator = animationHelper.getValueIncreaseAnimator(ajaePercentageValue, 2000);
        valueIncreaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ajaePercentage.setText(getResources().getString(R.string.x_percentage, (int) animation.getAnimatedValue()));
            }
        });
        valueIncreaseAnimator.start();

        AjaePower ajaePower = ajaeScoreRange.getAjaePower(ajaePercentageValue);
        Ajae ajae = new Ajae(ajaePower);

        ajaePercentage.setAjae(ajae);
        ajaeImage.setAjae(ajae);
        ajaeMessage.setAjae(ajae);
    }

    @OnClick(R.id.test_again)
    public void onTestAgainClick() {
        finish();
    }

    @OnClick(R.id.share_sns)
    public void onShareSNSClick() {
        nickNameInputDialog = dialogService.getInputDialog(this, this);
        nickNameInputDialog.show();
    }

    @Override
    public void onConfirm(String nickName) {
        String serverURL = getString(R.string.server_url);
        String shareURL = serverURL + "?score=" + ajaePercentageValue + "&nickName=" + nickName;

        Call<Map<String, Object>> mapCall = urlShortenApi.shortenedUrl(ImmutableMap.of("longUrl", shareURL), getString(R.string.google_api_key));

        progressDialog = dialogService.getProgressDialog(this, getString(R.string.packing_your_ajae_power, nickName));
        progressDialog.show();

        mapCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
        if(progressDialog != null) {
            progressDialog.hide();
        }

        String shortenedURL = response.body().get("id").toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shortenedURL);

        Intent chooser = Intent.createChooser(intent, getString(R.string.share_ajae_power));

        startActivity(chooser);
    }

    @Override
    public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
}
