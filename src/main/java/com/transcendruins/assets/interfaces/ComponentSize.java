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

package com.transcendruins.assets.interfaces;

import java.util.List;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedCollection;

/**
 * <code>ComponentSize</code>: A class representing a dimension within a
 * component.
 */
public final class ComponentSize {

    /**
     * <code>String</code>: The regular expression used to match a floating point
     * value.
     */
    private static final String NUMBER_PATTERN = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)$";

    /**
     * <code>int</code>: A constant representing a size in pixels.
     */
    private static final int PX = 0;

    /**
     * <code>int</code>: A constant representing a size in percent.
     */
    private static final int PCT = 1;

    /**
     * <code>ComponentSize</code>: A constant representing a size of 0 pixels.
     */
    public static final ComponentSize NONE = new ComponentSize(0, PX);

    /**
     * <code>ComponentSize</code>: A constant representing a size of 100%.
     */
    public static final ComponentSize FULL = new ComponentSize(100, PCT);

    /**
     * <code>double</code>: The dimension of this <code>ComponentSize</code>
     * instance.
     */
    private final double num;

    /**
     * <code>int</code>: The units of this <code>ComponentSize</code> instance.
     */
    private final int suffix;

    /**
     * Retrieves the size of this <code>ComponentSize</code> instance in pixels.
     * 
     * @param parent The size of the parent, in pixels, to use. This is typically
     *               the width or height of the parent.
     * @param min    <code>double</code>: The minimum size in pixels to return. This
     *               should be greater than or equal to 0.
     * @param max    <code>double</code>: The maximum size in pixels to return. This
     *               should be less than or equal to the <code>parent</code>
     *               parameter.
     * @return <code>double</code>: The resulting size, in pixels.
     */
    public double getSize(double parent, double min, double max) {

        double value = switch (suffix) {

        case PX -> num;

        case PCT -> parent * num / 100;

        default -> 0;
        };
        return Math.clamp(value, min, max);
    }

    /**
     * Creates a new instance of the <code>ComponentSize</code> class.
     * 
     * @param num    <code>double</code>: The dimension of this
     *               <code>ComponentSize</code> instance.
     * @param suffix <code>int</code>: The units of this <code>ComponentSize</code>
     *               instance.
     */
    private ComponentSize(double num, int suffix) {

        this.num = num;
        this.suffix = suffix;
    }

    /**
     * Parses a value from a <code>TracedCollection</code> instance into a new
     * <code>ComponentSize</code> instance.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to search for.
     * @return <code>ComponentSize</code>: The parsed size.
     * @throws LoggedException Thrown if an error occurs while parsing the
     *                         collection.
     */
    public static ComponentSize createSize(TracedCollection collection, Object key) throws LoggedException {

        return createSize(collection, key, null);
    }

    /**
     * Parses a value from a <code>TracedCollection</code> instance into a new
     * <code>ComponentSize</code> instance.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to search for.
     * @param ifNull     <code>ComponentSize</code>: The value to return if a size
     *                   could not be created.
     * @return <code>ComponentSize</code>: The parsed size.
     * @throws LoggedException Thrown if an error occurs while parsing the
     *                         collection.
     */
    public static ComponentSize createSize(TracedCollection collection, Object key, ComponentSize ifNull)
            throws LoggedException {

        return collection.get(key, List.of(

                collection.stringCase(entry -> { // If the entry is a string, the unit should be processed.

                    // Remove all whitespace.
                    String full = entry.getValue().replaceAll("\\s+", "");

                    // Break the suffix off of the number by removing non-digit chars at the end.
                    String numberPart = full.replaceAll("[^\\d.]+.*$", "");
                    if (!numberPart.matches(NUMBER_PATTERN)) {

                        throw new UnexpectedValueException(entry);
                    }

                    double number = Double.parseDouble(numberPart);
                    if (number < 0) {

                        throw new NumberBoundsException(number, entry);
                    }

                    // Isolate the suffix.
                    String suffixPart = full.substring(numberPart.length());
                    int suffix = switch (suffixPart) {

                    case "px", "" -> PX;

                    case "pct", "%" -> PCT;

                    default -> throw new UnexpectedValueException(entry);
                    };

                    if (suffix == PCT && number > 100) {

                        throw new NumberBoundsException(number, entry);
                    }

                    return new ComponentSize(number, suffix);
                }), collection.doubleCase(entry -> { // If the entry is a number, the unit will be pixels.

                    double number = entry.getValue();

                    if (number < 0) {

                        throw new NumberBoundsException(number, entry);
                    }

                    return new ComponentSize(number, PX);
                }), collection.nullCase(_ -> ifNull) // If the entry is missing, return ifNull.
        ));
    }
}
