package ai.langframework.langdatacore;

import org.slf4j.LoggerFactory;

public class Logger {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    public static void info(Object message) {
        if (message != null) {
            logger.info(message.toString());
        } else {
            logger.info("null");
        }
    }

    public static void error(Object message) {
        if (message != null) {
            logger.error(message.toString());
        } else {
            logger.error("null");
        }
    }
    public static void warn(Object message) {
        if (message != null) {
            logger.warn(message.toString());
        } else {
            logger.warn("null");
        }
    }
}