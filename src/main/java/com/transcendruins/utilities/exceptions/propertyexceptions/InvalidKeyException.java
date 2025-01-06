package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>InvalidKeyException</code>: An exception thrown to indicate an invalid JSON field type.
 */
public final class InvalidKeyException extends PropertyException {

    /**
     * Creates a new instance of the <code>InvalidKeyException</code> exception.
     * @param entry <code>TracedEntry&lt;TracedDictionary&gt;</code>: The entry to record.
     * @param key <code>String</code>: The invalid key.
     */
    public InvalidKeyException(TracedEntry<TracedDictionary> entry, String key) {

        super("The key \'" + key + "\' is invalid.", entry, "Invalid Key Exception");
    }
}
