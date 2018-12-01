package com.auth.face.faceauth.logger;

import java.util.Arrays;
import java.util.List;

public class CompositeLogger implements Logger {

	private final List<Logger> loggers;

	public CompositeLogger(Logger... loggers) {
		this.loggers = Arrays.asList(loggers);
	}

	@Override
	public void debug(String tag, String message) {
		for (Logger logger : loggers) {
			logger.debug(tag, message);
		}
	}

	@Override
	public void info(String tag, String message) {
		for (Logger logger : loggers) {
			logger.info(tag, message);
		}
	}

	@Override
	public void warning(String tag, String message) {
		for (Logger logger : loggers) {
			logger.warning(tag, message);
		}
	}

	@Override
	public void error(String tag, String message) {
		for (Logger logger : loggers) {
			logger.error(tag, message);
		}
	}

	@Override
	public void error(String tag, String message, Throwable error) {
		for (Logger logger : loggers) {
			logger.error(tag, message, error);
		}
	}

}
