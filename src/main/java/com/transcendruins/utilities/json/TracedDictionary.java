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

import org.json.simple.JSONObject;

import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>TracedDictionary</code>: A class representing a dictionary whose
 * <code>parent.get()</code> method has been traced.
 */
public final class TracedDictionary extends TracedCollection {

    /**
     * The stored JSON dictionary information which this
     * </code>TracedDictionary</code> draws from.
     */
    private final JSONObject dictionary;

    /**
     * Creates a new instance of the <code>TracedDictionary</code> class.
     * 
     * @param dictionary <code>TracedEntry&lt;?&gt;</code>: The dictionary to assign
     *                   to this value.
     */
    public TracedDictionary(TracedEntry<?> dictionary) {

        super(dictionary.getPathway());
        this.dictionary = (JSONObject) dictionary.getValue();
    }

    /**
     * Creates a new instance of the <code>TracedDictionary</code> class, tracing
     * another key from a previously traced path.
     * 
     * @param dictionary <code>JSONObject</code>: The dictionary to assign to this
     *                   value.
     * @param path       <code>TracedPath</code>: The path to trace from.
     */
    public TracedDictionary(JSONObject dictionary, TracedPath path) {

        super(path);
        this.dictionary = dictionary;
    }

    /**
     * Retrieves a list containing all keys stored in this
     * <code>TracedDictionary</code> instance.
     * 
     * @return <code>ArrayList&lt;String&gt;</code>: All keys in this
     *         <code>TracedDictionary</code> instance.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> getKeys() {

        return new ArrayList<>(dictionary.keySet());
    }

    @Override
    public Object getValue(Object key) {

        return dictionary.get(key);
    }

    @Override
    public boolean containsKey(Object key) {

        return dictionary.get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {

        return dictionary.containsValue(value);
    }

    @Override
    public int size() {

        return dictionary.size();
    }

    @Override
    public boolean isEmpty() {

        return dictionary.isEmpty();
    }
}
