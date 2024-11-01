package com.transcendruins.utilities.exceptions.fileexceptions;

import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>MissingFileException</code>: An exception thrown to indicate that a required file is missing.
 */
public final class MissingFileException extends FileException {

    /**
     * Creates a new instance of the <code>MissingFileException</code> exception.
     * @param path <code>TracedPath</code>: The missing filepath to log.
     */
    public MissingFileException(TracedPath path) {

        super(path, "Required file \"" + path + "\" is missing.", "Missing File Exception");
    }

}
