package com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions;

import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Metadata;

/**
 * <code>DependencyException</code>: An exception thrown to indicate a
 * dependency exception.
 */
public class DependencyException extends PropertyException {

    /**
     * Creates a new instance of the <code>PropertyException</code> exception.
     * 
     * @param message   <code>String</code>: The message to record in the log.
     * @param entry     <code>TracedEntry&lt;TracedArray&gt;</code>: The
     *                  <code>dependenciesEntry</code> field of the
     *                  <code>Pack</code> instance which caused the exception.
     * @param errorCode <code>String</code>: The error type to record in the log.
     */
    public DependencyException(String message, TracedEntry<TracedArray> entry, String errorCode) {

        super(message, entry, "Missing Dependency Exception");
    }

    /**
     * Checks the format of a dependency and labels it accordingly.
     * 
     * @param dependency <code>TracedEntry&lt;Metadata&gt;</code>: The dependency to
     *                   check.
     * @return <code>String</code>: The <code>dependency</code> perameter in the
     *         following string representation: <br>
     *         "<code>Dependency namespace:identifier</code>"
     *         <br>
     *         <b>OR</b> <br>
     *         "<code>Dependency namespace:identifier [a, b, c]</code>"
     *         <br>
     *         <b>OR</b> <br>
     *         "<code>Dependency namespace:identifier with minimum version [a, b, c]</code>"
     *         <br>
     *         <b>OR</b> <br>
     *         "<code>Dependency namespace:identifier with version range [a, b, c] to [d, e, f]</code>"
     */
    public static final String dependencyName(TracedEntry<Metadata> dependency) {

        Metadata metadata = dependency.getValue();
        String message = "Dependency " + metadata.getIdentifier();
        if (metadata.hasVersionRange()) {

            if (metadata.getUpperVersionRange() == null) {

                message += " with minimum version " + metadata.getLowerVersionRange().getVersion();
            } else {

                message += " with version range " + metadata.getLowerVersionRange().getVersion() + " to "
                        + metadata.getUpperVersionRange().getVersion();
            }
        }

        return message;
    }
}
