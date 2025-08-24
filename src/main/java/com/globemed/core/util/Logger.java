package com.globemed.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.PrintStream;

/**
 * Simple logger utility that provides basic logging functionality.
 * Acts as a facade for logging operations with fallback to System.out/err.
 */
public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final String className;
    private final PrintStream infoStream;
    private final PrintStream errorStream;

    private Logger(Class<?> clazz) {
        this.className = clazz.getSimpleName();
        this.infoStream = System.out;
        this.errorStream = System.err;
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    public void info(String message, Object... args) {
        log(infoStream, "INFO", formatMessage(message, args));
    }

    public void error(String message, Throwable error) {
        String fullMessage = formatMessage(message) + "\n" + getStackTrace(error);
        log(errorStream, "ERROR", fullMessage);
    }

    private void log(PrintStream stream, String level, String message) {
        stream.println(String.format("%s [%s] %s - %s",
            LocalDateTime.now().format(formatter),
            level,
            className,
            message));
    }

    private String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        String result = message;
        for (Object arg : args) {
            result = result.replaceFirst("\\{\\}", String.valueOf(arg));
        }
        return result;
    }

    private String getStackTrace(Throwable error) {
        if (error == null) return "";
        StringBuilder sb = new StringBuilder();
        error.printStackTrace(new PrintStream(System.err) {
            @Override
            public void println(String x) {
                sb.append(x).append("\n");
            }
        });
        return sb.toString();
    }
}
