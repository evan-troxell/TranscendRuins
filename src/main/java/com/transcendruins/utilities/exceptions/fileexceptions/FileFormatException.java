package com.transcendruins.utilities.exceptions.fileexceptions;

import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>FileFormatException</code>: An exception thrown to indicate that a file has an invalid format.
 */
public final class FileFormatException extends FileException {

    /**
     * Creates a new instance of the <code>FileFormatException</code> exception.
     * @param path <code>TracedPath</code>: The invalid filepath to log.
     */
    public FileFormatException(TracedPath path) {

        super(path, "File \"" + path + "\" is in an invalid format.", "File Format Exception");
    }

}
