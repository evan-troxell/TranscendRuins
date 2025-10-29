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

package com.transcendruins.utilities.json;

import java.awt.Color;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.jme3.math.ColorRGBA;
import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyExceptionPathway;
import com.transcendruins.utilities.exceptions.propertyexceptions.VersionBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.identifierexceptions.IdentifierFormatException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.files.TracedPath;
import static com.transcendruins.utilities.json.TracedCollection.JSONType.ARRAY;
import static com.transcendruins.utilities.json.TracedCollection.JSONType.BOOLEAN;
import static com.transcendruins.utilities.json.TracedCollection.JSONType.DICT;
import static com.transcendruins.utilities.json.TracedCollection.JSONType.DOUBLE;
import static com.transcendruins.utilities.json.TracedCollection.JSONType.LONG;
import static com.transcendruins.utilities.json.TracedCollection.JSONType.NULL;
import static com.transcendruins.utilities.json.TracedCollection.JSONType.STRING;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.metadata.Version;

/**
 * <code>TracedCollection</code>: A parent class representing a collection whose
 * <code>parent.get()</code> method has been traced.
 */
public abstract class TracedCollection {

    public static enum JSONType {

        BOOLEAN, LONG, DOUBLE, STRING, DICT, ARRAY, NULL
    }

    /**
     * <code>PropertyExceptionPathway</code>: The path to this
     * <code>TracedCollection</code> instance.
     */
    private final PropertyExceptionPathway pathway;

    /**
     * Retrieves the path to this <code>TracedCollection</code> instance.
     * 
     * @return <code>PropertyExceptionPathway</code>: The <code>pathway</code> field
     *         of this <code>TracedCollection</code> instance.
     */
    public PropertyExceptionPathway getPathway() {

        return pathway;
    }

    public PropertyExceptionPathway extend(Object key) {

        return pathway.extend(key);
    }

    /**
     * Creates a new, empty instance of the <code>TracedCollection</code> class.
     * 
     * @param path <code>TracedPath</code>: The filepath to this
     *             <code>TracedCollection</code> instance.
     */
    public TracedCollection(TracedPath path) {

        pathway = new PropertyExceptionPathway(path);
    }

    /**
     * Creates a new instance of the <code>TracedCollection</code> class, extended
     * by a single key from a parent collection.
     * 
     * @param pathway <code>PropertyExceptionPathway</code>: The path to trace from.
     */
    public TracedCollection(PropertyExceptionPathway pathway) {

        this.pathway = pathway;
    }

    /**
     * Determines the JSON type of a given value.
     * 
     * @param val <code>Object</code>: The value to determine the type of.
     * @return <code>JSONType</code>: The determined JSON type.
     */
    public static final JSONType typeOf(Object val) {

        return switch (val) {

        case null -> JSONType.NULL;

        case Boolean _ -> JSONType.BOOLEAN;

        case Long _ -> JSONType.LONG;
        case Double _ -> JSONType.DOUBLE;

        case String _ -> JSONType.STRING;

        case @SuppressWarnings("rawtypes") Map _ -> JSONType.DICT;
        case @SuppressWarnings("rawtypes") List _ -> JSONType.ARRAY;

        default -> JSONType.NULL;
        };
    }

    /**
     * Retrieves the JSON type of a value associated with a given key.
     * 
     * @param key <code>Object</code>: The key to retrieve the value for.
     * @return <code>JSONType</code>: The JSON type of the value.
     */
    public final JSONType getType(Object key) {

        return typeOf(getValue(key));
    }

    @FunctionalInterface
    public interface EntryOperator<K, T> {

        T apply(TracedEntry<K> entry) throws LoggedException;
    }

    @FunctionalInterface
    public interface EntryBuilder<K> {

        TracedEntry<K> create(TracedCollection collection, Object key) throws LoggedException;
    }

    /**
     * <code>TypeCase&lt;K, T&gt;</code>: An abstract class representing a type case
     * for retrieving and processing entries from a <code>TracedCollection</code>.
     * 
     * @param <K> <code>Object</code>: The type of the key.
     * @param <T> <code>Object</code>: The type of the value.
     */
    public final class TypeCase<K, T> {

        private final EntryOperator<K, T> onCall;
        private final EntryBuilder<K> getEntry;

        private final JSONType[] types;

        /**
         * Creates a new instance of the <code>TypeCase</code> class.
         * 
         * @param onCall   <code>EntryOperator&lt;K, T&gt;</code>: The operator to apply
         *                 to the entry.
         * @param getEntry <code>EntryBuilder&lt;K&gt;</code>: The builder to create the
         *                 entry.
         * @param types    <code>JSONType...</code>: The types for which this
         *                 <code>TypeCase</code> instance is applicable.
         */
        private TypeCase(EntryOperator<K, T> onCall, EntryBuilder<K> getEntry, JSONType... types) {

            this.onCall = onCall;
            this.getEntry = getEntry;
            this.types = types;
        }

        /**
         * Checks if the type case is valid for the given JSON type.
         * 
         * @param compare <code>JSONType</code>: The JSON type to compare.
         * @return <code>boolean</code>: Whether the type case is valid.
         */
        public final boolean isValid(JSONType compare) {

            // If no types were provided, assume typing does not matter.
            if (types.length == 0) {

                return true;
            }

            // Check if any of the provided types are applicable.
            return List.of(types).contains(compare);
        }

        /**
         * Retrieves the value for the given key.
         * 
         * @param collection <code>TracedCollection</code>: The collection to create the
         *                   entry from.
         * @param key        <code>Object</code>: The key to retrieve the value for.
         * @return <code>T</code>: The retrieved value.
         * @throws LoggedException Thrown if an exception is raised while retrieving the
         *                         value.
         */
        public final T get(TracedCollection collection, Object key) throws LoggedException {

            return onCall.apply(getEntry.create(collection, key));
        }
    }

