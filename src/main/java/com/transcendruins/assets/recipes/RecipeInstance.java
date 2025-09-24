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

package com.transcendruins.assets.recipes;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;

/**
 * <code>RecipeInstance</code>: A class representing a generated recipe
 * instance.
 */
public final class RecipeInstance extends AssetInstance {

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
    }

    @Override
    protected void onUpdate(double time) {
    }
}
