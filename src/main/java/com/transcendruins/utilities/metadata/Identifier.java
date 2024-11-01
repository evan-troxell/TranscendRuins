package com.transcendruins.utilities.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

import com.transcendruins.utilities.Sorter;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.IdentifierFormatException;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Identifier</code>: A class representing an identifier string.
 */
public final class Identifier {

    /**
     * <code>[String, Identifier]</code>: A set of identifiers to be retrieved in order to produce equivalent identifiers.
     */
    private static final HashMap<String, Identifier> IDENTIFIERS = new HashMap<>();

    /**
     * <code>Sorter&lt;Identifier&gt;</code>: A sorter of type <code>Identifier</code>, created to sort a collection of <code>Identifier</code> instances from highest to lowest.
     */
    public static final Sorter<Identifier> IDENTIFIER_SORTER = new Sorter<Identifier>() {

        @Override
        public Identifier sortSelector(Identifier newEntry, Identifier oldEntry) {

            return newEntry.highestVersion(oldEntry);
        }
    };

    /**
     * <code>String</code>: The full string representation of this <code>Identifier</code>.
     */
    private final String full;

    /**
     * <code>String</code>: The namespace string of this <code>Identifier</code> instance.
     */
    private final String nameSpace;

    /**
     * <code>String</code>: The identifier string of this <code>Identifier</code> instace.
     */
    public final String id;

    /**
     * <code>TracedEntry&lt;Version&gt;</code>: The version of this <code>Identifier</code> instance.
     */
    public final TracedEntry<Version> version;

    /**
     * Creates a new instance of the <code>Identifier</code> class.
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The entry from which to create this <code>Identifier</code> instance.
     * @param version <code>Version&lt;Version&gt;</code>: The entry representing the <code>Version</code> of this <code>Identifier</code> instance.
     * @throws IdentifierFormatException Thrown if the <code>identifier</code> perameter is in an improper format.
     */
    private Identifier(TracedEntry<String> entry, TracedEntry<Version> version) throws IdentifierFormatException {

        full = entry.getValue();
        this.version = version;

        if (!full.contains(":")) {

            throw new IdentifierFormatException(entry);
        }
        String[] splitIdentifier = full.split(":");

        if (splitIdentifier.length != 2) {

            throw new IdentifierFormatException(entry);
        }
        nameSpace = splitIdentifier[0];
        id = splitIdentifier[1];
        if (nameSpace.isEmpty() || id.isEmpty()) {

            throw new IdentifierFormatException(entry);
        }
    }

    /**
     * Generates an instance of the <code>Identifier</code> class. Once an identifier is generated, its instance will be used to represent that identifier string from then on.
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The entry from which to create the new <code>Identifier</code> instance.
     * @param version <code>Version&lt;Version&gt;</code>: The entry representing the <code>Version</code> of the new <code>Identifier</code> instance.
     * @return <code>Identifier</code>: The generated <code>Identifier</code> instance.
     * @throws IdentifierFormatException Thrown if the <code>identifier</code> perameter is in an improper format.
     */
    public static Identifier createIdentifier(TracedEntry<String> entry, TracedEntry<Version> version) throws IdentifierFormatException {

        Identifier newIdentifier = new Identifier(entry, version);
        String identifierString = newIdentifier.toString();

        if (!IDENTIFIERS.containsKey(identifierString)) {

            IDENTIFIERS.put(identifierString, newIdentifier);
            return newIdentifier;
        }
        return IDENTIFIERS.get(identifierString);
    }

    /**
     * Generates an instance of the <code>Identifier</code> class using only an identifier string and a version vector. This method is only to be used for testing purposes.
     * @param identifier <code>String</code>: The identifier <code>String</code> to parse into an <code>Identifier</code> instance.
     * @param version <code>long[3]</code>: The vector to parse into a version code.
     * @return <code>Identifier</code> The generated <code>Identifier</code> instance.
     */
    @Deprecated
    public static Identifier createTestIdentifier(String identifier, long[] version) {

        try {

            return createIdentifier(new TracedEntry<>(null, identifier), new TracedEntry<>(null, version == null ? null : Version.createTestVersion(version)));
        } catch (LoggedException e) {

            return null;
        }
    }

