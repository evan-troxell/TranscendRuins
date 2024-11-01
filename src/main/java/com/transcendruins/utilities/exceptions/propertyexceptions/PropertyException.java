package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>PropertyException</code>: A general exception thrown to indicate an error concerning a JSON property. This is a general exception, and should be inherited upon to improve clarity.
 */
public class PropertyException extends LoggedException {

    /**
     * Creates a new instance of the <code>PropertyException</code> exception.
     * @param message <code>String</code>: The message to record in the log.
     * @param entry <code>TracedEntry&lt;?&gt;</code>: The property pathway to record.
     * @param errorCode <code>String</code>: The error type to record in the log.
     */
    public PropertyException(String message, TracedEntry<?> entry, String errorCode) {

        super(entry.getPathway().path, "JSON parsing exception at property [" + entry.getPathway() + " -> " + entry.getPathway().key + "] : " + message, "Property Exception -> " + errorCode);
    }

    /**
     * Checks whether or not a property is an index or a string, and names it accordingly.
     * @param entry <code>TracedEntry&lt;?&gt;</code>: The property pathway to check.
     * @return <code>String</code>: The resulting property string.
     */
    public static final String propertyName(TracedEntry<?> entry) {

        if (entry.getPathway().key == null) {

            return "[PROPERTY UNLISTED]";
        }
        return (entry.getPathway().key instanceof Number ? "Index " + entry.getPathway().key : "Property \"" + entry.getPathway().key + "\"");
    }
}
