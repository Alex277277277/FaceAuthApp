package com.auth.face.faceauth.logger;

import android.support.annotation.Nullable;

public final class LoggerInstance implements Logger {

    private static final LoggerInstance INSTANCE = new LoggerInstance();

    @Nullable
    private static Logger LOGGER;

    public static Logger get() {
        return INSTANCE;
    }

    public static void init(Logger logger) {
        LOGGER = logger;
    }

    private LoggerInstance() {
    }

    @Override
    public void debug(String tag, String message) {
        if (LOGGER != null) {
            LOGGER.debug(tag, message);
        }
    }

    @Override
    public void info(String tag, String message) {
        if (LOGGER != null) {
            LOGGER.info(tag, message);
        }
    }

    @Override
    public void warning(String tag, String message) {
        if (LOGGER != null) {
            LOGGER.warning(tag, message);
        }
    }

    @Override
    public void error(String tag, String message) {
        if (LOGGER != null) {
            LOGGER.error(tag, message);
        }
    }

    @Override
    public void error(String tag, String message, Throwable error) {
        if (LOGGER != null) {
            LOGGER.error(tag, message, error);
        }
    }

}
