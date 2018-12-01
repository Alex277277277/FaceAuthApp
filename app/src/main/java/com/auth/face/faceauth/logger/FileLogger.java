package com.auth.face.faceauth.logger;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class FileLogger implements Logger {

    private static final String LOG_LEVEL_DEBUG = "ERROR";
    private static final String LOG_LEVEL_INFO = "INFO";
    private static final String LOG_LEVEL_WARNING = "WARNING";
    private static final String LOG_LEVEL_ERROR = "ERROR";
    private static final long FILE_SIZE_LIMIT_TO_BACKUP_IN_BYTES = 1048576;

    private final Subject<String> message = PublishSubject.<String>create().toSerialized();

    public FileLogger(File logFile, File backupFile) {
        Scheduler logScheduler = Schedulers.from(Executors.newSingleThreadExecutor());
        message
                .buffer(4, TimeUnit.SECONDS)
                .filter(messages -> !messages.isEmpty())
                .observeOn(logScheduler)
                .retry()
                .subscribe(messages -> {
                    PrintStream stream = null;
                    try {
                        stream = createPrintStream(logFile);
                        for (String message : messages) {
                            stream.println(message);
                        }
                        if (needBackup(logFile)) {
                            closeStream(stream);
                            backupAndDeleteFile(logFile, backupFile);
                        }
                    } catch (Exception e) {
                        Log.e(getClass().getSimpleName(), "Error writing message", e);
                    } finally {
                        closeStream(stream);
                    }
                });
    }

    @Override
    public void debug(String tag, String message) {
        pushMessage(LOG_LEVEL_DEBUG, tag, message);
    }

    @Override
    public void info(String tag, String message) {
        pushMessage(LOG_LEVEL_INFO, tag, message);
    }

    @Override
    public void warning(String tag, String message) {
        pushMessage(LOG_LEVEL_WARNING, tag, message);
    }

    @Override
    public void error(String tag, String message) {
        pushMessage(LOG_LEVEL_ERROR, tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable error) {
        pushMessage(LOG_LEVEL_ERROR, tag, message + " (" + error + ")");
    }

    private PrintStream createPrintStream(File file) throws IOException {
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                file.createNewFile();
                if (!file.exists()) {
                    throw new IOException("File is not created");
                }
            }
            return new PrintStream(new FileOutputStream(file, true), true);
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Failed to init", e);
            throw e;
        }
    }

    private boolean needBackup(File logFile) {
        return logFile.length() > FILE_SIZE_LIMIT_TO_BACKUP_IN_BYTES;
    }

    private void backupAndDeleteFile(File originalFile, File backupFile) {
        if (backupFile.exists()) {
            backupFile.delete();
        }
        originalFile.renameTo(backupFile);
    }

    private void closeStream(@Nullable Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Error closing Closeable", e);
        }
    }

    private void pushMessage(String logLevel, String tag, String message) {
        this.message.onNext(formatMessage(logLevel, tag, message));
    }

    private String formatMessage(String logLevel, String tag, String message) {
        return time() + " -- " + logLevel + " - " + tag + " - " + message;
    }

    private String time() {
        return "[" + new Timestamp(System.currentTimeMillis()) + "]";
    }

}
