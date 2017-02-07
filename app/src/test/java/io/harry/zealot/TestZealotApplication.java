package io.harry.zealot;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

import io.harry.zealot.component.DaggerTestZealotComponent;
import io.harry.zealot.component.TestZealotComponent;

public class TestZealotApplication extends ZealotApplication implements TestLifecycleApplication {
    TestZealotComponent testZealotComponent;

    @Override
    public void beforeTest(Method method) {

    }

    @Override
    public void prepareTest(Object test) {
        testZealotComponent = DaggerTestZealotComponent.builder().build();
    }

    @Override
    public void afterTest(Method method) {

    }

    @Override
    public TestZealotComponent getZealotComponent() {
        return testZealotComponent;
    }
}
