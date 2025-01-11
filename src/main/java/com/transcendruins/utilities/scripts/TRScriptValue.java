package com.transcendruins.utilities.scripts;

import java.util.List;

import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class TRScriptValue {

    private final Object value;

    @SuppressWarnings("unchecked")
    public TRScriptValue(TracedEntry<?> valueEntry) throws UnexpectedValueException, MissingPropertyException, PropertyTypeException, ArrayLengthException {

        Object rawTRScriptValue = valueEntry.getValue();
        
        value = switch (rawTRScriptValue) {

            case Boolean boolVal -> boolVal;

            case Long longVal -> longVal.doubleValue();

            case Double doubVal -> doubVal;

            case String stringVal -> TRScriptEvaluator.getEvaluator((TracedEntry<String>) valueEntry);

            case TracedDictionary exprVal -> new TRScriptExpression((TracedEntry<TracedDictionary>) valueEntry);

            default -> null;
        };
    }

    public Object evaluate() {

        return switch (value) {

            case Boolean boolVal -> boolVal;

            case Double doubVal -> doubVal;

            case TRScriptExpression exprVal -> exprVal.evaluate();

            case TRScriptEvaluator evalVal -> evalVal.evaluate();

            default -> null;
        };
    }

    public boolean evaluateBoolean() {

        Object result = evaluate();

        return switch (result) {

            case Boolean boolVal -> boolVal;

            case Double doubleVal -> doubleVal != 0.0;

            default -> false;
        };
    }

    public double evaluateDouble() {

        Object result = evaluate();

        return switch (result) {

            case Boolean boolVal -> boolVal ? 1.0 : 0.0;

            case Double doubleVal -> doubleVal;

            default -> 0.0;
        };
    }

    public static List<Boolean> evaluateBooleans(List<TRScriptValue> values) {

        return values.stream()
        .map(TRScriptValue::evaluateBoolean)
        .toList();
    }

    public static List<Double> evaluateDoubles(List<TRScriptValue> values) {

        return values.stream()
        .map(TRScriptValue::evaluateDouble)
        .toList();
    }
}