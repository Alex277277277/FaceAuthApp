package com.auth.face.faceauth.logger;

public class LogLevelLogger implements Logger {

	private final Logger logger;
	private final boolean debugAllowed;
	private final boolean infoAllowed;
	private final boolean warningAllowed;
	private final boolean errorAllowed;

	public LogLevelLogger(Logger logger,
						  boolean debugAllowed,
						  boolean infoAllowed,
						  boolean warningAllowed,
						  boolean errorAllowed) {
		this.logger = logger;
		this.debugAllowed = debugAllowed;
		this.infoAllowed = infoAllowed;
		this.warningAllowed = warningAllowed;
		this.errorAllowed = errorAllowed;
	}

	@Override
	public void debug(String tag, String message) {
		if (debugAllowed) {
			logger.debug(tag, message);
		}
	}

	@Override
	public void info(String tag, String message) {
		if (infoAllowed) {
			logger.info(tag, message);
		}
	}

	@Override
	public void warning(String tag, String message) {
		if (warningAllowed) {
			logger.warning(tag, message);
		}
	}

	@Override
	public void error(String tag, String message) {
		if (errorAllowed) {
			logger.error(tag, message);
		}
	}

	@Override
	public void error(String tag, String message, Throwable error) {
		if (errorAllowed) {
			logger.error(tag, message, error);
		}
	}

}
