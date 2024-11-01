package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>DuplicateIdentifierException</code>: An exception thrown to indicate a duplicate object identifier.
 */
public final class DuplicateIdentifierException extends PropertyException {

    /**
     * Creates a new instance of the <code>DuplicateIdentifierException</code> exception.
     * @param entry <code>TracedEntry&lt;Identifier&gt;</code>: The duplicate identifier which caused the exception to be thrown.
     */
    public DuplicateIdentifierException(TracedEntry<Identifier> entry) {

        super("\'" + entry + "\' is a duplicate identifier.", entry, "Duplicate Identifier Exception");
    }
}
