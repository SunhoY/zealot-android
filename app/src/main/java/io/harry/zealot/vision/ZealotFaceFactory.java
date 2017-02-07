package io.harry.zealot.vision;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

import io.harry.zealot.listener.FaceListener;

public class ZealotFaceFactory implements MultiProcessor.Factory<Face> {
    private FaceListener faceListener;

    public ZealotFaceFactory(FaceListener faceListener) {
        this.faceListener = faceListener;
    }

    @Override
    public Tracker<Face> create(Face face) {
        return new ZealotFaceTracker(faceListener);
    }
}
