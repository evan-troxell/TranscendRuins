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

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.PropertyHolder;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>TRScriptValue</code>: A class representing any object value in a
 * TRScript.
 */
public final class TRScriptValue {

    /**
     * <code>Object</code>: The value of this <code>TRScriptValue</code> instance.
     */
    private final Object value;

    /**
     * Creates a new instance of the <code>TRScriptValue</code> class.
     * 
     * @param valueEntry <code>TracedEntry&lt;?&gt;</code>: The entry from which
     *                   this <code>TRScriptValue</code> is created.
     * @throws LoggedException Thrown if an error occurs while parsing data from the
     *                         collection.
     */
    public TRScriptValue(TracedCollection collection, Object key) throws LoggedException {

        value = collection.get(key,
                List.of(collection.booleanCase(TracedEntry::getValue), collection.longCase(TracedEntry::getValue),
                        collection.doubleCase(TracedEntry::getValue),
                        collection.stringCase(_ -> TRScriptExpression.parseExpression(collection, key)),
                        collection.dictCase(_ -> TRScriptExpression.parseExpression(collection, key)),
                        collection.defaultCase(_ -> null)));
    }

    /**
     * Evalutes this <code>TRScriptValue</code> instance.
     * 
     * @param asset <code>PropertyHolder</code>: The asset to evaluate this
     *              <code>TRScriptValue</code> using.
     * @return <code>Object</code>: The resulting value.
     */
    public Object evaluate(PropertyHolder asset) {

        return switch (value) {

        case Boolean boolVal -> boolVal;

        case Double doubVal -> doubVal;

        case String stringVal -> stringVal;

        case TRScriptExpression exprVal -> exprVal.evaluate(asset);

        case null -> null;

        default -> null;
        };
    }

    /**
     * Evalutes this <code>TRScriptValue</code> instance as a boolean.
     * 
     * @param asset <code>PropertyHolder</code>: The asset to evaluate this
     *              <code>TRScriptValue</code> using.
     * @return <code>boolean</code>: The resulting boolean.
     */
    public boolean evaluateBoolean(PropertyHolder asset) {

        return switch (evaluate(asset)) {

        case Boolean boolVal -> boolVal;

        case Double doubleVal -> doubleVal != 0.0;

        case String stringVal -> stringVal.equals("true");

        case null -> false;

        default -> false;
        };
    }

    /**
     * Evalutes this <code>TRScriptValue</code> instance as a double.
     * 
     * @param asset <code>PropertyHolder</code>: The asset to evaluate this
     *              <code>TRScriptValue</code> using.
     * @return <code>double</code>: The resulting double.
     */
    public double evaluateDouble(PropertyHolder asset) {

        return switch (evaluate(asset)) {

        case Boolean boolVal -> boolVal ? 1.0 : 0.0;

        case Double doubleVal -> doubleVal;

        case String stringVal -> {

            try {

                yield Double.parseDouble(stringVal);
            } catch (NumberFormatException e) {

                yield 0.0;
            }
        }

        case null -> 0.0;

        default -> 0.0;
        };
    }

    /**
     * Evalutes this <code>TRScriptValue</code> instance as a string.
     * 
     * @param asset <code>PropertyHolder</code>: The asset to evaluate this
     *              <code>TRScriptValue</code> using.
     * @return <code>String</code>: The resulting string.
     */
    public String evaluateString(PropertyHolder asset) {

        return switch (evaluate(asset)) {

        case Boolean boolVal -> boolVal ? "true" : "false";

        case Double doubleVal -> String.valueOf(doubleVal);

        case String stringVal -> stringVal;

        case null -> "null";

        default -> "";
        };
    }

    /**
     * Process a list of TRScript values into a list of booleans.
     * 
     * @param values <code>List&lt;TRScriptValue&gt;</code>: The values to process.
     * @param asset  <code>PropertyHolder</code>: The asset to evaluate this
     *               <code>TRScriptValue</code> using.
     * @return <code>List&lt;Boolean&gt;</code>: The resulting boolean list.
     */
    public static List<Boolean> evaluateBooleans(List<TRScriptValue> values, PropertyHolder asset) {

        ArrayList<Boolean> returnValues = new ArrayList<>(values.size());

        for (TRScriptValue value : values) {

            returnValues.add(value.evaluateBoolean(asset));
        }

        return returnValues;
    }

    /**
     * Process a list of TRScript values into a list of doubles.
     * 
     * @param values <code>List&lt;TRScriptValue&gt;</code>: The values to process.
     * @param asset  <code>PropertyHolder</code>: The asset to evaluate this
     *               <code>TRScriptValue</code> using.
     * @return <code>List&lt;Double&gt;</code>: The resulting double list.
     */
    public static List<Double> evaluateDoubles(List<TRScriptValue> values, PropertyHolder asset) {

        ArrayList<Double> returnValues = new ArrayList<>(values.size());

        for (TRScriptValue value : values) {

            returnValues.add(value.evaluateDouble(asset));
        }

        return returnValues;
    }

    /**
     * Process a list of TRScript values into a list of strings.
     * 
     * @param values <code>List&lt;TRScriptValue&gt;</code>: The values to process.
     * @param asset  <code>PropertyHolder</code>: The asset to evaluate this
     *               <code>TRScriptValue</code> using.
     * @return <code>List&lt;String&gt;</code>: The resulting string list.
     */
    public static List<String> evaluateStrings(List<TRScriptValue> values, PropertyHolder asset) {

        ArrayList<String> returnValues = new ArrayList<>(values.size());

        for (TRScriptValue value : values) {

            returnValues.add(value.evaluateString(asset));
        }

        return returnValues;
    }
}