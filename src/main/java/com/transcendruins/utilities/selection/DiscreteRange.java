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
 * <code>DiscreteRange</code>: A class representing a discrete range of values
 * which may be picked from.
 */
public final class DiscreteRange {

    /**
     * <code>int</code>: The minimum value of this <code>Range</code> instance.
     */
    private final int min;

    /**
     * Retrieves the minimum value of this <code>Range</code> instance.
     * 
     * @return <code>int</code>: The <code>min</code> field of this
     *         <code>Range</code> instance.
     */
    public final int getMin() {

        return min;
    }

    /**
     * <code>int</code>: The maximum value of this <code>Range</code> instance.
     */
    private final int max;

    /**
     * Retrieves the maximum value of this <code>Range</code> instance.
     * 
     * @return <code>int</code>: The <code>max</code> field of this
     *         <code>Range</code> instance.
     */
    public final int getMax() {

        return max;
    }

    /**
     * Creates a new instance of the <code>Range</code> class.
     * 
     * @param value <code>int</code>: The value to assign to this <code>Range</code>
     *              instance.
     */
    public DiscreteRange(int value) {

        min = value;
        max = value;
    }

    private DiscreteRange(TracedDictionary json, Function<Integer, Boolean> inRange)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        TracedEntry<Integer> minEntry = json.getAsInteger("min", false, null, inRange);
        min = minEntry.getValue();

        TracedEntry<Integer> maxEntry = json.getAsInteger("max", false, null, num -> inRange.apply(num) && min <= num);
        max = maxEntry.getValue();
    }

    public static final DiscreteRange createRange(TracedCollection collection, Object key, boolean nullCaseAllowed,
            int ifNull, Function<Integer, Boolean> inRange) throws LoggedException {

        ArrayList<TracedCollection.TypeCase<?, DiscreteRange>> cases = new ArrayList<>(
                List.of(collection.dictCase(entry -> {

                    TracedDictionary json = entry.getValue();
                    return new DiscreteRange(json, inRange);
                }),

                        collection.intCase(entry -> {

                            return new DiscreteRange(entry.getValue());
                        }, inRange)));

        if (nullCaseAllowed) {

            cases.add(collection.nullCase(entry -> {

                return new DiscreteRange(ifNull);
            }));
        }

        return collection.get(key, cases);
    }

    /**
     * Retrieves an integer value from this <code>Range</code> instance.
     * 
     * @param random <code>long</code>: The random value to use.
     * @return <code>int</code>: The retrieved <code>int</code> value.
     */
    public final int get(long random) {

        if (min == max) {

            return min;
        }

        return min + (int) ((max - min + 1) * DeterministicRandom.toDouble(random));
    }
}
