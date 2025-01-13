package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedCollection;

/**
 * <code>PropertyExceptionPathway</code>: A class representing the pathway of a
 * field exception within the retrieved file.
 */
public final class PropertyExceptionPathway {

    /**
     * <code>TracedPath</code>: The directory leading to the data structure
     * containing this <code>PropertyExceptionPathway</code>.
     */
    private final TracedPath path;

    /**
     * Retrieves the path of this <code>TracedProperty</code> instance.
     * 
     * @return <code>TracedPath</code>: The <code>path</code> field of this
     *         <code>TracedProperty</code> instance.
     */
    public TracedPath getPath() {

        return path;
    }

    /**
     * <code>TracedCollection</code>: The collection from which this
     * <code>PropertyExceptionPathway</code> was derived.
     */
    private final TracedCollection collection;

    /**
     * Retrieves the collection of this <code>PropertyExceptionPathway</code>
     * instance.
     * 
     * @return <code>TracedCollection</code>: The <code>collection</code> field of
     *         this <code>PropertyExceptionPathway</code> instance.
     */
    public TracedCollection getCollection() {

        return collection;
    }

    /**
     * <code>Object</code>: The retrieved key which, if erroneous, is the final
     * pointer to the element generating an error.
     */
    private final Object key;

    /**
     * Retrieves the key of this <code>PropertyExceptionPathway</code> instance.
     * 
     * @return <code>Object</code>: The <code>key</code> field of this
     *         <code>PropertyExceptionPathway</code> instance.
     */
    public Object getKey() {

        return key;
    }

    /**
     * Creates a new instance of the <code>TracedProperty</code> class.
     * 
     * @param path       <code>TracedPath</code>: The directory leading to the data
     *                   structure containing this
     *                   <code>PropertyExceptionPathway</code>.
     * @param collection <code>TracedCollection</code>: The collection from which
     *                   this <code>PropertyExceptionPathway</code> was derived.
     * @param key        <code>Object</code>: The retrieved key which, if erroneous,
     *                   is the final pointer to the element generating an error.
     */
    public PropertyExceptionPathway(TracedPath path, TracedCollection collection, Object key) {

        this.path = path;
        this.collection = collection;
        this.key = key;
    }

    /**
     * Returns the <code>String</code> representation of this
     * <code>PropertyExceptionPathway</code> instance.
     * 
     * @return <code>String</code>: The string representation of the pathway traced
     *         by the <code>collection</code> field of this
     *         <code>PropertyExceptionPathway</code> instance.
     */
    @Override
    public String toString() {

        return collection.toString();
    }
}
