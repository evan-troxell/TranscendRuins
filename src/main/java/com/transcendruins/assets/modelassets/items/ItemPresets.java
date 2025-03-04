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

package com.transcendruins.assets.modelassets.items;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;

/**
 * <code>ItemPresets</code>: A class representing the presets of an item.
 */
public final class ItemPresets extends AssetPresets {

    /**
     * Creates a new instance of the <code>ItemPresets</code> class.
     * 
     * @param collection <code>TracedCollection</code>: The collection from which
     *                   this <code>ItemPresets</code> instance should be created.
     * @param key        <code>Object</code>: The key to retrieve from the
     *                   <code>collection</code> parameter.
     * @throws LoggedException Thrown if any exception is raised while creating this
     *                         <code>ItemPresets</code> instance.
     */
    private ItemPresets(TracedCollection collection, Object key) throws LoggedException {

        super(collection, key, AssetType.ITEM);
    }

    /**
     * Creates a new instance of the <code>ItemPresets</code> class.
     * 
     * @param collection      <code>TracedCollection</code>: The collection from
     *                        which
     *                        the new <code>ItemPresets</code> instance should be
     *                        created.
     * @param key             <code>Object</code>: The key to retrieve from the
     *                        <code>collection</code> parameter.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a null case is
     *                        allowed when creating the new
     *                        <code>ItemPresets</code> instance.
     * @return <code>ItemPresets</code>: The generated presets, or
     *         <code>null</code> if the value was not found and the
     *         <code>nullCaseAllowed</code> parameter is <code>false</code>.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new <code>ItemPresets</code> instance.
     */
    public static ItemPresets createPresets(TracedCollection collection, Object key, boolean nullCaseAllowed)
            throws LoggedException {

        if (!collection.containsKey(key) && nullCaseAllowed) {

            return null;
        }
        return new ItemPresets(collection, key);
    }
}
