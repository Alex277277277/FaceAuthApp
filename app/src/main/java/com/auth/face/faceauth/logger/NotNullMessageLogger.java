package com.auth.face.faceauth.logger;

public class NotNullMessageLogger implements Logger {

	private final Logger logger;

	public NotNullMessageLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void debug(String tag, String message) {
		if (message != null) {
			logger.debug(tag, message);
		}
	}

	@Override
	public void info(String tag, String message) {
		if (message != null) {
			logger.info(tag, message);
		}
	}

	@Override
	public void warning(String tag, String message) {
		if (message != null) {
			logger.warning(tag, message);
		}
	}

	@Override
	public void error(String tag, String message) {
		if (message != null) {
			logger.error(tag, message);
		}
	}

	@Override
	public void error(String tag, String message, Throwable error) {
		if (message != null) {
			if (error != null) {
				logger.error(tag, message, error);
			} else {
				logger.error(tag, message);
			}
		}
	}

}
