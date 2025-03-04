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

import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedCollection.JSONType;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>TRScriptExpression</code>: A class representing an evaluable TRScript
 * expression.
 */
final class TRScriptExpression {

    /**
     * <code>String</code>: The regular expression used to match a floating point
     * value.
     */
    private static final String NUMBER_PATTERN = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)$";

    /**
     * <code>TRScriptOperator</code>: The operator of this
     * <code>TRScriptExpression</code> instance.
     */
    private final TRScriptOperator operator;

    /**
     * <code>ImmutableList&lt;TRScriptValue&gt;</code>: The argument list of this
     * <code>TRScriptExpression</code> instance.
     */
    private final ImmutableList<TRScriptValue> args;

    /**
     * Creates a new instance of the <code>TRScriptExpression</code> class.
     * 
     * @param expressionEntry <code>TracedEntry&lt;?&gt;</code>: The entry
     *                        containing the expression of this
     *                        <code>TRScriptExpression</code> instance.
     * @throws NumberBoundsException
     * @throws LoggedException
     */
    private TRScriptExpression(TracedCollection collection, Object key)
            throws UnexpectedValueException, CollectionSizeException, MissingPropertyException,
            PropertyTypeException, NumberBoundsException {

        ArrayList<TRScriptValue> argsList = new ArrayList<>();

        switch (collection.getType(key)) {

            case DICT -> {

                TracedEntry<TracedDictionary> expressionEntry = collection.getAsDict(key, false);
                TracedDictionary expressionJson = expressionEntry.getValue();

                operator = TRScriptOperator.getOperator(expressionJson.getAsString("operator", false, null));

                switch (expressionJson.getType("args")) {

                    case ARRAY -> {

                        TracedEntry<TracedArray> argsEntry = expressionJson.getAsArray("args", false);
                        TracedArray argsArray = argsEntry.getValue();

                        if (operator.invalidArgs(argsArray.size())) {

                            throw new CollectionSizeException(argsEntry, argsArray);
                        }

                        for (int i : argsArray.getIndices()) {

                            argsList.add(new TRScriptValue(argsArray, i));
                        }
                    }

                    default -> {

                        if (operator.invalidArgs(1)) {

                            throw new UnexpectedValueException(expressionEntry);
                        }
                        argsList.add(new TRScriptValue(expressionJson, "args"));
                    }
                }

            }

            case STRING -> {

                TracedEntry<String> operatorEntry = collection.getAsString(key, false, null);
                operator = TRScriptOperator.getOperator(operatorEntry);

                if (operator.invalidArgs(0)) {

                    throw new UnexpectedValueException(operatorEntry);
                }

            }

            default -> operator = TRScriptOperator.getOperator();
        }

        args = new ImmutableList<>(argsList);
    }

    /**
     * Parses the value of an entry into an expression.
     * 
     * @param valueEntry <code>TracedEntry&lt;?&gt;</code>: The entry to parse;
     * @return <code>Object</code>: The resulting value or expression.
     * @throws NumberBoundsException
     * @throws LoggedException
     */
    public static Object parseExpression(TracedCollection collection, Object key)
            throws UnexpectedValueException, CollectionSizeException, MissingPropertyException,
            PropertyTypeException, NumberBoundsException {

        if (collection.getType(key) == JSONType.STRING) {

            TracedEntry<String> expressionEntry = collection.getAsString(key, false, null);
            String stringVal = expressionEntry.getValue();

            if (stringVal.matches(NUMBER_PATTERN)) {

                return Double.valueOf(stringVal);
            }

            return switch (stringVal) {

                case "PI", "pi" -> Math.PI;
                case "-PI", "-pi" -> -Math.PI;

                case "TAU", "tau" -> Math.TAU;
                case "-TAU", "-tau" -> -Math.TAU;

                case "E", "e" -> Math.E;
                case "-E", "-e" -> -Math.E;

                default -> stringVal;
            };
        }

        return new TRScriptExpression(collection, key);
    }

    /**
     * Evalutes this <code>TRScriptExpression</code> instance.
     * 
     * @param asset <code>AssetInstance</code>: The asset context to evaluate this
     *              <code>TRScriptExpression</code> instance with.
     * @return <code>Object</code>: The resulting value.
     */
    public Object evaluate(AssetInstance asset) {

        return operator.evaluate(args, asset);
    }
}