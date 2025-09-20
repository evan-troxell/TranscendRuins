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

package com.transcendruins.assets.elements;

import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>ElementAttributes</code>: A class which represents the attributes of an
 * <code>ElementSchema</code> instance.
 */
public final class ElementAttributes extends PrimaryAssetAttributes {

    /**
     * <code>Boolean</code>: Whether or not the rotation of this
     * <code>ElementAttributes</code> instance should snap to the four cardinal
     * directions.
     */
    private final Boolean gridRotationSnap;

    /**
     * Retrieves whether or not the rotation of this <code>ElementAttributes</code>
     * instance should snap to the four cardinal directions.
     * 
     * @return <code>Boolean</code>: The <code>gridRotationSnap</code> field of this
     *         <code>ElementAttributes</code> instance.
     */
    public Boolean getGridRotationSnap() {

        return gridRotationSnap;
    }

    /**
     * Compiles this <code>ElementAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>ElementAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>ElementAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>ElementAttributes</code> instance is the base attribute
     *               set of an <code>ElementSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>ElementAttributes</code> instance.
     */
    public ElementAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        TracedEntry<Boolean> gridRotationSnapEntry = json.getAsBoolean("gridRotationSnap", true, isBase ? true : null);
        gridRotationSnap = gridRotationSnapEntry.getValue();
    }
}
