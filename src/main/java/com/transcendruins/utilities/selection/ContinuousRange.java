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

package com.transcendruins.utilities.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.random.DeterministicRandom;

/**
 * <code>ContinuousRange</code>: A class representing a continuous range of
 * values which may be picked from.
 */
public final class ContinuousRange {

    /**
     * <code>double</code>: The minimum value of this <code>ContinuousRange</code>
     * instance.
     */
    private final double min;

    /**
     * Retrieves the minimum value of this <code>ContinuousRange</code> instance.
     * 
     * @return <code>double</code>: The <code>min</code> field of this
     *         <code>ContinuousRange</code> instance.
     */
    public final double getMin() {

        return min;
    }

    /**
     * <code>double</code>: The maximum value of this <code>ContinuousRange</code>
     * instance.
     */
    private final double max;

    /**
     * Retrieves the maximum value of this <code>ContinuousRange</code> instance.
     * 
     * @return <code>double</code>: The <code>max</code> field of this
     *         <code>ContinuousRange</code> instance.
     */
    public final double getMax() {

        return max;
    }

    /**
     * Creates a new instance of the <code>ContinuousRange</code> class.
     * 
     * @param value <code>double</code>: The value to assign to this
     *              <code>ContinuousRange</code> instance.
     */
    public ContinuousRange(double value) {

        min = value;
        max = value;
    }

    /**
     * Creates a new instance of the <code>ContinuousRange</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON whose value to parse into
     *             a range.
     * @param min  <code>Double</code>: The minimum allowed value for this
     *             <code>ContinuousRange</code> instance.
     * @param max  <code>Double</code>: The maximum allowed value for this
     *             <code>ContinuousRange</code> instance.
     * @throws MissingPropertyException Thrown if the retrieved field is missing.
     * @throws PropertyTypeException    Thrown if the retrieved field is not of the
     *                                  expected type.
     * @throws NumberBoundsException    Thrown if the retrieved field is out of the
     *                                  specified bounds.
     */
    private ContinuousRange(TracedDictionary json, Function<Double, Boolean> inRange)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        TracedEntry<Double> minEntry = json.getAsDouble("min", false, null, inRange);
        min = minEntry.getValue();

        TracedEntry<Double> maxEntry = json.getAsDouble("max", false, null, num -> inRange.apply(num) && min <= num);
        max = maxEntry.getValue();
    }

    /**
     * Creates a new range based on the value found in a collection.
     * 
     * @param collection      <code>TracedCollection</code>: The collection to
     *                        retrieve from.
     * @param key             <code>Object</code>: The key from the collection to
     *                        parse into a new <code>ContinuousRange</code>
     *                        instance.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a null case is
     *                        allowed when creating the new
     *                        <code>ContinuousRange</code> instance.
     * @param min             <code>Double</code>: The minimum allowed value for the
     *                        new <code>ContinuousRange</code> instance.
     * @param max             <code>Double</code>: The maximum allowed value for the
     *                        new <code>ContinuousRange</code> instance.
     * @param inclusiveMax    <code>boolean</code>: Whether the maximum value is
     *                        inclusive.
     * @return <code>ContinuousRange</code>: The generated range.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new <code>ContinuousRange</code> instance.
     */
    public static final ContinuousRange createRange(TracedCollection collection, Object key, boolean nullCaseAllowed,
            double ifNull, Function<Double, Boolean> inRange) throws LoggedException {

        ArrayList<TracedCollection.TypeCase<?, ContinuousRange>> cases = new ArrayList<>(
                List.of(collection.dictCase(entry -> {

                    TracedDictionary json = entry.getValue();
                    return new ContinuousRange(json, inRange);
                }),

                        collection.doubleCase(entry -> {

                            return new ContinuousRange(entry.getValue());
                        }, inRange)));

        if (nullCaseAllowed) {

            cases.add(collection.nullCase(entry -> {

                return new ContinuousRange(ifNull);
            }));
        }

        return collection.get(key, cases);
    }

    /**
     * Retrieves a double value from this <code>ContinuousRange</code> instance.
     * 
     * @param random <code>long</code>: The random value to use.
     * @return <code>long</code>: The retrieved <code>double</code> value.
     */
    public final double get(long random) {

        if (min == max) {

            return min;
        }

        return min + (max - min) * DeterministicRandom.toDouble(random);
    }
}
