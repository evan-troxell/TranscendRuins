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

package com.transcendruins.assets.layouts;

import java.awt.Dimension;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.layouts.LayoutAttributes.GenerationLayout;
import com.transcendruins.assets.layouts.placement.GenerationPlacement;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.utilities.selection.WeightedRoll;
import com.transcendruins.world.AreaGrid;

/**
 * <code>LayoutInstance</code>: A class representing a generated layout
 * instance.
 */
public final class LayoutInstance extends AssetInstance {

    /**
     * <code>GlobalLocationInstance</code>: The location of this
     * <code>LayoutInstance</code> instance.
     */
    private final GlobalLocationInstance location;

    /**
     * <code>WeightedRoll&lt;GenerationPlacement&gt;</code>: The spawn location of
     * this <code>LayoutInstance</code> instance.
     */
    private WeightedRoll<GenerationPlacement> spawn;

    /**
     * <code>GenerationLayout</code>: The default generation layout of this
     * <code>LayoutInstance</code> instance.
     */
    private GenerationLayout generation;

    /**
     * Generates the contents of this <code>LayoutInstance</code> instance.
     * 
     * @return <code>AreaGrid</code>: The generated contents.
     */
    public final AreaGrid generate() {

        DeterministicRandom random = getRandom();

        AreaGrid parent = new AreaGrid(new Dimension(), null);
        GenerationPlacement spawnPoint = spawn.get(random.next());
        AreaGrid area = generation.generateContent(parent, random, location, spawnPoint);

        if (area == null) {

            return parent;
        }

        return area;
    }

    /**
     * Creates a new instance of the <code>LayoutInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>LayoutInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public LayoutInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        LayoutContext context = (LayoutContext) assetContext;
        location = context.getLocation();
    }

    @Override
    public final void applyAttributes(Attributes attributeSet) {

        LayoutAttributes attributes = (LayoutAttributes) attributeSet;

        spawn = attributes.getSpawn();
        generation = attributes.getGeneration();
    }

    @Override
    protected final void onUpdate(double time) {
    }
}
