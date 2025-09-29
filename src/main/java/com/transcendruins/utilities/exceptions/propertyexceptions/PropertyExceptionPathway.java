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

package com.transcendruins.utilities.exceptions.propertyexceptions;

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableList;

/**
 * <code>PropertyExceptionPathway</code>: A class representing the pathway of a
 * field exception within the retrieved file.
 */
public final class PropertyExceptionPathway {

    /**
     * <code>TracedPath</code>: The directory leading to the collection containing
     * this <code>PropertyExceptionPathway</code>.
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
     * <code>ImmutableList&lt;Object&gt;</code>: The internal path along which this
     * <code>PropertyExceptionPathway</code> follows.
     */
    private final ImmutableList<Object> internalPath;

    /**
     * Retrieves the internal path along which this
     * <code>PropertyExceptionPathway</code> follows.
     * 
     * @return <code>ImmutableList&lt;Object&gt;</code>: The
     *         <code>internalPath</code> field of this
     *         <code>PropertyExceptionPathway</code> instance.
     */
    public final ImmutableList<Object> getInternalPath() {

        return internalPath;
    }

    /**
     * Creates a new instance of the <code>TracedProperty</code> class.
     * 
     * @param path <code>TracedPath</code>: The filepath leading to the collection
     *             containing this <code>PropertyExceptionPathway</code>.
     */
    public PropertyExceptionPathway(TracedPath path) {

        this.path = path;

        internalPath = new ImmutableList<>();
    }

    /**
     * Creates a new instance of the <code>TracedProperty</code> class.
     * 
     * @param path <code>PropertyExceptionPathway</code>: The pathway leading to the
     *             collection containing this <code>PropertyExceptionPathway</code>.
     * @param key  <code>Object</code>: The retrieved key which, if erroneous, is
     *             the final pointer to the element generating an error.
     */
    private PropertyExceptionPathway(PropertyExceptionPathway pathway, Object key) {

        this.path = pathway.getPath();

        ArrayList<Object> internalPathList = new ArrayList<>(pathway.getInternalPath());
        internalPathList.add(key);
        internalPath = new ImmutableList<>(internalPathList);
    }

    /**
     * Creates a new instance of the <code>TracedProperty</code> class.
     * 
     * @param key <code>Object</code>: The retrieved key which, if erroneous, is the
     *            final pointer to the element generating an error.
     */
    public PropertyExceptionPathway extend(Object key) {

        return new PropertyExceptionPathway(this, key);
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

        List<String> strings = new ArrayList<>(internalPath.stream().map(Object::toString).toList());
        String end = (!strings.isEmpty()) ? " -> " + strings.removeLast() : "";

        return String.join(", ", strings) + end;
    }
}
