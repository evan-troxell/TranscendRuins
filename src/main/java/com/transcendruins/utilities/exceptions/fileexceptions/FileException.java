package com.transcendruins.utilities.exceptions.fileexceptions;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>FileException</code>: A general exception thrown to indicate an invalid file. This is a general exception, and should be inherited upon to improve clarity.
 */
public class FileException extends LoggedException {

    /**
     * Creates a new instance of the <code>FileException</code> exception.
     * @param message <code>String</code>: The message to record in the log.
     * @param errorCode <code>String</code>: The error type to record in the log.
     */
    public FileException(TracedPath path, String message, String errorCode) {

        super(path, message, "File Exception -> " + errorCode);
    }

}
