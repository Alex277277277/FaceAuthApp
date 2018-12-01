package com.auth.face.faceauth.logger;

public interface Logger {

    void debug(String tag, String message);

    void info(String tag, String message);

    void warning(String tag, String message);

    void error(String tag, String message);

    void error(String tag, String message, Throwable error);

}
