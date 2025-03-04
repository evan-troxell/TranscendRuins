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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ImmutableMap<K, V> extends HashMap<K, V> implements Immutable {

    private boolean finalized = false;

    public ImmutableMap() {

        finalized = true;
    }

    public ImmutableMap(Map<K, V> map) {

        super(map);
        finalized = true;
    }

    @Override
    public V put(K key, V value) {

        raiseError(finalized);
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {

        raiseError(finalized);
        return super.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

        raiseError(finalized);
        super.putAll(m);
    }

    @Override
    public void clear() {

        raiseError(finalized);
        super.clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {

        return new ImmutableSet<>(super.entrySet());
    }

    @Override
    public Set<K> keySet() {

        return new ImmutableSet<>(super.keySet());
    }

    @Override
    public Collection<V> values() {

        return new ImmutableList<>(super.values());
    }
}