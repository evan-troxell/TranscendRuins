package com.transcendruins.utilities.scripts;

import java.util.ArrayList;

import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>TRScriptExpression</code>: A class representing an evaluable TRScript expression.
 */
final class TRScriptExpression {

    /**
     * <code>String</code>: The regular expression used to match a floating point value.
     */
    private static final String NUMBER_PATTERN = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)$";

    /**
     * <code>TRScriptOperator</code>: The operator of this <code>TRScriptExpression</code> instance.
     */
    private final TRScriptOperator operator;

    /**
     * <code>TRScriptValue</code>: The argument list of this <code>TRScriptExpression</code> instance.
     */
    private final ArrayList<TRScriptValue> args = new ArrayList<>();

    /**
     * Creates a new instance of the <code>TRScriptExpression</code> class.
     * @param expressionEntry <code>TracedEntry&lt;?&gt;</code>: The entry containing the expression of this <code>TRScriptExpression</code> instance.
     * @throws LoggedException
     */
    @SuppressWarnings("unchecked")
    private TRScriptExpression(TracedEntry<?> expressionEntry) throws UnexpectedValueException, ArrayLengthException, MissingPropertyException, PropertyTypeException {

        Object rawExpression = expressionEntry.getValue();

        if (rawExpression instanceof TracedDictionary expressionJson) {

            operator = TRScriptOperator.getOperator(expressionJson.getAsString("operator", false, null));

            TracedEntry<?> argsEntry = expressionJson.get("args", false, null);

            if (argsEntry.getValue() instanceof TracedArray argsArray) {

                if (operator.invalidArgs(argsArray.size())) {

                    throw new ArrayLengthException((TracedEntry<TracedArray>) argsEntry);
                }

                for (int i : argsArray.getIndices()) {

                    args.add(new TRScriptValue(argsArray.get(i, true, null)));
                }
            } else {

                if (operator.invalidArgs(1)) {

                    throw new UnexpectedValueException(argsEntry);
                }
                args.add(new TRScriptValue(argsEntry));
            }

        } else if (rawExpression instanceof String) {

            operator = TRScriptOperator.getOperator((TracedEntry<String>) expressionEntry);
            if (operator.invalidArgs(0)) {

                throw new UnexpectedValueException(expressionEntry);
            }

        } else {

            operator = TRScriptOperator.getOperator();
        }
    }

    /**
     * Parses the value of a dictionary entry into an expression.
     * @param valueEntry <code>TracedEntry&lt;TracedDictionary&gt;</code>: The entry to parse;
     * @return <code>TRScriptExpression</code>: The resulting expression.
     * @throws LoggedException
     */
    public static TRScriptExpression parseExpression(TracedEntry<TracedDictionary> expressionEntry) throws UnexpectedValueException, ArrayLengthException, MissingPropertyException, PropertyTypeException {

        return new TRScriptExpression(expressionEntry);
    }

    /**
     * Parses the value of a string entry into a constant expression or an expression.
     * @param valueEntry <code>TracedEntry&lt;String&gt;</code>: The entry to parse;
     * @return <code>Object</code>: The resulting value.
     * @throws LoggedException
     */
    public static Object parseStringExpression(TracedEntry<String> valueEntry) throws UnexpectedValueException, ArrayLengthException, MissingPropertyException, PropertyTypeException {

        String stringVal = valueEntry.getValue();

        if (stringVal.matches(NUMBER_PATTERN)) {

            return Double.valueOf(stringVal);
        }

        return switch (stringVal) {

            case "PI" -> Math.PI;
            case "-PI" -> -Math.PI;

            case "TAU" -> Math.TAU;
            case "-TAU" -> -Math.TAU;

            case "E" -> Math.E;
            case "-E" -> -Math.E;

            default -> new TRScriptExpression(valueEntry);
        };
    }

    /**
     * Evalutes this <code>TRScriptExpression</code> instance.
     * @return <code>Object</code>: The resulting value.
     */
    public Object evaluate() {

        return operator.evaluate(args);
    }
}