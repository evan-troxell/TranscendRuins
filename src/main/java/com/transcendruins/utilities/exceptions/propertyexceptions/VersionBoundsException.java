package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>VersionBoundsException</code>: An exception thrown to indicate an
 * invalid version bounds.
 */
public final class VersionBoundsException extends PropertyException {

    /**
     * Creates a new instance of the <code>VersionBoundsException</code> exception.
     * 
     * @param message <code>String</code>: The generated error message to save to
     *                the logs file.
     * @param entry   <code>TracedEntry&lt;?&gt;</code>: The field pathway to
     *                record.
     */
    private VersionBoundsException(String message, TracedEntry<?> entry) {

        super(message, entry, "Version Bounds Exception");
    }

    /**
     * Creates a new instance of the <code>VersionBoundsException</code> exception
     * which represents a set of version bounds whose maximum bounds is less than
     * its minimum bounds..
     * 
     * @param maxVersion <code>TracedEntry&lt;?&gt;</code>: The
     *                   <code>Identifier</code> instance representing the maximum
     *                   version in the version bounds.
     * @param minVersion <code>TracedEntry&lt;?&gt;</code>: The
     *                   <code>Identifier</code> instance representing the minimum
     *                   version in the version bounds.
     * @return <code>VersionBoundsException</code>: The generated
     *         <code>VersionBoundsException</code> exception.
     */
    public static VersionBoundsException inverseMaxAndMinBounds(TracedEntry<Identifier> maxVersion,
            TracedEntry<Identifier> minVersion) {

        return new VersionBoundsException("Maximum version " + maxVersion.getValue().getVersion()
                + " may not be lower than minimum version " + minVersion.getValue().getVersion() + ".", maxVersion);
    }
}
