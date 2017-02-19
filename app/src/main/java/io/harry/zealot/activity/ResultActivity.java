package io.harry.zealot.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.harry.zealot.R;
import io.harry.zealot.dialog.DialogService;
import io.harry.zealot.range.AjaeScoreRange;
import io.harry.zealot.state.AjaePower;
import io.harry.zealot.view.AjaeImageView;
import io.harry.zealot.view.AjaeMessageView;
import io.harry.zealot.view.AjaePercentageView;

public class ResultActivity extends ZealotBaseActivity {
    private static final String AJAE_SCORE = "ajaeScore";

    private String ajaeScoreText;
    private AlertDialog nickNameInputDialog;

    @BindView(R.id.ajae_score)
    AjaePercentageView ajaeScore;
    @BindView(R.id.result_image)
    AjaeImageView resultImage;

    @BindView(R.id.result_message)
    AjaeMessageView resultMessage;
    @Inject
    AjaeScoreRange ajaeScoreRange;

    @Inject
    DialogService dialogService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zealotComponent.inject(this);

        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        int score = getIntent().getIntExtra(AJAE_SCORE, 100);

        ajaeScoreText = String.valueOf(score);
        ajaeScore.setText(getResources().getString(R.string.x_percentage, score));

        AjaePower ajaePower = getAjaeStateByScore(score);

        ajaeScore.setAjaePower(ajaePower);
        resultImage.setAjaePower(ajaePower);
        resultMessage.setAjaePower(ajaePower);
    }

    private AjaePower getAjaeStateByScore(int score) {
        return ajaeScoreRange.getRange(score);
    }

    @OnClick(R.id.test_again)
    public void onTestAgainClick() {
        finish();
    }

    @OnClick(R.id.share_sns)
    public void onShareSNSClick() {
        nickNameInputDialog = dialogService.getInputDialog(this, new DialogService.InputDialogListener() {
            @Override
            public void onConfirm(String nickName) {
                String serverURL = getString(R.string.server_url);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, serverURL + "?score=" + ajaeScoreText + "&nickName=" + nickName);

                Intent chooser = Intent.createChooser(intent, getString(R.string.share_ajae_power));

                startActivity(chooser);
            }
        });

        nickNameInputDialog.show();
    }
}
