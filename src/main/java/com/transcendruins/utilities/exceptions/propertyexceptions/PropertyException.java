package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>PropertyException</code>: A general exception thrown to indicate an
 * error concerning a JSON field This is a general exception, and should be
 * inherited upon to improve clarity.
 */
public class PropertyException extends LoggedException {

    /**
     * Creates a new instance of the <code>PropertyException</code> exception.
     * 
     * @param message   <code>String</code>: The message to record in the log.
     * @param entry     <code>TracedEntry&lt;?&gt;</code>: The field pathway to
     *                  record.
     * @param errorCode <code>String</code>: The error type to record in the log.
     */
    public PropertyException(String message, TracedEntry<?> entry, String errorCode) {

        super(entry.getPathway().getPath(), "JSON parsing exception at field [" + entry.getPathway() + " -> "
                + entry.getPathway().getKey() + "] : " + message, "Property Exception -> " + errorCode);
    }

    /**
     * Checks whether or not a field is an index or a string, and names it
     * accordingly.
     * 
     * @param entry <code>TracedEntry&lt;?&gt;</code>: The field pathway to check.
     * @return <code>String</code>: The resulting field string.
     */
    public static final String propertyName(TracedEntry<?> entry) {

        Object key = entry.getPathway().getKey();

        if (key == null) {

            return "[PROPERTY UNLISTED]";
        }
        return (key instanceof Number ? "Index " + key : "Property \"" + key + "\"");
    }
}
