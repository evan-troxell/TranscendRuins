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

package com.transcendruins.assets.extra;

import java.util.List;
import java.util.function.Function;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Range</code>: A class representing a range of values which may be
 * picked from.
 */
public final class Range {

    /**
     * <code>double</code>: The minimum value of this <code>Range</code> instance.
     */
    private final double min;

    /**
     * Retrieves the minimum value of this <code>Range</code> instance.
     * 
     * @return <code>double</code>: The <code>min</code> field of this
     *         <code>Range</code> instance.
     */
    public double getMin() {

        return min;
    }

    /**
     * <code>double</code>: The maximum value of this <code>Range</code> instance.
     */
    private final double max;

    /**
     * Retrieves the maximum value of this <code>Range</code> instance.
     * 
     * @return <code>double</code>: The <code>max</code> field of this
     *         <code>Range</code> instance.
     */
    public double getMax() {

        return max;
    }

    /**
     * Creates a new instance of the <code>Range</code> class.
     */
    public Range() {

        min = 1;
        max = 1;
    }

    /**
     * Creates a new instance of the <code>Range</code> class.
     * 
     * @param entry <code>TracedEntry&lt;Double&gt;</code>: The entry to assign to
     *              the range.
     */
    public Range(TracedEntry<Double> entry) {

        min = entry.getValue();
        max = entry.getValue();
    }

    /**
     * Creates a new instance of the <code>Range</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The json whose value to parse into
     *             a range.
     * @param min  <code>Double</code>: The minimum allowed value for this
     *             <code>Range</code> instance.
     * @param max  <code>Double</code>: The maximum allowed value for this
     *             <code>Range</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  expected type.
     * @throws NumberBoundsException    Thrown if the retrieved field is out of the
     *                                  specified bounds.
     */
    public Range(TracedDictionary json, Function<Double, Boolean> inRange)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        TracedEntry<Double> minEntry = json.getAsDouble("min", false, null, inRange);
        double minValue = minEntry.getValue();

        TracedEntry<Double> maxEntry = json.getAsDouble("max", false, null, inRange);
        double maxValue = maxEntry.getValue();

        this.min = Math.min(minValue, maxValue);
        this.max = Math.max(minValue, maxValue);
    }

    /**
     * Creates a new range based on the value found in a collection.
     * 
     * @param collection      <code>TracedCollection</code>: The collection to
     *                        retrieve
     *                        from.
     * @param key             <code>Object</code>: The key from the collection to
     *                        parse
     *                        into a new <code>Range</code> instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a null case is
     *                        allowed when creating the new
     *                        <code>Range</code> instance.
     * @param min             <code>Double</code>: The minimum allowed value for the
     *                        new
     *                        <code>Range</code> instance.
     * @param max             <code>Double</code>: The maximum allowed value for the
     *                        new
     *                        <code>Range</code> instance.
     * @param inclusiveMax    <code>boolean</code>: Whether the maximum value is
     *                        inclusive.
     * @return <code>Range</code>: The generated range.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new <code>Range</code> instance.
     */
    public static Range createRange(TracedCollection collection, Object key, boolean nullCaseAllowed,
            boolean variableRangeAllowed, Function<Double, Boolean> inRange)
            throws LoggedException {

        return collection.get(key, List.of(

                collection.dictCase(entry -> {

                    if (!variableRangeAllowed) {

                        throw new PropertyTypeException(entry);
                    }
                    TracedDictionary json = entry.getValue();
                    return new Range(json, inRange);
                }),

                collection.doubleCase(entry -> {

                    return new Range(entry);
                }, _ -> collection.getAsDouble(key, false, null, inRange)),

                collection.nullCase(entry -> {
                    if (!nullCaseAllowed) {

                        throw new MissingPropertyException(entry);
                    }
                    return new Range();
                })));
    }

    /**
     * Retrieves a double value of this <code>Range</code> instance.
     * 
     * @param random <code>double</code>: The random value to use.
     * @return <code>double</code>: The retrieved <code>double</code> value.
     */
    public double getDoubleValue(double random) {

        if (min == max) {

            return min;
        }

        return min + (max - min) * random;
    }

    /**
     * Retrieves an int value of this <code>Range</code> instance.
     * 
     * @param random <code>double</code>: The random value to use.
     * @return <code>int</code>: The retrieved <code>int</code> value.
     */
    public int getIntegerValue(double random) {

        if (min == max) {

            return (int) min;
        }

        return (int) (min + (max - min) * random);
    }
}
