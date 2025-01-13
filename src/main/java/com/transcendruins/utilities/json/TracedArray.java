package com.transcendruins.utilities.json;

import org.json.simple.JSONArray;

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
     * Retrieves the JSONArray object used as the collection method of this
     * <code>TracedArray</code> instance method.
     * 
     * @return <code>JSONArray</code>: The <code>JSONArray</code> representing the
     *         stored list of this collection.
     */
    @Override
    public JSONArray getCollection() {

        return array;
    }

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
     * Retrieves an array containing all numerical indices stored in this
     * <code>TracedArray</code> instance.
     * 
     * @return <code>int[]</code>: All indices in this <code>TracedArray</code>
     *         instance.
     */
    public int[] getIndices() {

        int n = size();
        int[] indicesArray = new int[n];

        for (int i = 0; i < n; i++) {
            indicesArray[i] = i;
        }

        return indicesArray;
    }

    /**
     * Retrieve a value from this <code>TracedArray</code> instance using a specific
     * key.
     * 
     * @param key <code>Object</code>: The key to retrieve using.
     * @return <code>Object</code>: The retrieved value.
     */
    @Override
    public Object getValue(Object key) {

        return ((int) key < array.size()) ? array.get((int) key) : null;
    }

    /**
     * Determines whether or not this <code>TracedArray</code> instance contains a
     * specific key.
     * 
     * @param key <code>Object</code>: The key to apply.
     * @return <code>boolean</code>: Whether or not this <code>TracedArray</code>
     *         instance contains the applied key.
     */
    @Override
    public boolean containsKey(Object key) {

        return getValue(key) != null;
    }

    /**
     * Determines whether or not this <code>TracedArray</code> instance contains a
     * specific value.
     * 
     * @param value <code>Object</code>: The value to apply.
     * @return <code>boolean</code>: Whether or not this <code>TracedArray</code>
     *         instance contains the applied value.
     */
    @Override
    public boolean containsValue(Object value) {

        return array.contains(value);
    }

    /**
     * Retrieves the length of this array.
     * 
     * @return <code>int</code>: The length of this array.
     */
    public int size() {

        return array.size();
    }

    /**
     * Determines whether or not this <code>TracedArray</code> instance is empty.
     * 
     * @return <code>boolean</code>: If the <code>array</code> field of this
     *         <code>TracedArray</code> instance is empty.
     */
    public boolean isEmpty() {

        return array.isEmpty();
    }
}
