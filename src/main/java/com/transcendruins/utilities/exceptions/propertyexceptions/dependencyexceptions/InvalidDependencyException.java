package com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions;

import java.util.Collection;
import java.util.HashSet;

import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Metadata;

/**
 * <code>InvalidDependencyException</code>: An exception thrown to indicate an
 * invalid dependency.
 */
public final class InvalidDependencyException extends DependencyException {

    /**
     * Creates a new instance of the <code>InvalidDependencyException</code>
     * exception.
     * 
     * @param message <code>String</code>: The generated error message to save to
     *                the logs file.
     * @param entry   <code>TracedEntry&lt;TracedArray&gt;</code>: The
     *                <code>dependenciesEntry</code> field of the <code>Pack</code>
     *                instance from which this
     *                <code>InvalidDependencyException</code> instance originated.
     */
    private InvalidDependencyException(String message, TracedEntry<TracedArray> entry) {

        super(message, entry, "Invalid Dependency Exception");
    }

    /**
     * Creates a new instance of the <code>InvalidDependencyException</code>
     * exception which represents a set of identical, overlapping dependencies.
     * 
     * @param entry                   <code>TracedEntry&lt;TracedArray&gt;</code>:
     *                                The <code>dependenciesEntry</code> field of
     *                                the <code>Pack</code> instance from which this
     *                                <code>InvalidDependencyException</code>
     *                                instance originated.
     * @param dependency              <code>TracedEntry&lt;Metadata&gt;</code>: The
     *                                overlapping dependency.
     * @param overlappingDependencies <code>HashSet&lt;TracedEntry&lt;Metadata&gt;&gt;</code>:
     *                                The set of all overlapping dependencies.
     * @return <code>InvalidDependencyException</code>: The generated
     *         <code>InvalidDependencyException</code> exception.
     */
    public static InvalidDependencyException overlappingDependencies(TracedEntry<TracedArray> entry,
            TracedEntry<Metadata> dependency, HashSet<TracedEntry<Metadata>> overlappingDependencies) {

        String message = DependencyException.dependencyName(dependency);
        message += " overlaps with " + (overlappingDependencies.size() > 1 ? "dependencies " : "dependency ");
        message += overlappingDependencies + ".";

        return new InvalidDependencyException(message, entry);
    }

    /**
     * Creates a new instance of the <code>InvalidDependencyException</code>
     * exception which represents a dependency that overlaps the its own pack
     * identifier.
     * 
     * @param entry      <code>TracedEntry&lt;TracedArray&gt;</code>: The
     *                   <code>dependenciesEntry</code> field of the
     *                   <code>Pack</code> instance from which this
     *                   <code>InvalidDependencyException</code> instance
     *                   originated.
     * @param dependency <code>TracedEntry&lt;Metadata&gt;</code>: The overlapping
     *                   dependency.
     * @return <code>InvalidDependencyException</code>: The generated
     *         <code>InvalidDependencyException</code> exception.
     */
    public static InvalidDependencyException overlapsPackIdentifier(TracedEntry<TracedArray> entry,
            TracedEntry<Metadata> dependency) {

        String message = DependencyException.dependencyName(dependency);
        message += " contains the same identifier as the parent pack. Note that this exception may have been caused by a sub-dependency.";

        return new InvalidDependencyException(message, entry);
    }

    /**
     * Creates a new instance of the <code>InvalidDependencyException</code>
     * exception which represents two dependencies with referencing the same
     * identifier with incompatible versions.
     * 
     * @param entry                  <code>TracedEntry&lt;TracedArray&gt;</code>:
     *                               The <code>dependenciesEntry</code> field of the
     *                               <code>Pack</code> instance from which the new
     *                               <code>InvalidDependencyException</code>
     *                               instance originated.
     * @param dependency             <code>TracedEntry&lt;Metadata&gt;</code>: The
     *                               first incompatible dependency.
     * @param incompatibleDependency <code>TracedEntry&lt;Metadata&gt;</code>: The
     *                               second incompatible dependency.
     * @return <code>InvalidDependencyException</code>: The generated
     *         <code>InvalidDependencyException</code> exception.
     */
    public static InvalidDependencyException incompatibleDependencyVersions(TracedEntry<TracedArray> entry,
            TracedEntry<Metadata> dependency, TracedEntry<Metadata> incompatibleDependency) {

        String message = DependencyException.dependencyName(dependency);
        message += " is incompatible with the version"
                + ((incompatibleDependency.getValue().hasVersionRange() ? "s" : "")) + " of dependency "
                + incompatibleDependency + ". Note that this exception may have been caused by a sub-dependency.";

        return new InvalidDependencyException(message, entry);
    }

    /**
     * Creates a new instance of the <code>InvalidDependencyException</code>
     * exception which represent a dependency whose set of allowed packs diverge
     * (contain different dependencies).
     * 
     * @param entry      <code>TracedEntry&lt;TracedArray&gt;</code>: The
     *                   <code>dependenciesEntry</code> field of the
     *                   <code>Pack</code> instance from which the new
     *                   <code>InvalidDependencyException</code> instance
     *                   originated.
     * @param dependency <code>TracedEntry&lt;Metadata&gt;</code>: The dependency
     *                   referencing multiple diverging dependencies.
     * @return <code>InvalidDependencyException</code>: The generated
     *         <code>InvalidDependencyException</code> exception.
     */
    public static InvalidDependencyException divergingDependencies(TracedEntry<TracedArray> entry,
            TracedEntry<Metadata> dependency) {

        String message = dependencyName(dependency);
        message += " references multiple pack versions which require different dependencies. Note that this exception may have been caused by a sub-dependency.";

        return new InvalidDependencyException(message, entry);
    }

    /**
     * Creates a new instance of the <code>InvalidDependencyException</code>
     * exception which represent two dependencies whose set of allowed packs diverge
     * (contain different dependencies).
     * 
     * @param entry                  <code>TracedEntry&lt;TracedArray&gt;</code>:
     *                               The <code>dependenciesEntry</code> field of the
     *                               <code>Pack</code> instance from which the new
     *                               <code>InvalidDependencyException</code>
     *                               instance originated.
     * @param dependency             <code>TracedEntry&lt;Metadata&gt;</code>: The
     *                               first diverging dependency.
     * @param incompatibleDependency <code>TracedEntry&lt;Metadata&gt;</code>: The
     *                               second diverging dependency.
     * @return <code>InvalidDependencyException</code>: The generated
     *         <code>InvalidDependencyException</code> exception.
     */
    public static InvalidDependencyException divergingDependencies(TracedEntry<TracedArray> entry,
            TracedEntry<Metadata> dependency, TracedEntry<Metadata> incompatibleDependency) {

        String message = dependencyName(dependency);

        message += " references one or more pack versions which require different dependencies from those of dependency "
                + incompatibleDependency + ". Note that this exception may have been caused by a sub-dependency.";

        return new InvalidDependencyException(message, entry);
    }

    public static InvalidDependencyException missingDependencies(TracedEntry<TracedArray> entry,
            TracedEntry<Metadata> dependency, Collection<TracedEntry<Metadata>> dependencies) {

        String message = dependencyName(dependency);
        message += " is missing the following required " + (dependencies.size() > 1 ? "dependencies " : "dependency ");
        message += dependencies + ". Note that this exception may have been caused by a sub-dependency.";

        return new InvalidDependencyException(message, entry);
    }
}
