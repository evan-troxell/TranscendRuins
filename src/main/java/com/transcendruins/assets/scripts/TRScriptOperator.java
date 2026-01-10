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

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.transcendruins.utilities.PropertyHolder;

/**
 * <code>TRScriptOperator</code>: A class representing an evaluable TRScript
 * operator.
 */
public final class TRScriptOperator {

    /**
     * <code>(List&lt;TRScript&gt;, PropertyHolder) -> Object</code>: The expression
     * by which this <code>TRScriptOperator</code> instance should be evaluated.
     */
    private final BiFunction<List<TRScript>, PropertyHolder, Object> evaluate;

    /**
     * <code>(Integer) -> Boolean</code>: The expression by which the validity of
     * arguments of this <code>TRScriptOperator</code> instance should be evaluated.
     */
    private final Function<Integer, Boolean> invalidArgs;

    /**
     * Creates a new instance of the <code>TRScriptOperator</code> class.
     * 
     * @param evaluate    <code>(List&lt;TRScript&gt;, PropertyHolder) -> Object)</code>:
     *                    The expression by which this <code>TRScriptOperator</code>
     *                    instance should be evaluated.
     * @param invalidArgs <code>(Integer) -> Boolean)</code>: The expression by
     *                    which the validity of arguments of this
     *                    <code>TRScriptOperator</code> instance should be
     *                    evaluated.
     */
    public TRScriptOperator(BiFunction<List<TRScript>, PropertyHolder, Object> evaluate,
            Function<Integer, Boolean> invalidArgs) {

        this.evaluate = evaluate;
        this.invalidArgs = invalidArgs;
    }

    /**
     * Evalutes this <code>TRScriptExpression</code> instance.
     * 
     * @param args  <code>List&lt;TRScript&gt;</code>: The arguments of this
     *              <code>TRScriptOperator</code> instance.
     * @param asset <code>PropertyHolder</code>: The asset context to use.
     * @return <code>Object</code>: The resulting value.
     */
    public Object evaluate(List<TRScript> args, PropertyHolder asset) {

        return evaluate.apply(args, asset);
    }

    /**
     * Evaluates whether or not the length of the arguments of this
     * <code>TRScriptExpression</code> instance are invalid.
     * 
     * @param argsLength <code>int</code>: The length of the arguments of this
     *                   <code>TRScriptOperator</code> instance.
     * @return <code>boolean</code>: Whether or not this
     *         <code>TRScriptOperator</code> instance has invalid arguments.
     */
    public boolean invalidArgs(int argsLength) {

        return invalidArgs.apply(argsLength);
    }
}