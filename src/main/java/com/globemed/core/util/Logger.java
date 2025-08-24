package com.globemed.core.util;

import org.slf4j.LoggerFactory;

/**
 * Logger utility class that provides a wrapper around SLF4J/Logback logging.
 * Ensures consistent logging across the application with proper separation of log levels.
 */
public class Logger {
    private final org.slf4j.Logger logger;

    private Logger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Get a logger instance for the specified class.
     *
     * @param clazz The class to get the logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    /**
     * Log a debug message.
     *
     * @param message The message to log
     */
    public void debug(String message) {
        logger.debug(message);
    }

    /**
     * Log a debug message with parameters.
     *
     * @param message The message to log with {} placeholders
     * @param args The arguments to replace placeholders
     */
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    /**
     * Log an info message.
     *
     * @param message The message to log
     */
    public void info(String message) {
        logger.info(message);
    }

    /**
     * Log an info message with parameters.
     *
     * @param message The message to log with {} placeholders
     * @param args The arguments to replace placeholders
     */
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    /**
     * Log a warning message.
     *
     * @param message The message to log
     */
    public void warn(String message) {
        logger.warn(message);
    }

    /**
     * Log a warning message with parameters.
     *
     * @param message The message to log with {} placeholders
     * @param args The arguments to replace placeholders
     */
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    /**
     * Log a warning message with exception.
     *
     * @param message The message to log
     * @param e The exception to log
     */
    public void warn(String message, Throwable e) {
        logger.warn(message, e);
    }

    /**
     * Log an error message.
     *
     * @param message The message to log
     */
    public void error(String message) {
        logger.error(message);
    }

    /**
     * Log an error message with parameters.
     *
     * @param message The message to log with {} placeholders
     * @param args The arguments to replace placeholders
     */
    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    /**
     * Log an error message with exception.
     *
     * @param message The message to log
     * @param e The exception to log
     */
    public void error(String message, Throwable e) {
        logger.error(message, e);
    }

    /**
     * Check if debug logging is enabled.
     *
     * @return true if debug is enabled
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }
}
