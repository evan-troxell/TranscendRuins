package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>ArrayLengthException</code>: An exception thrown to indicate an array has an invalid length.
 */
public final class ArrayLengthException extends PropertyException {

    /**
     * Creates a new instance of the <code>ArrayLengthException</code> exception.
     * @param entry <code>TracedEntry&lt;TracedArray&gt;</code>: The array of invalid length to record.
     */
    public ArrayLengthException(TracedEntry<TracedArray> entry) {

        super(propertyName(entry) + " is the invalid length " + entry.getValue().size() + ".", entry, "Array Length Exception");
    }
}
