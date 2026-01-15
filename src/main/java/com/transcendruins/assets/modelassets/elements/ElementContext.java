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

package com.transcendruins.assets.modelassets.elements;

import com.jme3.math.Vector3f;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetContext;

/**
 * <code>ElementContext</code>: A class representing the instantiation context
 * of an element.
 */
public final class ElementContext extends PrimaryAssetContext {

    private final Vector3f tileOffset;

    public final Vector3f getTileOffset() {

        return tileOffset;
    }

    /**
     * Creates a new instance of the <code>ElementContext</code> class.
     * 
     * @param presets  <code>AssetPresets</code>: The presets containing schema and
     *                 instantiation information of this <code>ElementContext</code>
     *                 instance.
     * @param location <code>GlobalLocationInstance</code>: The location of this
     *                 <code>ElementContext</code> instance.
     */
    public ElementContext(AssetPresets presets, GlobalLocationInstance location, Vector3f tileOffset) {

        super(presets, location);

        this.tileOffset = tileOffset;
    }

    @Override
    public final ElementInstance instantiate() {

        return (ElementInstance) instantiate(this);
    }
}
