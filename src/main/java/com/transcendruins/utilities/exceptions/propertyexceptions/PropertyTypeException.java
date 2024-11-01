package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>PropertyTypeException</code>: An exception thrown to indicate an invalid JSON property type.
 */
public final class PropertyTypeException extends PropertyException {

    /**
     * Creates a new instance of the <code>PropertyTypeException</code> exception.
     * @param entry <code>TracedEntry&lt;?&gt;</code>: The entry to record.
     */
    public PropertyTypeException(TracedEntry<?> entry) {

        super(propertyName(entry) + " is of the invalid type " + (entry.getValue() == null ? "null" : entry.getValue().getClass().getSimpleName()) + ".", entry, "Property Type Exception");
    }
}
