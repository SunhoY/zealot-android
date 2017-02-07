package io.harry.zealot;

import android.app.Application;

import io.harry.zealot.component.DaggerZealotComponent;
import io.harry.zealot.component.ZealotComponent;
import io.harry.zealot.module.ContextModule;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ZealotApplication extends Application {
    private ZealotComponent zealotComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        zealotComponent = DaggerZealotComponent.builder()
                .contextModule(new ContextModule(this)).build();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    public ZealotComponent getZealotComponent() {
        return zealotComponent;
    }
}
