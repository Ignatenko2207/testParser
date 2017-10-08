package com.mywork.finance.utils.plasticine;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class Logger {

	private org.slf4j.Logger logger = null;

	public Logger(org.slf4j.Logger logger) {
		this.logger = logger;
	}

	public void trace(String format, Object... args) {
		if (logger.isTraceEnabled()) {
			if (logger != null) {
				logger.trace(String.format(format, args));
			}
		}
	}

	public void debug(String format, Object... args) {
		if (logger.isDebugEnabled()) {
			if (logger != null) {
				logger.debug(String.format(format, args));
			}
		}
	}

	public void info(String format, Object... args) {
		if (logger.isInfoEnabled()) {
			if (logger != null) {
				logger.info(String.format(format, args));
			}
		}
	}

	public void warn(String format, Object... args) {
		if (logger.isWarnEnabled()) {
			if (logger != null) {
				logger.warn(String.format(format, args));
			}
		}
	}

	public void error(String format, Object... args) {
		if (logger.isErrorEnabled()) {
			if (logger != null) {
				logger.error(String.format(format, args));
			}
		}
	}

	public void fatal(String format, Object... args) {
		if (logger.isErrorEnabled()) {
			if (logger != null) {
				logger.error(String.format(format, args));
			}
		}
	}

	public void printStackTrace(Throwable e) {
		if (logger != null) {
			logger.error(String.format("Exception. Reason %s. Stacktrace %s.", e.getMessage(), ExceptionUtils.getStackTrace(e)));
		}
	}
}
