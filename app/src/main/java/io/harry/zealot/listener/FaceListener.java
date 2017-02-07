package io.harry.zealot.listener;

import com.google.android.gms.vision.face.Face;

public interface FaceListener {
    void onFaceDetect(Face face);
}
