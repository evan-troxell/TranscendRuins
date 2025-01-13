package com.transcendruins.utilities.scripts;

import java.util.List;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>TRScriptValue</code>: A class representing any object value in a TRScript.
 */
final class TRScriptValue {

    /**
     * <code>Object</code>: The value of this <code>TRScriptValue</code> instance.
     */
    private final Object value;

    /**
     * Creates a new instance of the <code>TRScriptValue</code> class.
     * @param valueEntry <code>TracedEntry&lt;?&gt;</code>: The entry from which this <code>TRScriptValue</code> is created.
     * @throws LoggedException
     */
    @SuppressWarnings("unchecked")
    public TRScriptValue(TracedEntry<?> valueEntry) throws UnexpectedValueException, MissingPropertyException, PropertyTypeException, ArrayLengthException {

        Object rawTRScriptValue = valueEntry.getValue();

        value = switch (rawTRScriptValue) {

            case Boolean boolVal -> boolVal;

            case Long longVal -> longVal.doubleValue();

            case Double doubVal -> doubVal;

            case String _ -> TRScriptExpression.parseStringExpression((TracedEntry<String>) valueEntry);

            case TracedDictionary _ -> TRScriptExpression.parseExpression((TracedEntry<TracedDictionary>) valueEntry);

            default -> null;
        };
    }

    /**
     * Evalutes this <code>TRScriptValue</code> instance.
     * @return <code>Object</code>: The resulting value.
     */
    public Object evaluate() {

        return switch (value) {

            case Boolean boolVal -> boolVal;

            case Double doubVal -> doubVal;

            case TRScriptExpression exprVal -> exprVal.evaluate();

            default -> null;
        };
    }

    /**
     * Evalutes this <code>TRScriptValue</code> instance as a boolean.
     * @return <code>boolean</code>: The resulting boolean.
     */
    public boolean evaluateBoolean() {

        Object result = evaluate();

        return switch (result) {

            case Boolean boolVal -> boolVal;

            case Double doubleVal -> doubleVal != 0.0;

            default -> false;
        };
    }

    /**
     * Evalutes this <code>TRScriptValue</code> instance as a double.
     * @return <code>double</code>: The resulting double.
     */
    public double evaluateDouble() {

        Object result = evaluate();

        return switch (result) {

            case Boolean boolVal -> boolVal ? 1.0 : 0.0;

            case Double doubleVal -> doubleVal;

            default -> 0.0;
        };
    }

    /**
     * Process a list of TRScript values into a list of booleans.
     * @param values <code>List&lt;TRScriptValue&gt;</code>: The values to process.
     * @return <code>List&lt;Boolean&gt;</code>: The resulting boolean list.
     */
    public static List<Boolean> evaluateBooleans(List<TRScriptValue> values) {

        return values.stream()
        .map(TRScriptValue::evaluateBoolean)
        .toList();
    }

    /**
     * Process a list of TRScript values into a list of doubles.
     * @param values <code>List&lt;TRScriptValue&gt;</code>: The values to process.
     * @return <code>List&lt;Double&gt;</code>: The resulting double list.
     */
    public static List<Double> evaluateDoubles(List<TRScriptValue> values) {

        return values.stream()
        .map(TRScriptValue::evaluateDouble)
        .toList();
    }
}