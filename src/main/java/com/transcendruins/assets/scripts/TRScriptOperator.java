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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.transcendruins.PropertyHolder;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>TRScriptOperator</code>: A class representing an evaluable TRScript
 * operator.
 */
final class TRScriptOperator {

    /**
     * <code>ImmutableMap&lt;String, TRScriptOperator&gt;</code>: The set of all
     * operators.
     */
    private static final ImmutableMap<String, TRScriptOperator> OPERATORS = createOperators();

    /**
     * Determines whether or not an operator exists.
     * 
     * @param operator <code>String</code>: The operator to check for.
     * @return <code>boolean</code>: Whether the <code>OPERATORS</code> field
     *         contains the <code>operator</code> parameter.
     */
    public static boolean containsOperator(String operator) {

        return OPERATORS.containsKey(operator);
    }

    /**
     * <code>(List&lt;TRScriptValue&gt;, PropertyHolder) -> Object</code>: The
     * expression by which this <code>TRScriptOperator</code> instance should be
     * evaluated.
     */
    private final BiFunction<List<TRScriptValue>, PropertyHolder, Object> evaluate;

    /**
     * <code>(Integer) -> Boolean</code>: The expression by which the validity of
     * arguments of this <code>TRScriptOperator</code> instance should be evaluated.
     */
    private final Function<Integer, Boolean> invalidArgs;

    /**
     * Creates a new instance of the <code>TRScriptOperator</code> class.
     * 
     * @param evaluate    <code>(List&lt;TRScriptValue&gt;, PropertyHolder) -> Object)</code>:
     *                    The expression by which this <code>TRScriptOperator</code>
     *                    instance should be evaluated.
     * @param invalidArgs <code>(Integer) -> Boolean)</code>: The expression by
     *                    which the validity of arguments of this
     *                    <code>TRScriptOperator</code> instance should be
     *                    evaluated.
     */
    public TRScriptOperator(BiFunction<List<TRScriptValue>, PropertyHolder, Object> evaluate,
            Function<Integer, Boolean> invalidArgs) {

        this.evaluate = evaluate;
        this.invalidArgs = invalidArgs;
    }