    /**
     * Creates a boolean type case.
     * 
     * @param <T>      <code>Object</code>: The type of the value.
     * @param onCall   <code>EntryOperator&lt;Boolean, T&gt;</code>: The operator to
     *                 apply to the entry.
     * @param getEntry <code>EntryBuilder&lt;Boolean&gt;</code>: The builder to
     *                 create the entry.
     * @return <code>TypeCase&lt;Boolean, T&gt;</code>: The created boolean type
     *         case.
     */
    public <T> TypeCase<Boolean, T> booleanCase(EntryOperator<Boolean, T> onCall, EntryBuilder<Boolean> getEntry) {

        return new TypeCase<>(onCall, getEntry, BOOLEAN);
    }

    /**
     * Creates a boolean type case with a default entry builder.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;Boolean, T&gt;</code>: The operator to
     *               apply to the entry.
     * @return <code>TypeCase&lt;Boolean, T&gt;</code>: The created boolean type
     *         case.
     */
    public <T> TypeCase<Boolean, T> booleanCase(EntryOperator<Boolean, T> onCall) {

        return booleanCase(onCall, (collection, key) -> collection.getAsBoolean(key, false, null));
    }

    /**
     * Creates a double type case.
     * 
     * @param <T>      <code>Object</code>: The type of the value.
     * @param onCall   <code>EntryOperator&lt;Double, T&gt;</code>: The operator to
     *                 apply to the entry.
     * @param getEntry <code>EntryBuilder&lt;Double&gt;</code>: The builder to
     *                 create the entry.
     * @return <code>TypeCase&lt;Double, T&gt;</code>: The created double type case.
     */
    public <T> TypeCase<Double, T> doubleCase(EntryOperator<Double, T> onCall, EntryBuilder<Double> getEntry) {

        return new TypeCase<>(onCall, getEntry, DOUBLE, LONG);
    }

    /**
     * Creates a double type case with a default entry builder.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;Double, T&gt;</code>: The operator to
     *               apply to the entry.
     * @return <code>TypeCase&lt;Double, T&gt;</code>: The created double type case.
     */
    public <T> TypeCase<Double, T> doubleCase(EntryOperator<Double, T> onCall) {

        return doubleCase(onCall, (collection, key) -> collection.getAsDouble(key, false, null));
    }

    /**
     * Creates a float type case.
     * 
     * @param <T>      <code>Object</code>: The type of the value.
     * @param onCall   <code>EntryOperator&lt;Float, T&gt;</code>: The operator to
     *                 apply to the entry.
     * @param getEntry <code>EntryBuilder&lt;Float&gt;</code>: The builder to create
     *                 the entry.
     * @return <code>TypeCase&lt;Float, T&gt;</code>: The created float type case.
     */
    public <T> TypeCase<Float, T> floatCase(EntryOperator<Float, T> onCall, EntryBuilder<Float> getEntry) {

        return new TypeCase<>(onCall, getEntry, DOUBLE, LONG);
    }

    /**
     * Creates a float type case with a default entry builder.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;Float, T&gt;</code>: The operator to
     *               apply to the entry.
     * @return <code>TypeCase&lt;Float, T&gt;</code>: The created float type case.
     */
    public <T> TypeCase<Float, T> floatCase(EntryOperator<Float, T> onCall) {

        return floatCase(onCall, (collection, key) -> collection.getAsFloat(key, false, null));
    }

    /**
     * Creates a long type case.
     * 
     * @param <T>      <code>Object</code>: The type of the value.
     * @param onCall   <code>EntryOperator&lt;Long, T&gt;</code>: The operator to
     *                 apply to the entry.
     * @param getEntry <code>EntryBuilder&lt;Long&gt;</code>: The builder to create
     *                 the entry.
     * @return <code>TypeCase&lt;Long, T&gt;</code>: The created long type case.
     */
    public <T> TypeCase<Long, T> longCase(EntryOperator<Long, T> onCall, EntryBuilder<Long> getEntry) {

        return new TypeCase<>(onCall, getEntry, LONG);
    }

    /**
     * Creates a long type case with a default entry builder.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;Long, T&gt;</code>: The operator to
     *               apply to the entry.
     * @return <code>TypeCase&lt;Long, T&gt;</code>: The created long type case.
     */
    public <T> TypeCase<Long, T> longCase(EntryOperator<Long, T> onCall) {

        return longCase(onCall, (collection, key) -> collection.getAsLong(key, false, null));
    }

    /**
     * Creates an integer type case.
     * 
     * @param <T>      <code>Object</code>: The type of the value.
     * @param onCall   <code>EntryOperator&lt;Integer, T&gt;</code>: The operator to
     *                 apply to the entry.
     * @param getEntry <code>EntryBuilder&lt;Integer&gt;</code>: The builder to
     *                 create the entry.
     * @return <code>TypeCase&lt;Integer, T&gt;</code>: The created integer type
     *         case.
     */
    public <T> TypeCase<Integer, T> intCase(EntryOperator<Integer, T> onCall, EntryBuilder<Integer> getEntry) {

        return new TypeCase<>(onCall, getEntry, LONG);
    }

    /**
     * Creates an integer type case with a default entry builder.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;Integer, T&gt;</code>: The operator to
     *               apply to the entry.
     * @return <code>TypeCase&lt;Integer, T&gt;</code>: The created integer type
     *         case.
     */
    public <T> TypeCase<Integer, T> intCase(EntryOperator<Integer, T> onCall) {

        return intCase(onCall, (collection, key) -> collection.getAsInteger(key, false, null));
    }

    /**
     * Creates a string type case.
     * 
     * @param <T>      <code>Object</code>: The type of the value.
     * @param onCall   <code>EntryOperator&lt;String, T&gt;</code>: The operator to
     *                 apply to the entry.
     * @param getEntry <code>EntryBuilder&lt;String&gt;</code>: The builder to
     *                 create the entry.
     * @return <code>TypeCase&lt;String, T&gt;</code>: The created string type case.
     */
    public <T> TypeCase<String, T> stringCase(EntryOperator<String, T> onCall, EntryBuilder<String> getEntry) {

        return new TypeCase<>(onCall, getEntry, STRING);
    }

