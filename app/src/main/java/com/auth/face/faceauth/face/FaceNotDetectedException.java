package com.auth.face.faceauth.face;

import com.auth.face.faceauth.base.AppException;

public class FaceNotDetectedException extends AppException {

    public FaceNotDetectedException(String message) {
        super(message);
    }

    public FaceNotDetectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
