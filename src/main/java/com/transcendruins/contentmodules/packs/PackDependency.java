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

package com.transcendruins.contentmodules.packs;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.VersionBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.identifierexceptions.IdentifierFormatException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedCollection.JSONType;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.metadata.Version;

/*
 * A class representing the metadata of a compiled class, containing an identifier and version.
 */
public final class PackDependency {

    public static final String ASSET = "asset";

    public static final String RESOURCE = "resource";

    private final TracedEntry<TracedDictionary> entry;

    public TracedEntry<TracedDictionary> getEntry() {

        return entry;
    }

    /**
     * <code>TracedEntry&lt;Identifier&gt;</code>: The
     * <code>TracedEntry&lt;Identifier&gt;</code> representing the identifier of
     * this <code>PackDependency</code> instance.
     */
    private final TracedEntry<Identifier> identifierEntry;

    /**
     * Retrieves the identifier entry of this <code>PackDependency</code> instance.
     * 
     * @return <code>TracedEntry&lt;Identifier&gt;</code>: The
     *         <code>identifierEntry</code> field of this
     *         <code>PackDependency</code>
     *         instance.
     */
    public TracedEntry<Identifier> getIdentifierEntry() {

        return identifierEntry;
    }

    /**
     * <code>Identifier</code>: The identifier of this <code>PackDependency</code>
     * instance.
     */
    private final Identifier identifier;

    /**
     * Retrieves the <code>Identifier</code> of this <code>PackDependency</code>
     * instance.
     * 
     * @return <code>Identifier</code>: The <code>Identifier</code> of this
     *         <code>PackDependency</code> instance.
     */
    public Identifier getIdentifier() {

        return identifier;
    }

    /**
     * <code>boolean</code>: Whether or not this <code>PackDependency</code>
     * instance
     * utilized a version range or a regular version.
     */
    private final boolean hasVersionRange;

    /**
     * Retrieves whether or not this <code>PackDependency</code> instance has a
     * version
     * range.
     * 
     * @return <code>boolean</code>: The <code>hasVersionRange</code> field of this
     *         <code>PackDependency</code> instance.
     */
    public boolean getHasVersionRange() {

        return hasVersionRange;
    }

    /**
     * <code>Version[2]</code>: The version range to check between, if the
     * <code>hasVersionRange</code> field is <code>true</code>.
     */
    public final Version[] versionRange;

    /**
     * Retrieves the upper version range of this <code>PackDependency</code>
     * instance.
     * 
     * @return <code>Version</code>: The second index of the
     *         <code>versionRange</code> field of this <code>PackDependency</code>
     *         instance.
     */
    public Version getUpperVersionRange() {

        return versionRange[1];
    }

    /**
     * Retrieves the lower version range of this <code>PackDependency</code>
     * instance.
     * 
     * @return <code>Version</code>: The first index of the
     *         <code>versionRange</code> field of this <code>PackDependency</code>
     *         instance.
     */
    public Version getLowerVersionRange() {

        return versionRange[0];
    }

    private final String type;

    public String getType() {

        return type;
    }

