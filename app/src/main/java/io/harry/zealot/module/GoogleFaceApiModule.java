package io.harry.zealot.module;

import dagger.Module;
import dagger.Provides;
import io.harry.zealot.vision.wrapper.ZealotCameraSourceWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceDetectorWrapper;
import io.harry.zealot.vision.wrapper.ZealotFaceFactoryWrapper;
import io.harry.zealot.vision.wrapper.ZealotMultiProcessorWrapper;

@Module(
        includes = {
                ContextModule.class,
        }
)
public class GoogleFaceApiModule {
    @Provides
    public ZealotFaceFactoryWrapper provideZealotFaceFactoryWrapper() {
        return new ZealotFaceFactoryWrapper();
    }

    @Provides
    public ZealotFaceDetectorWrapper provideZealotFaceDetectorWrapper() {
        return new ZealotFaceDetectorWrapper();
    }

    @Provides
    public ZealotMultiProcessorWrapper provideZealotMultiProcessorWrapper() {
        return new ZealotMultiProcessorWrapper();
    }

    @Provides
    public ZealotCameraSourceWrapper provideZealotCameraSourceWrapper() {
        return new ZealotCameraSourceWrapper();
    }
}
