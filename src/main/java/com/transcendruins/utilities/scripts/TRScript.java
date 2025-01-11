package com.transcendruins.utilities.scripts;

import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedEntry;

public final class TRScript {

    private final TRScriptValue value;

    public TRScript(TracedEntry<?> valueEntry) throws ArrayLengthException, UnexpectedValueException, MissingPropertyException, PropertyTypeException {

        value = new TRScriptValue(valueEntry);
    }

    public boolean evaluate() {

        return value.evaluateBoolean();
    }
}