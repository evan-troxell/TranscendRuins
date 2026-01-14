/* Copyright 2026 Evan Troxell
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

package com.transcendruins.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;

/**
 * <code>PropertyHolder</code>: A class representing a game object which can
 * hold both public and private properties.
 */
public abstract class PropertyHolder {

    /**
     * <code>PropertyHolder</code>: The parent asset to this
     * <code>PropertyHolder</code> instance. This is not required to hold a value.
     */
    private PropertyHolder parent;

    /**
     * Sets the parent of this <code>PropertyHolder</code> instance.
     * 
     * @param parent <code>PropertyHolder</code>: The parent of this
     *               <code>PropertyHolder</code> instance.
     */
    protected final void setParent(PropertyHolder parent) {

        this.parent = parent;
        setProperty("parent", parent);
    }

    /**
     * Determines whether or not this <code>PropertyHolder</code> instance has a
     * parent asset.
     * 
     * @return <code>boolean</code>: Whether or not the <code>parent</code> field of
     *         this <code>PropertyHolder</code> instance is not null.
     */
    public final boolean hasParent() {

        return parent != null;
    }

    /**
     * <code>HashMap&lt;String, Object&gt;</code>: The set of private properties of
     * this <code>PropertyHolder</code> instance.
     */
    private final HashMap<String, Object> privateProperties = new HashMap<>();

    protected final void setProperty(String property, boolean value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, int value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, long value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, float value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, double value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, String value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, List<?> value) {

        privateProperties.put(property, new ImmutableList<>(value));
    }

    protected final void setProperty(String property, ImmutableList<?> value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, Map<String, ?> value) {

        privateProperties.put(property, new ImmutableMap<>(value));
    }

    protected final void setProperty(String property, ImmutableMap<String, ?> value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, PropertyHolder holder) {

        privateProperties.put(property, holder);
    }

    /**
     * <code>HashMap&lt;String, Object&gt;</code>: The set of public properties of
     * this <code>PropertyHolder</code> instance.
     */
    private final HashMap<String, Object> publicProperties = new HashMap<>();

    /**
     * Sets a public property of this <code>PropertyHolder</code> instance.
     * 
     * @param property <code>String</code>: The property to set.
     * @param value    <code>Object</code>: The value to set.
     */
    public final void setPublicProperty(String property, Object value) {

        publicProperties.put(property, value);
    }

    /**
     * Determines whether or not this <code>PropertyHolder</code> instance contains
     * a property.
     * 
     * @param property <code>String</code>: The property to check for.
     * @return <code>boolean</code>: Whether or not the property is contained within
     *         the <code>privateProperties</code> field,
     *         <code>publicProperties</code>, or the parent properties of this
     *         <code>PropertyHolder</code> instance.
     */
    public final boolean hasProperty(String property) {

        return getProperty(property) != null;
    }

    /**
     * Retrieves a property from an asset, dictionary, or list using a pathway of
     * token values.
     * 
     * @param val    <code>Object</code>: The object whose value to retrieve. If the
     *               <code>tokens</code> parameter is empty, this is the value which
     *               will be returned; otherwise, if this is an asset, dictionary,
     *               or list, its property will be returned, and if it is NOT an
     *               indexable item, a <code>null</code> value will be returned.
     * @param tokens <code>String[]</code>: The token pathway to follow. If this has
     *               a length of <code>0</code>, the <code>val</code> parameter will
     *               be returned; otherwise, this pathway will be traced.
     * @return <code>TRScript</code>: The retrieved property as a script.
     */
    private static TRScript getProperty(Object val, String[] tokens) {

        if (val == null || tokens.length == 0) {

            return new TRScript(val);
        }

        return switch (val) {

        case PropertyHolder holder -> {

            yield holder.getProperty(tokens);
        }

        case Map<?, ?> mapVal -> {

            String next = tokens[0];

            yield getProperty(mapVal.get(next), Arrays.copyOfRange(tokens, 1, tokens.length));
        }

        case List<?> listVal -> {

            String next = tokens[0];
            int nextIndex;

            try {

                nextIndex = Integer.parseInt(next);
            } catch (NumberFormatException e) {

                yield new TRScript(null);
            }

            yield getProperty(listVal.get(nextIndex), Arrays.copyOfRange(tokens, 1, tokens.length));
        }

        default -> new TRScript(null);

        };
    }

    /**
     * Retrieves a property from this <code>PropertyHolder</code> following a
     * property path.
     * 
     * @param tokens <code>String[]</code>: The property pathway to follow, split
     *               into tokens between the '<code>.</code>' character.
     * @return <code>TRScript</code>: The retrieved property.
     */
    private TRScript getProperty(String[] tokens) {

        if (tokens.length == 0) {

            return null;
        }

        String property = tokens[0];

        // Retrieve the public property if applicable, otherwise use the private
        // property
        // This allows the public property to override the private without overriding
        // the public property.
        Object propertyVal = publicProperties.getOrDefault(property, privateProperties.get(property));

        // Check the parent value if the property could not be found.
        if (propertyVal == null && hasParent()) {

            return parent.getProperty(tokens);
        }

        String[] nextTokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        return getProperty(propertyVal, nextTokens);
    }

    /**
     * Retrieves a property from this <code>PropertyHolder</code> instance.
     * 
     * @param property <code>String</code>: The property to retrieve.
     * @return <code>TRScript</code>: The retrieved property
     */
    public final TRScript getProperty(String property) {

        String[] tokens = property.split("\\.");

        return getProperty(tokens);
    }
}