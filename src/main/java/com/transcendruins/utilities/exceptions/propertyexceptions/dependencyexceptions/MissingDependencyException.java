package com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions;

import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Metadata;

/**
 * <code>MissingDependencyException</code>: An exception thrown to indicate a missing dependency.
 */
public final class MissingDependencyException extends DependencyException {

    /**
     * Creates a new instance of the <code>MissingDependencyException</code> exception.
     * @param entry <code>TracedEntry&lt;TracedArray&gt;</code>: The <code>dependenciesEntry</code> field of the <code>Pack</code> instance from which the new <code>InvalidDependencyException</code> instance originated.
     * @param dependency <code>TracedEntry&lt;Metadata&gt;</code>: The missing dependency which caused the exception.
     */
    public MissingDependencyException(TracedEntry<TracedArray> entry, TracedEntry<Metadata> dependency) {

        super(DependencyException.dependencyName(dependency) + " is referenced but cannot be traced.", entry, "Missing Dependency Exception");
    }
}
