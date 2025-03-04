/* Copyright 2025 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions;

import com.transcendruins.contentmodules.packs.PackDependency;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>DependencyException</code>: An exception thrown to indicate an
 * invalid dependency.
 */
public final class DependencyException extends PropertyException {

    /**
     * Creates a new instance of the <code>DependencyException</code>
     * exception.
     * 
     * @param message   <code>String</code>: The generated error message to save to
     *                  the logs file.
     * @param entry     <code>TracedEntry&lt;TracedDictionary&gt;</code>: The
     *                  dependency entry to log.
     * @param errorCode <code>String</code>: The error code to log.
     */
    private DependencyException(String message, TracedEntry<TracedDictionary> entry, String errorCode) {

        super(message, entry, errorCode);
    }

    /**
     * Creates a new instance of the <code>DependencyException</code> exception
     * which represents a dependency which may reference the identifier of the
     * parent pack.
     * 
     * @param pack       <code>Identifier</code>: The identifier of the parent pack.
     * @param dependency <code>PackDependency</code>: The
     *                   ambiguous dependency.
     * @return <code>DependencyException</code>: The generated
     *         <code>DependencyException</code> exception.
     */
    public static DependencyException ambiguousDependency(Identifier pack, PackDependency dependency) {

        String message = "Dependency %s may create an ambiguous reference to the parent pack %s."
                .formatted(dependencyName(dependency), pack);

        return new DependencyException(message, dependency.getEntry(), "Ambiguous Dependency Exception");
    }

    /**
     * Creates a new instance of the <code>DependencyException</code> exception
     * which represents an unresolved dependency.
     * 
     * @param dependency <code>PackDependency</code>: The
     *                   unresolved dependency.
     * @return <code>DependencyException</code>: The generated
     *         <code>DependencyException</code> exception.
     */
    public static DependencyException unresolvedDependency(PackDependency dependency) {

        String message = "Dependency %s could not be resolved.".formatted(dependencyName(dependency));

        return new DependencyException(message, dependency.getEntry(), "Unresolved Dependency Exception");
    }

    /**
     * Creates a new instance of the <code>DependencyException</code> exception
     * which represents a duplicate dependency definition in a pack.
     * 
     * @param pack       <code>Identifier</code>: The identifier of the parent pack.
     * @param dependency <code>PackDependency</code>: The
     *                   duplicate dependency.
     * @return <code>DependencyException</code>: The generated
     *         <code>DependencyException</code> exception.
     */
    public static DependencyException duplicateDependency(Identifier pack, PackDependency dependency) {

        String message = "Pack %s declares multiple dependencies which reference the same pack."
                .formatted(pack, dependencyName(dependency), dependency.getIdentifier().toGeneric());

        return new DependencyException(message, dependency.getEntry(), "Duplicate Dependency Exception");
    }

    /**
     * Creates a new instance of the <code>DependencyException</code> exception
     * which represents a subdependency (dependency of a pack referenced by a parent
     * dependency) which overlaps with the parent pack.
     * 
     * @param pack          <code>Identifier</code>: The identifier of the parent
     *                      pack.
     * @param dependency    <code>PackDependency</code>: The root of the dependency
     *                      path where the invalid subdependency was found.
     * @param subdependency <code>PackDependency</code>: The dependency which
     *                      referenced an invalid parent pack version.
     * @return <code>DependencyException</code>: The generated
     *         <code>DependencyException</code> exception.
     */
    public static DependencyException versionHierarchyException(Identifier pack, PackDependency dependency,
            PackDependency subdependency) {

        String message = "Dependency %s references subdependency %s which exceeds the version of the parent pack %s."
                .formatted(dependencyName(dependency), dependencyName(subdependency), pack);

        return new DependencyException(message, dependency.getEntry(), "Version Hierarchy Exception");
    }

    /**
     * Checks the format of a dependency and labels it accordingly.
     * 
     * @param dependency <code>PackDependency</code>: The
     *                   dependency to
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
    public static final String dependencyName(PackDependency dependency) {

        String message = dependency.getIdentifier().toString();
        if (dependency.getHasVersionRange()) {

            if (dependency.getUpperVersionRange() == null) {

                message += " with minimum version " + dependency.getLowerVersionRange();
            } else {

                message += " with version range " + dependency.getLowerVersionRange() + " to "
                        + dependency.getUpperVersionRange();
            }
        }

        return message;
    }
}
