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

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.world.World;

/**
 * <code>ModelContext</code>: A class representing the instantiation context of
 * a model.
 */
public final class ModelContext extends AssetContext {

    /**
     * Creates a new instance of the <code>ModelContext</code> class.
     * 
     * @param presets <code>AssetPresets</code>: The presets containing schema and
     *                instantiation information of this <code>ModelContext</code>
     *                instance.
     * @param world   <code>World</code>: The world copy of this
     *                <code>ModelContext</code> instance.
     * @param parent  <code>AssetInstance</code>: The parent to assign to this
     *                <code>ModelContext</code> instance.
     */
    public ModelContext(AssetPresets presets, World world, AssetInstance parent) {

        super(presets, world, parent);
    }

    @Override
    public final ModelInstance instantiate() {

        return (ModelInstance) instantiate(this);
    }
}
