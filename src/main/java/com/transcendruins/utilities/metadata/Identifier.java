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

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.identifierexceptions.IdentifierFormatException;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Identifier</code>: A class representing an identifier string.
 */
public final class Identifier {

    /**
     * <code>String</code>: The regular expression used to ensure all identifier are
     * of the expected pattern.
     */
    private static final String IDENTIFIER_PATTERN = "^[^: ]+:[^: ]+$";

    /**
     * <code>[String, Identifier]</code>: A set of identifiers to be retrieved in
     * order to produce equivalent identifiers.
     */
    private static final HashMap<String, Identifier> IDENTIFIERS = new HashMap<>();

    /**
     * <code>TracedEntry&lt;String&gt;</code>: The identifier entry of this
     * <code>Identifier</code> instance.
     */
    private final TracedEntry<String> idEntry;

    /**
     * Retrieves the identifier entry of this <code>Identifier</code> instance.
     * 
     * @return <code>TracedEntry&lt;String&gt;</code>: The <code>idEntry</code>
     *         field of this <code>Identifier</code> instance.
     */
    public TracedEntry<String> getIdEntry() {

        return idEntry;
    }

    /**
     * <code>String</code>: The identifier string of this <code>Identifier</code>
     * instance.
     */
    private final String id;

    /**
     * Retrieves the identifier string of this <code>Identifier</code> instance.
     * 
     * @return <code>String</code>: The <code>id</code> field of this
     *         <code>Identifier</code> instance.
     */
    public String getId() {

        return id;
    }

    /**
     * <code>TracedEntry&lt;Version&gt;</code>: The version entry of this
     * <code>Identifier</code> instance.
     */
    private final TracedEntry<Version> versionEntry;

    /**
     * Retrieves the version entry of this <code>Identifier</code> instance.
     * 
     * @return <code>TracedEntry&lt;Version&gt;</code>: The
     *         <code>versionEntry</code> field of this <code>Identifier</code>
     *         instance.
     */
    public TracedEntry<Version> getVersionEntry() {

        return versionEntry;
    }

    /**
     * <code>Version</code>: The version of this <code>Identifier</code> instance.
     */
    private final Version version;

    /**
     * Retrieves the version of this <code>Identifier</code> instance.
     * 
     * @return <code>Version</code>: The <code>version</code> field of this
     *         <code>Identifier</code> instance.
     */
    public Version getVersion() {

        return version;
    }

    /**
     * Creates a new instance of the <code>Identifier</code> class.
     * 
     * @param entry        <code>TracedEntry&lt;String&gt;</code>: The entry from
     *                     which to create this <code>Identifier</code> instance.
     * @param versionEntry <code>Version&lt;Version&gt;</code>: The entry
     *                     representing the version of this <code>Identifier</code>
     *                     instance.
     * @throws IdentifierFormatException Thrown if the <code>identifier</code>
     *                                   perameter is in an improper format.
     */
    private Identifier(TracedEntry<String> entry, TracedEntry<Version> versionEntry) throws IdentifierFormatException {

        this.idEntry = entry;
        id = entry.getValue();

        if (!id.matches(IDENTIFIER_PATTERN)) {

            throw new IdentifierFormatException(entry);
        }
        this.versionEntry = versionEntry;
        version = versionEntry == null ? null : versionEntry.getValue();
    }

    /**
     * Generates an instance of the <code>Identifier</code> class. Once an
     * identifier is generated, its instance will be used to represent that
     * identifier string from then on.
     * 
     * @param entry   <code>TracedEntry&lt;String&gt;</code>: The entry from which
     *                to create the new <code>Identifier</code> instance.
     * @param version <code>Version&lt;Version&gt;</code>: The entry representing
     *                the <code>Version</code> of the new <code>Identifier</code>
     *                instance.
     * @return <code>Identifier</code>: The generated <code>Identifier</code>
     *         instance.
     * @throws IdentifierFormatException Thrown if the <code>identifier</code>
     *                                   perameter is in an improper format.
     */
    public static Identifier createIdentifier(TracedEntry<String> entry, TracedEntry<Version> version)
            throws IdentifierFormatException {

        Identifier newIdentifier = new Identifier(entry, version);
        String identifierString = newIdentifier.toString();

        if (!IDENTIFIERS.containsKey(identifierString)) {

            IDENTIFIERS.put(identifierString, newIdentifier);
            return newIdentifier;
        }
        return IDENTIFIERS.get(identifierString);
    }

