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

package com.transcendruins.utilities.immutable;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>ImmutableMap&lt;K, V&gt;</code>: A <code>Map</code> which is immutable.
 */
public final class ImmutableMap<K, V> implements Map<K, V>, Immutable {

    private final HashMap<K, V> map;

    /**
     * Creates a new, empty instance of the <code>ImmutableMap</code> class.
     */
    public ImmutableMap() {

        map = new HashMap<>();
    }

    /**
     * Creates a new instance of the <code>ImmutableMap</code> class using a map of
     * values.
     * 
     * @param map <code>Map&lt;K, V&gt;</code>: The values to add to this
     *            <code>ImmutableMap</code> instance.
     */
    public ImmutableMap(Map<K, V> map) {

        this.map = new HashMap<>(map);
    }

    /**
     * This method is not implemented in the <code>ImmutableMap</code> class and
     * will raise an exception.
     */
    @Override
    public V put(K key, V value) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableMap</code> class and
     * will raise an exception.
     */
    @Override
    public V remove(Object key) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableMap</code> class and
     * will raise an exception.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableMap</code> class and
     * will raise an exception.
     */
    @Override
    public void clear() {

        throw raiseError();
    }

    /**
     * Creates an immutable set of the entries in this <code>ImmutableMap</code>
     * instance.
     * 
     * @return <code>ImmutableSet&lt;Entry&lt;K, V&gt;&gt;</code>: An immutable
     *         version of the map entries.
     */
    @Override
    public ImmutableSet<Entry<K, V>> entrySet() {

        return new ImmutableSet<>(map.entrySet());
    }

    /**
     * Creates an immutable set of the keys in this <code>ImmutableMap</code>
     * instance.
     * 
     * @return <code>ImmutableSet&lt;K&gt;</code>: An immutable version of the key
     *         set.
     */
    @Override
    public ImmutableSet<K> keySet() {

        return new ImmutableSet<>(map.keySet());
    }

    /**
     * Creates an immutable list of the values in this <code>ImmutableMap</code>
     * instance.
     * 
     * @return <code>ImmutableList&lt;V&gt;</code>: An immutable version of the
     *         values collection.
     */
    @Override
    public ImmutableList<V> values() {

        return new ImmutableList<>(map.values());
    }

    @Override
    public int size() {

        return map.size();
    }

    @Override
    public boolean isEmpty() {

        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {

        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {

        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {

        return map.get(key);
    }
}