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

package com.transcendruins.utilities.json;

import java.util.ArrayList;
import java.util.Collection;

import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyExceptionPathway;

/**
 * <code>TracedEntry&lt;K&gt;</code>: A class representing an entry of type K in
 * a <code>TracedCollection</code> instance.
 */
public final class TracedEntry<K> {

    /**
     * <code>PropertyExceptionPathway</code>: The pathway to this
     * <code>TracedEntry</code> instance.
     */
    private final PropertyExceptionPathway pathway;

    /**
     * Retrieves the pathway of this <code>TracedEntry&lt;K&gt;</code> instance.
     * 
     * @return <code>PropertyExceptionPathway</code>: The pathway of this
     *         <code>TracedEntry&lt;K&gt;</code> instance.
     */
    public PropertyExceptionPathway getPathway() {

        return pathway;
    }

    /**
     * <code>K</code>: The value contained within this <code>TracedEntry</code>
     * instance.
     */
    private final K value;

    /**
     * Retrieves the value of this <code>TracedEntry&lt;K&gt;</code> instance.
     * 
     * @return <code>K</code>: The value of this <code>TracedEntry&lt;K&gt;</code>
     *         instance.
     */
    public K getValue() {

        return value;
    }

    /**
     * Retrieves the type of this <code>TracedEntry&lt;K&gt;</code> instance.
     * 
     * @return <code>TracedCollection.JSONType</code>: The type of this
     *         <code>TracedEntry&lt;K&gt;</code> instance.
     */
    public TracedCollection.JSONType getType() {

        return TracedCollection.typeOf(value);
    }

    /**
     * Creates a new instance of the <code>TracedEntry<K></code> class.
     * 
     * @param pathway <code>PropertyExceptionPathway</code>: The pathway to this
     *                <code>TracedEntry</code> instance.
     * @param value   <code>K</code>: The value contained within this
     *                <code>TracedEntry</code> instance.
     */
    public TracedEntry(PropertyExceptionPathway pathway, K value) {

        this.pathway = pathway;
        this.value = value;
    }

    /**
     * Creates a new instance of the <code>TracedEntry&lt;T&gt;</code> class,
     * casting the value to the specified type.
     * 
     * @param <T> The type to cast the value to.
     * @param val <code>T</code>: The value to cast.
     * @return <code>TracedEntry&lt;T&gt;</code>: The new entry with the specified
     *         value.
     */
    public <T> TracedEntry<T> cast(T val) {

        return new TracedEntry<>(pathway, val);
    }

    /**
     * Unboxes all of the values from within a collection of
     * <code>TracedEntry&lt;T&gt;</code> values.
     * 
     * @param <T>     The type of the <code>entries</code> parameter to unbox.
     * @param entries <code>Collection&lt;TracedEntry&lt;T&gt;&gt;</code>: The
     *                entries to unbox.
     * @return <code>ArrayList&lt;T&gt;</code>: The list of unboxed entries.
     */
    public static <T> ArrayList<T> unboxValues(Collection<TracedEntry<T>> entries) {

        ArrayList<T> values = new ArrayList<>(entries.size());
        for (TracedEntry<T> entry : entries) {

            values.add(entry.getValue());
        }
        return values;
    }

    /**
     * Returns whether or not this <code>TracedEntry</code> instance contains a
     * value.
     * 
     * @return <code>boolean</code>: Whether or not the <code>value</code> field of
     *         this <code>TracedEntry</code> instance is not equal to null.
     */
    public boolean containsValue() {

        return value != null;
    }

    /**
     * Returns the string representation of this <code>TracedEntry</code> instance.
     * 
     * @return <code>String</code>: The string representation of the
     *         <code>pathway</code> field of this <code>TracedEntry</code> instance.
     */
    @Override
    public String toString() {

        return pathway.toString();
    }
}
