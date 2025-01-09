package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>MissingModuleSetException</code>: An exception thrown to indicate a reference to a missing module set.
 */
public final class MissingModuleSetException extends PropertyException {

    /**
     * Creates a new instance of the <code>MissingModuleSetException</code> exception.
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The missing module set.
     */
    public MissingModuleSetException(TracedEntry<String> entry) {

        super("Module set \'" + entry + "\'' is referenced but could not be found.", entry, "Missing Module Set Exception");
    }
}
