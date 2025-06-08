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
 * <code>ImmutableMap&lt;K, V&gt;</code>: A <code>HashMap</code> which is
 * immutable.
 */
public final class ImmutableMap<K, V> extends HashMap<K, V> implements Immutable {

    /**
     * <code>boolean</code>: Whether or not this <code>ImmutableMap</code> instance
     * has been finalized yet.
     */
    private boolean finalized = false;

    /**
     * Creates a new, empty instance of the <code>ImmutableMap</code> class.
     */
    public ImmutableMap() {

        finalized = true;
    }

    /**
     * Creates a new instance of the <code>ImmutableMap</code> class using a map of
     * values.
     * 
     * @param map <code>Map&lt;K, V&gt;</code>: The values to add to this
     *            <code>ImmutableMap</code> instance.
     */
    public ImmutableMap(Map<K, V> map) {

        super(map);
        finalized = true;
    }

    /**
     * This method is not implemented in the <code>ImmutableMap</code> class and
     * will raise an exception.
     */
    @Override
    public V put(K key, V value) {

        raiseError(finalized);
        return super.put(key, value);
    }

    /**
     * This method is not implemented in the <code>ImmutableMap</code> class and
     * will raise an exception.
     */
    @Override
    public V remove(Object key) {

        raiseError(finalized);
        return super.remove(key);
    }

    /**
     * This method is not implemented in the <code>ImmutableMap</code> class and
     * will raise an exception.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

        raiseError(finalized);
        super.putAll(m);
    }

    /**
     * This method is not implemented in the <code>ImmutableMap</code> class and
     * will raise an exception.
     */
    @Override
    public void clear() {

        raiseError(finalized);
        super.clear();
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

        return new ImmutableSet<>(super.entrySet());
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

        return new ImmutableSet<>(super.keySet());
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

        return new ImmutableList<>(super.values());
    }
}