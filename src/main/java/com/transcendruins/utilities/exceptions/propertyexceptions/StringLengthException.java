package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>StringLengthException</code>: An exception thrown to indicate a <code>String</code> has an invalid length.
 */
public final class StringLengthException extends PropertyException {

    /**
     * Creates a new instance of the <code>StringLengthException</code> exception.
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The invalid string.
     */
    public StringLengthException(TracedEntry<String> entry) {

        super(propertyName(entry) + " is the invalid length " + entry.getValue().length() + ".", entry, "String Length Exception");
    }
}
