package com.auth.face.faceauth.logger;

import android.util.Log;

public class AndroidLogger implements Logger {

	@Override
	public void debug(String tag, String message) {
		Log.d(tag, message);
	}

	@Override
	public void info(String tag, String message) {
		Log.i(tag, message);
	}

	@Override
	public void warning(String tag, String message) {
		Log.w(tag, message);
	}

	@Override
	public void error(String tag, String message) {
		Log.e(tag, message);
	}

	@Override
	public void error(String tag, String message, Throwable error) {
		Log.e(tag, message, error);
	}

}