    /**
     * Evalutes this <code>TRScriptExpression</code> instance.
     * 
     * @param args  <code>List&lt;TRScriptValue&gt;</code>: The arguments of this
     *              <code>TRScriptOperator</code> instance.
     * @param asset <code>PropertyHolder</code>: The asset context to use.
     * @return <code>Object</code>: The resulting value.
     */
    public Object evaluate(List<TRScriptValue> args, PropertyHolder asset) {

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

    /**
     * Creates the operators of this <code>TRScriptOperator</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, TRScriptOperator&gt;</code>: The
     *         resulting operators.
     */
    private static ImmutableMap<String, TRScriptOperator> createOperators() {

        HashMap<String, TRScriptOperator> operatorsMap = new HashMap<>();

        createOperator("identity", (_, _) -> 1.0, _ -> false, operatorsMap);

        createOperator("?", (args, asset) -> {

            boolean conditional = args.get(0).evaluateBoolean(asset);

            return conditional ? args.get(1).evaluate(asset) : args.get(2).evaluate(asset);
        }, argsLength -> argsLength != 3, operatorsMap);

        createOperator("!", (args, asset) -> {

            return !args.get(0).evaluateBoolean(asset);
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("&&", (args, asset) -> {

            for (boolean value : TRScriptValue.evaluateBooleans(args, asset)) {

                if (!value) {

                    return value;
                }
            }

            return true;
        }, argsLength -> argsLength < 2, operatorsMap);

        createOperator("||", (args, asset) -> {

            for (boolean value : TRScriptValue.evaluateBooleans(args, asset)) {

                if (value) {

                    return value;
                }
            }

            return false;
        }, argsLength -> argsLength < 2, operatorsMap);

        createOperator("==", (args, asset) -> {

            Object first = args.get(0).evaluate(asset);
            Object second = args.get(1).evaluate(asset);

            if (first == null || second == null) {

                return first == second;
            }

            return first.equals(second);
        }, argsLength -> argsLength != 2, operatorsMap);

        createOperator("!=", (args, asset) -> {

            Object first = args.get(0).evaluate(asset);
            Object second = args.get(1).evaluate(asset);

            if (first == null || second == null) {

                return first != second;
            }

            return !first.equals(second);
        }, argsLength -> argsLength != 2, operatorsMap);

        createOperator("<", (args, asset) -> {

            return args.get(0).evaluateDouble(asset) < args.get(1).evaluateDouble(asset);
        }, argsLength -> argsLength != 2, operatorsMap);

        createOperator(">", (args, asset) -> {

            return args.get(0).evaluateDouble(asset) > args.get(1).evaluateDouble(asset);
        }, argsLength -> argsLength != 2, operatorsMap);

        createOperator("<=", (args, asset) -> {

            return args.get(0).evaluateDouble(asset) <= args.get(1).evaluateDouble(asset);
        }, argsLength -> argsLength != 2, operatorsMap);

        createOperator(">=", (args, asset) -> {

            return args.get(0).evaluateDouble(asset) >= args.get(1).evaluateDouble(asset);
        }, argsLength -> argsLength != 2, operatorsMap);

        createOperator("max", (args, asset) -> {

            double max = Double.NEGATIVE_INFINITY;

            for (double value : TRScriptValue.evaluateDoubles(args, asset)) {

                if (value > max) {

                    max = value;
                }
            }
            return max;
        }, argsLength -> argsLength < 2, operatorsMap);

        createOperator("min", (args, asset) -> {

            double min = Double.NEGATIVE_INFINITY;

            for (double value : TRScriptValue.evaluateDoubles(args, asset)) {

                if (value < min) {

                    min = value;
                }
            }
            return min;
        }, argsLength -> argsLength < 2, operatorsMap);

        createOperator("+", (args, asset) -> {

            double sum = 0;

            for (double value : TRScriptValue.evaluateDoubles(args, asset)) {

                sum += value;
            }

            return sum;
        }, argsLength -> argsLength < 1, operatorsMap);

        createOperator("-", (args, asset) -> {

            if (args.size() == 1) {

                return -args.get(0).evaluateDouble(asset);
            }

            return args.get(0).evaluateDouble(asset) - args.get(1).evaluateDouble(asset);
        }, argsLength -> argsLength != 1 && argsLength != 2, operatorsMap);

        createOperator("*", (args, asset) -> {

            double prod = 1;

            for (double value : TRScriptValue.evaluateDoubles(args, asset)) {

                prod *= value;
            }

            return prod;
        }, argsLength -> argsLength < 2, operatorsMap);

        createOperator("/", (args, asset) -> {

            if (args.size() == 1) {

                return 1.0 / args.get(0).evaluateDouble(asset);
            }

            return args.get(0).evaluateDouble(asset) / args.get(1).evaluateDouble(asset);
        }, argsLength -> argsLength != 1 && argsLength != 2, operatorsMap);

        createOperator("%", (args, asset) -> {

            return args.get(0).evaluateDouble(asset) % args.get(1).evaluateDouble(asset);
        }, argsLength -> argsLength != 2, operatorsMap);

        createOperator("exp", (args, asset) -> {

            return Math.exp(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("pow", (args, asset) -> {

            return Math.pow(args.get(0).evaluateDouble(asset), args.get(1).evaluateDouble(asset));
        }, argsLength -> argsLength != 2, operatorsMap);

        createOperator("root", (args, asset) -> {

            return Math.pow(args.get(0).evaluateDouble(asset), 1.0 / args.get(1).evaluateDouble(asset));
        }, argsLength -> argsLength != 2, operatorsMap);

        createOperator("log", (args, asset) -> {

            if (args.size() == 1) {

                return Math.log(args.get(0).evaluateDouble(asset));
            }

            double first = args.get(0).evaluateDouble(asset);
            double second = args.get(1).evaluateDouble(asset);

            if (first < 0 || second < 0) {

                return Double.NaN;
            }

            if (first == 0 && second != 0) {

                return Double.NEGATIVE_INFINITY;
            }

            if (first != 0 && second == 0) {

                return 0d;
            }

            if (first != 1 && second == 1) {

                return (first < 1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            }

            if (second == first && (first == 0 || first == 1)) {

                return null;
            }

            return Math.log(first) / Math.log(second);
        }, argsLength -> argsLength != 2 && argsLength != 1, operatorsMap);

        createOperator("sin", (args, asset) -> {

            return Math.sin(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("cos", (args, asset) -> {

            return Math.cos(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("tan", (args, asset) -> {

            return Math.tan(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("asin", (args, asset) -> {

            return Math.asin(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("acos", (args, asset) -> {

            return Math.acos(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("atan", (args, asset) -> {

            return switch (args.size()) {

            case 1 -> Math.atan(args.get(0).evaluateDouble(asset));

            case 2 -> Math.atan2(args.get(0).evaluateDouble(asset), args.get(1).evaluateDouble(asset));

            default -> Math.atan2(args.get(0).evaluateDouble(asset) - args.get(2).evaluateDouble(asset),
                    args.get(1).evaluateDouble(asset) - args.get(3).evaluateDouble(asset));
            };
        }, argsLength -> argsLength != 1 && argsLength != 2 && argsLength != 4, operatorsMap);

        createOperator("sinh", (args, asset) -> {

            return Math.sinh(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("cosh", (args, asset) -> {

            return Math.cosh(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("tanh", (args, asset) -> {

            return Math.tanh(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("dist", (args, asset) -> {

            if (args.size() == 2) {

                return Math.hypot(args.get(0).evaluateDouble(asset), args.get(1).evaluateDouble(asset));
            }

            return Math.hypot(args.get(0).evaluateDouble(asset) - args.get(2).evaluateDouble(asset),
                    args.get(1).evaluateDouble(asset) - args.get(3).evaluateDouble(asset));
        }, argsLength -> argsLength != 2 || argsLength != 4, operatorsMap);

        createOperator("sign", (args, asset) -> {

            return Math.signum(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("abs", (args, asset) -> {

            return Math.abs(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("floor", (args, asset) -> {

            return Math.floor(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("ceil", (args, asset) -> {

            return Math.ceil(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("round", (args, asset) -> {

            return Math.round(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("sqrt", (args, asset) -> {

            return Math.sqrt(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("cbrt", (args, asset) -> {

            return Math.cbrt(args.get(0).evaluateDouble(asset));
        }, argsLength -> argsLength != 1, operatorsMap);

        createOperator("random", (args, asset) -> {

            return switch (args.size()) {

            case 2 -> {

                double first = args.get(0).evaluateDouble(asset);

                yield first + Math.random() * (args.get(1).evaluateDouble(asset) - first);
            }

            case 1 -> Math.random() * args.get(0).evaluateDouble(asset);

            default -> Math.random();
            };
        }, argsLength -> argsLength > 2, operatorsMap);

        createOperator("getProperty", (args, asset) -> {

            String property = String.join(".",
                    args.stream().map(value -> value.evaluateString(asset)).collect(Collectors.toList()));
            return asset.getProperty(property);
        }, argsLength -> argsLength == 0, operatorsMap);

        return new ImmutableMap<>(operatorsMap);
    }

    private static void createOperator(String key, BiFunction<List<TRScriptValue>, PropertyHolder, Object> operator,
            Function<Integer, Boolean> invalidLength, Map<String, TRScriptOperator> operators) {

        operators.put(key, new TRScriptOperator(operator, invalidLength));
    }

    /**
     * Retrieves the identity operate <code>(a, b, c...) -> 1.0</code>.
     * 
     * @return <code>TRScriptOperator</code>: The identity operator in the
     *         <code>OPERATORS</code> field.
     */
    public static final TRScriptOperator getOperator() {

        return OPERATORS.get("identity");
    }

    /**
     * Retrieves an operator.
     * 
     * @param operatorEntry <code>TracedEntry&lt;String&gt;</code>: The operator
     *                      entry to parse.
     * @return <code>TRScriptOperator</code>: The operator in the
     *         <code>OPERATORS</code> field.
     * @throws LoggedException
     */
    public static final TRScriptOperator getOperator(TracedEntry<String> operatorEntry)
            throws UnexpectedValueException {

        String operatorName = operatorEntry.getValue();

        TRScriptOperator operator = OPERATORS.get(operatorName);

        if (operator == null) {

            throw new UnexpectedValueException(operatorEntry);
        }

        return operator;
    }
}