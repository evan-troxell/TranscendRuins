package com.transcendruins.utilities.metadata;

import java.util.HashMap;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.VersionBoundsException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Version</code>: A class representing a version vector.
 */
public final class Version {

    /**
     * <code>Hashmap&lt;String, Version&gt;</code>: A set of versions to be retrieved in order to produce equivalent versions.
     */
    private static final HashMap<String, Version> VERSIONS = new HashMap<>();

    /**
     * <code>long[3]</code>: An vector array representing the version values.
     */
    public final long[] vector = new long[Vector.DIMENSION_3D];

    /**
     * Creates a new instance of the <code>Version</code> class.
     * @param entry <code>TracedEntry&lt;TracedArray&gt;</code>: The entry from which to create this <code>Version</code> instance.
     * @param negativeVectorValuesAllowed <code>boolean</code>: Whether or not negative version vector values are allowed.
     * @throws ArrayLengthException Thrown if the length of the <code>version</code> perameter is not 3.
     * @throws MissingPropertyException Thrown if any of the indices of the <code>version</code> perameter are missing.
     * @throws PropertyTypeException Thrown if any of the indices of the <code>version</code> perameter are not of the <code>Long</code> class.
     * @throws NumberBoundsException Thrown if any vector value in this <code>Version</code> instance is negative.
     */
    private Version(TracedEntry<TracedArray> entry, boolean negativeVectorValuesAllowed) throws ArrayLengthException, MissingPropertyException, PropertyTypeException, VersionBoundsException, NumberBoundsException {

        TracedArray list = entry.getValue();
        if (list.size() != Vector.DIMENSION_3D) {

            throw new ArrayLengthException(entry);
        }

        for (int i = 0; i < list.size(); i++) {

            TracedEntry<Long> numEntry = list.getAsLong(i, false, null, negativeVectorValuesAllowed ? null : 0l, null);
            vector[i] = (long) numEntry.getValue();
        }
    }

    /**
     * Generates an instance of the <code>Version</code> class. Once a version is generated, its instance will be used to represent that version vector from then on.
     * @param entry <code>TracedEntry&lt;TracedArray&gt;</code>: The entry from which to create this <code>Version</code> instance.
     * @param negativeVectorValuesAllowed <code>boolean</code>: Whether or not negative version vector values are allowed.
     * @return <code>Version</code>: The generated <code>Version</code> instance.
     * @throws ArrayLengthException Thrown if the length of the <code>version</code> perameter is not 3.
     * @throws MissingPropertyException Thrown if any of the indices of the <code>version</code> perameter are missing.
     * @throws PropertyTypeException Thrown if any of the indices of the <code>version</code> perameter are not of the <code>Long</code> class.
     * @throws VersionBoundsException Thrown if any vector value in this <code>Version</code> instance is negative.
     */
    public static Version createVersion(TracedEntry<TracedArray> entry, boolean negativeVectorValuesAllowed) throws ArrayLengthException, MissingPropertyException, PropertyTypeException, VersionBoundsException, NumberBoundsException {

        Version newVersion = new Version(entry, negativeVectorValuesAllowed);
        String versionString = newVersion.toString();

        if (!VERSIONS.containsKey(versionString)) {

            VERSIONS.put(versionString, newVersion);
            return newVersion;
        }
        return VERSIONS.get(versionString);
    }

    /**
     * Generates an instance of the <code>Version</code> class using only a version vector. This method is only to be used for testing purposes.
     * @param version <code>long[3]</code>: The vector to parse into a version code.
     */
    @Deprecated
    private Version(long[] version) {

        System.arraycopy(version, 0, vector, 0, Vector.DIMENSION_3D);
    }

    /**
     * Generates an instance of the <code>Version</code> class using only a version vector. This method is only to be used for testing purposes.
     * @param version <code>long[3]</code>: The vector to parse into a version code.
     * @return <code>Version</code> The generated <code>Version</code> instance.
     */
    @Deprecated
    public static Version createTestVersion(long[] version) {

        Version newVersion = new Version(version);
        String versionString = newVersion.toString();

        if (!VERSIONS.containsKey(versionString)) {

            VERSIONS.put(versionString, newVersion);
            return newVersion;
        }
        return VERSIONS.get(versionString);
    }

    /**
     * Returns a string representation of this <code>Version</code> instance.
     * @return <code>String</code>: This <code>Version</code> instance in the string representation: <br>"<code>[a, b, c]</code>"
     */
    @Override
    public String toString() {

        return "[" + vector[0] + ", " + vector[1] + ", " + vector[2] + "]";
    }
}
