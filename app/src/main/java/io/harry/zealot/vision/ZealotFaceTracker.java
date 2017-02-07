package io.harry.zealot.vision;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

import io.harry.zealot.listener.FaceListener;

public class ZealotFaceTracker extends Tracker<Face> {
    private FaceListener faceListener;

    public ZealotFaceTracker(FaceListener faceListener) {
        this.faceListener = faceListener;
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        faceListener.onFaceDetect(face);
    }
}
