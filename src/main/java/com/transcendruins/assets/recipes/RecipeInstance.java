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

package com.transcendruins.assets.recipes;

import java.util.function.Function;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.World;

/**
 * <code>RecipeInstance</code>: A class representing a generated recipe
 * instance.
 */
public final class RecipeInstance extends AssetInstance {

    private ImmutableMap<AssetPresets, Integer> input;

    public final ImmutableMap<AssetPresets, Integer> getInput() {

        return input;
    }

    private ImmutableMap<Identifier, ImmutableList<Integer>> inputCounts;

    public final ImmutableMap<Identifier, ImmutableList<Integer>> getInputCounts() {

        return inputCounts;
    }

    private AssetPresets output;

    public final AssetPresets getOutput() {

        return output;
    }

    private int outputCount;

    public final int getOutputCount() {

        return outputCount;
    }

    private long duration;

    public final long getDuration() {

        return duration;
    }

    /**
     * Creates a new instance of the <code>RecipeInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>RecipeInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public RecipeInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        RecipeContext context = (RecipeContext) assetContext;
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        RecipeAttributes attributes = (RecipeAttributes) attributeSet;

        input = calculateAttribute(attributes.getInput(), input);
        inputCounts = calculateAttribute(attributes.getInputCounts(), inputCounts);

        output = calculateAttribute(attributes.getOutput(), output);
        outputCount = calculateAttribute(attributes.getOutputCount(), outputCount);

        duration = calculateAttribute(attributes.getDuration(), duration, attributes, 0l);
    }

    @Override
    protected void onUpdate(double time) {
    }

    @Override
    public final RecipeInstance clone(Function<AssetPresets, ? extends AssetContext> contextualize, World world) {

        RecipeInstance asset = (RecipeInstance) super.clone(contextualize, world);

        asset.input = input;
        asset.inputCounts = inputCounts;
        asset.output = output;
        asset.outputCount = outputCount;
        asset.duration = duration;

        return asset;
    }
}
