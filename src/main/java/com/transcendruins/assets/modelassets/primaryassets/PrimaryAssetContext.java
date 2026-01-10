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

package com.transcendruins.assets.modelassets.primaryassets;

import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.modelassets.ModelAssetContext;

/**
 * <code>PrimaryAssetContext</code>: A class representing the instantiation
 * context of a primary asset.
 */
public abstract class PrimaryAssetContext extends ModelAssetContext {

    private final GlobalLocationInstance location;

    public final GlobalLocationInstance getLocation() {

        return location;
    }

    /**
     * Creates a new instance of the <code>PrimaryAssetContext</code> class.
     * 
     * @param presets  <code>AssetPresets</code>: The presets containing schema and
     *                 instantiation information of this
     *                 <code>PrimaryAssetContext</code> instance.
     * @param location <code>GlobalLocationInstance</code>: The location of this
     *                 <code>PrimaryAssetContext</code> instance.
     */
    public PrimaryAssetContext(AssetPresets presets, GlobalLocationInstance location) {

        super(presets, location.getWorld(), location);

        this.location = location;
    }
}
