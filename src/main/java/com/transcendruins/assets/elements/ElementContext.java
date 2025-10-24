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

import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.primaryassets.PrimaryAssetContext;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.world.World;

/**
 * <code>ElementContext</code>: A class representing the instantiation context
 * of an element.
 */
public final class ElementContext extends PrimaryAssetContext {

    private Vector tileOffset = Vector.IDENTITY_VECTOR;

    public final void setTileOffset(Vector tileOffset) {

        this.tileOffset = tileOffset;
    }

    public final Vector getTileOffset() {

        return tileOffset;
    }

    /**
     * Creates a new instance of the <code>ElementContext</code> class.
     * 
     * @param presets <code>AssetPresets</code>: The presets containing schema and
     *                instantiation information of this <code>ElementContext</code>
     *                instance.
     * @param world   <code>World</code>: The world copy of this
     *                <code>ElementContext</code> instance.
     * @param parent  <code>AssetInstance</code>: The parent to assign to this
     *                <code>ElementContext</code> instance.
     */
    public ElementContext(AssetPresets presets, World world, AssetInstance parent) {

        super(presets, world, parent);
    }

    @Override
    public final ElementInstance instantiate() {

        return (ElementInstance) instantiate(this);
    }
}
