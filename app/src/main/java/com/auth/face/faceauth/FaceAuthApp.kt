package com.auth.face.faceauth

import android.app.Application
import com.auth.face.faceauth.logger.NotNullMessageLogger
import com.auth.face.faceauth.logger.LogLevelLogger
import com.auth.face.faceauth.logger.LoggerInstance
import com.auth.face.faceauth.logger.FileLogger
import com.auth.face.faceauth.logger.AndroidLogger
import com.auth.face.faceauth.logger.CompositeLogger
import java.io.File


class FaceAuthApp : Application() {

    private lateinit var sPrefs: PrefStorage

    override fun onCreate() {
        super.onCreate()
        app = this
        sPrefs = PrefStorage(this)
        initializeLogger()
    }

    fun getPrefs(): PrefStorage {
        return sPrefs
    }

    private fun initializeLogger() {
        val logFile = File(getExternalFilesDir(null)!!.absolutePath + LOGS_DIR, LOG_FILE_NAME)
        val logBakFile = File(getExternalFilesDir(null)!!.absolutePath + LOGS_DIR, LOG_BACKUP_FILE_NAME)

        val logger = CompositeLogger(AndroidLogger(), FileLogger(logFile, logBakFile))
        LoggerInstance.init(LogLevelLogger(
                NotNullMessageLogger(logger),
                BuildConfig.DEBUG,
                BuildConfig.DEBUG,
                BuildConfig.DEBUG,
                BuildConfig.DEBUG))
    }


    companion object {

        val TAG = "FaceAuth"

        lateinit var app: FaceAuthApp
            private set

        private val LOGS_DIR = "/logs"
        private val LOG_FILE_NAME = "log.txt"
        private val LOG_BACKUP_FILE_NAME = "log_bak.txt"
    }

}
