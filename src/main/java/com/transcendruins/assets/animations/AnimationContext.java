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

package com.transcendruins.assets.animations;

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.world.World;

public final class AnimationContext extends AssetContext {

    /**
     * <code>AssetInstance</code>: The asset which created this
     * <code>AnimationContext</code> instance.
     */
    private final AssetInstance parent;

    /**
     * Retrieves the asset which created this
     * <code>AnimationContext</code> instance.
     * 
     * @return <code>AssetInstance</code>: The <code>parent</code> field of this
     *         <code>AnimationContext</code> instance.
     */
    public AssetInstance getParent() {

        return parent;
    }

    public AnimationContext(AnimationPresets presets, World world, AssetInstance parent) {

        super(presets, world);
        this.parent = parent;
    }
}
