package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>NumberBoundsException</code>: An exception thrown to indicate a retrieved number is outside of expected bounds.
 */
public final class NumberBoundsException extends PropertyException {

    /**
     * Creates a new instance of the <code>NumberBoundsException</code> exception.
     * @param entry <code>TracedEntry&lt;Long&gt;</code>: The number outside of the expected bounds.
     * @param min <code>Long</code>: The minimum number bounds. A <code>null</code> value represents no minimum bounds.
     * @param max <code>Long</code>: The maximum number bounds. A <code>null</code> value represents no maximum bounds.
     */
    public NumberBoundsException(TracedEntry<Long> entry, Long min, Long max) {

        super(propertyName(entry) + " with value " + entry + (
            (min == null) ? (" is above expected value " + max) :
            (max == null) ? (" is below expected value " + min) :
            (" is outside of expected range (" + min + ", " + max + ")")) + ".",
            entry, "Number Bounds Exception");
    }

    /**
     * Creates a new instance of the <code>NumberBoundsException</code> exception.
     * @param entry <code>TracedEntry&lt;Double&gt;</code>: The number outside of the expected bounds.
     * @param min <code>Double</code>: The minimum number bound. A <code>null</code> value represents no minimum bounds.
     * @param max <code>Double</code>: The maximum number bound. A <code>null</code> value represents no maximum bounds.
     */
    public NumberBoundsException(TracedEntry<Double> entry, Double min, Double max) {

        super(propertyName(entry) + " with value " + entry + (
            (min == null) ? (" is above expected value " + max) :
            (max == null) ? (" is below expected value " + min) :
            (" is outside of expected range (" + min + ", " + max + ")")) + ".",
            entry, "Number Bounds Exception");
    }
}
