package com.transcendruins.utilities.json;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.IdentifierFormatException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyExceptionPathway;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.VersionBoundsException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.metadata.Metadata;
import com.transcendruins.utilities.metadata.Version;

/**
 * <code>TracedCollection</code>: A parent class representing a colection whose <code>parent.get()</code> method has been traced.
 */
public abstract class TracedCollection {

    /**
     * <code>PropertyExceptionPathway</code>: The filepath to this <code>TracedCollection</code> instance.
     */
    public final PropertyExceptionPathway path;

    /**
     * <code>ArrayList&lt;Object&gt;</code>: The path traced to this <code>TracedCollection</code> from inside the JSON file.
     */
    private final ArrayList<Object> internalPath;

    /**
     * Creates a new, empty instance of the <code>TracedCollection</code> class.
     * @param path <code>TracedPath</code>: The filepath to this <code>TracedCollection</code> instance.
     */
    public TracedCollection(TracedPath path) {

        this.path = new PropertyExceptionPathway(path, null, null);
        internalPath = new ArrayList<>();
    }

    /**
     * Creates a new instance of the <code>TracedCollection</code> class, extended by a single key from a parent collection.
     * @param path <code>PropertyExceptionPathway</code>: The path to trace from.
     */
    public TracedCollection(PropertyExceptionPathway path) {

        this.path = path;

        // Create a duplicate of the internal path of the 'path' perameter to avoid modifying the original.
        internalPath = new ArrayList<>(path.collection.internalPath);
        extend(path.key);
    }

    /**
     * Extends this <code>TracedCollection</code> instance by a single key.
     * @param key <code>Object</code>: The key to extend by.
     */
    public final void extend(Object key) {

        internalPath.add(key);
    }

