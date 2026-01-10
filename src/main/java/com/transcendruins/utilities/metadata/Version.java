/* Copyright 2026 Evan Troxell
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

package com.transcendruins.utilities.metadata;

import java.util.HashMap;

import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.identifierexceptions.VersionBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Version</code>: A class representing a version vector.
 */
public final class Version {

    /**
     * <code>Hashmap&lt;String, Version&gt;</code>: A set of versions to be
     * retrieved in order to produce equivalent versions.
     */
    private static final HashMap<String, Version> VERSIONS = new HashMap<>();

    /**
     * <code>int[3]</code>: An vector array representing the version values.
     */
    private final int[] vector = new int[3];

    /**
     * Retrieves a version field of this <code>Version</code> instance.
     * 
     * @param index <code>int</code>: The index of the version field to retrieve.
     * @return <code>int</code>: The value retrieved from the
     *         <code>vector<code> field of this <code>Version</code> instance.
     */
    public int getVersion(int index) {

        return vector[index];
    }

    /**
     * Creates a new instance of the <code>Version</code> class.
     * 
     * @param entry <code>TracedEntry&lt;TracedArray&gt;</code>: The entry from
     *              which to create this <code>Version</code> instance.
     * @throws CollectionSizeException  Thrown if the length of the
     *                                  <code>version</code> perameter is not 3.
     * @throws MissingPropertyException Thrown if any of the indices of the
     *                                  <code>version</code> perameter are missing.
     * @throws PropertyTypeException    Thrown if any of the indices of the
     *                                  <code>version</code> perameter are not of
     *                                  the <code>Long</code> class.
     * @throws NumberBoundsException    Thrown if any vector value in this
     *                                  <code>Version</code> instance is negative.
     */
    private Version(TracedEntry<TracedArray> entry) throws CollectionSizeException, MissingPropertyException,
            PropertyTypeException, VersionBoundsException, NumberBoundsException {

        TracedArray list = entry.getValue();
        if (list.size() != 3) {

            throw new CollectionSizeException(entry, list);
        }

        for (int i : list) {

            TracedEntry<Integer> numEntry = list.getAsInteger(i, false, null, num -> num >= 0);
            vector[i] = numEntry.getValue();
        }
    }

    /**
     * Generates an instance of the <code>Version</code> class. Once a version is
     * generated, its instance will be used to represent that version vector from
     * then on.
     * 
     * @param entry <code>TracedEntry&lt;TracedArray&gt;</code>: The entry from
     *              which to create this <code>Version</code> instance.
     * @return <code>Version</code>: The generated <code>Version</code> instance.
     * @throws CollectionSizeException  Thrown if the length of the
     *                                  <code>version</code> perameter is not 3.
     * @throws MissingPropertyException Thrown if any of the indices of the
     *                                  <code>version</code> perameter are missing.
     * @throws PropertyTypeException    Thrown if any of the indices of the
     *                                  <code>version</code> perameter are not of
     *                                  the <code>Long</code> class.
     * @throws VersionBoundsException   Thrown if any vector value in this
     *                                  <code>Version</code> instance is negative.
     */
    public static Version createVersion(TracedEntry<TracedArray> entry) throws CollectionSizeException,
            MissingPropertyException, PropertyTypeException, VersionBoundsException, NumberBoundsException {

        Version newVersion = new Version(entry);
        String versionString = newVersion.toString();

        if (!VERSIONS.containsKey(versionString)) {

            VERSIONS.put(versionString, newVersion);
            return newVersion;
        }
        return VERSIONS.get(versionString);
    }

    /**
     * Generates an instance of the <code>Version</code> class using only a version
     * vector. This method is only to be used for testing purposes.
     * 
     * @param version <code>int[3]</code>: The vector to parse into a version code.
     */
    @Deprecated
    private Version(int[] version) {

        System.arraycopy(version, 0, vector, 0, 3);
    }

    /**
     * Generates an instance of the <code>Version</code> class using only a version
     * vector. This method is only to be used for testing purposes.
     * 
     * @param version <code>int[3]</code>: The vector to parse into a version code.
     * @return <code>Version</code> The generated <code>Version</code> instance.
     */
    @Deprecated
    public static Version createTestVersion(int[] version) {

        Version newVersion = new Version(version);
        String versionString = newVersion.toString();

        if (!VERSIONS.containsKey(versionString)) {

            VERSIONS.put(versionString, newVersion);
            return newVersion;
        }
        return VERSIONS.get(versionString);
    }

    /**
     * Determines whether or not this <code>Version</code> is less than another
     * version.
     * 
     * @param version <code>Version</code>: The version to compare.
     * @return <code>boolean</code>: If the inequality
     *         <code>this &lt; version</code> holds true.
     */
    public boolean lessThan(Version version) {

        if (version == null) {

            return true;
        }

        if (version == this) {

            return false;
        }

        for (int i = 0; i < 3; i++) {

            if (getVersion(i) < version.getVersion(i)) {

                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether or not this <code>Version</code> is less than or equal to
     * another version.
     * 
     * @param version <code>Version</code>: The version to compare.
     * @return <code>boolean</code>: If the inequality
     *         <code>this &lt;= version </code> holds true.
     */
    public boolean lessThanEqual(Version version) {

        if (version == null) {

            return true;
        }

        if (version == this) {

            return true;
        }

        for (int i = 0; i < 3; i++) {

            if (getVersion(i) < version.getVersion(i)) {

                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether or not this <code>Version</code> is greater than another
     * version.
     * 
     * @param version <code>Version</code>: The version to compare.
     * @return <code>boolean</code>: If the inequality
     *         <code>this &gt; version </code> holds true.
     */
    public boolean greaterThan(Version version) {

        if (version == null) {

            return true;
        }

        if (version == this) {

            return false;
        }

        for (int i = 0; i < 3; i++) {

            if (getVersion(i) > version.getVersion(i)) {

                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether or not this <code>Version</code> is greater than or equal
     * to another version.
     * 
     * @param version <code>Version</code>: The version to compare.
     * @return <code>boolean</code>: If the inequality
     *         <code>this &gt;= version </code> holds true.
     */
    public boolean greaterThanEqual(Version version) {

        if (version == null) {

            return true;
        }

        if (version == this) {

            return true;
        }

        for (int i = 0; i < 3; i++) {

            if (getVersion(i) > version.getVersion(i)) {

                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether or not this <code>Version</code> instance is within the
     * range <code>[min, max)</code>.
     * 
     * @param min <code>Version</code>: The minimum version to compare.
     * @param max <code>Version</code>: The maximum version to compare.
     * @return <code>boolean</code>: If the
     *         inequality<code>min &lt;= this &lt; max</code> holds true.
     */
    public boolean isInRange(Version min, Version max) {

        return greaterThanEqual(min) && lessThan(max);
    }

    /**
     * Returns a string representation of this <code>Version</code> instance.
     * 
     * @return <code>String</code>: This <code>Version</code> instance in the string
     *         representation: <br>
     *         "<code>[a, b, c]</code>"
     */
    @Override
    public String toString() {

        return "[" + vector[0] + ", " + vector[1] + ", " + vector[2] + "]";
    }
}
