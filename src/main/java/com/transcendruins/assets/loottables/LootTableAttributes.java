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

package com.transcendruins.assets.loottables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.extra.Range;
import com.transcendruins.assets.modelassets.items.ItemPresets;
import com.transcendruins.assets.scripts.TRScriptValue;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>LootTableAttributes</code>: A class which represents the
 * attributes of a <code>LootTableSchema</code> instance.
 */
public final class LootTableAttributes extends AssetAttributes {

    /**
     * <code>LootSchema</code>: The base loot schema of this
     * <code>LootTableAttributes</code> instance.
     */
    private final LootSchema loot;

    /**
     * Retrieves the base loot schema of this <code>LootTableAttributes</code>
     * instance.
     * 
     * @return <code>LootSchema</code>: The <code>loot</code> field of this
     *         <code>LootTableAttributes</code> instance.
     */
    public LootSchema getLoot() {

        return loot;
    }

    /**
     * <code>ImmutableList&lt;ItemPresets&gt;</code>: All items which can be dropped
     * by this <code>LootTableAttributes</code> instance.
     */
    private final ImmutableList<ItemPresets> items;

    /**
     * Retrieves all items which can be dropped by this
     * <code>LootTableAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;ItemPresets&gt;</code>: The <code>items</code>
     *         field of this <code>LootTableAttributes</code> instance.
     */
    public ImmutableList<ItemPresets> getItems() {

        return items;
    }

    /**
     * Compiles this <code>LootTableAttributes</code> instance into a
     * completed instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created
     *               this <code>LootTableAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to
     *               compile this <code>LootTableAttributes</code>
     *               instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>LootTableAttributes</code> instance is the
     *               base attribute set of a
     *               <code>LootTableAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>LootTableAttributes</code> instance.
     */
    public LootTableAttributes(AssetSchema schema, TracedDictionary json, boolean isBase)
            throws LoggedException {

        super(schema, json, isBase);

        loot = (json.getAsString("type", !isBase, null).containsValue()) ? createLoot(json) : null;
        items = loot != null ? new ImmutableList<>(loot.getItems()) : null;
    }

    /**
     * Creates a new instance of the
     * <code>LootTableAttributes.LootSchema</code>
     * subclass.
     * 
     * @param json <code>TracedDictionary</code>: The json to create the new
     *             <code>LootTableAttributes.LootSchema</code> instance from.
     * @return <code>LootSchema</code>: The created loot schema.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new <code>LootTableAttributes.Loot</code>
     *                         instance.
     */
    public final LootSchema createLoot(TracedDictionary json)
            throws MissingPropertyException, PropertyTypeException, LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);

        return switch (typeEntry.getValue()) {

            case "item" -> new LootValueSchema(json);

            case "collection" -> new LootPoolSchema(json, false);

            case "selection" -> new LootPoolSchema(json, true);

            default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    /**
     * <code>LootTableAttributes.LootSchema</code>: A subclass representing
     * any loot
     * schema.
     */
    public abstract class LootSchema {

        /**
         * <code>ImmutableList&lt;TRScriptValue&gt;</code>: The conditions required to
         * apply this <code>LootTableAttributes.LootSchema</code> instance.
         */
        private final ImmutableList<TRScriptValue> conditions;

        /**
         * Retrieves the conditions required to apply this
         * <code>LootTableAttributes.LootSchema</code> instance.
         * 
         * @return <code>ImmutableList&lt;TRScriptValue&gt;</code>: The
         *         <code>conditions</code> field of this
         *         <code>LootTableAttributes.LootSchema</code> instance.
         */
        public final ImmutableList<TRScriptValue> getConditions() {

            return conditions;
        }

        /**
         * <code>double</code>: The chance value of this
         * <code>LootTableAttributes.LootSchema</code> instance, represented as a
         * percentage greater than 0.
         */
        private final double chance;

        /**
         * Retrieves the chance value of this
         * <code>LootTableAttributes.LootSchema</code> instance.
         * 
         * @return <code>double</code>: The <code>chance</code> field of this
         *         <code>LootTableAttributes.LootSchema</code> instance.
         */
        public double getChance() {

            return chance;
        }

        private final Range count;

        public Range getCount() {

            return count;
        }

        /**
         * Creates a new instance of the
         * <code>LootTableAttributes.LootSchema</code>
         * subclass.
         * 
         * @param json <code>TracedDictionary</code>: The json to create this
         *             <code>LootTableAttributes.LootSchema</code> instance from.
         * @throws LoggedException Thrown if any exception is raised while creating this
         *                         <code>LootTableAttributes.LootSchema</code>
         *                         instance.
         */
        public LootSchema(TracedDictionary json)
                throws LoggedException {

            ArrayList<TRScriptValue> conditionsList = new ArrayList<>();

            TracedEntry<TracedArray> conditionsEntry = json.getAsArray("conditions", true);
            if (conditionsEntry.containsValue()) {

                TracedArray conditionsJson = conditionsEntry.getValue();
                for (int i : conditionsJson.getIndices()) {

                    conditionsList.add(new TRScriptValue(conditionsJson, i));
                }
            }

            conditions = new ImmutableList<>(conditionsList);

            TracedEntry<Double> chanceEntry = json.getAsDouble("chance", true, 100.0, num -> num > 0);
            chance = chanceEntry.getValue();

            count = Range.createRange(json, "count", true, true, num -> num >= 1);
        }

        public abstract List<ItemPresets> getItems();
    }

    public final class LootValueSchema extends LootSchema {

        private final ItemPresets item;

        public ItemPresets getItem() {

            return item;
        }

        public LootValueSchema(TracedDictionary json) throws LoggedException {

            super(json);

            item = ItemPresets.createPresets(json, "item", false);
            addAssetDependency(item);
        }

        @Override
        public List<ItemPresets> getItems() {

            return List.of(item);
        }
    }

    public final class LootPoolSchema extends LootSchema {

        private final boolean selection;

        public boolean getSelection() {

            return selection;
        }

        private final ImmutableMap<LootSchema, Integer> pools;

        public ImmutableMap<LootSchema, Integer> getPools() {

            return pools;
        }

        public LootPoolSchema(TracedDictionary json, boolean selection) throws LoggedException {

            super(json);

            this.selection = selection;

            HashMap<LootSchema, Integer> poolsMap = new HashMap<>();

            TracedEntry<TracedArray> poolsEntry = json.getAsArray("components", false);
            TracedArray poolsJson = poolsEntry.getValue();

            for (int i : poolsJson.getIndices()) {

                TracedEntry<TracedDictionary> poolEntry = poolsJson.getAsDict(i, false);
                TracedDictionary poolJson = poolEntry.getValue();

                // Retrieve the maximum number of times a pool can be rolled in a single check.
                // A value of -1 means there is no limit to the number of times a pool can be
                // rolled.
                TracedEntry<Integer> poolLimitEntry = poolJson.getAsInteger("limit", true, -1, num -> num >= 1);
                int poolLimit = poolLimitEntry.getValue();

                poolsMap.put(createLoot(poolJson), poolLimit);
            }

            pools = new ImmutableMap<>(poolsMap);
        }

        @Override
        public List<ItemPresets> getItems() {

            return pools.keySet().stream().flatMap(pool -> pool.getItems().stream()).collect(Collectors.toList());
        }
    }
}
