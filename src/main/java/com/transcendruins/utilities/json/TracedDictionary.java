package com.transcendruins.utilities.json;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>TracedDictionary</code>: A class representing a dictionary whose <code>parent.get()</code> method has been traced.
 */
public final class TracedDictionary extends TracedCollection {

    /**
     * The stored JSON dictionary information which this </code>TracedDictionary</code> draws from.
     */
    private final JSONObject dictionary;

    /**
     * Creates a new instance of the <code>TracedDictionary</code> class.
     * @param dictionary <code>TracedEntry&lt;?&gt;</code>: The dictionary to assign to this value.
     */
    public TracedDictionary(TracedEntry<?> dictionary) {

        super(dictionary.getPathway());
        this.dictionary = (JSONObject) dictionary.getValue();
    }

    /**
     * Creates a new instance of the <code>TracedDictionary</code> class, tracing another key from a previously traced path.
     * @param dictionary <code>JSONObject</code>: The dictionary to assign to this value.
     * @param path <code>TracedPath</code>: The path to trace from.
     */
    public TracedDictionary(JSONObject dictionary, TracedPath path) {

        super(path);
        this.dictionary = dictionary;
    }

    /**
     * Retrieves a list containing all keys stored in this <code>TracedDictionary</code> instance.
     * @return <code>List&lt;String&gt;</code>: All keys in this <code>TracedDictionary</code> instance.
     */
    @SuppressWarnings("unchecked")
    public List<String> getKeys() {

        return new ArrayList<>(dictionary.keySet());
    }

    /**
     * Retrieves a value from this <code>TracedDictionary</code> instance using a specific key.
     * @param key <code>Object</code>: The key to retrieve using.
     * @return <code>Object</code>: The retrieved value.
     */
    @Override
    public Object getValue(Object key) {

        return dictionary.get(key);
    }

    /**
     * Determines whether or not this <code>TracedDictionary</code> instance contains a specific key.
     * @param key <code>Object</code>: The key to apply.
     * @return <code>boolean</code>: Whether or not this <code>TracedDictionary</code> instance contains the applied key.
     */
    @Override
    public boolean containsKey(Object key) {

        return dictionary.get(key) != null;
    }

    /**
     * Determines whether or not this <code>TracedDictionary</code> instance contains a specific value.
     * @param value <code>Object</code>: The value to apply.
     * @return <code>boolean</code>: Whether or not this <code>TracedDictionary</code> instance contains the applied value.
     */
    @Override
    public boolean containsValue(Object value) {

        return dictionary.containsValue(value);
    }

    /**
     * Retrieves the dictionary object used as the collection method of this <code>TracedDictionary</code> instance.
     * @return <code>JSONObject</code>: The <code>JSONObject</code> representing the stored dictionary of this collection.
     */
    @Override
    public JSONObject getCollection() {

        return dictionary;
    }
}
