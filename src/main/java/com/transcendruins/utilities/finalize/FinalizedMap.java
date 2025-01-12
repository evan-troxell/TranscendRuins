package com.transcendruins.utilities.finalize;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FinalizedMap<K, V> extends HashMap<K, V> implements Finalized {
    
    private boolean isFinalized = false;

    public FinalizedMap() {}

    public FinalizedMap(int initialCapacity) {

        super(initialCapacity);
    }

    public FinalizedMap(int initialCapacity, float loadFactor) {

        super(initialCapacity, loadFactor);
    }

    public FinalizedMap(Map<K, V> map) {

        super(map);
    }

    @Override
    public void finalizeData() {

        if (isFinalized) {
            
            return;
        }

        isFinalized = true;

        for (Map.Entry<K, V> entry : entrySet()) {

            if (entry.getKey() instanceof Finalized finalKey) {

                finalKey.finalizeData();
            }
            
            if (entry.getValue() instanceof Finalized finalValue) {

                finalValue.finalizeData();
            }
        }
    }

    @Override
    public V put(K key, V value) {

        checkFinalized(isFinalized);
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {

        checkFinalized(isFinalized);
        return super.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

        checkFinalized(isFinalized);
        super.putAll(m);
    }

    @Override
    public void clear() {

        checkFinalized(isFinalized);
        super.clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {

        Set<Entry<K, V>> originalSet = super.entrySet();
        return Collections.unmodifiableSet(originalSet);
    }

    @Override
    public Set<K> keySet() {

        Set<K> originalSet = super.keySet();
        return Collections.unmodifiableSet(originalSet);
    }

    @Override
    public Collection<V> values() {

        Collection<V> originalCollection = super.values();
        return Collections.unmodifiableCollection(originalCollection);
    }
}