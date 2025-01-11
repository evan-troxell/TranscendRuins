package com.transcendruins.utilities.scripts;

import java.util.ArrayList;

import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public class TRScriptExpression {

    private final TRScriptOperator operator;

    private final ArrayList<TRScriptValue> args = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public TRScriptExpression(TracedEntry<TracedDictionary> expressionEntry) throws UnexpectedValueException, ArrayLengthException, MissingPropertyException, PropertyTypeException {

        TracedDictionary expressionJson = expressionEntry.getValue();

        TracedEntry<String> operatorEntry = expressionJson.getAsString("operator", false, null);
        operator = TRScriptOperator.getOperator(operatorEntry);

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
    }

    public Object evaluate() {

        return operator.evaluate(args);
    }
}