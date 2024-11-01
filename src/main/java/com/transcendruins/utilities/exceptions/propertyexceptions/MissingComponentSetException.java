package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>MissingComponentSetException</code>: An exception thrown to indicate a reference to a missing component set.
 */
public final class MissingComponentSetException extends PropertyException {

    /**
     * Creates a new instance of the <code>MissingComponentSetException</code> exception.
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The missing component set.
     */
    public MissingComponentSetException(TracedEntry<String> entry) {

        super("Component set \'" + entry + "\'' is referenced but could not be found.", entry, "Missing Component Set Exception");
    }
}
