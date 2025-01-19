package com.transcendruins.utilities.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>TRScriptOperator</code>: A class representing an evaluable TRScript
 * operator.
 */
final class TRScriptOperator {

    /**
     * <code>HashMap&lt;String, TRScriptOperator&gt;</code>: The set of all
     * operators.
     */
    private static final HashMap<String, TRScriptOperator> OPERATORS = createOperators();

    /**
     * <code>(ArrayList&lt;TRScriptValue&gt;) -> Object</code>: The expression by
     * which this <code>TRScriptOperator</code> instance should be evaluated.
     */
    private final Function<ArrayList<TRScriptValue>, Object> evaluate;

    /**
     * <code>(Integer) -> Boolean</code>: The expression by which the validity of
     * arguments of this <code>TRScriptOperator</code> instance should be evaluated.
     */
    private final Function<Integer, Boolean> invalidArgs;

    /**
     * Creates a new instance of the <code>TRScriptOperator</code> class.
     * 
     * @param evaluate    <code>(ArrayList&lt;TRScriptValue&gt;) -> Object)</code>:
     *                    The expression by which this <code>TRScriptOperator</code>
     *                    instance should be evaluated.
     * @param invalidArgs <code>(Integer) -> Boolean)</code>: The expression by
     *                    which the validity of arguments of this
     *                    <code>TRScriptOperator</code> instance should be
     *                    evaluated.
     */
    public TRScriptOperator(Function<ArrayList<TRScriptValue>, Object> evaluate,
            Function<Integer, Boolean> invalidArgs) {

        this.evaluate = evaluate;
        this.invalidArgs = invalidArgs;
    }

    /**
     * Evalutes this <code>TRScriptExpression</code> instance.
     * 
     * @param args <code>ArrayList&lt;TRScriptValue&gt;</code>: The arguments of
     *             this <code>TRScriptOperator</code> instance.
     * @return <code>Object</code>: The resulting value.
     */
    public Object evaluate(ArrayList<TRScriptValue> args) {

        return evaluate.apply(args);
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
     * @return <code>HashMap&lt;String, TRScriptOperator&gt;</code>: The resulting
     *         operators.
     */
    public static final HashMap<String, TRScriptOperator> createOperators() {

        HashMap<String, TRScriptOperator> operators = new HashMap<>();

        operators.put("identity", new TRScriptOperator(_ -> 1.0, _ -> false));

        operators.put("?", new TRScriptOperator(args -> {

            boolean conditional = args.get(0).evaluateBoolean();

            return conditional ? args.get(1).evaluate() : args.get(2).evaluate();
        }, argsLength -> argsLength != 3));

        operators.put("!", new TRScriptOperator(args -> {

            return !args.get(0).evaluateBoolean();
        }, argsLength -> argsLength != 1));

        operators.put("&&", new TRScriptOperator(args -> {

            for (boolean value : TRScriptValue.evaluateBooleans(args)) {

                if (!value) {

                    return value;
                }
            }

            return true;
        }, argsLength -> argsLength < 2));

        operators.put("||", new TRScriptOperator(args -> {

            for (boolean value : TRScriptValue.evaluateBooleans(args)) {

                if (value) {

                    return value;
                }
            }

            return false;
        }, argsLength -> argsLength < 2));

        operators.put("<", new TRScriptOperator(args -> {

            return args.get(0).evaluateDouble() < args.get(1).evaluateDouble();
        }, argsLength -> argsLength != 2));

        operators.put(">", new TRScriptOperator(args -> {

            return args.get(0).evaluateDouble() > args.get(1).evaluateDouble();
        }, argsLength -> argsLength != 2));

        operators.put("<=", new TRScriptOperator(args -> {

            return args.get(0).evaluateDouble() <= args.get(1).evaluateDouble();
        }, argsLength -> argsLength != 2));

        operators.put(">=", new TRScriptOperator(args -> {

            return args.get(0).evaluateDouble() >= args.get(1).evaluateDouble();
        }, argsLength -> argsLength != 2));

        operators.put("max", new TRScriptOperator(args -> {

            double max = Double.NEGATIVE_INFINITY;

            for (double value : TRScriptValue.evaluateDoubles(args)) {

                if (value > max) {

                    max = value;
                }
            }
            return max;
        }, argsLength -> argsLength < 2));

        operators.put("min", new TRScriptOperator(args -> {

            double min = Double.NEGATIVE_INFINITY;

            for (double value : TRScriptValue.evaluateDoubles(args)) {

                if (value < min) {

                    min = value;
                }
            }
            return min;
        }, argsLength -> argsLength < 2));

        operators.put("+", new TRScriptOperator(args -> {

            double sum = 0;

            for (double value : TRScriptValue.evaluateDoubles(args)) {

                sum += value;
            }

            return sum;
        }, argsLength -> argsLength < 1));

        operators.put("-", new TRScriptOperator(args -> {

            if (args.size() == 1) {

                return -args.get(0).evaluateDouble();
            }

            return args.get(0).evaluateDouble() - args.get(1).evaluateDouble();
        }, argsLength -> argsLength != 1 && argsLength != 2));

        operators.put("*", new TRScriptOperator(args -> {

            double prod = 0;

            for (double value : TRScriptValue.evaluateDoubles(args)) {

                prod *= value;
            }

            return prod;
        }, argsLength -> argsLength < 2));

        operators.put("/", new TRScriptOperator(args -> {

            if (args.size() == 1) {

                return 1.0 / args.get(0).evaluateDouble();
            }

            return args.get(0).evaluateDouble() / args.get(1).evaluateDouble();
        }, argsLength -> argsLength != 1 && argsLength != 2));

        operators.put("%", new TRScriptOperator(args -> {

            return args.get(0).evaluateDouble() % args.get(1).evaluateDouble();
        }, argsLength -> argsLength != 2));

        operators.put("pow", new TRScriptOperator(args -> {

            return Math.pow(args.get(0).evaluateDouble(), args.get(1).evaluateDouble());
        }, argsLength -> argsLength != 2));

        operators.put("log", new TRScriptOperator(args -> {

            double first = args.get(0).evaluateDouble();
            double second = args.get(1).evaluateDouble();

            if (first < 0 || second < 0) {

                return null;
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

            return Math.log(args.get(0).evaluateDouble()) / Math.log(args.get(1).evaluateDouble());
        }, argsLength -> argsLength != 2));

        operators.put("random", new TRScriptOperator(args -> {

            if (args.isEmpty()) {

                return Math.random();
            } else if (args.size() == 1) {

                return Math.random() * args.get(0).evaluateDouble();
            } else {

                double first = args.get(0).evaluateDouble();

                return first + Math.random() * (args.get(1).evaluateDouble() - first);
            }

        }, argsLength -> argsLength < 0 || argsLength > 2));
        return operators;
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