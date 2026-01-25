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

import com.transcendruins.assets.assets.schema.AssetSchema;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.json.TracedCollection.TypeCase;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>RecipeAttributes</code>: A class which represents the attributes of a
 * <code>RecipeSchema</code> instance.
 */
public final class RecipeAttributes extends AssetAttributes {

    private final ImmutableMap<AssetPresets, Integer> input;

    public final ImmutableMap<AssetPresets, Integer> getInput() {

        return input;
    }

    private final ImmutableMap<Identifier, ImmutableList<Integer>> inputCounts;

    public final ImmutableMap<Identifier, ImmutableList<Integer>> getInputCounts() {

        return inputCounts;
    }

    private final AssetPresets output;

    public final AssetPresets getOutput() {

        return output;
    }

    private final Integer outputCount;

    public final Integer getOutputCount() {

        return outputCount;
    }

    private final Long duration;

    public final Long getDuration() {

        return duration;
    }

    /**
     * Compiles this <code>RecipeAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>RecipeAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>RecipeAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>RecipeAttributes</code> instance is the base attribute
     *               set of a <code>RecipeAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>RecipeAttributes</code> instance.
     */
    public RecipeAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        ArrayList<TypeCase<?, ImmutableMap<AssetPresets, Integer>>> inputCases = new ArrayList<>();
        inputCases.add(json.arrayCase(entry -> {

            TracedArray inputJson = entry.getValue();
            if (inputJson.isEmpty()) {

                throw new CollectionSizeException(entry, inputJson);
            }

            HashMap<AssetPresets, Integer> inputMap = new HashMap<>();
            for (int i : inputJson) {

                TracedEntry<TracedDictionary> inputEntry = inputJson.getAsDict(i, false);
                TracedDictionary inputEntryJson = inputEntry.getValue();

                TracedEntry<AssetPresets> itemEntry = inputEntryJson.getAsPresets("item", false, AssetType.ITEM);
                AssetPresets item = itemEntry.getValue();
                addAssetDependency(item);

                TracedEntry<Integer> countEntry = inputEntryJson.getAsInteger("count", true, 1, num -> num > 0);
                int count = countEntry.getValue();
                inputMap.put(item, count);
            }

            return new ImmutableMap<>(inputMap);
        }));
        inputCases.add(json.dictCase(entry -> {

            TracedDictionary inputJson = entry.getValue();

            TracedEntry<AssetPresets> itemEntry = inputJson.getAsPresets("item", false, AssetType.ITEM);
            AssetPresets item = itemEntry.getValue();
            addAssetDependency(item);

            TracedEntry<Integer> countEntry = inputJson.getAsInteger("count", true, 1, num -> num > 0);
            int count = countEntry.getValue();

            return new ImmutableMap<>(Map.of(item, count));
        }));

        if (!isBase()) {

            inputCases.add(json.nullCase(_ -> null));
        }

        input = json.get("input", inputCases);

        if (input != null) {

            inputCounts = new ImmutableMap<>(input.entrySet().stream().collect(Collectors.groupingBy(
                    entry -> entry.getKey().getIdentifier(),
                    Collectors.collectingAndThen(Collectors.toList(), list -> new ImmutableList<>(
                            list.stream().map(Map.Entry::getValue).sorted(Comparator.naturalOrder()).toList())))));
        } else {

            inputCounts = null;
        }

        TracedEntry<TracedDictionary> outputEntry = json.getAsDict("output", !isBase);
        if (outputEntry.containsValue()) {

            TracedDictionary outputJson = outputEntry.getValue();

            TracedEntry<AssetPresets> outputItemEntry = outputJson.getAsPresets("item", false, AssetType.ITEM);
            output = outputItemEntry.getValue();

            TracedEntry<Integer> outputCountEntry = outputJson.getAsInteger("count", true, 1, num -> num > 0);
            outputCount = outputCountEntry.getValue();
        } else {

            output = null;
            outputCount = null;
        }

        TracedEntry<Long> durationEntry = json.getAsDuration("duration", true, null, false);
        duration = durationEntry.getValue();
    }
}