    /**
     * Tests a value to ensure it is of the expected classes.
     * @param key <code>Object</code>: The key to check for in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param classes <code>Class...</code>: All classes which the entry can be an instance of.
     * @return <code>Clas&lt;?&gt;</code>: The class of the entry tested in this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the entry is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws PropertyTypeException Thrown if the entry is of an invalid type.
     */
    public final Class<?> retrieveRequiredClass(Object key, boolean nullCaseAllowed, Class<?>... classes) throws MissingPropertyException, PropertyTypeException {

        // Retrieve the pathway and value which will be used to parse into a new entry.
        PropertyExceptionPathway pathway = new PropertyExceptionPathway(path.path, this, key);
        Object value = getValue(key);
        TracedEntry<Object> entry = new TracedEntry<>(pathway, value);

        JSONOperator.isRequiredClass(entry, nullCaseAllowed, classes);

        return value.getClass();
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance,
     * optionally checking for a <code>null</code> case or an invalid class.
     * Returns the value of the <code>ifNull</code>
     * perameter if the retrieved value in this collection is <code>null</code>.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param ifNull <code>Object</code>: The value to return if the value retrieved from this <code>TracedCollection</code> instance is <code>null</code>.
     * @param classes <code>Class...</code>: All classes which the retrieved field can be an instance of.
     * @return <code>TracedEntry&lt;Object&g;</code>: The field retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws PropertyTypeException Thrown if the retrieved field is of an invalid type.
     */
    public final TracedEntry<Object> get(Object key, boolean nullCaseAllowed, Object ifNull, Class<?>... classes) throws MissingPropertyException, PropertyTypeException {

        // Retrieve the pathway and value which will be used to parse into a new entry.
        PropertyExceptionPathway pathway = new PropertyExceptionPathway(path.path, this, key);
        Object value = getValue(key);

        // If the null case is allowed and the value is null, return the ifNull value instead of the retrieved entry.
        if (nullCaseAllowed && value == null) {

            return new TracedEntry<>(pathway, ifNull);
        }

        TracedEntry<Object> entry = new TracedEntry<>(pathway, value);

        // An argument of no classes represents that any class is allowed, while an argument of one or more classes indicates that the retrieved entry must be one of them.
        if (classes.length > 0) {

            JSONOperator.isRequiredClass(entry, nullCaseAllowed, classes);
        } else {

            JSONOperator.isRequiredClass(entry, nullCaseAllowed, Object.class);
        }

        // If the value is a json object, parse it into the appropriate dictionary.
        if (value instanceof JSONObject) {

            return new TracedEntry<>(pathway, new TracedDictionary(entry));
        }

        // If the value is a json array, parse it into the appropriate array.
        if (value instanceof JSONArray) {

            return new TracedEntry<>(pathway, new TracedArray(entry));
        }

        return entry;
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a <code>TracedDictionary</code> value.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @return <code>TracedEntry&lt;TracedDictionary&gt;</code>: The dictionary retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>JSONObject</code> class.
     */
    public final TracedEntry<TracedDictionary> getAsDictionary(Object key, boolean nullCaseAllowed) throws MissingPropertyException, PropertyTypeException {

        // Retrieves the value associated with the key.
        TracedEntry<Object> retrievedVal = get(key, nullCaseAllowed, null, JSONObject.class);

        return new TracedEntry<>(retrievedVal.getPathway(), (TracedDictionary) retrievedVal.getValue());
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a <code>TracedArray</code> value.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @return <code>TracedEntry&lt;TracedArray&gt;</code>: The array retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>JSONArray</code> class.
     */
    public final TracedEntry<TracedArray> getAsArray(Object key, boolean nullCaseAllowed) throws MissingPropertyException, PropertyTypeException {

        // Retrieves the value associated with the key.
        TracedEntry<Object> retrievedVal = get(key, nullCaseAllowed, null, JSONArray.class);

        return new TracedEntry<>(retrievedVal.getPathway(), (TracedArray) retrievedVal.getValue());
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a <code>Vector</code> value.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param dimensions <code>int</code>: The dimensions of the vector to retrieve.
     * @return <code>TracedEntry&lt;TracedArray&gt;</code>: The vector retrieved from this <code>TracedCollection</code> instance.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>JSONArray</code> class.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws ArrayLengthException Thrown if the retrieved array does not have a length of <code>3</code>.
     */
    public final TracedEntry<Vector> getAsVector(Object key, boolean nullCaseAllowed, int dimensions) throws PropertyTypeException, MissingPropertyException, ArrayLengthException {

        // Retrieves the value associated with the key.
        TracedEntry<TracedArray> retrievedVal = getAsArray(key, nullCaseAllowed);
        TracedArray array = retrievedVal.getValue();

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true, and thus a null entry may be returned.
        if (array == null) {

            return new TracedEntry<>(retrievedVal.getPathway(), null);
        }

        if (array.size() != dimensions) {

            throw new ArrayLengthException(retrievedVal);
        }

        double[] vectorList = new double[dimensions];

        for (int i = 0; i < dimensions; i++) {

            try {

                TracedEntry<Double> vectorEntry = array.getAsDouble(i, false, null, null, null);
                vectorList[i] = vectorEntry.getValue();
            } catch (NumberBoundsException e) {}
        }

        return new TracedEntry<>(retrievedVal.getPathway(), new Vector(vectorList));
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a <code>Boolean</code> value.
     * @param key <code>Object</code>: The key to retrieve from this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param ifNull <code>Boolean</code>: The value to return if the value retrieved from this <code>TracedCollection</code> instance is <code>null</code>.
     * @return <code>TracedEntry&lt;Boolean&gt;</code>: The boolean retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>Boolean</code> class.
     */
    public final TracedEntry<Boolean> getAsBoolean(Object key, boolean nullCaseAllowed, Boolean ifNull) throws MissingPropertyException, PropertyTypeException {

        // Retrieves the value associated with the key.
        TracedEntry<Object> retrievedVal = get(key, nullCaseAllowed, ifNull, Boolean.class);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true, and thus a null entry may be returned.
        if (retrievedVal.getValue() == null) {

            return new TracedEntry<>(retrievedVal.getPathway(), ifNull);
        }

        return new TracedEntry<>(retrievedVal.getPathway(), (Boolean) retrievedVal.getValue());
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a <code>String</code> value.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param ifNull <code>String</code>: The value to return if the value retrieved from this <code>TracedCollection</code> instance is <code>null</code>.
     * @return <code>TracedEntry&lt;String&gt;</code>: The string retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>String</code> class.
     */
    public final TracedEntry<String> getAsString(Object key, boolean nullCaseAllowed, String ifNull) throws MissingPropertyException, PropertyTypeException {

        // Retrieves the value associated with the key.
        TracedEntry<Object> retrievedVal = get(key, nullCaseAllowed, ifNull, String.class);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true, and thus a null entry may be returned.
        if (retrievedVal.getValue() == null) {

            return new TracedEntry<>(retrievedVal.getPathway(), ifNull);
        }

        return new TracedEntry<>(retrievedVal.getPathway(), (String) retrievedVal.getValue());
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a <code>long</code> value.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param ifNull <code>Long</code>: The value to return if the value retrieved from this <code>TracedCollection</code> instance is <code>null</code>.
     * @return <code>TracedEntry&lt;Long&gt;</code>: The long retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>long</code> class.
     */
    public final TracedEntry<Long> getAsLong(Object key, boolean nullCaseAllowed, Long ifNull) throws MissingPropertyException, PropertyTypeException {

        // Retrieves the value associated with the key.
        TracedEntry<Object> retrievedVal = get(key, nullCaseAllowed, ifNull, Long.class);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true, and thus a null entry may be returned.
        if (retrievedVal.getValue() == null) {

            return new TracedEntry<>(retrievedVal.getPathway(), ifNull);
        }

        // Parse the retrieved value into a long entry.
        long num = (long) retrievedVal.getValue();
        return new TracedEntry<>(retrievedVal.getPathway(), num);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a <code>long</code> value.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param ifNull <code>Long</code>: The value to return if the value retrieved from this <code>TracedCollection</code> instance is <code>null</code>.
     * @param min <code>Long</code>: The minimum number bound. A <code>null</code> value represents no minimum bounds.
     * @param max <code>Long</code>: The maximum number bound. A <code>null</code> value represents no maximum bounds.
     * @return <code>TracedEntry&lt;Long&gt;</code>: The long retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>long</code> class.
     * @throws NumberBoundsException Thrown if the retrieved field is less than the <code>min</code> perameter or greater than the <code>max</code> perameter.
     */
    public final TracedEntry<Long> getAsLong(Object key, boolean nullCaseAllowed, Long ifNull, Long min, Long max) throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        // Retrieves the value associated with the key.
        TracedEntry<Long> retrievedVal = getAsLong(key, nullCaseAllowed, ifNull);
        Long num = retrievedVal.getValue();

        // If the number is outside of the expected bounds (if any), throw an exception stating such.
        if (num != null && min != null && (double) num < min || max != null && (double) num > max) {

            throw new NumberBoundsException(retrievedVal, min, max);
        }
        return retrievedVal;
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a <code>double</code> value.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param ifNull <code>Double</code>: The value to return if the value retrieved from this <code>TracedCollection</code> instance is <code>null</code>.
     * @return <code>TracedEntry&lt;Double&gt;</code>: The double retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>double</code> class or the <code>long</code> class.
     */
    public final TracedEntry<Double> getAsDouble(Object key, boolean nullCaseAllowed, Double ifNull) throws MissingPropertyException, PropertyTypeException {

        // Retrieves the value associated with the key.
        TracedEntry<Object> retrievedVal = get(key, nullCaseAllowed, ifNull, Double.class, Long.class);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true, and thus a null entry may be returned.
        if (retrievedVal.getValue() == null) {

            return new TracedEntry<>(retrievedVal.getPathway(), ifNull);
        }

        // Parse the retrieved value into a double entry.
        double num = (retrievedVal.getValue() instanceof Long) ? ((Long) retrievedVal.getValue()).doubleValue() : (double) retrievedVal.getValue();
        return new TracedEntry<>(retrievedVal.getPathway(), num);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a <code>double</code> value.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param ifNull <code>Double</code>: The value to return if the value retrieved from this <code>TracedCollection</code> instance is <code>null</code>.
     * @param min <code>Double</code>: The minimum number bound. A <code>null</code> value represents no minimum bounds.
     * @param max <code>Double</code>: The maximum number bound. A <code>null</code> value represents no maximum bounds.
     * @return <code>TracedEntry&lt;Double&gt;</code>: The double retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and the <code>nullCaseAllowed</code> perameter is <code>false</code>.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>double</code> class or the <code>long</code> class.
     * @throws NumberBoundsException Thrown if the retrieved field is less than the <code>min</code> perameter or greater than the <code>max</code> perameter.
     */
    public final TracedEntry<Double> getAsDouble(Object key, boolean nullCaseAllowed, Double ifNull, Double min, Double max) throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        TracedEntry<Double> retrievedVal = getAsDouble(key, nullCaseAllowed, ifNull);
        Double num = retrievedVal.getValue();

        // If the number is outside of the expected bounds (if any), throw an exception stating such.
        if (num != null && min != null && (double) num < min || max != null && (double) num > max) {

            throw new NumberBoundsException(retrievedVal, min, max);
        }
        return retrievedVal;
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a new <code>Identifier</code> instance. Note that the retrieved field MUST be of the <code>String</code> class and cannot be null.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @return <code>TracedEntry&lt;Identifier&gt;</code>: The identifier retrieved from this <code>TracedCollection</code> instance.
     * @throws IdentifierFormatException Thrown if the retrieved field is in an improper format.
     * @throws MissingPropertyException Thrown if the retrieved field is missing.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>String</code> class.
     */
    public final TracedEntry<Identifier> getAsIdentifier(Object key, boolean nullCaseAllowed) throws IdentifierFormatException, MissingPropertyException, PropertyTypeException {

        // Retrieves the string value associated with the key.
        TracedEntry<String> retrievedVal = getAsString(key, nullCaseAllowed, null);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true, and thus a null entry may be returned.
        if (retrievedVal.getValue() == null) {

            return new TracedEntry<>(retrievedVal.getPathway(), null);
        }

        // Parses the string into an identifier instance.
        return new TracedEntry<>(retrievedVal.getPathway(), Identifier.createIdentifier(retrievedVal, null));
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a new <code>Identifier</code> instance. Note that the retrieved field MUST be of the <code>String</code> class and cannot be null.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param version <code>TracedEntry&lt;Version&gt;</code>: The version used to create the new <code>Identifier</code> instance.
     * @return <code>TracedEntry&lt;Identifier&gt;</code>: The identifier retrieved from this <code>TracedCollection</code> instance.
     * @throws IdentifierFormatException Thrown if the retrieved field is in an improper format.
     * @throws MissingPropertyException Thrown if the retrieved field is missing.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>String</code> class.
     */
    public final TracedEntry<Identifier> getAsIdentifier(Object key, boolean nullCaseAllowed, TracedEntry<Version> version) throws IdentifierFormatException, MissingPropertyException, PropertyTypeException {

        // Retrieves the string value associated with the key.
        TracedEntry<String> retrievedVal = getAsString(key, nullCaseAllowed, null);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true, and thus a null entry may be returned.
        if (retrievedVal.getValue() == null) {

            return new TracedEntry<>(retrievedVal.getPathway(), null);
        }

        // Parses the string into an identifier instance.
        return new TracedEntry<>(retrievedVal.getPathway(), Identifier.createIdentifier(retrievedVal, version));
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a new <code>Version</code> instance. Note that the retrieved field MUST be of the <code>JSONArray</code> class with a length of 3 and cannot be null.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param negativeVectorValuesAllowed <code>boolean</code>: Whether or not negative version vector values are allowed.
     * @return <code>TracedEntry&lt;Version&gt;</code>: The version retrieved from this <code>TracedCollection</code> instance.
     * @throws PropertyTypeException Thrown if the retrieved field is not of the <code>JSONArray</code> class.
     * @throws PropertyTypeException Thrown if an index of the version vector is of the <code>Long</code> class.
     * @throws MissingPropertyException Thrown if the retrieved field is missing.
     * @throws ArrayLengthException Thrown if the version vector does not have a length of 3.
     * @throws VersionBoundsException Thrown if any vector value in the generated <code>Version</code> instance is negative.
     */
    public final TracedEntry<Version> getAsVersion(Object key, boolean nullCaseAllowed, boolean negativeVectorValuesAllowed) throws ArrayLengthException, MissingPropertyException, PropertyTypeException, NumberBoundsException, VersionBoundsException {

        // Retrieves the array value associated with the key.
        TracedEntry<TracedArray> retrievedVal = getAsArray(key, nullCaseAllowed);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true, and thus a null entry may be returned.
        if (retrievedVal.getValue() == null) {

            return new TracedEntry<>(retrievedVal.getPathway(), null);
        }

        // Parses the array into a version instance.
        return new TracedEntry<>(retrievedVal.getPathway(), Version.createVersion(retrievedVal, negativeVectorValuesAllowed));
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a new <code>Metadata</code> instance. Note that the retrieved field MUST be of the <code>JSONObject</code> class and cannot be null. This version WILL retrieve the version.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @param versionRangeAllowed <code>boolean</code>: Whether or not a range of versions is allowed in this <code>Metadata</code> instance.
     * @return <code>TracedEntry&lt;Metadata&gt;</code>: The metadata retrieved from this <code>TracedCollection</code> instance.
     * @throws PropertyTypeException Thrown if the type of a field in this <code>Metadata</code> instance is invalid.
     * @throws MissingPropertyException Thrown if the <code>key</code> perameter retrieved from this <code>Metadata</code> instance is missing.
     * @throws MissingPropertyException Thrown if a field is missing from this <code>Metadata</code> instance.
     * @throws ArrayLengthException Thrown if the version vector does not have a length of 3.
     * @throws IdentifierFormatException Thrown if the identifier field is in an improper format.
     * @throws VersionBoundsException Thrown if the minimum version bounds is greater than the maximum version bounds in the generated <code>Metadata</code> instance.
     * @throws NumberBoundsException Thrown if any vector value in the generated <code>Metadata</code> instance is negative.
     */
    public final TracedEntry<Metadata> getAsMetadata(Object key, boolean nullCaseAllowed, boolean versionRangeAllowed) throws ArrayLengthException, MissingPropertyException, PropertyTypeException, IdentifierFormatException, VersionBoundsException, NumberBoundsException {

        // Retrieves the dictionary value associated with the key.
        TracedEntry<TracedDictionary> retrievedVal = getAsDictionary(key, nullCaseAllowed);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true, and thus a null entry may be returned.
        if (retrievedVal.getValue() == null) {

            return new TracedEntry<>(retrievedVal.getPathway(), null);
        }

        // Parses the dictionary into a metadata instance.
        return new TracedEntry<>(retrievedVal.getPathway(), new Metadata(retrievedVal, versionRangeAllowed));
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses it into a new <code>Metadata</code> instance. Note that the retrieved field MUST be of the <code>JSONObject</code> class and cannot be null. This version WILL NOT retrieve the version.
     * @param key <code>Object</code>: The key whose entry to retrieve in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a <code>null</code> case should cause an exception.
     * @return <code>Metadata</code>: The generated <code>Metadata</code> instance.
     * @throws PropertyTypeException Thrown if the type of a field in this <code>Metadata</code> instance is invalid.
     * @throws MissingPropertyException Thrown if the <code>key</code> perameter retrieved from this <code>Metadata</code> instance is missing.
     * @throws MissingPropertyException Thrown if a field is missing from this <code>Metadata</code> instance.
     * @throws IdentifierFormatException Thrown if the identifier field is in an improper format.
     */
    public final TracedEntry<Metadata> getAsMetadata(Object key, boolean nullCaseAllowed) throws MissingPropertyException, PropertyTypeException, IdentifierFormatException {

        // Retrieves the dictionary value associated with the key.
        TracedEntry<TracedDictionary> retrievedVal = getAsDictionary(key, nullCaseAllowed);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true, and thus a null entry may be returned.
        if (retrievedVal.getValue() == null) {

            return new TracedEntry<>(retrievedVal.getPathway(), null);
        }

        // Parses the dictionary into a metadata instance.
        return new TracedEntry<>(retrievedVal.getPathway(), new Metadata(retrievedVal));
    }

    /**
     * Retrieves a field from the storage method of this <code>TracedCollection</code> instance.
     * @param key <code>Object</code>: The key to search using.
     * @return <code>Object</code>: The retrieved field
     */
    protected abstract Object getValue(Object key);

    /**
     * Retrieves the storage method behind this <code>TracedCollection</code> instance.
     */
    public abstract Object getCollection();

    /**
     * Returns whether or not a key value is present in this <code>TracedCollection</code> instance.
     * @param key <code>Object</code>: The key to search for.
     * @return <code>boolean</code>: Whether or not the key is present in this <code>TracedCollection</code> instance.
     */
    public abstract boolean containsKey(Object key);

    /**
     * Returns whether or not a field value is present in this <code>TracedCollection</code> instance.
     * @param key <code>Object</code>: The field to search for.
     * @return <code>boolean</code>: Whether or not the value is present in this <code>TracedCollection</code> instance.
     */
    public abstract boolean containsValue(Object key);

    /**
     * Returns the string representation of this <code>TracedCollection</code> instance.
     * @return <code>String</code>: This <code>TracedCollection</code> instance in the following string representation: <br>"<code>example, 0, path, 1, inside, 2, file</code>"
     */
    @Override
    public final String toString() {

        String str = internalPath.toString();
        str = str.substring(1, str.length() - 1);
        return str;
    }
}
