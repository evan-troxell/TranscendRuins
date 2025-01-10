package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>UnexpectedValueException</code>: An exception thrown to indicate a retrieved entry has an unexpected value.
 */
public final class UnexpectedValueException extends PropertyException {

    /**
     * Creates a new instance of the <code>UnexpectedValueException</code> exception.
     * @param entry <code>TracedEntry&lt;?&gt;</code>: The unexpected entry.
     */
    public UnexpectedValueException(TracedEntry<?> entry) {

        super(propertyName(entry) + " is the unexpected value " + entry + ".", entry, "Unexpected Value Exception");
    }
}
