package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>MissingIdentifierException</code>: An exception thrown to indicate a missing identifier.
 */
public final class MissingIdentifierException extends PropertyException {

    /**
     * Creates a new instance of the <code>MissingIdentifierException</code> exception.
     * @param entry <code>TracedEntry&lt;Identifier&gt;</code>: The missing identifier which caused the exception to be thrown.
     */
    public MissingIdentifierException(TracedEntry<Identifier> entry) {

        super(entry + " is referenced but cannot be traced.", entry, "Missing Identifier Exception");
    }
}
