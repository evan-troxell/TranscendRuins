package com.transcendruins.utilities.metadata;

import java.util.Collection;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.IdentifierFormatException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.VersionBoundsException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/*
 * A class representing the metadata of a compiled class, containing an identifier and version.
 */
public final class Metadata {

    /**
     * <code>TracedDictionary</code>: The JSON representation of this
     * <code>Metadata</code> instance.
     */
    private final TracedDictionary json;

    /**
     * Retrieves the json of this <code>Metadata</code> instance.
     * 
     * @return <code>TracedDictionary</code>: The <code>json</code> field of this
     *         <code>Metadata</code> instance.
     */
    public TracedDictionary getJson() {

        return json;
    }

    /**
     * <code>TracedEntry&lt;Identifier&gt;</code>: The
     * <code>TracedEntry&lt;Identifier&gt;</code> representing the identifier of
     * this <code>Metadata</code> instance.
     */
    private final TracedEntry<Identifier> identifierEntry;

    /**
     * Retrieves the identifier entry of this <code>Metadata</code> instance.
     * 
     * @return <code>TracedEntry&lt;Identifier&gt;</code>: The
     *         <code>identifierEntry</code> field of this <code>Metadata</code>
     *         instance.
     */
    public TracedEntry<Identifier> getIdentifierEntry() {

        return identifierEntry;
    }

    /**
     * <code>Identifier</code>: The identifier of this <code>Metadata</code>
     * instance.
     */
    private final Identifier identifier;

    /**
     * Retrieves the <code>Identifier</code> of this <code>Metadata</code> instance.
     * 
     * @return <code>Identifier</code>: The <code>Identifier</code> of this
     *         <code>Metadata</code> instance.
     */
    public Identifier getIdentifier() {

        return identifier;
    }

    /**
     * <code>TracedEntry&lt;Version&gt;</code>: The
     * 
     * @return <code>TracedEntry&lt;Version&gt;</code> representing the version of
     *         this <code>Metadata</code> instance.
     */
    private final TracedEntry<Version> versionEntry;

    /**
     * Retrieves the version entry of this <code>Metadata</code> instance.
     * 
     * @return <code>TracedEntry&lt;Version&gt;</code>: The
     *         <code>versionEntry</code> field of this <code>Metadata</code>
     *         instance.
     */
    public TracedEntry<Version> getVersionEntry() {

        return versionEntry;
    }

    /**
     * <code>Version</code>: The version of this <code>Metadata</code>
     * instance.
     */
    private final Version version;

    /**
     * Retrieves the version of this <code>Metadata</code> instance.
     * 
     * @return <code>Version</code>: The <code>version</code> field of this
     *         <code>Metadata</code> instance.
     */
    public Version getVersion() {

        return version;
    }

    /**
     * <code>boolean</code>: Whether or not this <code>Metadata</code> instance
     * utilized a version range or a regular version.
     */
    private final boolean hasVersionRange;

    /**
     * Retrieves whether or not this <code>Metadata</code> instance has a version
     * range.
     * 
     * @return <code>boolean</code>: The <code>hasVersionRange</code> field of this
     *         <code>Metadata</code> instance.
     */
    public boolean hasVersionRange() {

        return hasVersionRange;
    }

    /**
     * <code>Identifier[2]</code>: The version range to check between, if the
     * <code>hasVersionRange</code> field is <code>true</code>.
     */
    public final Identifier[] versionRange = new Identifier[2];

    /**
     * Retrieves the upper version range of this <code>Metadata</code> instance.
     * 
     * @return <code>Identifier</code>: The second index of the
     *         <code>versionRange</code> field of this <code>Metadata</code>
     *         instance.
     */
    public Identifier getUpperVersionRange() {

        return versionRange[1];
    }

    /**
     * Retrieves the lower version range of this <code>Metadata</code> instance.
     * 
     * @return <code>Identifier</code>: The first index of the
     *         <code>versionRange</code> field of this <code>Metadata</code>
     *         instance.
     */
    public Identifier getLowerVersionRange() {

        return versionRange[0];
    }

