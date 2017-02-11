package io.harry.zealot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.harry.zealot.R;

public class CheckAdminActivity extends AppCompatActivity {
    @BindView(R.id.admin_code)
    EditText adminCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check_admin);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.confirm)
    public void onConfirmClick() {
        String passCode = adminCode.getText().toString();

        if(passCode.equals(getResources().getString(R.string.admin_pass_code))) {
            startActivity(new Intent(this, VerificationActivity.class));
        }

        finish();
    }
}
