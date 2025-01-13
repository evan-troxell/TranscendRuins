package com.transcendruins.utilities.scripts;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>TRScript</code>: A class representing an evaluatable JSON expression.
 */
public final class TRScript {

    /**
     * <code>TRScriptValue</code>: The root value of this <code>TRScript</code> instance.
     */
    private final TRScriptValue value;

    /**
     * Creates a new instance of the <code>TRScript</code> class.
     * @param valueEntry <code>TracedEntry&lt;?&gt;</code>: The root entry of this <code>TRScript</code> instance\\\\\\\\\\\\\\\\\
     * @throws LoggedException Thrown if any exception is raised while creating this <code>TRScript</code> instance.
     */
    public TRScript(TracedEntry<?> valueEntry) throws ArrayLengthException, UnexpectedValueException, MissingPropertyException, PropertyTypeException {

        value = new TRScriptValue(valueEntry);
    }

    /**
     * Evaluates the boolean value of this <code>TRScript</code> instance.
     * @return <code>boolean</code>: The boolean value of this <code>TRScript</code> instance.
     */
    public boolean evaluate() {

        return value.evaluateBoolean();
    }
}