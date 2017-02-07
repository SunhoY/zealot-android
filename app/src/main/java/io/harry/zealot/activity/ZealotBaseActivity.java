package io.harry.zealot.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.harry.zealot.ZealotApplication;
import io.harry.zealot.component.ZealotComponent;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ZealotBaseActivity extends AppCompatActivity {
    protected ZealotComponent zealotComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zealotComponent = ((ZealotApplication) getApplication()).getZealotComponent();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
