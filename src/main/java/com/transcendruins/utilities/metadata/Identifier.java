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
     * <code>[String, Identifier]</code>: A set of identifiers to be retrieved in
     * order to produce equivalent identifiers.
     */
    private static final HashMap<String, Identifier> IDENTIFIERS = new HashMap<>();

    /**
     * <code>Sorter&lt;Identifier&gt;</code>: A sorter of type
     * <code>Identifier</code>, created to sort a collection of
     * <code>Identifier</code> instances from highest to lowest.
     */
    public static final Sorter<Identifier> IDENTIFIER_SORTER = new Sorter<Identifier>() {

        @Override
        public Identifier sortSelector(Identifier newEntry, Identifier oldEntry) {

            return newEntry.highestVersion(oldEntry);
        }
    };

    /**
     * <code>String</code>: The full string representation of this
     * <code>Identifier</code>.
     */
    private final String full;

    /**
     * Retrieves the full identifier of this <code>Identifier</code> instance.
     * 
     * @return <code>String</code>: The <code>full</code> field of this
     *         <code>Identifier</code> instance.
     */
    public String getFull() {

        return full;
    }

    /**
     * <code>String</code>: The namespace string of this <code>Identifier</code>
     * instance.
     */
    private final String nameSpace;

    /**
     * Retrieves the namespace of this <code>Identifier</code> instance.
     * 
     * @return <code>String</code>: The <code>nameSpace</code> field of this
     *         <code>Identifier</code> instance.
     */
    public String getNameSpace() {

        return nameSpace;
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
     *                     which
     *                     to create this <code>Identifier</code> instance.
     * @param versionEntry <code>Version&lt;Version&gt;</code>: The entry
     *                     representing
     *                     the version of this <code>Identifier</code>
     *                     instance.
     * @throws IdentifierFormatException Thrown if the <code>identifier</code>
     *                                   perameter is in an improper format.
     */
    private Identifier(TracedEntry<String> entry, TracedEntry<Version> versionEntry) throws IdentifierFormatException {

        full = entry.getValue();
        this.versionEntry = versionEntry;
        version = versionEntry == null ? null : versionEntry.getValue();

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
     * @param version    <code>long[3]</code>: The vector to parse into a version
     *                   code.
     * @return <code>Identifier</code> The generated <code>Identifier</code>
     *         instance.
     */
    @Deprecated
    public static Identifier createTestIdentifier(String identifier, long[] version) {

        try {

            return createIdentifier(new TracedEntry<>(null, identifier),
                    new TracedEntry<>(null, version == null ? null : Version.createTestVersion(version)));
        } catch (LoggedException e) {

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
     * Sorts a <code>Collection</code> of <code>Identifier</code> instances by
     * highest versions.
     * 
     * @param identifiers <code>Collection&lt;Identifier&gt;</code>: The
     *                    <code>Collection</code> of <code>Identifier</code>
     *                    instances to sort.
     * @return <code>Collection&lt;Identifier&gt;</code>: The sorted
     *         <code>Collection</code> of <code>Identifier</code> instances.
     */
    public static Collection<Identifier> sortIdentifiersByHighestVersion(Collection<Identifier> identifiers) {

        // Convert the sorted list of identifiers into a map to strip duplicate
        // elements.
        return new LinkedHashSet<>(IDENTIFIER_SORTER.sort(identifiers));
    }

    /**
     * Returns a string representation of this <code>Identifier</code> instance.
     * 
     * @return <code>String</code>: This <code>Identifier</code> instance in the
     *         following string representation: <br>
     *         "<code>namespace:identifier</code>"
     *         <br>
     *         <b>OR</b> <br>
     *         "<code>namespace:identifier [a, b, c]</code>"
     */
    @Override
    public String toString() {

        return full + (getVersion() != null ? " " + getVersion() : "");
    }
}
