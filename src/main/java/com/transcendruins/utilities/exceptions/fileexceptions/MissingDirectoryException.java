package com.transcendruins.utilities.exceptions.fileexceptions;

import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>MissingDirectoryException</code>: An exception thrown to indicate that a required directory is missing.
 */
public final class MissingDirectoryException extends FileException {

    /**
     * Creates a new instance of the <code>MissingDirectoryException</code> exception.
     * @param path <code>TracedPath</code>: The missing directory to log.
     */
    public MissingDirectoryException(TracedPath path) {

        super(path, "Required directory \"" + path + "\" is missing.", "Missing Directory Exception");
    }

}
