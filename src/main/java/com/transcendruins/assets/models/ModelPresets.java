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

package com.transcendruins.assets.models;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;

/**
 * <code>ModelPresets</code>: A class representing the presets of a model.
 */
public final class ModelPresets extends AssetPresets {

    /**
     * Creates a new instance of the <code>ModelPresets</code> class.
     * 
     * @param collection <code>TracedCollection</code>: The collection from which
     *                   this <code>ModelPresets</code> instance should be created.
     * @param key        <code>Object</code>: The key to retrieve from the
     *                   <code>collection</code> parameter.
     * @throws LoggedException Thrown if any exception is raised while creating this
     *                         <code>ModelPresets</code> instance.
     */
    private ModelPresets(TracedCollection collection, Object key) throws LoggedException {

        super(collection, key, AssetType.MODEL);
    }

    /**
     * Creates a new instance of the <code>ModelPresets</code> class.
     * 
     * @param collection      <code>TracedCollection</code>: The collection from
     *                        which
     *                        the new <code>ModelPresets</code> instance should be
     *                        created.
     * @param key             <code>Object</code>: The key to retrieve from the
     *                        <code>collection</code> parameter.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a null case is
     *                        allowed when creating the new
     *                        <code>ModelPresets</code> instance.
     * @return <code>ModelPresets</code>: The generated presets, or
     *         <code>null</code> if the value was not found and the
     *         <code>nullCaseAllowed</code> parameter is <code>false</code>.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new <code>ModelPresets</code> instance.
     */
    public static ModelPresets createPresets(TracedCollection collection, Object key, boolean nullCaseAllowed)
            throws LoggedException {

        if (!collection.containsKey(key) && nullCaseAllowed) {

            return null;
        }
        return new ModelPresets(collection, key);
    }
}
