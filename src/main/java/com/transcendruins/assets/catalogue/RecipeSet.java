package com.transcendruins.assets.catalogue;

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
