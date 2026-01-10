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

package com.transcendruins.assets.assets.schema;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>AssetAttributes</code>: A class which represents the attributes of an
 * <code>AssetSchema</code> instance.
 */
public abstract class AssetAttributes extends Attributes {

    /**
     * <code>AssetSchema</code>: The asset schema which created this
     * <code>AssetAttributes</code> instance.
     */
    private final AssetSchema schema;

    /**
     * Creates a new instance of the <code>AssetAttributes</code> class.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>AssetAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>AssetAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>AssetAttributes</code> instance is the base attribute set
     *               of an <code>AssetSchema</code> instance.
     */
    public AssetAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) {

        super(isBase);
        this.schema = schema;
    };

    /**
     * Adds an asset dependency to the <code>AssetSchema</code> instance which
     * created this <code>AssetAttributes</code> instance.
     * 
     * @param dependency <code>AssetPresets</code>: The presets of the dependency to
     *                   be added.
     */
    public final void addAssetDependency(AssetPresets dependency) {

        schema.addAssetDependency(dependency);
    }

    /**
     * Adds an asset dependency to the <code>AssetSchema</code> instance which
     * created this <code>AssetAttributes</code> instance.
     * 
     * @param type       <code>AssetType</code>: The type of the dependency to be
     *                   added.
     * @param identifier <code>TracedEntry&lt;Identifier&gt;</code>: The identifier
     *                   of the dependency to be added.
     */
    public final void addAssetDependency(AssetType type, TracedEntry<Identifier> identifier) {

        schema.addAssetDependency(type, identifier);
    }
}
