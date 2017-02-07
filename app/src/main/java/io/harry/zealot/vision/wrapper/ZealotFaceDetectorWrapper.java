package io.harry.zealot.vision.wrapper;

import android.content.Context;

import com.google.android.gms.vision.face.FaceDetector;

public class ZealotFaceDetectorWrapper {
    public FaceDetector getFaceDetector(Context context) {
        return new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
    }
}
