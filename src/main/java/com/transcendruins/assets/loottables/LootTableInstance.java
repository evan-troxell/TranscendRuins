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

package com.transcendruins.assets.loottables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.modelassets.items.ItemContext;
import com.transcendruins.assets.modelassets.items.ItemInstance;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.utilities.selection.DiscreteRange;
import com.transcendruins.utilities.selection.SelectionType;
import com.transcendruins.world.World;

public final class LootTableInstance extends AssetInstance {

    private LootInstance loot;

    private ImmutableList<AssetPresets> items;

    public final List<ItemInstance> generate(GlobalLocationInstance location) {

        return loot.generate(location, getRandom());
    }

    private final ArrayList<String> disableByComponentId = new ArrayList<>();

    private final ArrayList<String> disableByComponentTag = new ArrayList<>();

    public LootTableInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        LootTableContext context = (LootTableContext) assetContext;
    }

    @Override
    public final void applyAttributes(Attributes attributeSet) {

        LootTableAttributes attributes = (LootTableAttributes) attributeSet;

        // Updates the loot field.
        loot = calculateAttribute(attributes.getLoot(), val -> createLoot(val), loot);

        items = calculateAttribute(attributes.getItems(), items);
        setProperty("items", items);

        attributes.getDisableByComponentId();
        attributes.getDisableByComponentTag();

        if (attributes.isBase()) {

            disableByComponentId.clear();
            disableByComponentTag.clear();
        }

        computeAttribute(attributes.getDisableByComponentId(), disableByComponentId::addAll);
        computeAttribute(attributes.getEnableByComponentId(), disableByComponentId::removeAll);
        setProperty("disableByComponentId", disableByComponentId);

        computeAttribute(attributes.getDisableByComponentTag(), disableByComponentTag::addAll);
        computeAttribute(attributes.getEnableByComponentTag(), disableByComponentTag::removeAll);
        setProperty("disableByComponentTag", disableByComponentTag);
    }

    @Override
    protected final void onUpdate(double time) {

    }

    /**
     * Creates a new instance of the <code>LootInstance</code> class.
     * 
     * @param schema <code>LootSchema</code>: The schema to create the new
     *               <code>LootInstance</code> instance from.
     * @return <code>LootInstance</code>: The resulting loot instance.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new loot instance.
     */
    public final LootInstance createLoot(LootTableAttributes.LootSchema schema) {

        return switch (schema) {

        case LootTableAttributes.LootValueSchema value -> new LootValueInstance(value);

        case LootTableAttributes.LootPoolSchema pool -> new LootPoolInstance(pool);

        default -> null;
        };
    }

    /**
     * <code>LootInstance</code>: A class representing any loot type.
     */
    public abstract class LootInstance {

        /**
         * <code>String</code>: The component id of this <code>LootInstance</code>
         * instance.
         */
        private final String componentId;

        /**
         * Retrieves the component tags of this <code>LootInstance</code> instance.
         * 
         * @return <code>String</code>: The <code>componentId</code> field of this
         *         <code>LootInstance</code> instance.
         */
        private final ImmutableList<String> componentTags;

        /**
         * <code>ImmutableList&lt;TRScript&gt;</code>: The conditions required to be met
         * to apply this <code>LootInstance</code> instance.
         */
        private final ImmutableList<TRScript> conditions;

        /**
         * Determines whether or not this <code>LootInstance</code> passes the applied
         * conditions.
         * 
         * @return <code>boolean</code>: Whether or not this loot passes its conditions.
         */
        public final boolean passes() {

            for (TRScript script : conditions) {

                if (!script.evaluateBoolean(LootTableInstance.this)) {

                    return false;
                }
            }

            // Pass if the component id is not disabled and there are not any tags shared
            // between the component and the disabled tags list.
            return !disableByComponentId.contains(componentId)
                    && Collections.disjoint(disableByComponentTag, componentTags);
        }

        /**
         * <code>double</code>: The chance value of this <code>LootInstance</code>
         * instance, represented as a percentage greater than 0.
         */
        private final double chance;

        /**
         * Retrieves the chance value of this <code>LootInstance</code> instance.
         * 
         * @return <code>double</code>: The <code>chance</code> field of this
         *         <code>LootInstance</code> instance.
         */
        public final double getChance() {

            return chance;
        }

        /**
         * <code>DiscreteRange</code>: The count range of this <code>LootInstance</code>
         * instance, which is used to determine how many items to generate.
         */
        private final DiscreteRange count;

        /**
         * Retrieves the count range of this <code>LootInstance</code> instance.
         * 
         * @param random <code>DeterministicRandom</code>: The random number generator
         *               used to calculate the item count.
         * @return <code>Range</code>: The <code>count</code> field of this
         *         <code>LootInstance</code> instance.
         */
        public final int getCount(DeterministicRandom random) {

            return count.get(random.next());
        }

        /**
         * Creates a new instance of the <code>LootInstance</code> class.
         * 
         * @param schema <code>LootSchema</code>: The schema to create this
         *               <code>LootInstance</code> instance from.
         */
        public LootInstance(LootTableAttributes.LootSchema schema) {

            conditions = schema.getConditions();
            chance = schema.getChance();
            count = schema.getCount();

            componentId = schema.getComponentId();
            componentTags = schema.getComponentTags();
        }

        /**
         * Evaluates this <code>LootInstance</code> instance into a set of item
         * contexts.
         * 
         * @param world  <code>World</code>: The world used to generate item presets.
         * @param random <code>DeterministicRandom</code>: The random number generater
         *               used to generate item presets.
         * @return <code>ArrayList&lt;ItemInstance&gt;</code>: The generated item
         *         contexts.
         */
        public abstract ArrayList<ItemInstance> generate(GlobalLocationInstance location, DeterministicRandom random);
    }

    public final class LootValueInstance extends LootInstance {

        private final AssetPresets presets;

        public LootValueInstance(LootTableAttributes.LootValueSchema schema) {

            super(schema);

            presets = schema.getItem();
        }

        @Override
        public final ArrayList<ItemInstance> generate(GlobalLocationInstance location, DeterministicRandom random) {

            ItemContext itemContext = new ItemContext(presets, getWorld(), getCount(random));
            ItemInstance item = itemContext.instantiate();

            return new ArrayList<>(List.of(item));
        }
    }

    public final class LootPoolInstance extends LootInstance {

        private final ImmutableMap<LootInstance, DiscreteRange> pools;

        private final SelectionType iterationType;

        public LootPoolInstance(LootTableAttributes.LootPoolSchema schema) {

            super(schema);

            pools = new ImmutableMap<>(
                    schema.getPools().entrySet().stream().collect(Collectors.toMap(entry -> createLoot(entry.getKey()),
                            entry -> entry.getValue(), (previous, _) -> previous, HashMap::new)));

            iterationType = schema.getIterationType();
        }

        @Override
        public final ArrayList<ItemInstance> generate(GlobalLocationInstance location, DeterministicRandom random) {

            ArrayList<ItemInstance> items = new ArrayList<>();

            int rolled = getCount(random);
            iterationType.generate(pools.keySet(), rolled, loot -> {

                items.addAll(loot.generate(location, random));
                return new int[] { 1, 0 };
            }, LootInstance::getChance, loot -> pools.get(loot).get(random.next()), random, 1.0, -1);

            return items;
        }
    }

    @Override
    public final LootTableInstance clone(Function<AssetPresets, ? extends AssetContext> contextualize, World world) {

        LootTableInstance asset = (LootTableInstance) super.clone(contextualize, world);

        asset.loot = loot;
        asset.items = items;

        return asset;
    }
}