    /**
     * Creates a string type case with a default entry builder.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;String, T&gt;</code>: The operator to
     *               apply to the entry.
     * @return <code>TypeCase&lt;String, T&gt;</code>: The created string type case.
     */
    public <T> TypeCase<String, T> stringCase(EntryOperator<String, T> onCall) {

        return stringCase(onCall, (collection, key) -> collection.getAsString(key, false, null));
    }

    /**
     * Creates a scalar type case.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;Object, T&gt;</code>: The operator to
     *               apply to the entry.
     * @return <code>TypeCase&lt;Object, T&gt;</code>: The created scalar type case.
     */
    public <T> TypeCase<Object, T> scalarCase(EntryOperator<Object, T> onCall) {

        return new TypeCase<>(onCall, (collection, key) -> collection.getAsScalar(key, false, null), BOOLEAN, LONG,
                DOUBLE, STRING);
    }

    /**
     * Creates a dictionary type case.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;TracedDictionary, T&gt;</code>: The
     *               operator to apply to the entry.
     * @return <code>TypeCase&lt;TracedDictionary, T&gt;</code>: The created
     *         dictionary type case.
     */
    public <T> TypeCase<TracedDictionary, T> dictCase(EntryOperator<TracedDictionary, T> onCall) {

        return new TypeCase<>(onCall, (collection, key) -> collection.getAsDict(key, false), DICT);
    }

    /**
     * Creates an array type case.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;TracedArray, T&gt;</code>: The operator
     *               to apply to the entry.
     * @return <code>TypeCase&lt;TracedArray, T&gt;</code>: The created array type
     *         case.
     */
    public <T> TypeCase<TracedArray, T> arrayCase(EntryOperator<TracedArray, T> onCall) {

        return new TypeCase<>(onCall, (collection, key) -> collection.getAsArray(key, false), ARRAY);
    }

    /**
     * Creates a collection type case.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;TracedCollection, T&gt;</code>: The
     *               operator to apply to the entry.
     * @return <code>TypeCase&lt;TracedCollection, T&gt;</code>: The created
     *         collection type case.
     */
    public <T> TypeCase<TracedCollection, T> collectionCase(EntryOperator<TracedCollection, T> onCall) {

        return new TypeCase<>(onCall, (collection, key) -> collection.getAsCollection(key, false), DICT, ARRAY);
    }

    /**
     * Creates a vector type case.
     * 
     * @param <T>      <code>Object</code>: The type of the value.
     * @param onCall   <code>EntryOperator&lt;Vector, T&gt;</code>: The operator to
     *                 apply to the entry.
     * @param getEntry <code>EntryBuilder&lt;Vector&gt;</code>: The builder to
     *                 create the entry.
     * @return <code>TypeCase&lt;Vector, T&gt;</code>: The created vector type case.
     */
    public <T> TypeCase<Vector, T> vectorCase(EntryOperator<Vector, T> onCall, EntryBuilder<Vector> getEntry) {

        return new TypeCase<>(onCall, getEntry, ARRAY);
    }

    /**
     * Creates an identifier type case.
     * 
     * @param <T>      <code>Object</code>: The type of the value.
     * @param onCall   <code>EntryOperator&lt;Identifier, T&gt;</code>: The operator
     *                 to apply to the entry.
     * @param getEntry <code>EntryBuilder&lt;Identifier&gt;</code>: The builder to
     *                 create the entry.
     * @return <code>TypeCase&lt;Identifier, T&gt;</code>: The created identifier
     *         type case.
     */
    public <T> TypeCase<Identifier, T> identifierCase(EntryOperator<Identifier, T> onCall,
            EntryBuilder<Identifier> getEntry) {

        return new TypeCase<>(onCall, getEntry, STRING);
    }

    /**
     * Creates a version type case.
     * 
     * @param <T>      <code>Object</code>: The type of the value.
     * @param onCall   <code>EntryOperator&lt;Version, T&gt;</code>: The operator to
     *                 apply to the entry.
     * @param getEntry <code>EntryBuilder&lt;Version&gt;</code>: The builder to
     *                 create the entry.
     * @return <code>TypeCase&lt;Version, T&gt;</code>: The created version type
     *         case.
     */
    public <T> TypeCase<Version, T> versionCase(EntryOperator<Version, T> onCall, EntryBuilder<Version> getEntry) {

        return new TypeCase<>(onCall, getEntry, ARRAY);
    }

    /**
     * Creates an asset presets type case.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;AssetPresets, T&gt;</code>: The operator
     *               to apply to the entry.
     * @param type   <code>AssetType</code>: The type of asset presets to retrieve.
     * @return <code>TypeCase&lt;AssetPresets, T&gt;</code>: The asset presets type
     *         case.
     */
    public <T> TypeCase<AssetPresets, T> presetsCase(EntryOperator<AssetPresets, T> onCall, AssetType type) {

        return new TypeCase<>(onCall, (collection, key) -> collection.getAsPresets(key, false, type), STRING, DICT);
    }

    /**
     * Creates a script type case.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;TRScript, T&gt;</code>: The operator to
     *               apply to the entry.
     * @return <code>TypeCase&lt;TRScript, T&gt;</code>: The script type case.
     */
    public <T> TypeCase<TRScript, T> scriptCase(EntryOperator<TRScript, T> onCall) {

        return new TypeCase<>(onCall, (collection, key) -> collection.getAsScript(key, false), BOOLEAN, LONG, DOUBLE,
                STRING, DICT);
    }

    /**
     * Creates a null type case.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;Void, T&gt;</code>: The operator to
     *               apply to the entry.
     * @return <code>TypeCase&lt;Void, T&gt;</code>: The created null type case.
     */
    public <T> TypeCase<Void, T> nullCase(EntryOperator<Void, T> onCall) {

        return new TypeCase<>(onCall, (collection, key) -> new TracedEntry<>(collection.extend(key), null), NULL);
    }

