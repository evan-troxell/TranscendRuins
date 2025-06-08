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

import static com.transcendruins.assets.AssetType.ITEM;
import com.transcendruins.assets.SelectionType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.extra.Range;
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
 * <code>LootTableAttributes</code>: A class which represents the attributes of
 * a <code>LootTableSchema</code> instance.
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
     * <code>ImmutableList&lt;AssetPresets&gt;</code>: All items which can be
     * dropped by this <code>LootTableAttributes</code> instance.
     */
    private final ImmutableList<AssetPresets> items;

    /**
     * Retrieves all items which can be dropped by this
     * <code>LootTableAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;AssetPresets&gt;</code>: The
     *         <code>items</code> field of this <code>LootTableAttributes</code>
     *         instance.
     */
    public ImmutableList<AssetPresets> getItems() {

        return items;
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The list of component IDs which
     * should be disabled by this <code>LootTableAttributes</code> instance.
     */
    private final ImmutableList<String> disableByComponentId;

    /**
     * Retrieves the list of component IDs which should be disabled by this
     * <code>LootTableAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The
     *         <code>disableByComponentId</code> field of this
     *         <code>LootTableAttributes</code> instance.
     */
    public ImmutableList<String> getDisableByComponentId() {

        return disableByComponentId;
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The list of component tags which
     * should be disabled by this <code>LootTableAttributes</code> instance.
     */
    private final ImmutableList<String> disableByComponentTag;

    /**
     * Retrieves the list of component tags which should be disabled by this
     * <code>LootTableAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The
     *         <code>disableByComponentTag</code> field of this
     *         <code>LootTableAttributes</code> instance.
     */
    public ImmutableList<String> getDisableByComponentTag() {

        return disableByComponentTag;
    }

    /**
     * Compiles this <code>LootTableAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>LootTableAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>LootTableAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>LootTableAttributes</code> instance is the base attribute
     *               set of a <code>LootTableAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>LootTableAttributes</code> instance.
     */
    public LootTableAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        boolean defined = json.getAsString("type", !isBase, null).containsValue();
        loot = defined ? createLoot(json, new ArrayList<>()) : null;
        items = loot != null ? new ImmutableList<>(loot.getItems()) : null;

        TracedEntry<TracedArray> disableByComponentIdEntry = json.getAsArray("disableByComponentId", false);
        if (disableByComponentIdEntry.containsValue()) {

            TracedArray disableByComponentIdJson = disableByComponentIdEntry.getValue();
            ArrayList<String> disableByComponentIdList = new ArrayList<>();

            for (int i : disableByComponentIdJson) {

                TracedEntry<String> disableIdEntry = disableByComponentIdJson.getAsString(i, false, null);
                String disableId = disableIdEntry.getValue();

                disableByComponentIdList.add(disableId);
            }

            disableByComponentId = new ImmutableList<>(disableByComponentIdList);
        } else {

            disableByComponentId = null;
        }

        TracedEntry<TracedArray> disableByComponentTagsEntry = json.getAsArray("disableByComponentTags", false);

        if (disableByComponentTagsEntry.containsValue()) {

            TracedArray disableByComponentTagsJson = disableByComponentTagsEntry.getValue();
            ArrayList<String> disableByComponentTagsList = new ArrayList<>();

            for (int i : disableByComponentTagsJson) {

                TracedEntry<String> disableTagEntry = disableByComponentTagsJson.getAsString(i, false, null);
                String disableTag = disableTagEntry.getValue();

                disableByComponentTagsList.add(disableTag);
            }

            disableByComponentTag = new ImmutableList<>(disableByComponentTagsList);
        } else {

            disableByComponentTag = null;
        }
    }

    /**
     * Creates a new instance of the <code>LootTableAttributes.LootSchema</code>
     * subclass.
     * 
     * @param json            <code>TracedDictionary</code>: The JSON to create the
     *                        new <code>LootTableAttributes.LootSchema</code>
     *                        instance from.
     * @param componentIdList <code>List&lt;String&gt;</code>: The list of component
     *                        IDs contained in this
     *                        <code>LootTableAttributes.LootSchema</code> instance.
     * @return <code>LootSchema</code>: The created loot schema.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new <code>LootTableAttributes.Loot</code> instance.
     */
    public final LootSchema createLoot(TracedDictionary json, List<String> componentIdList)
            throws MissingPropertyException, PropertyTypeException, LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);

        return switch (typeEntry.getValue()) {

        case "item" -> new LootValueSchema(json, componentIdList);

        default -> new LootPoolSchema(json, SelectionType.parseSelectionType(json, "type"), componentIdList);
        };
    }

    /**
     * <code>LootTableAttributes.LootSchema</code>: A subclass representing any loot
     * schema.
     */
    public abstract class LootSchema {

        /**
         * <code>String</code>: The component ID of this
         * <code>LootTableAttributes.LootSchema</code> instance.
         */
        private final String componentId;

        /**
         * Retrieves the component ID of this
         * <code>LootTableAttributes.LootSchema</code> instance.
         * 
         * @return <code>String</code>: The <code>componentId</code> field of this
         *         <code>LootTableAttributes.LootSchema</code> instance.
         */
        public String getComponentId() {

            return componentId;
        }

        /**
         * <code>ImmutableList&lt;String&gt;</code>: The list of component tags of this
         * <code>LootTableAttributes.LootSchema</code> instance.
         */
        private final ImmutableList<String> componentTags;

        /**
         * Retrieves the list of component tags of this
         * <code>LootTableAttributes.LootSchema</code> instance.
         * 
         * @return <code>ImmutableList&lt;String&gt;</code>: The
         *         <code>componentTags</code> field of this
         *         <code>LootTableAttributes.LootSchema</code> instance.
         */
        public ImmutableList<String> getComponentTags() {

            return componentTags;
        }

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
         * Creates a new instance of the <code>LootTableAttributes.LootSchema</code>
         * subclass.
         * 
         * @param json            <code>TracedDictionary</code>: The JSON to create this
         *                        <code>LootTableAttributes.LootSchema</code> instance
         *                        from.
         * @param componentIdList <code>List&lt;String&gt;</code>: The list of component
         *                        IDs contained in this
         *                        <code>LootTableAttributes.LootSchema</code> instance.
         * @throws LoggedException Thrown if any exception is raised while creating this
         *                         <code>LootTableAttributes.LootSchema</code> instance.
         */
        public LootSchema(TracedDictionary json, List<String> componentIdList) throws LoggedException {

            ArrayList<TRScriptValue> conditionsList = new ArrayList<>();

            TracedEntry<String> componentIdEntry = json.getAsString("componentId", true, null);
            if (componentIdEntry.containsValue()) {

                componentId = componentIdEntry.getValue();
                if (componentIdList.contains(componentId)) {

                    throw new UnexpectedValueException(componentIdEntry);
                }

                componentIdList.add(componentId);
            } else {

                componentId = null;
            }

            TracedEntry<TracedArray> componentTagsEntry = json.getAsArray("componentTags", true);
            if (componentTagsEntry.containsValue()) {

                TracedArray componentTagsJson = componentTagsEntry.getValue();
                ArrayList<String> componentTagsList = new ArrayList<>();

                for (int i : componentTagsJson) {

                    TracedEntry<String> tagEntry = componentTagsJson.getAsString(i, false, null);
                    String tag = tagEntry.getValue();

                    componentTagsList.add(tag);
                }

                componentTags = new ImmutableList<>(componentTagsList);
            } else {

                componentTags = new ImmutableList<>();
            }

            TracedEntry<TracedArray> conditionsEntry = json.getAsArray("conditions", true);
            if (conditionsEntry.containsValue()) {

                TracedArray conditionsJson = conditionsEntry.getValue();
                for (int i : conditionsJson) {

                    conditionsList.add(new TRScriptValue(conditionsJson, i));
                }
            }

            conditions = new ImmutableList<>(conditionsList);

            TracedEntry<Double> chanceEntry = json.getAsDouble("chance", true, 100.0, num -> num > 0);
            chance = chanceEntry.getValue();

            count = Range.createRange(json, "count", true, num -> num >= 1);
        }

        public abstract List<AssetPresets> getItems();
    }

    public final class LootValueSchema extends LootSchema {

        private final AssetPresets item;

        public AssetPresets getItem() {

            return item;
        }

        public LootValueSchema(TracedDictionary json, List<String> componentIdList) throws LoggedException {

            super(json, componentIdList);

            TracedEntry<AssetPresets> itemEntry = json.getAsPresets("item", false, ITEM);
            item = itemEntry.getValue();
            addAssetDependency(item);
        }

        @Override
        public List<AssetPresets> getItems() {

            return List.of(item);
        }
    }

    public final class LootPoolSchema extends LootSchema {

        private final SelectionType SelectionType;

        public SelectionType getSelectionType() {

            return SelectionType;
        }

        private final ImmutableMap<LootSchema, Integer> pools;

        public ImmutableMap<LootSchema, Integer> getPools() {

            return pools;
        }

        public LootPoolSchema(TracedDictionary json, SelectionType SelectionType, List<String> componentIdList)
                throws LoggedException {

            super(json, componentIdList);

            this.SelectionType = SelectionType;

            HashMap<LootSchema, Integer> poolsMap = new HashMap<>();

            TracedEntry<TracedArray> poolsEntry = json.getAsArray("components", false);
            TracedArray poolsJson = poolsEntry.getValue();

            for (int i : poolsJson) {

                TracedEntry<TracedDictionary> poolEntry = poolsJson.getAsDict(i, false);
                TracedDictionary poolJson = poolEntry.getValue();

                // Retrieve the maximum number of times a pool can be rolled in a single check.
                // A value of -1 means there is no limit to the number of times a pool can be
                // rolled.
                TracedEntry<Integer> poolLimitEntry = poolJson.getAsInteger("limit", true, -1, num -> num >= 1);
                int poolLimit = poolLimitEntry.getValue();

                poolsMap.put(createLoot(poolJson, componentIdList), poolLimit);
            }

            pools = new ImmutableMap<>(poolsMap);
        }

        @Override
        public List<AssetPresets> getItems() {

            return pools.keySet().stream().flatMap(pool -> pool.getItems().stream()).collect(Collectors.toList());
        }
    }
}
