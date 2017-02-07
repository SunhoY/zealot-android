package io.harry.zealot.vision.wrapper;

import io.harry.zealot.listener.FaceListener;
import io.harry.zealot.vision.ZealotFaceFactory;

public class ZealotFaceFactoryWrapper {
    public ZealotFaceFactory getZealotFaceFactory(FaceListener faceListener) {
        return new ZealotFaceFactory(faceListener);
    }
}