    /**
     * Creates a default type case.
     * 
     * @param <T>    <code>Object</code>: The type of the value.
     * @param onCall <code>EntryOperator&lt;Object, T&gt;</code>: The operator to
     *               apply to the entry.
     * @return <code>TypeCase&lt;Object, T&gt;</code>: The created default type
     *         case.
     */
    public <T> TypeCase<Object, T> defaultCase(EntryOperator<Object, T> onCall) {

        return new TypeCase<>(onCall, (collection, key) -> collection.get(key, true, null));
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance.
     * 
     * @param key <code>Object</code>: The key whose entry to retrieve in this
     *            <code>TracedCollection</code> instance.
     * @return <code>TracedEntry&lt;?&gt;</code>: The field retrieved from this
     *         <code>TracedCollection</code> instance.
     */
    public final TracedEntry<?> getEntry(Object key) {

        PropertyExceptionPathway extended = extend(key);
        Object value = getValue(key);

        return new TracedEntry<>(extended, value);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance, handling
     * each separate type case in the provided list of cases.
     * 
     * @param <T>   The type of the value to retrieve.
     * @param key   <code>Object</code>: The key whose entry to retrieve in this
     *              <code>TracedCollection</code> instance.
     * @param cases <code>List&lt;TypeCase&lt;?, T&gt;&gt;</code>: The list of type
     *              cases to handle the retrieval of the entry.
     * @return <code>T</code>: The value retrieved from this
     *         <code>TracedCollection</code> instance.
     * @throws LoggedException Thrown if none of the type cases are valid for the
     *                         retrieved value or if an applied case raised an
     *                         exception.
     */
    public final <T> T get(Object key, List<TypeCase<?, T>> cases) throws LoggedException {

        JSONType type = getType(key);
        for (TypeCase<?, T> typeCase : cases) {

            if (typeCase.isValid(type)) {

                return typeCase.get(this, key);
            }
        }

        // Raise an error if it is missing.
        TracedEntry<?> entry = get(key, false, null);

        // Raise an error if it is the wrong type.
        throw new PropertyTypeException(entry);
    }

    /**
     * Computes a function using field from this <code>TracedCollection</code>
     * instance, handling each separate type case in the provided list of cases.
     * 
     * @param key   <code>Object</code>: The key whose entry to compute in this
     *              <code>TracedCollection</code> instance.
     * @param cases <code>List&lt;TypeCase&lt;?, T&gt;&gt;</code>: The list of type
     *              cases to handle the computation of the entry.
     * @throws LoggedException Thrown if none of the type cases are valid for the
     *                         retrieved value or if an applied case raised an
     *                         exception.
     */
    public final void compute(Object key, List<TypeCase<?, Void>> cases) throws LoggedException {

        get(key, cases);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance,
     * optionally checking for a <code>null</code> case or an invalid class. Returns
     * the value of the <code>ifNull</code> perameter if the retrieved value in this
     * collection is <code>null</code>.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>Object</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @param classes         <code>Class...</code>: All classes which the retrieved
     *                        field can be an instance of.
     * @return <code>TracedEntry&lt;?&gt;</code>: The field retrieved from this
     *         <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> perameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is of an
     *                                  invalid type.
     */
    private TracedEntry<Object> get(Object key, boolean nullCaseAllowed, Object ifNull, JSONType... types)
            throws MissingPropertyException, PropertyTypeException {

        Object value = getValue(key);
        JSONType type = typeOf(value);

        TracedEntry<Object> entry = getEntry(key).cast(value != null ? value : ifNull);

        // Check for a null case so another error is not thrown.
        if (type == JSONType.NULL) {

            if (nullCaseAllowed) {

                return entry;
            }

            throw new MissingPropertyException(entry);
        }

        // Compare the value to valid types.
        if (types.length > 0 && !Stream.of(types).anyMatch(type::equals)) {

            throw new PropertyTypeException(entry);
        }

        return entry;
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>TracedDictionary</code> value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @return <code>TracedEntry&lt;TracedDictionary&gt;</code>: The dictionary
     *         retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> perameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  <code>JSONObject</code> class.
     */
    public final TracedEntry<TracedDictionary> getAsDict(Object key, boolean nullCaseAllowed)
            throws MissingPropertyException, PropertyTypeException {

        // Retrieves the value associated with the key.
        TracedEntry<?> entry = get(key, nullCaseAllowed, null, JSONType.DICT);

        return entry.cast(entry.containsValue() ? new TracedDictionary(entry) : null);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>TracedArray</code> value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @return <code>TracedEntry&lt;TracedArray&gt;</code>: The array retrieved from
     *         this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> perameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  <code>JSONArray</code> class.
     */
    public final TracedEntry<TracedArray> getAsArray(Object key, boolean nullCaseAllowed)
            throws MissingPropertyException, PropertyTypeException {

        // Retrieves the value associated with the key.
        TracedEntry<?> entry = get(key, nullCaseAllowed, null, JSONType.ARRAY);

        return entry.cast(entry.containsValue() ? new TracedArray(entry) : null);
    }

    public final TracedEntry<TracedCollection> getAsCollection(Object key, boolean nullCaseAllowed)
            throws MissingPropertyException, PropertyTypeException {

        JSONType type = getType(key);

        TracedEntry<? extends TracedCollection> entry = (type == JSONType.DICT) ? getAsDict(key, nullCaseAllowed)
                : getAsArray(key, nullCaseAllowed);
        TracedCollection collection = entry.getValue();

        return entry.cast(collection);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>Vector</code> value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param dimensions      <code>int</code>: The dimensions of the vector to
     *                        retrieve.
     * @return <code>TracedEntry&lt;Vector&gt;</code>: The vector retrieved from
     *         this <code>TracedCollection</code> instance.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  <code>JSONArray</code> class.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> perameter
     *                                  is <code>false</code>.
     * @throws CollectionSizeException  Thrown if the retrieved array does not have
     *                                  a length of <code>dimensions</code>.
     * @throws NumberBoundsException
     */
    public final TracedEntry<Vector> getAsVector(Object key, boolean nullCaseAllowed, int dimensions)
            throws PropertyTypeException, MissingPropertyException, CollectionSizeException, NumberBoundsException {

        // Retrieves the value associated with the key.
        TracedEntry<TracedArray> entry = getAsArray(key, nullCaseAllowed);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true,
        // and thus a null entry may be returned.
        if (!entry.containsValue()) {

            return entry.cast(null);
        }

        TracedArray array = entry.getValue();

        if (array.size() != dimensions) {

            throw new CollectionSizeException(entry, array);
        }

        double[] vectorList = new double[dimensions];

        for (int i = 0; i < dimensions; i++) {

            TracedEntry<Double> vectorEntry = array.getAsDouble(i, false, null);
            vectorList[i] = vectorEntry.getValue();
        }

        return entry.cast(new Vector(vectorList));
    }

    public final TracedEntry<Vector> getAsVector(Object key, boolean nullCaseAllowed, int dimensions, Vector min,
            Vector max)
            throws PropertyTypeException, MissingPropertyException, CollectionSizeException, NumberBoundsException {

        // Retrieves the value associated with the key.
        TracedEntry<TracedArray> entry = getAsArray(key, nullCaseAllowed);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true,
        // and thus a null entry may be returned.
        if (!entry.containsValue()) {

            return entry.cast(null);
        }

        TracedArray array = entry.getValue();

        if (array.size() != dimensions) {

            throw new CollectionSizeException(entry, array);
        }

        double[] vectorList = new double[dimensions];

        for (int i = 0; i < dimensions; i++) {

            double minI = (min != null) ? min.get(i) : Double.NEGATIVE_INFINITY;
            double maxI = (max != null) ? max.get(i) : Double.POSITIVE_INFINITY;

            TracedEntry<Double> vectorEntry = array.getAsDouble(i, false, null, num -> minI <= num && num <= maxI);
            vectorList[i] = vectorEntry.getValue();
        }

        return entry.cast(new Vector(vectorList));
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>Boolean</code> value.
     * 
     * @param key             <code>Object</code>: The key to retrieve from this
     *                        <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>Boolean</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @return <code>TracedEntry&lt;Boolean&gt;</code>: The boolean retrieved from
     *         this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> perameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  <code>Boolean</code> class.
     */
    public final TracedEntry<Boolean> getAsBoolean(Object key, boolean nullCaseAllowed, Boolean ifNull)
            throws MissingPropertyException, PropertyTypeException {

        // Retrieves the value associated with the key.
        TracedEntry<?> entry = get(key, nullCaseAllowed, ifNull, JSONType.BOOLEAN);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true,
        // and thus a null entry may be returned.
        if (!entry.containsValue()) {

            return entry.cast(ifNull);
        }

        return entry.cast((Boolean) entry.getValue());
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>String</code> value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>String</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @return <code>TracedEntry&lt;String&gt;</code>: The string retrieved from
     *         this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> perameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  <code>String</code> class.
     */
    public final TracedEntry<String> getAsString(Object key, boolean nullCaseAllowed, String ifNull)
            throws MissingPropertyException, PropertyTypeException {

        // Retrieves the value associated with the key.
        TracedEntry<?> entry = get(key, nullCaseAllowed, ifNull, JSONType.STRING);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true,
        // and thus a null entry may be returned.
        if (!entry.containsValue()) {

            return entry.cast(ifNull);
        }

        return entry.cast((String) entry.getValue());
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>ZonedDateTime</code> value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @return <code>TracedEntry&lt;ZonedDateTime&gt;</code>: The date retrieved
     *         from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> perameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  expected type.
     * @throws UnexpectedValueException Thrown if the retrieved field is not a valid
     *                                  date.
     */
    public final TracedEntry<ZonedDateTime> getAsTimestamp(Object key, boolean nullCaseAllowed)
            throws MissingPropertyException, PropertyTypeException, UnexpectedValueException {

        TracedEntry<String> entry = getAsString(key, nullCaseAllowed, null);

        if (!entry.containsValue()) {

            return entry.cast(null);
        }

        String date = entry.getValue();
        try {

            ZonedDateTime zonedDateTime = java.time.ZonedDateTime.parse(date);
            return entry.cast(zonedDateTime);
        } catch (DateTimeParseException e) {

            throw new UnexpectedValueException(entry);
        }
    }

    public final <T extends Number> TracedEntry<T> getAsNumber(Object key, boolean nullCaseAllowed, T ifNull,
            Function<Number, T> generator, Function<T, Boolean> isInRange)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        TracedEntry<?> entry = get(key, nullCaseAllowed, null, JSONType.LONG, JSONType.DOUBLE);

        if (!entry.containsValue()) {

            return entry.cast(ifNull);
        }

        Number num = (Number) entry.getValue();
        T val = generator.apply(num);

        if (!isInRange.apply(val)) {

            throw new NumberBoundsException(num, entry);
        }

        return entry.cast(val);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>Number</code> value.
     * 
     * @param <T>             <code>Number</code>: The type of number to retrieve.
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>T</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @param generator       <code>Function&lt;Number, T&gt;</code>: The function
     *                        to generate the number.
     * @return <code>TracedEntry&lt;T&gt;</code>: The number retrieved from this
     *         <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> parameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  expected type.
     * @throws NumberBoundsException    Thrown if the retrieved field is out of the
     *                                  specified bounds.
     */
    public final <T extends Number> TracedEntry<T> getAsNumber(Object key, boolean nullCaseAllowed, T ifNull,
            Function<Number, T> generator)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        return getAsNumber(key, nullCaseAllowed, ifNull, generator, _ -> true);
    }

    public final TracedEntry<Integer> getAsByte(Object key, boolean nullCaseAllowed, Integer ifNull)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        return getAsInteger(key, nullCaseAllowed, ifNull, num -> 0 <= num && num < 256);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>Long</code> value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>Long</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @return <code>TracedEntry&lt;Long&gt;</code>: The long retrieved from this
     *         <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> parameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  expected type.
     * @throws NumberBoundsException    Thrown if the retrieved field is out of the
     *                                  specified bounds.
     */
    public final TracedEntry<Long> getAsLong(Object key, boolean nullCaseAllowed, Long ifNull)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        return getAsLong(key, nullCaseAllowed, ifNull, _ -> true);
    }

    public final TracedEntry<Long> getAsLong(Object key, boolean nullCaseAllowed, Long ifNull,
            Function<Long, Boolean> isInRange)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        return getAsNumber(key, nullCaseAllowed, ifNull, num -> num.longValue(), isInRange);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into an <code>Integer</code> value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>Integer</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @return <code>TracedEntry&lt;Integer&gt;</code>: The integer retrieved from
     *         this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> parameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  expected type.
     * @throws NumberBoundsException    Thrown if the retrieved field is out of the
     *                                  specified bounds.
     */
    public final TracedEntry<Integer> getAsInteger(Object key, boolean nullCaseAllowed, Integer ifNull)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        return getAsInteger(key, nullCaseAllowed, ifNull, _ -> true);
    }

    public final TracedEntry<Integer> getAsInteger(Object key, boolean nullCaseAllowed, Integer ifNull,
            Function<Integer, Boolean> isInRange)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        return getAsNumber(key, nullCaseAllowed, ifNull, num -> num.intValue(), isInRange);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>Float</code> value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>Float</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @return <code>TracedEntry&lt;Float&gt;</code>: The float retrieved from this
     *         <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> parameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  expected type.
     * @throws NumberBoundsException    Thrown if the retrieved field is out of the
     *                                  specified bounds.
     */
    public final TracedEntry<Float> getAsFloat(Object key, boolean nullCaseAllowed, Float ifNull)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        return getAsFloat(key, nullCaseAllowed, ifNull, _ -> true);
    }

    public final TracedEntry<Float> getAsFloat(Object key, boolean nullCaseAllowed, Float ifNull,
            Function<Float, Boolean> isInRange)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        return getAsNumber(key, nullCaseAllowed, ifNull, num -> num.floatValue(), isInRange);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>Double</code> value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>Double</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @return <code>TracedEntry&lt;Double&gt;</code>: The double retrieved from
     *         this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> parameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  expected type.
     * @throws NumberBoundsException    Thrown if the retrieved field is out of the
     *                                  specified bounds.
     */
    public final TracedEntry<Double> getAsDouble(Object key, boolean nullCaseAllowed, Double ifNull)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        return getAsDouble(key, nullCaseAllowed, ifNull, _ -> true);
    }

    public final TracedEntry<Double> getAsDouble(Object key, boolean nullCaseAllowed, Double ifNull,
            Function<Double, Boolean> isInRange)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        return getAsNumber(key, nullCaseAllowed, ifNull, num -> num.doubleValue(), isInRange);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a scalar value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>Object</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @return <code>TracedEntry&lt;Object&gt;</code>: The scalar value retrieved
     *         from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing and
     *                                  the <code>nullCaseAllowed</code> parameter
     *                                  is <code>false</code>.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  expected type.
     */
    public final TracedEntry<Object> getAsScalar(Object key, boolean nullCaseAllowed, Object ifNull)
            throws MissingPropertyException, PropertyTypeException {

        TracedEntry<?> entry = get(key, nullCaseAllowed, ifNull, JSONType.BOOLEAN, JSONType.LONG, JSONType.DOUBLE,
                JSONType.STRING);

        if (!entry.containsValue()) {

            return entry.cast(ifNull);
        }

        return entry.cast(entry.getValue());
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a <code>Color</code> instance. The value must be an array of 3 or 4
     * integers, representing RGB or RGBA components respectively. If the array has
     * 3 elements, the alpha value defaults to 255 (fully opaque).
     *
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>Color</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @return <code>TracedEntry&lt;Color&gt;</code>: The color retrieved from this
     *         <code>TracedCollection</code> instance.
     * @throws LoggedException Thrown if there are any issues processing the color.
     */
    public final TracedEntry<Color> getAsColor(Object key, boolean nullCaseAllowed, Color ifNull)
            throws LoggedException {

        ArrayList<TypeCase<?, TracedEntry<Color>>> cases = new ArrayList<>(List.of(arrayCase(entry -> {

            TracedArray array = entry.getValue();
            int size = array.size();

            if (size != 3 && size != 4) {

                throw new CollectionSizeException(entry, array);
            }

            int[] rgb = new int[size];

            for (int i : array) {

                TracedEntry<Integer> colorEntry = array.getAsInteger(i, false, null, num -> 0 <= num && num < 256);
                rgb[i] = colorEntry.getValue();
            }

            int alpha = size == 4 ? rgb[3] : 255;
            return entry.cast(new Color(rgb[0], rgb[1], rgb[2], alpha));
        }), stringCase(entry -> {

            String color = entry.getValue();
            return entry.cast(switch (color) {

            case "white" -> Color.WHITE;
            case "lightGray" -> Color.LIGHT_GRAY;
            case "gray" -> Color.GRAY;
            case "darkGray" -> Color.DARK_GRAY;
            case "black" -> Color.BLACK;

            case "red" -> Color.RED;
            case "pink" -> Color.PINK;
            case "orange" -> Color.ORANGE;
            case "yellow" -> Color.YELLOW;
            case "green" -> Color.GREEN;
            case "magenta" -> Color.MAGENTA;
            case "cyan" -> Color.CYAN;
            case "blue" -> Color.BLUE;

            default -> {

                Color colorVal;
                try {

                    colorVal = Color.decode(color);
                } catch (NumberFormatException e) {

                    throw new UnexpectedValueException(entry);
                }

                yield colorVal;
            }
            });
        })));

        if (nullCaseAllowed) {

            cases.add(nullCase(entry -> entry.cast(ifNull)));
        }

        return get(key, cases);
    }

    public final TracedEntry<ColorRGBA> getAsColorRGBA(Object key, boolean nullCaseAllowed, ColorRGBA ifNull)
            throws LoggedException {

        TracedEntry<Color> colorEntry = getAsColor(key, nullCaseAllowed, null);

        Color color = colorEntry.getValue();
        ColorRGBA colorRGBA = color == null ? ifNull
                : new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
                        color.getAlpha() / 255f);

        return colorEntry.cast(colorRGBA);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a metadata value.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param versionRequired <code>boolean</code>: Whether a version is required.
     * @return <code>TracedEntry&lt;Identifier&gt;</code>: The metadata value
     *         retrieved from this <code>TracedCollection</code> instance.
     * @throws MissingPropertyException  Thrown if the retrieved field is missing
     *                                   and the <code>nullCaseAllowed</code>
     *                                   parameter is <code>false</code>.
     * @throws PropertyTypeException     Thrown if the retrieved field is not of the
     *                                   expected type.
     * @throws CollectionSizeException   Thrown if the retrieved field is not of the
     *                                   expected size.
     * @throws NumberBoundsException     Thrown if the retrieved field is out of the
     *                                   specified bounds.
     * @throws VersionBoundsException    Thrown if the retrieved field is out of the
     *                                   specified version bounds.
     * @throws IdentifierFormatException Thrown if the retrieved field is in an
     *                                   improper format.
     */
    public final TracedEntry<Identifier> getAsMetadata(Object key, boolean nullCaseAllowed, boolean versionRequired)
            throws LoggedException {

        return get(key, List.of(dictCase(entry -> {

            TracedDictionary json = entry.getValue();
            TracedEntry<Version> versionEntry = json.getAsVersion("version",
                    !json.containsKey("identifier") || !versionRequired);
            return json.getAsIdentifier("identifier", nullCaseAllowed, versionEntry);
        }), identifierCase(entry -> {

            if (!versionRequired) {

                return getAsIdentifier(key, nullCaseAllowed, null);
            }

            throw new PropertyTypeException(entry);
        }, (collection, _) -> collection.getAsIdentifier(key, false, null))));
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a new <code>Identifier</code> instance. Note that the retrieved field
     * MUST be of the <code>String</code> class and cannot be null.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param version         <code>TracedEntry&lt;Version&gt;</code>: The version
     *                        used to create the new <code>Identifier</code>
     *                        instance.
     * @return <code>TracedEntry&lt;Identifier&gt;</code>: The identifier retrieved
     *         from this <code>TracedCollection</code> instance.
     * @throws IdentifierFormatException Thrown if the retrieved field is in an
     *                                   improper format.
     * @throws MissingPropertyException  Thrown if the retrieved field is missing.
     * @throws PropertyTypeException     Thrown if the retrieved field is not of the
     *                                   <code>String</code> class.
     */
    public final TracedEntry<Identifier> getAsIdentifier(Object key, boolean nullCaseAllowed,
            TracedEntry<Version> version)
            throws IdentifierFormatException, MissingPropertyException, PropertyTypeException {

        // Retrieves the string value associated with the key.
        TracedEntry<String> entry = getAsString(key, nullCaseAllowed, null);

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true,
        // and thus a null entry may be returned.
        if (!entry.containsValue()) {

            return entry.cast(null);
        }

        // Parses the string into an identifier instance.
        return entry.cast(Identifier.createIdentifier(entry, version));
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a new <code>Version</code> instance. Note that the retrieved field
     * MUST be of the <code>JSONArray</code> class with a length of 3 and cannot be
     * null.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @return <code>TracedEntry&lt;Version&gt;</code>: The version retrieved from
     *         this <code>TracedCollection</code> instance.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  <code>JSONArray</code> class.
     * @throws PropertyTypeException    Thrown if an index of the version vector is
     *                                  of the <code>Long</code> class.
     * @throws MissingPropertyException Thrown if the retrieved field is missing.
     * @throws CollectionSizeException  Thrown if the version vector does not have a
     *                                  length of 3.
     * @throws VersionBoundsException   Thrown if any vector value in the generated
     *                                  <code>Version</code> instance is negative.
     */
    public final TracedEntry<Version> getAsVersion(Object key, boolean nullCaseAllowed) throws CollectionSizeException,
            MissingPropertyException, PropertyTypeException, NumberBoundsException, VersionBoundsException {

        // Retrieves the array value associated with the key.
        TracedEntry<TracedArray> entry = getAsArray(key, nullCaseAllowed);
        TracedArray array = entry.getValue();

        // If the retrieved value is null, the 'nullCaseAllowed' perameter must be true,
        // and thus a null entry may be returned.
        if (!entry.containsValue()) {

            return entry.cast(null);
        }

        if (array.size() != 3) {

            throw new CollectionSizeException(entry, array);
        }

        // Parses the array into a version instance.
        return entry.cast(Version.createVersion(entry));
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a new <code>AssetPresets</code> instance.
     * 
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @return <code>TracedEntry&lt;AssetPresets&gt;</code>: The presets retrieved
     *         from this <code>TracedCollection</code> instance.
     * @throws LoggedException Thrown if there are any issues processing the asset
     *                         presets.
     */
    public final TracedEntry<AssetPresets> getAsPresets(Object key, boolean nullCaseAllowed, AssetType type)
            throws LoggedException {

        AssetPresets presets = (nullCaseAllowed && !containsKey(key)) ? null : new AssetPresets(this, key, type);

        return getEntry(key).cast(presets);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a new <code>WeightedRoll</code> instance with a designated type. This
     * method assumes that in the case the retrieved entry is an array, the values
     * to be parsed are contained as a key inside of dictionaries in the array.
     * 
     * @param <K>             The type of entry to process.
     * @param <T>             The type of value to return.
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>T</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @param subKey          <code>String</code>: The key to retrieve from
     *                        dictionaries in the event of an array entry.
     * @param operator        <code>TypeCase&lt;K, T&gt;</code>: The operator used
     *                        to generate return values.
     * @return <code>WeightedRoll&lt;T&gt;</code>: The weighted roll generated from
     *         this <code>TracedCollection</code> instance.
     * @throws LoggedException Thrown if there are any issues processing the
     *                         weighted roll.
     */
    public final <K, T> WeightedRoll<T> getAsRoll(Object key, boolean nullCaseAllowed, T ifNull, String subKey,
            TypeCase<K, T> operator) throws LoggedException {

        ArrayList<TypeCase<?, WeightedRoll<T>>> cases = new ArrayList<>(List.of(

                // Process an array of values.
                arrayCase(entry -> {

                    ArrayList<WeightedRoll.Entry<T>> entries = new ArrayList<>();

                    TracedArray json = entry.getValue();
                    for (int i : json) {

                        TracedEntry<TracedDictionary> indexEntry = json.getAsDict(i, false);
                        TracedDictionary indexJson = indexEntry.getValue();

                        T val = indexJson.get(subKey, List.of(operator));

                        TracedEntry<Double> chanceEntry = indexJson.getAsDouble("chance", true, 100.0,
                                num -> num > 0.0);
                        double chance = chanceEntry.getValue();

                        entries.add(new WeightedRoll.Entry<>(val, chance));
                    }

                    return new WeightedRoll<>(entry, json, entries);
                }),

                // Process a single value.
                new TypeCase<>(entry -> new WeightedRoll<>(operator.onCall.apply(entry)), operator.getEntry,
                        operator.types)));

        if (nullCaseAllowed) {

            cases.add(nullCase(_ -> new WeightedRoll<>(ifNull)));
        }
        return get(key, cases);
    }

    /**
     * Retrieves a field from this <code>TracedCollection</code> instance and parses
     * it into a new <code>WeightedRoll</code> instance with a designated type. This
     * method assumes that in the case the retrieved entry is an array, the values
     * to be parsed are the dictionaries in the array.
     * 
     * @param <T>             The type of value to return.
     * @param key             <code>Object</code>: The key whose entry to retrieve
     *                        in this <code>TracedCollection</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a
     *                        <code>null</code> case should cause an exception.
     * @param ifNull          <code>T</code>: The value to return if the value
     *                        retrieved from this <code>TracedCollection</code>
     *                        instance is <code>null</code>.
     * @param operator        <code>EntryOperator&lt;TracedDictionary, T&gt;</code>:
     *                        The operator used to generate return values.
     * @return <code>WeightedRoll&lt;T&gt;</code>: The weighted roll generated from
     *         this <code>TracedCollection</code> instance.
     * @throws LoggedException Thrown if there are any issues processing the
     *                         weighted roll.
     */
    public final <T> WeightedRoll<T> getAsRoll(Object key, boolean nullCaseAllowed, T ifNull,
            EntryOperator<TracedDictionary, T> operator) throws LoggedException {

        ArrayList<TypeCase<?, WeightedRoll<T>>> cases = new ArrayList<>(List.of(

                // Process an array of values.
                arrayCase(entry -> {

                    ArrayList<WeightedRoll.Entry<T>> entries = new ArrayList<>();

                    TracedArray json = entry.getValue();
                    for (int i : json) {

                        TracedEntry<TracedDictionary> indexEntry = json.getAsDict(i, false);
                        TracedDictionary indexJson = indexEntry.getValue();

                        T val = operator.apply(indexEntry);

                        TracedEntry<Double> chanceEntry = indexJson.getAsDouble("chance", true, 100.0,
                                num -> num > 0.0);
                        double chance = chanceEntry.getValue();

                        entries.add(new WeightedRoll.Entry<>(val, chance));
                    }

                    return new WeightedRoll<>(entry, json, entries);
                }),

                // Process a single value.
                dictCase(entry -> new WeightedRoll<>(operator.apply(entry)))));

        if (nullCaseAllowed) {

            cases.add(nullCase(_ -> new WeightedRoll<>(ifNull)));
        }

        return get(key, cases);
    }

    public final TracedEntry<TRScript> getAsScript(Object key, boolean nullCaseAllowed) throws LoggedException {

        TracedEntry<?> entry = get(key, nullCaseAllowed, null);
        if (entry.containsValue()) {

            return entry.cast(new TRScript(this, key));
        } else {

            return entry.cast(null);
        }
    }

    /**
     * Retrieves a field from the storage method of this
     * <code>TracedCollection</code> instance.
     * 
     * @param key <code>Object</code>: The key to search using.
     * @return <code>Object</code>: The retrieved field
     */
    protected abstract Object getValue(Object key);

    /**
     * Returns whether or not a key value is present in this
     * <code>TracedCollection</code> instance.
     * 
     * @param key <code>Object</code>: The key to search for.
     * @return <code>boolean</code>: Whether or not the key is present in this
     *         <code>TracedCollection</code> instance.
     */
    public abstract boolean containsKey(Object key);

    /**
     * Returns whether or not a field value is present in this
     * <code>TracedCollection</code> instance.
     * 
     * @param key <code>Object</code>: The field to search for.
     * @return <code>boolean</code>: Whether or not the value is present in this
     *         <code>TracedCollection</code> instance.
     */
    public abstract boolean containsValue(Object key);

    /**
     * Returns the number of entries in this <code>TracedCollection</code> instance.
     * 
     * @return <code>int</code>: The number of entries in this
     *         <code>TracedCollection</code> instance.
     */
    public abstract int size();

    /**
     * Returns whether or not this <code>TracedCollection</code> instance is empty.
     * 
     * @return <code>boolean</code>: Whether or not this
     *         <code>TracedCollection</code> instance is empty.
     */
    public abstract boolean isEmpty();

    /**
     * Returns the string representation of this <code>TracedCollection</code>
     * instance.
     * 
     * @return <code>String</code>: This <code>TracedCollection</code> instance in
     *         the following string representation: <br>
     *         "<code>example, 0, path, 1, inside, 2, file</code>"
     */
    @Override
    public final String toString() {

        return pathway.toString();
    }
}
