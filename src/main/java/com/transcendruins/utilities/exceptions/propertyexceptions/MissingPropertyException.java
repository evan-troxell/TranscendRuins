package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>MissingPropertyException</code>: An exception thrown to indicate a missing JSON field.
 */
public final class MissingPropertyException extends PropertyException {

    /**
     * Creates a new instance of the <code>MissingPropertyException</code> exception.
     * @param entry <code>TracedEntry&lt;?&gt;</code>: The field pathway to record.
     */
    public MissingPropertyException(TracedEntry<?> entry) {

        super(propertyName(entry) + " is undefined.", entry, "Missing Property Exception");
    }
}
