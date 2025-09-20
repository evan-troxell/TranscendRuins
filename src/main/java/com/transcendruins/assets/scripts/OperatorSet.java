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
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.transcendruins.PropertyHolder;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.world.World;

public final class OperatorSet {

    /**
     * <code>OperatorSet</code>: The set of basic operators.
     */
    public static final OperatorSet OPERATORS = new OperatorSet(

            entry("?", (args, asset) -> {

                boolean conditional = args.get(0).evaluateBoolean(asset);

                return conditional ? args.get(1).evaluate(asset) : args.get(2).evaluate(asset);
            }, argsLength -> argsLength != 3),

            entry("!", (args, asset) -> {

                return !args.get(0).evaluateBoolean(asset);
            }, argsLength -> argsLength != 1),

            entry("&&", (args, asset) -> {

                for (boolean value : TRScript.evaluate(args, asset, TRScript::evaluateBoolean)) {

                    if (!value) {

                        return value;
                    }
                }

                return true;
            }, argsLength -> argsLength < 2),

            entry("||", (args, asset) -> {

                for (boolean value : TRScript.evaluate(args, asset, TRScript::evaluateBoolean)) {

                    if (value) {

                        return value;
                    }
                }

                return false;
            }, argsLength -> argsLength < 2),

            entry("==", (args, asset) -> {

                Object first = args.get(0).evaluate(asset);
                Object second = args.get(1).evaluate(asset);

                if (first == null || second == null) {

                    return first == second;
                }

                return first.equals(second);
            }, argsLength -> argsLength != 2),

            entry("!=", (args, asset) -> {

                Object first = args.get(0).evaluate(asset);
                Object second = args.get(1).evaluate(asset);

                if (first == null || second == null) {

                    return first != second;
                }

                return !first.equals(second);
            }, argsLength -> argsLength != 2),

            entry("<", (args, asset) -> {

                return args.get(0).evaluateDouble(asset) < args.get(1).evaluateDouble(asset);
            }, argsLength -> argsLength != 2),

            entry(">", (args, asset) -> {

                return args.get(0).evaluateDouble(asset) > args.get(1).evaluateDouble(asset);
            }, argsLength -> argsLength != 2),

            entry("<=", (args, asset) -> {

                return args.get(0).evaluateDouble(asset) <= args.get(1).evaluateDouble(asset);
            }, argsLength -> argsLength != 2),

            entry(">=", (args, asset) -> {

                return args.get(0).evaluateDouble(asset) >= args.get(1).evaluateDouble(asset);
            }, argsLength -> argsLength != 2),

            entry("max", (args, asset) -> {

                double max = Double.NEGATIVE_INFINITY;

                for (double value : TRScript.evaluate(args, asset, TRScript::evaluateDouble)) {

                    if (value > max) {

                        max = value;
                    }
                }
                return max;
            }, argsLength -> argsLength < 2),

            entry("min", (args, asset) -> {

                double min = Double.NEGATIVE_INFINITY;

                for (double value : TRScript.evaluate(args, asset, TRScript::evaluateDouble)) {

                    if (value < min) {

                        min = value;
                    }
                }
                return min;
            }, argsLength -> argsLength < 2),

            entry("+", (args, asset) -> {

                double sum = 0;

                for (double value : TRScript.evaluate(args, asset, TRScript::evaluateDouble)) {

                    sum += value;
                }

                return sum;
            }, argsLength -> argsLength < 1),

            entry("-", (args, asset) -> {

                if (args.size() == 1) {

                    return -args.get(0).evaluateDouble(asset);
                }

                return args.get(0).evaluateDouble(asset) - args.get(1).evaluateDouble(asset);
            }, argsLength -> argsLength != 1 && argsLength != 2),

            entry("*", (args, asset) -> {

                double prod = 1;

                for (double value : TRScript.evaluate(args, asset, TRScript::evaluateDouble)) {

                    prod *= value;
                }

                return prod;
            }, argsLength -> argsLength < 2),

            entry("/", (args, asset) -> {

                if (args.size() == 1) {

                    return 1.0 / args.get(0).evaluateDouble(asset);
                }

                return args.get(0).evaluateDouble(asset) / args.get(1).evaluateDouble(asset);
            }, argsLength -> argsLength != 1 && argsLength != 2),

            entry("%", (args, asset) -> {

                return args.get(0).evaluateDouble(asset) % args.get(1).evaluateDouble(asset);
            }, argsLength -> argsLength != 2),

            entry("exp", (args, asset) -> {

                return Math.exp(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("pow", (args, asset) -> {

                return Math.pow(args.get(0).evaluateDouble(asset), args.get(1).evaluateDouble(asset));
            }, argsLength -> argsLength != 2),

            entry("root", (args, asset) -> {

                return Math.pow(args.get(0).evaluateDouble(asset), 1.0 / args.get(1).evaluateDouble(asset));
            }, argsLength -> argsLength != 2),

            entry("log", (args, asset) -> {

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
            }, argsLength -> argsLength != 2 && argsLength != 1),

            entry("sin", (args, asset) -> {

                return Math.sin(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("cos", (args, asset) -> {

                return Math.cos(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("tan", (args, asset) -> {

                return Math.tan(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("asin", (args, asset) -> {

                return Math.asin(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("acos", (args, asset) -> {

                return Math.acos(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("atan", (args, asset) -> {

                return switch (args.size()) {

                case 1 -> Math.atan(args.get(0).evaluateDouble(asset));

                case 2 -> Math.atan2(args.get(0).evaluateDouble(asset), args.get(1).evaluateDouble(asset));

                default -> Math.atan2(args.get(0).evaluateDouble(asset) - args.get(2).evaluateDouble(asset),
                        args.get(1).evaluateDouble(asset) - args.get(3).evaluateDouble(asset));
                };
            }, argsLength -> argsLength != 1 && argsLength != 2 && argsLength != 4),

            entry("sinh", (args, asset) -> {

                return Math.sinh(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("cosh", (args, asset) -> {

                return Math.cosh(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("tanh", (args, asset) -> {

                return Math.tanh(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("dist", (args, asset) -> {

                if (args.size() == 2) {

                    return Math.hypot(args.get(0).evaluateDouble(asset), args.get(1).evaluateDouble(asset));
                }

                return Math.hypot(args.get(0).evaluateDouble(asset) - args.get(2).evaluateDouble(asset),
                        args.get(1).evaluateDouble(asset) - args.get(3).evaluateDouble(asset));
            }, argsLength -> argsLength != 2 || argsLength != 4),

            entry("sign", (args, asset) -> {

                return Math.signum(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("abs", (args, asset) -> {

                return Math.abs(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("floor", (args, asset) -> {

                return Math.floor(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("ceil", (args, asset) -> {

                return Math.ceil(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("round", (args, asset) -> {

                return Math.round(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("sqrt", (args, asset) -> {

                return Math.sqrt(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("cbrt", (args, asset) -> {

                return Math.cbrt(args.get(0).evaluateDouble(asset));
            }, argsLength -> argsLength != 1),

            entry("random", (args, asset) -> {

                return switch (args.size()) {

                case 2 -> {

                    double first = args.get(0).evaluateDouble(asset);

                    yield first + Math.random() * (args.get(1).evaluateDouble(asset) - first);
                }

                case 1 -> Math.random() * args.get(0).evaluateDouble(asset);

                default -> Math.random();
                };
            }, argsLength -> argsLength > 2),

            entry("concat", (args, asset) -> {

                // Concatenate multiple strings together.
                return String.join("", TRScript.evaluate(args, asset, TRScript::evaluateString));
            }, argsLength -> argsLength < 1),

            entry("join", (args, asset) -> {

                ArrayList<String> strings = new ArrayList<>(TRScript.evaluate(args, asset, TRScript::evaluateString));
                String del = strings.remove(0);

                // Concatenate multiple strings together with a delimiter.
                return String.join(del, strings);
            }, argsLength -> argsLength < 2),

            entry("substring", (args, asset) -> {

                String str = args.get(0).evaluateString(asset);

                if (args.size() == 2) {

                    int begin = (int) args.get(1).evaluateDouble(asset);

                    // Ensure the bound is inside the string.
                    begin = Math.max(0, Math.min(begin, str.length()));

                    return str.substring(begin);
                } else {

                    int begin = (int) args.get(1).evaluateDouble(asset);
                    int end = (int) args.get(2).evaluateDouble(asset);

                    // Ensure the bounds are inside of the string.
                    begin = Math.max(0, Math.min(begin, str.length()));
                    end = Math.max(0, Math.min(end, str.length()));

                    // Check the order of the bounds.
                    if (begin > end) {

                        int temp = begin;
                        begin = end;
                        end = temp;
                    }

                    return str.substring(begin, end);
                }
            }, argsLength -> argsLength != 2 && argsLength != 3),

            entry("getProperty", (args, asset) -> {

                // Parse the arguments into a single string separated by periods.
                String property = String.join(".", TRScript.evaluate(args, asset, TRScript::evaluateString));

                // Return the local property.
                return asset.getProperty(property);
            }, argsLength -> argsLength == 0),

            entry("getGlobalProperty", (args, asset) -> {

                // Parse the arguments into a single string separated by periods.
                String property = String.join(".", TRScript.evaluate(args, asset, TRScript::evaluateString));

                // Return the global property.
                return World.getWorld().getProperty(property);
            }, argsLength -> argsLength == 0));

    public final record OperatorEntry(String key, TRScriptOperator operator) {
    }

    public static OperatorEntry entry(String key, BiFunction<List<TRScript>, PropertyHolder, Object> operator,
            Function<Integer, Boolean> invalidLength) {

        return new OperatorEntry(key, new TRScriptOperator(operator, invalidLength));
    }

    private final ImmutableMap<String, TRScriptOperator> operators;

    public OperatorSet(OperatorEntry... entries) {

        HashMap<String, TRScriptOperator> operatorsMap = new HashMap<>();

        for (OperatorEntry entry : entries) {

            operatorsMap.put(entry.key(), entry.operator());
        }

        operators = new ImmutableMap<>(operatorsMap);
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
    public static final TRScriptOperator getOperator(TracedEntry<String> operatorEntry, OperatorSet... operators)
            throws UnexpectedValueException {

        String operatorName = operatorEntry.getValue();

        for (OperatorSet set : operators) {

            if (set.operators.containsKey(operatorName)) {

                return set.operators.get(operatorName);
            }
        }

        throw new UnexpectedValueException(operatorEntry);
    }
}
