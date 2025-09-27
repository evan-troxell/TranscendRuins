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

import java.util.HashMap;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class RecipeSet {

    private final ImmutableMap<String, AssetPresets> recipes;

    public final ImmutableMap<String, AssetPresets> getRecipes() {

        return recipes;
    }

    public RecipeSet(TracedDictionary json) {

        HashMap<String, AssetPresets> recipesMap = new HashMap<>();

        for (String key : json) {

            try {

                TracedEntry<AssetPresets> recipeEntry = json.getAsPresets(key, false, AssetType.RECIPE);
                AssetPresets recipe = recipeEntry.getValue();

                recipesMap.put(key, recipe);
            } catch (LoggedException _) {
            }
        }

        recipes = new ImmutableMap<>(recipesMap);
    }
}