    /**
     * Creates a new instance of the <code>PackDependency</code> class using a field
     * from
     * a <code>TracedCollection</code> instance. This version WILL retrieve the
     * version.
     * 
     * @param entry <code>TracedEntry&lt;TracedDictionary&gt;</code>:
     *              The entry from which to create this
     *              <code>PackDependency</code> instance.
     * @throws LoggedException If an exception was raised while processing this
     *                         <code>PackDependency</code> instance.
     */
    public PackDependency(TracedEntry<TracedDictionary> entry)
            throws IdentifierFormatException, MissingPropertyException, PropertyTypeException,
            CollectionSizeException,
            NumberBoundsException, VersionBoundsException, UnexpectedValueException {

        this.entry = entry;
        TracedDictionary json = entry.getValue();

        if (json.getType("version") == JSONType.ARRAY) {

            TracedEntry<TracedDictionary> versionEntry = json.getAsDict("version", false);
            TracedDictionary versionJson = versionEntry.getValue();

            TracedEntry<Version> minVersionEntry = versionJson.getAsVersion("min", false);
            Version minVersion = minVersionEntry.getValue();

            TracedEntry<Version> maxVersionEntry = versionJson.getAsVersion("max", true);
            Version maxVersion = maxVersionEntry.getValue();

            if (maxVersion != null && !minVersion.lessThan(maxVersion)) {

                throw VersionBoundsException.inverseMaxAndMinBounds(maxVersionEntry, minVersionEntry);
            }

            identifierEntry = json.getAsIdentifier("identifier", false, null);

            hasVersionRange = true;
            versionRange = new Version[] { minVersion, maxVersion };
        } else {

            identifierEntry = json.getAsIdentifier("identifier", false, json.getAsVersion("version", false));

            hasVersionRange = false;
            versionRange = null;
        }

        identifier = identifierEntry.getValue();

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        type = switch (typeEntry.getValue()) {

            case ASSET -> ASSET;

            case RESOURCE -> RESOURCE;

            default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    public boolean compatible(Identifier identifier) {

        return getIdentifier().compatible(identifier);
    }

    /**
     * Iterates through a collection of <code>Identifier</code> instances and
     * determines which are compatible with this <code>PackDependency</code>
     * instance.
     * 
     * @param identifiers <code>Collection&lt;Identifier&gt;</code>: A collection of
     *                    identifiers of which to determine compatibility with this
     *                    <code>PackDependency</code> instance.
     * @return <code>HashSet&lt;Identifier&gt;</code>: The set of compatible
     *         <code>Identifier</code> instances from the <code>identifiers</code>
     *         perameter.
     */
    public HashSet<Identifier> getCompatible(Collection<Identifier> identifiers) {

        HashSet<Identifier> overlapping = new HashSet<>(
                identifiers.stream().filter(t -> compatible(t)).collect(Collectors.toSet()));

        return overlapping;
    }

    public boolean lessThan(Version version) {

        if (hasVersionRange) {

            if (!versionRange[0].lessThan(version)) {

                return false;
            }

            return versionRange[1] != null && versionRange[1].lessThanEqual(version);

        } else {

            return identifier.getVersion().lessThan(version);
        }
    }

    /**
     * Tests if an identifier overlaps with this <code>PackDependency</code>
     * instance.
     * 
     * @param identifier <code>Identifier</code>: The <code>Identifier</code>
     *                   instance to check against.
     * @return <code>boolean</code>: Whether or not the <code>identifier</code>
     *         parameter overlaps with this <code>PackDependency</code> instance.
     */
    public boolean overlaps(Identifier identifier) {

        if (!compatible(identifier)) {

            return false;
        }

        if (hasVersionRange) {

            return identifier.getVersion().isInRange(versionRange[0], versionRange[1]);

        } else {

            return identifier == getIdentifier();
        }
    }

    /**
     * Iterates through a collection of <code>Identifier</code> instances and
     * determines which overlap with this <code>PackDependency</code> instance.
     * 
     * @param identifiers <code>Collection&lt;Identifier&gt;</code>: A collection of
     *                    identifiers of which to determine compatibility with this
     *                    <code>PackDependency</code> instance.
     * @return <code>HashSet&lt;Identifier&gt;</code>: The set of overlapping
     *         <code>Identifier</code> instances from the <code>identifiers</code>
     *         perameter.
     */
    public HashSet<Identifier> getOverlaps(Collection<Identifier> identifiers) {

        HashSet<Identifier> overlapping = new HashSet<>(
                identifiers.stream().filter(t -> overlaps(t)).collect(Collectors.toSet()));

        return overlapping;
    }

    /**
     * Tests whether or not another <code>PackDependency</code> instance version
     * bounds
     * (or version perameter) overlaps with this <code>PackDependency</code>
     * instance
     * version bounds (or version perameter).
     * 
     * @param dependency     <code>PackDependency</code>: The dependency to compare.
     * @param ignoreVersions <code>boolean</code>: Whether or not the versions of
     *                       the identifiers should be considered.
     * @return <code>boolean</code>: Whether or not the two versions overlap.
     */
    public boolean overlaps(PackDependency dependency, boolean ignoreVersions) {

        if (!compatible(dependency.getIdentifier())) {

            return false;
        } else if (ignoreVersions) {

            return true;
        }

        if (dependency.hasVersionRange) {

            if (!hasVersionRange) {

                return dependency.overlaps(identifier);
            }

            return versionRange[0].lessThan(dependency.versionRange[1])
                    && dependency.versionRange[0].lessThan(versionRange[1]);

        } else {

            if (!hasVersionRange) {

                return identifier == dependency.identifier;
            }

            return overlaps(dependency.identifier);
        }
    }

    /**
     * Retrieves a set of all overlapping <code>PackDependency</code> instances from
     * a
     * <code>Collection</code> of <code>PackDependency</code> instances.
     * 
     * @param dependencies   <code>Collection&lt;PackDependency&gt;</code>:
     *                       The collection of all <code>PackDependency</code>
     *                       instances.
     * @param ignoreVersions <code>boolean</code>: Whether or not the versions of
     *                       the identifiers should be considered.
     * @return <code>HashSet&lt;PackDependency&gt;</code>: A
     *         <code>HashSet</code> containing all overlapping
     *         <code>PackDependency</code>
     *         instances.
     */
    public HashSet<PackDependency> getOverlaps(Collection<PackDependency> dependencies,
            boolean ignoreVersions) {

        HashSet<PackDependency> overlapping = new HashSet<>(
                dependencies.stream().filter(t -> overlaps(t, ignoreVersions)).collect(Collectors.toSet()));

        return overlapping;
    }

    /**
     * Returns a string representation of this <code>PackDependency</code> instance.
     * 
     * @return <code>String</code>: This <code>PackDependency</code> instance in the
     *         following string representation: <br>
     *         "<code>namespace:identifier</code>"
     *         <br>
     *         <b>OR</b> <br>
     *         "<code>namespace:identifier [a, b, c]</code>"
     *         <br>
     *         <b>OR</b> <br>
     *         "<code>namspace:identifier {[a, b, c], [?, ?, ?]}</code>"
     *         <br>
     *         <b>OR</b> <br>
     *         "<code>namspace:identifier {[a, b, c], [d, e, f]}</code>"
     */
    @Override
    public String toString() {

        if (hasVersionRange) {

            return "%s {%s, %s}".formatted(getIdentifier(), getLowerVersionRange(),
                    (getUpperVersionRange() == null) ? "[?, ?, ?]}" : getUpperVersionRange());
        }

        return getIdentifier().toString();
    }
}
