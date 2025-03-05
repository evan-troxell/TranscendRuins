/* Copyright 2025 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins.utilities.exceptions;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.transcendruins.save.CacheOperator;
import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>LoggedException</code>: A general exception which logs a message and a
 * filepath to a predetermined directory when thrown. This is a general
 * exception, and should be inherited upon to improve clarity.
 */
public class LoggedException extends Exception {

    /**
     * <code>String</code>: The absolute directory of the log cache.
     */
    public static final TracedPath LOGS_DIRECTORY = CacheOperator.CACHE_DIRECTORY.extend("logs");

    private final static TracedPath LOG_PATH;

    static {

        Date date = new Date(System.currentTimeMillis());

        LOG_PATH = LOGS_DIRECTORY.extend("Log %s.log".formatted(new SimpleDateFormat("MM-dd-yyyy").format(date)));
        String logs = (LOG_PATH.exists() ? LOG_PATH.retrieve() + "\n\n\n" : "") + """
                ----------------------------------------
                         Log %s
                ----------------------------------------

                """.formatted(new SimpleDateFormat("HH:mm:ss").format(date));
        try {

            if (!LOGS_DIRECTORY.exists()) {

                LOGS_DIRECTORY.createFile(true);
            }

            LOG_PATH.writeTo(logs);
        } catch (IOException e) {

            System.out.println(e);
        }
    }

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
     * <code>Date</code>: The time at which this exception occurred.
     */
    private final Date time;

    /**
     * Creates a new instance of the <code>LoggedException</code> exception.
     * 
     * @param path      <code>TracedPath</code>: The path to the exception.
     * @param message   <code>String</code>: The message to record in the log.
     * @param errorCode <code>String</code>: The error type to record in the log.
     */
    public LoggedException(TracedPath path, String message, String errorCode) {

        this.path = path;
        this.message = message;
        this.errorCode = errorCode;
        time = new Date(System.currentTimeMillis());
    }

    public final void print() {

        String timeString = new SimpleDateFormat("HH:mm:ss").format(time);
        String errorMessage = "[" + errorCode + "] >>> ";
        errorMessage += path;

        errorMessage += " | " + timeString + " | ";
        errorMessage += message;

        write(errorMessage);
    }

    /**
     * Records an error message to the logs directory.
     * 
     * @param message <code>String</code>: The message to record to the file.
     */
    public static void write(String message) {

        String logs = LOG_PATH.retrieve() + "\n\n\n" + message;
        try {

            if (!LOGS_DIRECTORY.exists())
                LOGS_DIRECTORY.createFile(true);
            LOG_PATH.writeTo(logs);
            System.out.println(message);
        } catch (IOException e) {

            System.out.println(e);
            System.out.println("INTERNAL EXCEPTION: \"" + message + "\" COULD NOT BE LOGGED");
        }

        System.out.println("\n\n");
    }
}
