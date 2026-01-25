/* Copyright 2026 Evan Troxell
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

import java.util.Iterator;
import java.util.List;

import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyExceptionPathway;

/**
 * <code>TracedArray</code>: A class representing an array whose
 * <code>parent.get()</code> method has been traced.
 */
public final class TracedArray extends TracedCollection implements Iterable<Integer> {

    /**
     * <code>List&lt;?&gt;</code>: The stored JSON array information which this
     * </code>TracedArray</code> draws from.
     */
    private final List<?> array;

    /**
     * Creates a new instance of the <code>TracedArray</code> class, tracing another
     * key from a previously traced path.
     * 
     * @param dictionary <code>List&lt;Object&gt;</code>: The array to assign to
     *                   this value.
     * @param path       <code>PropertyExceptionPathway</code>: The path to trace
     *                   from.
     */
    public TracedArray(List<?> array, PropertyExceptionPathway path) {

        super(path);
        this.array = array;
    }

    @Override
    public final Object getValue(Object key) {

        return (0 <= (int) key && (int) key < array.size()) ? array.get((int) key) : null;
    }

    @Override
    public final boolean containsKey(Object key) {

        return getValue(key) != null;
    }

    @Override
    public final boolean containsValue(Object value) {

        return array.contains(value);
    }

    @Override
    public final int size() {

        return array.size();
    }

    @Override
    public final boolean isEmpty() {

        return array.isEmpty();
    }

    @Override
    public final Iterator<Integer> iterator() {

        int size = size();

        return new Iterator<Integer>() {

            private int i = 0;

            @Override
            public boolean hasNext() {

                return i < size;
            }

            @Override
            public Integer next() {

                return i++;
            }

        };
    }
}
