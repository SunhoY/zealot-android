package io.harry.zealot.module;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.vision.wrapper.ZealotCameraSourceWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceDetectorWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceFactoryWrapper;
import io.harry.zealot.vision.wrapper.ZealotMultiProcessorWrapper;

import static org.mockito.Mockito.mock;

@Module
public class TestGoogleFaceApiModule {
    @Provides
    @Singleton
    ZealotFaceDetectorWrapper provideZealotFaceDetectorWrapper() {
        return mock(ZealotFaceDetectorWrapper.class);
    }

    @Provides
    @Singleton
    ZealotFaceFactoryWrapper provideZealotFaceFactoryWrapper() {
        return mock(ZealotFaceFactoryWrapper.class);
    }

    @Provides
    @Singleton
    ZealotMultiProcessorWrapper provideZealotMultiProcessorWrapper() {
        return mock(ZealotMultiProcessorWrapper.class);
    }

    @Provides
    @Singleton
    ZealotCameraSourceWrapper provideZealotCameraSourceWrapper() {
        return mock(ZealotCameraSourceWrapper.class);
    }
}
