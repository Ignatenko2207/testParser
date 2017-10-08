package com.mywork.finance.utils.plasticine;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.net.URL;
import java.util.logging.LogManager;

public class LogbackConfigureLoader {

	/**
	 * Loads logback configuration from the {@code fileUrl} path. Not resets previous logback configuration.
	 * 
	 * @param fileUrl
	 */
	public static void configure(URL fileUrl) {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			context.reset();
			configurator.doConfigure(fileUrl);
		} catch (JoranException ex) {
			ex.printStackTrace();
		}

		StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}

	public static void initializeLogging(ClasspathResourceManager resourceManager, String filename, String julPropertiesFile) {
		FileInputStream loggingFilePropertiesInputStream = null;
		try {
			LogbackConfigureLoader.configure(resourceManager.getFileUrl(filename));
			loggingFilePropertiesInputStream = (FileInputStream) resourceManager.getInputStream(julPropertiesFile);
			LogManager.getLogManager().readConfiguration(loggingFilePropertiesInputStream);
		} catch (Exception ex) {
			System.err.format("Can not initialize logging framework. Reason %s. Stacktrace: %s.:", ex.getMessage(), ex.getStackTrace());
		} finally {
			IOUtils.closeQuietly(loggingFilePropertiesInputStream);
		}
		// Redirect System.out & System.err for JFX runtime to the logger.
		System.setErr(LoggerUtil.getRedirectedToLoggerErrPrintStream(System.err));
		System.setOut(LoggerUtil.getRedirectedToLoggerOutPrintStream(System.out));
	}
}
