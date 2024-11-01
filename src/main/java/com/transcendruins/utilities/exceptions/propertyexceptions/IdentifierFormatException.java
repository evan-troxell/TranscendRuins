package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>IdentifierFormatException</code>: An exception thrown to indicate an invalid identifier format.
 */
public final class IdentifierFormatException extends PropertyException {

    /**
     * Creates a new instance of the <code>IdentifierFormatException</code> exception.
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The invalid identifier which caused the exception to be thrown.
     */
    public IdentifierFormatException(TracedEntry<String> entry) {

        super(entry + " is in an invalid identifier format.", entry, "Identifier Format Exception");
    }
}
