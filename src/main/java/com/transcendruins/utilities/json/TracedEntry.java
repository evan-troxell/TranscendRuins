package com.transcendruins.utilities.json;

import java.util.ArrayList;
import java.util.Collection;

import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyExceptionPathway;

/**
 * <code>TracedEntry&lt;K&gt;</code>: A class representing an entry of type K in a <code>TracedCollection</code> instance.
 */
public final class TracedEntry<K> {

    /**
     * <code>PropertyExceptionPathway</code>: The pathway to this <code>TracedEntry</code> instance.
     */
    private final PropertyExceptionPathway pathway;

    /**
     * <code>K</code>: The value contained within this <code>TracedEntry</code> instance.
     */
    private final K value;

    /**
     * Creates a new instance of the <code>TracedEntry<K></code> class.
     * @param pathway <code>PropertyExceptionPathway</code>: The pathway to this <code>TracedEntry</code> instance.
     * @param value <code>K</code>: The value contained within this <code>TracedEntry</code> instance.
     */
    public TracedEntry(PropertyExceptionPathway pathway, K value) {

        this.pathway = pathway;
        this.value = value;
    }

    /**
     * Retrieves the value of this <code>TracedEntry&lt;K&gt;</code> instance.
     * @return <code>K</code>: The value of this <code>TracedEntry&lt;K&gt;</code> instance.
     */
    public K getValue() {

        return value;
    }

    /**
     * Retrieves the pathway of this <code>TracedEntry&lt;K&gt;</code> instance.
     * @return <code>PropertyExceptionPathway</code>: The pathway of this <code>TracedEntry&lt;K&gt;</code> instance.
     */
    public PropertyExceptionPathway getPathway() {

        return pathway;
    }

    public static <T> ArrayList<T> getValues(Collection<TracedEntry<T>> entries) {

        ArrayList<T> values = new ArrayList<>(entries.size());
        for (TracedEntry<T> entry : entries) {

            values.add(entry.getValue());
        }
        return values;
    }

    /**
     * Returns the string representation of this <code>TracedEntry</code> instance.
     * @return <code>String</code>: The string representation of the <code>value</code> property of this <code>TracedEntry</code> instance.
     */
    @Override
    public String toString() {

        return value.toString();
    }
}