    /**
     * Generates an instance of the <code>Identifier</code> class using only an
     * identifier string and a version vector. This method is only to be used for
     * testing purposes.
     * 
     * @param identifier <code>String</code>: The identifier <code>String</code> to
     *                   parse into an <code>Identifier</code> instance.
     * @param version    <code>int[3]</code>: The vector to parse into a version
     *                   code.
     * @return <code>Identifier</code> The generated <code>Identifier</code>
     *         instance.
     */
    @Deprecated
    public static Identifier createTestIdentifier(String identifier, int[] version) {

        try {

            return createIdentifier(new TracedEntry<>(null, identifier),
                    version == null ? null : new TracedEntry<>(null, Version.createTestVersion(version)));
        } catch (LoggedException _) {

            return null;
        }
    }

    /**
     * Determines which <code>Identifier</code> instance between this
     * <code>Identifier</code> instance and another has the lowest
     * <code>Version</code> value.
     * 
     * @param identifier <code>Identifier</code>: The identifier to compare against.
     * @return <code>Identifier</code>: The identifier with the lowest
     *         <code>Version</code> value.
     */
    public Identifier lowestVersion(Identifier identifier) {

        return (highestVersion(identifier) == identifier) ? this : identifier;
    }

    /**
     * Determines which <code>Identifier</code> instance between this
     * <code>Identifier</code> instance and another has the highest
     * <code>Version</code> value.
     * 
     * @param identifier <code>Identifier</code>: The identifier to compare against.
     * @return <code>Identifier</code>: The identifier with the highest
     *         <code>Version</code> value.
     */
    public Identifier highestVersion(Identifier identifier) {

        // Checks for null version field values.
        if (identifier == null || !identifier.getVersionEntry().containsValue()) {

            return this;
        }

        if (!identifier.getVersionEntry().containsValue() && !getVersionEntry().containsValue()) {

            return null;
        }

        if (!getVersionEntry().containsValue()) {

            return identifier;
        }

        Version selfVersion = getVersion();
        Version identifierVersion = identifier.getVersion();

        // Compares the first version values.
        if (selfVersion.getVersion(0) < identifierVersion.getVersion(0)) {

            return identifier;
        }

        // Compares the second version values.
        if (selfVersion.getVersion(0) == identifierVersion.getVersion(0)
                && selfVersion.getVersion(1) < identifierVersion.getVersion(1)) {

            return identifier;
        }

        // Compares the third version values.
        if (selfVersion.getVersion(0) == identifierVersion.getVersion(0)
                && selfVersion.getVersion(1) == identifierVersion.getVersion(1)
                && selfVersion.getVersion(2) < identifierVersion.getVersion(2)) {

            return identifier;
        }

        return this;
    }

    /**
     * Returns the generic version of this <code>Identifier</code> instance, i.e. an
     * identifier without a version.
     * 
     * @return <code>Identifier</code>: A copy of this <code>Identifier</code>
     *         instance without a version tag.
     */
    public Identifier toGeneric() {

        if (version == null) {

            return this;
        }

        try {

            return createIdentifier(idEntry, null);
        } catch (IdentifierFormatException _) {

            return createTestIdentifier(id, null);
        }
    }

    /**
     * Returns a string representation of this <code>Identifier</code> instance.
     * 
     * @return <code>String</code>: This <code>Identifier</code> instance in the
     *         following string representation: <br>
     *         "<code>namespace:identifier</code>" <br>
     *         <b>OR</b> <br>
     *         "<code>namespace:identifier [a, b, c]</code>"
     */
    @Override
    public String toString() {

        return id + (version != null ? " " + version : "");
    }

    @Override
    public int hashCode() {

        return toString().hashCode();
    }

    @Override
    public boolean equals(Object val) {

        if (!(val instanceof Identifier) || !(val instanceof String)) {

            return false;
        }

        if (val instanceof Identifier) {

            return val == this;
        }

        return toString().equals(val);
    }

    /**
     * Determines if the full identifier string of the other <code>Identifier</code>
     * instance matches that of this <code>Identifier</code> instance.
     * 
     * @param identifier <code>Identifier</code>: The identifier to compare.
     * @return <code>boolean</code>: Whether or not the other identifier is
     *         compatible.
     */
    public boolean compatible(Identifier identifier) {

        if (identifier == null) {

            return false;
        }

        if (identifier == this) {

            return true;
        }

        return id.equals(identifier.id);
    }
}
