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

package com.transcendruins.assets.scripts;

import java.util.List;
import java.util.function.BiFunction;

import com.transcendruins.PropertyHolder;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>TRScript</code>: A class representing any object value in a TRScript.
 */
public final class TRScript {

    /**
     * <code>TRScript</code>: A value representing an empty script.
     */
    public static final TRScript EMPTY = new TRScript(null);

    /**
     * <code>Object</code>: The value of this <code>TRScript</code> instance.
     */
    private final Object value;

    /**
     * Creates a new instance of the <code>TRScript</code> class.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to search for.
     * @throws LoggedException Thrown if an error occurs while parsing data from the
     *                         collection.
     */
    public TRScript(TracedCollection collection, Object key) throws LoggedException {

        value = collection.get(key, List.of(collection.booleanCase(TracedEntry::getValue),
                collection.longCase(entry -> (double) entry.getValue()), collection.doubleCase(TracedEntry::getValue),
                collection.stringCase(TracedEntry::getValue), collection.dictCase(entry -> {

                    TracedDictionary json = entry.getValue();
                    return new TRScriptExpression(json);
                }), collection.defaultCase(_ -> null)));
    }

    /**
     * Creates a new instance of the <code>TRScript</code> class.
     * 
     * @param value <code>Object</code>: The value to assign to this
     *              <code>TRScript</code> instance.
     */
    public TRScript(Object value) {

        this.value = switch (value) {

        case Integer intVal -> intVal.doubleValue();
        case Long longVal -> longVal.doubleValue();
        case Float floatVal -> floatVal.doubleValue();
        case null, default -> value;
        };
    }

    /**
     * Evalutes this <code>TRScript</code> instance.
     * 
     * @param asset <code>PropertyHolder</code>: The asset to evaluate this
     *              <code>TRScript</code> using.
     * @return <code>Object</code>: The resulting value.
     */
    public final Object evaluate(PropertyHolder asset) {

        if (value instanceof TRScriptExpression expression) {

            return expression.evaluate(asset);
        }

        return value;
    }

    /**
     * Evalutes this <code>TRScript</code> instance as a boolean.
     * 
     * @param asset <code>PropertyHolder</code>: The asset to evaluate this
     *              <code>TRScript</code> using.
     * @return <code>boolean</code>: The resulting boolean.
     */
    public final boolean evaluateBoolean(PropertyHolder asset) {

        return switch (evaluate(asset)) {

        case Boolean boolVal -> boolVal;

        case Double doubleVal -> doubleVal != 0.0;

        case String stringVal -> stringVal.equals("true");

        case null, default -> false;
        };
    }

    /**
     * Evalutes this <code>TRScript</code> instance as a double.
     * 
     * @param asset <code>PropertyHolder</code>: The asset to evaluate this
     *              <code>TRScript</code> using.
     * @return <code>double</code>: The resulting double.
     */
    public final double evaluateDouble(PropertyHolder asset) {

        return switch (evaluate(asset)) {

        case Boolean boolVal -> boolVal ? 1.0 : 0.0;

        case Double doubleVal -> doubleVal;

        case String stringVal -> {

            try {

                // Process the string into a number or mathematical constant, if possible.
                yield switch (stringVal) {

                case "PI", "pi" -> Math.PI;
                case "-PI", "-pi" -> -Math.PI;

                case "TAU", "tau" -> Math.TAU;
                case "-TAU", "-tau" -> -Math.TAU;

                case "E", "e" -> Math.E;
                case "-E", "-e" -> -Math.E;

                default -> Double.parseDouble(stringVal);
                };
            } catch (NumberFormatException e) {

                yield 0.0;
            }
        }

        case null, default -> 0.0;
        };
    }

    /**
     * Evalutes this <code>TRScript</code> instance as a string.
     * 
     * @param asset <code>PropertyHolder</code>: The asset to evaluate this
     *              <code>TRScript</code> using.
     * @return <code>String</code>: The resulting string.
     */
    public final String evaluateString(PropertyHolder asset) {

        return switch (evaluate(asset)) {

        case Boolean boolVal -> boolVal ? "true" : "false";

        case Double doubleVal -> String.valueOf(doubleVal);

        case String stringVal -> stringVal;

        case null, default -> null;
        };
    }

    /**
     * Evalutes this <code>TRScript</code> instance as an asset.
     * 
     * @param asset <code>PropertyHolder</code>: The asset to evaluate this
     *              <code>TRScript</code> using.
     * @return <code>PropertyHolder</code>: The resulting asset.
     */
    public final PropertyHolder evaluateAsset(PropertyHolder asset) {

        if (evaluate(asset) instanceof PropertyHolder propertyHolder) {

            return propertyHolder;
        }

        return null;
    }

    /**
     * Process a list of <code>TRScript</code> values into a list of a specific
     * type.
     * 
     * @param <K>    The type to process the values into.
     * @param values <code>List&lt;TRScript&gt;</code>: The values to process.
     * @param asset  <code>PropertyHolder</code>: The asset to evaluate this
     *               <code>TRScript</code> using.
     * @param getter <code>BiFunction&lt;TRScript, PropertyHolder, K&gt;</code>: The
     *               retrieval function to use.
     * @return <code>List&lt;K&gt;</code>: The resulting list.
     */
    public static final <K> List<K> evaluate(List<TRScript> values, PropertyHolder asset,
            BiFunction<TRScript, PropertyHolder, K> getter) {

        return values.stream().map(value -> getter.apply(value, asset)).toList();
    }
}