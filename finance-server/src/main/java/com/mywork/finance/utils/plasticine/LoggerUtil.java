package com.mywork.finance.utils.plasticine;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LoggerUtil {

    // protected static Logger logger = Logger.getLogger("Logger");

    protected static Map<Class, Logger> loggerInstances = new HashMap<Class, Logger>();

    protected static Logger nullLogger = new Logger(null);

    private static org.slf4j.Logger outLogger = LoggerFactory.getLogger("SystemOut");
    private static org.slf4j.Logger errLogger = LoggerFactory.getLogger("SystemErr");

    protected static boolean disable = false;

    /**
     * Default static constructor.
     */
    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        // Redirect default runtime System.out & System.err output according to
        // the log4j rules
//		System.setOut(getRedirectedToLoggerOutPrintStream(System.out));
//		System.setErr(getRedirectedToLoggerErrPrintStream(System.err));
    }

    /**
     * Initializes redirecting binding for JUL (java.util.logging).
     */
    public static void initialize() {
    }

    /**
     * Returns stream written to the sysout and syserr loggers.
     *
     * @param realPrintStream
     * @param logger
     * @param loggingLevel
     * @return
     */
    private static PrintStream createLoggingProxy(final PrintStream realPrintStream, final org.slf4j.Logger logger, Level loggingLevel) {
        if (loggingLevel.equals(Level.INFO)) {
            return new PrintStream(realPrintStream) {
                public void print(final String string) {
                    realPrintStream.print(string);
                    logger.info(string);
                }
            };
        } else if (loggingLevel.equals(Level.WARNING)) {
            return new PrintStream(realPrintStream) {
                public void print(final String string) {
                    realPrintStream.print(string);
                    logger.warn(string);
                }
            };
        }
        LoggerUtil.getLogger(LoggerUtil.class).warn("Couldn't initialize proxy stream between logger %s and system out.", logger.getName());
        return null;
    }

    /**
     * Returns printStream binded with SystemErr logger.
     *
     * @param platformSystemErrStream
     * @return
     */
    public static PrintStream getRedirectedToLoggerErrPrintStream(PrintStream platformSystemErrStream) {
        PrintStream errStream;
        if (null != (errStream = createLoggingProxy(platformSystemErrStream, errLogger, Level.WARNING))) {
            return errStream;
        }
        LoggerUtil.getLogger(LoggerUtil.class).warn("Couldn't redirect err stream %s to the logger.", platformSystemErrStream);
        // Return non-modified err stream.
        return platformSystemErrStream;
    }

    /**
     * Returns printStream binded with SystemOut logger.
     *
     * @param platformSystemOutStream
     * @return
     */
    public static PrintStream getRedirectedToLoggerOutPrintStream(PrintStream platformSystemOutStream) {
        PrintStream outStream;
        if (null != (outStream = createLoggingProxy(platformSystemOutStream, outLogger, Level.INFO))) {
            return outStream;
        }
        LoggerUtil.getLogger(LoggerUtil.class).warn("Couldn't redirect out stream %s to the logger.", platformSystemOutStream);
        // Return non-modified out stream.
        return platformSystemOutStream;
    }

    public static Logger getLogger(Class clazz) {
        if (disable) {
            return nullLogger;
        }
        Logger logger = LoggerUtil.loggerInstances.get(clazz);
        if (null == logger) {
            logger = new Logger(LoggerFactory.getLogger(clazz));
            loggerInstances.put(clazz, logger);
        }
        return logger;
    }

    public static void disable() {
        disable = true;
    }
}