    /**
     * Creates a new instance of the <code>Metadata</code> class using a field from
     * a <code>TracedCollection</code> instance. This version WILL retrieve the
     * version.
     * 
     * @param entry               <code>TracedEntry&lt;TracedDictionary&gt;</code>:
     *                            The entry from which to create this
     *                            <code>Metadata</code> instance.
     * @param versionRangeAllowed <code>boolean</code>: Whether or not a range of
     *                            versions is allowed in this <code>Metadata</code>
     *                            instance.
     * @throws ArrayLengthException      Thrown if an array in this
     *                                   <code>Metadata</code> instance is of an
     *                                   invalid length.
     * @throws PropertyTypeException     Thrown if the type of a field in this
     *                                   <code>Metadata</code> instance is invalid.
     * @throws MissingPropertyException  Thrown if a field is missing from this
     *                                   <code>Metadata</code> instance.
     * @throws IdentifierFormatException Thrown if this <code>Metadata</code>
     *                                   instance identifier is in an invalid
     *                                   format.
     * @throws VersionBoundsException    Thrown if the minimum version bounds is
     *                                   greater than the maximum version bounds in
     *                                   this <code>Metadata</code> instance.
     * @throws VersionBoundsException    Thrown if any vector value in the
     *                                   <code>Version</code> vector or any vector
     *                                   value in the minimum allowed version vector
     *                                   is negative.
     * @throws NumberBoundsException     Thrown if any vector value in the generated
     *                                   <code>Metadata</code> instance is negative.
     */
    public Metadata(TracedEntry<TracedDictionary> entry, boolean versionRangeAllowed)
            throws IdentifierFormatException, MissingPropertyException, PropertyTypeException, ArrayLengthException,
            NumberBoundsException, VersionBoundsException {

        json = entry.getValue();

        // Whether or not a version may be defined as a single version, or a range
        // between a minimum and maximum version value.
        if (versionRangeAllowed) {

            TracedEntry<?> retrievedVersionEntry = json.get("version", false, null, JSONArray.class, JSONObject.class);

            // If the version key is a regular version vector, treat it as such.
            if (retrievedVersionEntry.getValue() instanceof TracedDictionary versionJson) {

                TracedEntry<Identifier> minVersionEntry = json.getAsIdentifier("identifier", false,
                        versionJson.getAsVersion("min", false, false));
                Identifier minVersion = minVersionEntry.getValue();

                TracedEntry<Identifier> maxVersionEntry = json.getAsIdentifier("identifier", false,
                        versionJson.getAsVersion("max", true, true));
                Identifier maxVersion = maxVersionEntry.getValue();

                if (minVersion.lowestVersion(maxVersion) != minVersion) {

                    throw VersionBoundsException.inverseMaxAndMinBounds(maxVersionEntry, minVersionEntry);
                }

                if (minVersion == maxVersion) {

                    identifierEntry = minVersionEntry;
                    hasVersionRange = false;
                } else {

                    identifierEntry = json.getAsIdentifier("identifier", false);
                    versionRange[0] = minVersion;
                    versionRange[1] = maxVersion;
                    hasVersionRange = true;
                }
            } else {

                hasVersionRange = false;
                identifierEntry = json.getAsIdentifier("identifier", false, json.getAsVersion("version", false, false));
            }
        } else {

            // Retrieve the version if the useVersion perameter is true.
            hasVersionRange = false;
            TracedEntry<Version> absoluteVersionEntry = json.getAsVersion("version", false, false);

            identifierEntry = json.getAsIdentifier("identifier", false, absoluteVersionEntry);
        }

        identifier = identifierEntry.getValue();

        versionEntry = getIdentifier().getVersionEntry();
        version = versionEntry.getValue();
    }

    /**
     * Creates a new instance of the <code>Metadata</code> class using a field from
     * a <code>TracedCollection</code> instance. This version WILL NOT retrieve the
     * version.
     * 
     * @param entry <code>TracedEntry&lt;TracedDictionary&gt;</code>: The entry from
     *              which to create the new <code>Metadata</code> instance.
     * @throws PropertyTypeException     Thrown if the type of a field in this
     *                                   <code>Metadata</code> instance is invalid.
     * @throws MissingPropertyException  Thrown if a field is missing from this
     *                                   <code>Metadata</code> instance.
     * @throws IdentifierFormatException Thrown if this <code>Metadata</code>
     *                                   instance identifier is in an invalid
     *                                   format.
     */
    public Metadata(TracedEntry<TracedDictionary> entry)
            throws IdentifierFormatException, MissingPropertyException, PropertyTypeException {

        json = entry.getValue();

        hasVersionRange = false;
        identifierEntry = json.getAsIdentifier("identifier", false);
        identifier = identifierEntry.getValue();

        versionEntry = getIdentifier().getVersionEntry();
        version = versionEntry.getValue();
    }

