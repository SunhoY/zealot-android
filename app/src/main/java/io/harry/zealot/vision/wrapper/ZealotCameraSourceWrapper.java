package io.harry.zealot.vision.wrapper;

import android.content.Context;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;

public class ZealotCameraSourceWrapper {
    public static final int PREVIEW_WIDTH = 960;
    public static final int PREVIEW_HEIGHT = 1280;
    public static final float PREVIEW_FPS = 30.0f;
    public static final int CAMERA_FACING = CameraSource.CAMERA_FACING_FRONT;

    public CameraSource getCameraSource(Context context, FaceDetector faceDetector) {
        return new CameraSource.Builder(context, faceDetector)
                .setRequestedPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT)
                .setRequestedFps(PREVIEW_FPS)
                .setFacing(CAMERA_FACING)
                .build();
    }
}