    /**
     * Retrieves the <code>Version</code> of this <code>Identifier</code> instance.
     * @return <code>Version</code>: The <code>Version</code> of this <code>Identifier</code> instance.
     */
    public Version getVersion() {

        return version == null ? null : version.getValue();
    }

    /**
     * Determines which <code>Identifier</code> instance between this <code>Identifier</code> instance and another has the lowest <code>Version</code> value.
     * @param identifier <code>Identifier</code>: The identifier to compare against.
     * @return <code>Identifier</code>: The identifier with the lowest <code>Version</code> value.
     */
    public Identifier lowestVersion(Identifier identifier) {

        // Checks for null version property value.
        if (identifier == null || identifier.version == null) {

            return this;
        }

        if (identifier.version == null && version == null) {

            return null;
        }

        if (version == null) {

            return identifier;
        }

        long[] selfVersion = getVersion().vector;
        long[] identifierVersion = identifier.getVersion().vector;

        // Compares the first version values.
        if (selfVersion[0] < identifierVersion[0]) {

            return this;
        }

        // Compares the second version values.
        if (selfVersion[0] == identifierVersion[0] && selfVersion[1] < identifierVersion[1]) {

            return this;
        }

        // Compares the third version values.
        if (selfVersion[0] == identifierVersion[0] && selfVersion[1] == identifierVersion[1] && selfVersion[2] < identifierVersion[2]) {

            return this;
        }

        return identifier;
    }

    /**
     * Determines which <code>Identifier</code> instance between this <code>Identifier</code> instance and another has the highest <code>Version</code> value.
     * @param identifier <code>Identifier</code>: The identifier to compare against.
     * @return <code>Identifier</code>: The identifier with the highest <code>Version</code> value.
     */
    public Identifier highestVersion(Identifier identifier) {

        // Checks for null version property values.
        if (identifier == null || identifier.version == null) {

            return this;
        }

        if (identifier.version == null && version == null) {

            return null;
        }

        if (version == null) {

            return identifier;
        }

        long[] selfVersion = getVersion().vector;
        long[] identifierVersion = identifier.getVersion().vector;

        // Compares the first version values.
        if (selfVersion[0] < identifierVersion[0]) {

            return identifier;
        }

        // Compares the second version values.
        if (selfVersion[0] == identifierVersion[0] && selfVersion[1] < identifierVersion[1]) {

            return identifier;
        }

        // Compares the third version values.
        if (selfVersion[0] == identifierVersion[0] && selfVersion[1] == identifierVersion[1] && selfVersion[2] < identifierVersion[2]) {

            return identifier;
        }

        return this;
    }

    /**
     * Sorts a <code>Collection</code> of <code>Identifier</code> instances by highest versions.
     * @param identifiers <code>Collection&lt;Identifier&gt;</code>: The <code>Collection</code> of <code>Identifier</code> instances to sort.
     * @return <code>Collection&lt;Identifier&gt;</code>: The sorted <code>Collection</code> of <code>Identifier</code> instances.
     */
    public static Collection<Identifier> sortIdentifiersByHighestVersion(Collection<Identifier> identifiers) {

        // Convert the sorted list of identifiers into a map to strip duplicate elements.
        return new LinkedHashSet<>(IDENTIFIER_SORTER.sort(identifiers));
    }

    /**
     * Retrieves the full identifier of this <code>Identifier</code> instance.
     * @return <code>String</code>: The <code>full</code> property of this <code>Identifier</code> instance.
     */
    public String getFull() {

        return full;
    }

    /**
     * Retrieves the namespace of this <code>Identifier</code> instance.
     * @return <code>String</code>: The <code>nameSpace</code> property of this <code>Identifier</code> instance.
     */
    public String getNameSpace() {

        return nameSpace;
    }

    /**
     * Returns a string representation of this <code>Identifier</code> instance.
     * @return <code>String</code>: This <code>Identifier</code> instance in the following string representation: <br>"<code>namespace:identifier</code>"
     * <br> <b>OR</b> <br>"<code>namespace:identifier [a, b, c]</code>"
     */
    @Override
    public String toString() {

        return full + (getVersion() != null ? " " + getVersion() : "");
    }
}