    /**
     * Tests if another <code>Identifier</code> instance is compatible with this
     * <code>Metadata</code> instance.
     * 
     * @param identifier <code>Identifier</code>: The <code>Identifier</code>
     *                   instance to check against.
     * @return <code>boolean</code>: Whether or not the <code>identifier</code>
     *         perameter is compatible with this <code>Metadata</code> instance.
     */
    public boolean compatableIdentifier(Identifier identifier) {

        if (hasVersionRange) {

            if (identifier == null) {

                return false;
            }

            if (!getIdentifier().getFull().equals(identifier.getFull())) {

                return false;
            }

            Identifier min = versionRange[0];
            Identifier max = versionRange[1];

            return identifier.highestVersion(min) == identifier && identifier.lowestVersion(max) == identifier;

        } else {

            return getIdentifier() == identifier;
        }
    }

    /**
     * Iterates through a collection of <code>Identifier</code> instances and
     * determines which are compatible with this <code>Metadata</code> instance.
     * 
     * @param identifiers <code>Collection&lt;Identifier&gt;</code>: A collection of
     *                    identifiers of which to determine compatibility with this
     *                    <code>Metadata</code> instance.
     * @return <code>HashSet&lt;Identifier&gt;</code>: A set of compatible
     *         <code>Identifier</code> instances from the <code>identifiers</code>
     *         perameter.
     */
    public HashSet<Identifier> retrieveCompatibleIdentifiers(Collection<Identifier> identifiers) {

        HashSet<Identifier> compatibleIdentifiers = new HashSet<>();
        for (Identifier compareIdentifier : identifiers) {

            if (compatableIdentifier(compareIdentifier)) {

                compatibleIdentifiers.add(compareIdentifier);
            }
        }

        return compatibleIdentifiers;
    }

    /**
     * Tests whether or not another <code>Metadata</code> instance version bounds
     * (or version perameter) overlaps with this <code>Metadata</code> instance
     * version bounds (or version perameter).
     * 
     * @param metadata       <code>Metadata</code>: The metadata to compare.
     * @param ignoreVersions <code>boolean</code>: Whether or not the versions of
     *                       the identifiers should be considered.
     * @return <code>boolean</code>: Whether or not the two versions overlap.
     */
    public boolean overlaps(Metadata metadata, boolean ignoreVersions) {

        if (ignoreVersions) {

            return getIdentifier().getFull().equals(metadata.getIdentifier().getFull());
        }

        if (metadata.hasVersionRange()) {

            if (!hasVersionRange) {

                return metadata.compatableIdentifier(getIdentifier());
            }

            boolean minVersionOverlap = metadata.compatableIdentifier(getLowerVersionRange());
            boolean maxVersionOverlap = metadata.compatableIdentifier(getUpperVersionRange());

            return minVersionOverlap || maxVersionOverlap;

        } else {

            if (!hasVersionRange) {

                return getIdentifier() == metadata.getIdentifier();
            }

            return compatableIdentifier(metadata.getIdentifier());
        }
    }

    /**
     * Retrieves a set of all overlapping <code>Metadata</code> instances from a
     * <code>Collection</code> of <code>Metadata</code> instances.
     * 
     * @param metadatas      <code>Collection&lt;TracedEntry&lt;Metadata&gt;&gt;</code>:
     *                       The collection of all <code>Metadata</code> instances.
     * @param ignoreVersions <code>boolean</code>: Whether or not the versions of
     *                       the identifiers should be considered.
     * @return <code>HashSet&lt;TracedEntry&lt;Metadata&gt;&gt;</code>: A
     *         <code>HashSet</code> containing all overlapping <code>Metadata</code>
     *         instances.
     */
    public HashSet<TracedEntry<Metadata>> retrieveOverslaps(Collection<TracedEntry<Metadata>> metadatas,
            boolean ignoreVersions) {

        HashSet<TracedEntry<Metadata>> overlappingMetadatas = new HashSet<>();
        for (TracedEntry<Metadata> metadata : metadatas) {

            if (overlaps(metadata.getValue(), ignoreVersions)) {

                overlappingMetadatas.add(metadata);
            }
        }

        return overlappingMetadatas;
    }

    /**
     * Returns a string representation of this <code>Metadata</code> instance.
     * 
     * @return <code>String</code>: This <code>Metadata</code> instance in the
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

            String returnString = getIdentifier() + " {" + getLowerVersionRange().getVersion() + ", ";
            returnString += (getUpperVersionRange() == null) ? "[?, ?, ?]}" : getUpperVersionRange().getVersion() + "}";

            return returnString;
        }

        return getIdentifier().toString();
    }
}
