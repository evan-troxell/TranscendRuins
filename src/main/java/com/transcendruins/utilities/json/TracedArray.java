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
import java.util.List;

import org.json.simple.JSONArray;

import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>TracedArray</code>: A class representing an array whose
 * <code>parent.get()</code> method has been traced.
 */
public final class TracedArray extends TracedCollection {

    /**
     * The stored JSON array information which this </code>TracedArray</code> draws
     * from.
     */
    private final JSONArray array;

    /**
     * Creates a new instance of the <code>TracedArray</code> class.
     * 
     * @param array <code>TracedEntry&lt;?&gt;</code>: The array to assign to this
     *              value.
     */
    public TracedArray(TracedEntry<?> array) {

        super(array.getPathway());
        this.array = (JSONArray) array.getValue();
    }

    /**
     * Creates a new instance of the <code>TracedArray</code> class, tracing
     * another key from a previously traced path.
     * 
     * @param dictionary <code>JSONArray</code>: The array to assign to this
     *                   value.
     * @param path       <code>TracedPath</code>: The path to trace from.
     */
    public TracedArray(JSONArray array, TracedPath path) {

        super(path);
        this.array = array;
    }

    /**
     * Retrieves an array containing all numerical indices stored in this
     * <code>TracedArray</code> instance.
     * 
     * @return <code>List&lt;Integer&gt;</code>: All indices in this
     *         <code>TracedArray</code>
     *         instance.
     */
    public List<Integer> getIndices() {

        int n = size();
        ArrayList<Integer> indices = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {

            indices.add(i);
        }

        return indices;
    }

    @Override
    public Object getValue(Object key) {

        return ((int) key < array.size()) ? array.get((int) key) : null;
    }

    @Override
    public boolean containsKey(Object key) {

        return getValue(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {

        return array.contains(value);
    }

    @Override
    public int size() {

        return array.size();
    }

    @Override
    public boolean isEmpty() {

        return array.isEmpty();
    }
}
