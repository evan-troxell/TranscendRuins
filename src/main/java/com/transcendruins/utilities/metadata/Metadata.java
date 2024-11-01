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
     * <code>TracedDictionary</code>: The JSON representation of this <code>Metadata</code> instance.
     */
    public final TracedDictionary json;

    /**
     * <code>TracedEntry&lt;Identifier&gt;</code>: The <code>TracedEntry&lt;Identifier&gt;</code> representing the identifier of this <code>Metadata</code> instance.
     */
    public final TracedEntry<Identifier> identifierEntry;

    /**
     * <code>TracedEntry&lt;Version&gt;</code>: The <code>TracedEntry&lt;Version&gt;</code> representing the version of this <code>Metadata</code> instance.
     */
    public final TracedEntry<Version> versionEntry;

    /**
     * <code>boolean</code>: Whether or not this <code>Metadata</code> instance utilized a version range or a regular version.
     */
    public final boolean versionRange;

    /**
     * <code>Identifier[2]</code>: The version bounds to check between, if the <code>versionRange</code> property is <code>true</code>.
     */
    public final Identifier[] versionBounds = new Identifier[2];

    /**
     * Creates a new instance of the <code>Metadata</code> class using a property from a <code>TracedCollection</code> instance. This version WILL retrieve the version.
     * @param entry <code>TracedEntry&lt;TracedDictionary&gt;</code>: The entry from which to create this <code>Metadata</code> instance.
     * @param versionRangeAllowed <code>boolean</code>: Whether or not a range of versions is allowed in this <code>Metadata</code> instance.
     * @throws ArrayLengthException Thrown if an array in this <code>Metadata</code> instance is of an invalid length.
     * @throws PropertyTypeException Thrown if the type of a property in this <code>Metadata</code> instance is invalid.
     * @throws MissingPropertyException Thrown if a property is missing from this <code>Metadata</code> instance.
     * @throws IdentifierFormatException Thrown if this <code>Metadata</code> instance identifier is in an invalid format.
     * @throws VersionBoundsException Thrown if the minimum version bounds is greater than the maximum version bounds in this <code>Metadata</code> instance.
     * @throws VersionBoundsException Thrown if any vector value in the <code>Version</code> vector or any vector value in the minimum allowed version vector is negative.
     * @throws NumberBoundsException Thrown if any vector value in the generated <code>Metadata</code> instance is negative.
     */
    public Metadata(TracedEntry<TracedDictionary> entry, boolean versionRangeAllowed) throws IdentifierFormatException, MissingPropertyException, PropertyTypeException, ArrayLengthException, VersionBoundsException, NumberBoundsException {

        json = entry.getValue();

        // Whether or not a version may be defined as a single version, or a range between a minimum and maximum version value.
        if (versionRangeAllowed) {

            TracedEntry<Object> retrievedVersionEntry = json.get("version", false, null, JSONArray.class, JSONObject.class);

            // If the version key is a regular version vector, treat it as such.
            if (retrievedVersionEntry.getValue() instanceof TracedDictionary) {

                TracedDictionary versionJson = (TracedDictionary) retrievedVersionEntry.getValue();

                TracedEntry<Identifier> minVersionEntry = json.getAsIdentifier("identifier", false, versionJson.getAsVersion("min", false, false));
                Identifier minVersion = minVersionEntry.getValue();

                TracedEntry<Identifier> maxVersionEntry = json.getAsIdentifier("identifier", false, versionJson.getAsVersion("max", true, true));
                Identifier maxVersion = maxVersionEntry.getValue();

                if (minVersion.lowestVersion(maxVersion) != minVersion) {

                    throw VersionBoundsException.inverseMaxAndMinBounds(maxVersionEntry, minVersionEntry);
                }

                if (minVersion == maxVersion) {

                    identifierEntry = minVersionEntry;
                    versionRange = false;
                } else {

                    identifierEntry = json.getAsIdentifier("identifier", false);
                    versionBounds[0] = minVersion;
                    versionBounds[1] = maxVersion;
                    versionRange = true;
                }
            } else {

                versionRange = false;
                identifierEntry = json.getAsIdentifier("identifier", false, json.getAsVersion("version", false, false));
            }
        } else {

            // Retrieve the version if the useVersion perameter is true.
            versionRange = false;
            TracedEntry<Version> absoluteVersionEntry = json.getAsVersion("version", false, false);

            identifierEntry = json.getAsIdentifier("identifier", false, absoluteVersionEntry);
        }

        versionEntry = getIdentifier().version;
    }

    /**
     * Retrieves the <code>Identifier</code> of this <code>Metadata</code> instance.
     * @return <code>Identifier</code>: The <code>Identifier</code> of this <code>Metadata</code> instance.
     */
    public Identifier getIdentifier() {

        return identifierEntry.getValue();
    }

    /**
     * Retrieves the <code>Version</code> of this <code>Metadata</code> instance.
     * @return <code>Version</code>: The <code>Version</code> of this <code>Metadata</code> instance.
     */
    public Version getVersion() {

        return versionEntry.getValue();
    }

    /**
     * Creates a new instance of the <code>Metadata</code> class using a property from a <code>TracedCollection</code> instance. This version WILL NOT retrieve the version.
     * @param entry <code>TracedEntry&lt;TracedDictionary&gt;</code>: The entry from which to create the new <code>Metadata</code> instance.
     * @throws PropertyTypeException Thrown if the type of a property in this <code>Metadata</code> instance is invalid.
     * @throws MissingPropertyException Thrown if a property is missing from this <code>Metadata</code> instance.
     * @throws IdentifierFormatException Thrown if this <code>Metadata</code> instance identifier is in an invalid format.
     */
    public Metadata(TracedEntry<TracedDictionary> entry) throws IdentifierFormatException, MissingPropertyException, PropertyTypeException {

        json = entry.getValue();

        versionRange = false;
        identifierEntry = json.getAsIdentifier("identifier", false);

        versionEntry = getIdentifier().version;
    }

    /**
     * Tests if another <code>Identifier</code> instance is compatible with this <code>Metadata</code> instance.
     * @param identifier <code>Identifier</code>: The <code>Identifier</code> instance to check against.
     * @return <code>boolean</code>: Whether or not the <code>identifier</code> perameter is compatible with this <code>Metadata</code> instance.
     */
    public boolean compatableIdentifier(Identifier identifier) {

        if (versionRange) {

            if (identifier == null) {

                return false;
            }

            if (!getIdentifier().getFull().equals(identifier.getFull())) {

                return false;
            }

            Identifier min = versionBounds[0];
            Identifier max = versionBounds[1];

            return identifier.highestVersion(min) == identifier && identifier.lowestVersion(max) == identifier;

        } else {

            return getIdentifier() == identifier;
        }
    }

    /**
     * Iterates through a collection of <code>Identifier</code> instances and determines which are compatible with this <code>Metadata</code> instance.
     * @param identifiers <code>Collection&lt;Identifier&gt;</code>: A collection of identifiers of which to determine compatibility with this <code>Metadata</code> instance.
     * @return <code>HashSet&lt;Identifier&gt;</code>: A set of compatible <code>Identifier</code> instances from the <code>identifiers</code> perameter.
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
     * Tests whether or not another <code>Metadata</code> instance version bounds (or version perameter) overlaps with this <code>Metadata</code> instance version bounds (or version perameter).
     * @param metadata <code>Metadata</code>: The metadata to compare.
     * @param ignoreVersions <code>boolean</code>: Whether or not the versions of the identifiers should be considered.
     * @return <code>boolean</code>: Whether or not the two versions overlap.
     */
    public boolean overlaps(Metadata metadata, boolean ignoreVersions) {

        if (ignoreVersions) {

            return getIdentifier().getFull().equals(metadata.getIdentifier().getFull());
        }

        if (metadata.versionRange) {

            if (!versionRange) {

                return metadata.compatableIdentifier(getIdentifier());
            }

            boolean minVersionOverlap = metadata.compatableIdentifier(versionBounds[0]);
            boolean maxVersionOverlap = metadata.compatableIdentifier(versionBounds[1]);

            return minVersionOverlap || maxVersionOverlap;

        } else {

            if (!versionRange) {

                return getIdentifier() == metadata.getIdentifier();
            }

            return compatableIdentifier(metadata.getIdentifier());
        }
    }

    /**
     * Retrieves a set of all overlapping <code>Metadata</code> instances from a <code>Collection</code> of <code>Metadata</code> instances.
     * @param metadatas <code>Collection&lt;TracedEntry&lt;Metadata&gt;&gt;</code>: The collection of all <code>Metadata</code> instances.
     * @param ignoreVersions <code>boolean</code>: Whether or not the versions of the identifiers should be considered.
     * @return <code>HashSet&lt;TracedEntry&lt;Metadata&gt;&gt;</code>: A <code>HashSet</code> containing all overlapping <code>Metadata</code> instances.
     */
    public HashSet<TracedEntry<Metadata>> retrieveOverslaps(Collection<TracedEntry<Metadata>> metadatas, boolean ignoreVersions) {

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
     * @return <code>String</code>: This <code>Metadata</code> instance in the following string representation: <br>"<code>namespace:identifier</code>"
     * <br> <b>OR</b> <br>"<code>namespace:identifier [a, b, c]</code>"
     * <br> <b>OR</b> <br>"<code>namspace:identifier {[a, b, c], [?, ?, ?]}</code>"
     * <br> <b>OR</b> <br>"<code>namspace:identifier {[a, b, c], [d, e, f]}</code>"
     */
    @Override
    public String toString() {

        if (versionRange) {

            String returnString = getIdentifier() + " {" + versionBounds[0].version + ", ";
            returnString += (versionBounds[1] == null) ? "[?, ?, ?]}" : versionBounds[1].version + "}";

            return returnString;
        }

        return getIdentifier().toString();
    }
}
