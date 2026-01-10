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

package com.transcendruins.assets.scripts;

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.utilities.PropertyHolder;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>TRScriptExpression</code>: A class representing an evaluable TRScript
 * expression.
 */
public final class TRScriptExpression {

    /**
     * <code>TRScriptOperator</code>: The operator of this
     * <code>TRScriptExpression</code> instance.
     */
    private final TRScriptOperator operator;

    /**
     * <code>ImmutableList&lt;TRScript&gt;</code>: The argument list of this
     * <code>TRScriptExpression</code> instance.
     */
    private final ImmutableList<TRScript> args;

    /**
     * Creates a new instance of the <code>TRScriptExpression</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The json containing the expression
     *             of this <code>TRScriptExpression</code> instance.
     * @throws LoggedException Thrown if an error occurs while parsing data from the
     *                         collection.
     */
    public TRScriptExpression(TracedDictionary json) throws LoggedException {

        TracedEntry<String> operatorEntry = json.getAsString("operator", false, null);
        operator = OperatorSet.getOperator(operatorEntry, OperatorSet.OPERATORS);

        args = json.get("args", List.of(

                // Process a list of arguments.
                json.arrayCase(entry -> {

                    ArrayList<TRScript> argsList = new ArrayList<>();

                    TracedArray argsArray = entry.getValue();
                    if (operator.invalidArgs(argsArray.size())) {

                        throw new CollectionSizeException(entry, argsArray);
                    }

                    for (int i : argsArray) {

                        argsList.add(new TRScript(argsArray, i));
                    }

                    return new ImmutableList<>(argsList);
                }),

                // Process a single argument.
                json.scalarCase(entry -> {

                    if (operator.invalidArgs(1)) {

                        throw new UnexpectedValueException(entry);
                    }
                    return new ImmutableList<>(new TRScript(json, "args"));
                }),

                // Process no arguments.
                json.nullCase(entry -> {

                    if (operator.invalidArgs(0)) {

                        throw new MissingPropertyException(entry);
                    }
                    return new ImmutableList<>();
                })));
    }

    /**
     * Evalutes this <code>TRScriptExpression</code> instance.
     * 
     * @param asset <code>PropertyHolder</code>: The asset context to evaluate this
     *              <code>TRScriptExpression</code> instance with.
     * @return <code>Object</code>: The resulting value.
     */
    public Object evaluate(PropertyHolder asset) {

        return operator.evaluate(args, asset);
    }
}