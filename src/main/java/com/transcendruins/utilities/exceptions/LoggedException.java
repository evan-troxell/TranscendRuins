package com.transcendruins.utilities.exceptions;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.transcendruins.settings.CacheOperator;
import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>LoggedException</code>: A general exception which logs a message and a filepath to a predetermined directory when thrown. This is a general exception, and should be inherited upon to improve clarity.
 */
public class LoggedException extends Exception {

    /**
     * <code>String</code>: The absolute directory of the log cache.
     */
    public static final TracedPath LOGS_DIRECTORY = CacheOperator.getCacheDirectory().extend("logs");

    /**
     * <code>TracedPath</code>: The filepath to the root of this exception.
     */
    private final TracedPath path;

    /**
     * <code>String</code>: The error message to be displayed.
     */
    private final String message;

    /**
     * <code>String</code>: The error code to be displayed.
     */
    private final String errorCode;

    /**
     * Creates a new instance of the <code>LoggedException</code> exception.
     * @param path <code>TracedPath</code>: The path to the exception.
     * @param message <code>String</code>: The message to record in the log.
     * @param errorCode <code>String</code>: The error type to record in the log.
     */
    public LoggedException(TracedPath path, String message, String errorCode) {

        this.path = path;
        this.message = message;
        this.errorCode = errorCode;
    }

    /**
     * Logs this <code>LoggedException</code> instance to the logs directory.
     */
    public final void logException() {

        Date date = new Date(System.currentTimeMillis());

        String timeString = new SimpleDateFormat("HH:mm:ss").format(date);
        String errorMessage = "[" + errorCode + "] >>> ";
        errorMessage += path;

        errorMessage += " | " + timeString + " | ";
        errorMessage += message;

        String dateString = new SimpleDateFormat("MM-dd-yyyy").format(date);
        saveErrorMessage(errorMessage, "Log " + dateString + ".log");
    }

    /**
     * Records an error message to the logs directory.
     * @param message <code>String</code>: The message to record to the file.
     * @param name <code>String</code>: The name of the log file, specifically in the format of "Log MMMM dd, yyyy + [MESSAGE]".
     */
    private static void saveErrorMessage(String message, String name) {

        TracedPath path = LOGS_DIRECTORY.extend(name);
        String logs = path.exists() ? path.retrieve() + "\n\n\n" + message : message;
        try {

            if (!LOGS_DIRECTORY.exists()) LOGS_DIRECTORY.createFile(true);
            path.writeTo(logs);
            System.out.println(message);
        } catch (IOException e) {

            System.out.println(e);
            System.out.println("INTERNAL EXCEPTION: \"" + message + "\" COULD NOT BE LOGGED");
        }

        System.out.println("\n\n");
    }
}
