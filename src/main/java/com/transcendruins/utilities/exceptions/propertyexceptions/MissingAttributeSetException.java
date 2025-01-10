package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>MissingAttributeSetException</code>: An exception thrown to indicate a reference to a missing attribute set.
 */
public final class MissingAttributeSetException extends PropertyException {

    /**
     * Creates a new instance of the <code>MissingAttributeSetException</code> exception.
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The missing attribute set.
     */
    public MissingAttributeSetException(TracedEntry<String> entry) {

        super("Attribute set \'" + entry + "\'' is referenced but could not be found.", entry, "Missing Attribute Set Exception");
    }
}
