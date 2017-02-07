package io.harry.zealot.vision.wrapper;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;

public class ZealotMultiProcessorWrapper {
    public MultiProcessor<Face> getMultiProcessor(MultiProcessor.Factory<Face> faceFactory) {
        return new MultiProcessor.Builder<>(faceFactory).build();
    }
}
