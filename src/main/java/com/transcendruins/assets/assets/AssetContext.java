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

package com.transcendruins.assets.assets;

import com.transcendruins.world.World;

public abstract class AssetContext {

    private final AssetPresets presets;

    public final AssetPresets getPresets() {

        return presets;
    }

    private final World world;

    public final World getWorld() {

        return world;
    }

    /**
     * <code>double</code>: The randomized ID of this <code>AssetContext</code>
     * instance, in the range of <code>[0.0, 1.0]</code>.
     */
    private final double randomId;

    /**
     * Retreives the randomized ID of this <code>AssetContext</code> instance.
     * 
     * @return <code>double</code>: The <code>randomId</code> field of this
     *         <code>AssetContext</code> instance.
     */
    public final double getRandomId() {

        return randomId;
    }

    /**
     * <code>double</code>: The time of creation of this <code>AssetContext</code>
     * instance.
     */
    private final double runtimeSeconds;

    /**
     * Retrieves the time of creation of this <code>AssetContext</code> instance.
     * 
     * @return <code>double</code>: The <code>runtimeSeconds</code> field of this
     *         <code>AssetContext</code> instance.
     */
    public final double getRuntimeSeconds() {

        return runtimeSeconds;
    }

    public AssetContext(AssetPresets presets, World world) {

        this.presets = presets;

        this.world = world;
        this.randomId = world.nextRandom();

        this.runtimeSeconds = world.getRuntimeSeconds();
    }
}
