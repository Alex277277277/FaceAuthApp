package com.auth.face.faceauth.base;

public class AppException extends RuntimeException {

    private int errCode;

    public AppException(String message, int errCode) {
        this(message);
        this.errCode = errCode;
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getErrCode() {
        return errCode;
    }
}
