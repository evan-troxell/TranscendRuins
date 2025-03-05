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
import java.util.Map;
import java.util.stream.Collectors;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.extra.Range;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.assets.modelassets.items.ItemContext;
import com.transcendruins.assets.scripts.TRScriptValue;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.world.World;

public final class LootTableInstance extends AssetInstance {

    private LootInstance loot;

    private ImmutableList<AssetPresets> items;

    public List<ItemContext> evaluateLoot() {

        return loot.evaluate(getWorld());
    }

    public LootTableInstance(LootTableContext context) {

        super(context);
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        LootTableAttributes attributes = (LootTableAttributes) attributeSet;

        // Updates the loot field.
        loot = calculateAttribute(attributes.getLoot(), val -> createLoot(val), loot);

        items = calculateAttribute(attributes.getItems(), items);
        setProperty("items", items);
    }

    @Override
    protected void onUpdate(double time) {

    }

    /**
     * Creates a new instance of the <code>LootTableInstance.LootInstance</code>
     * subclass.
     * 
     * @param schema <code>LootTableAttributes.LootSchema</code>: The schema
     *               to create the new <code>LootTableInstance.LootInstance</code>
     *               instance from.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new <code>LootTableInstance.LootInstance</code>
     *                         instance.
     */
    public final LootInstance createLoot(LootTableAttributes.LootSchema schema) {

        return switch (schema) {

            case LootTableAttributes.LootValueSchema value -> new LootValueInstance(value);

            case LootTableAttributes.LootPoolSchema pool ->
                pool.getSelection() ? new LootSelectionInstance(pool) : new LootPoolInstance(pool);

            default -> null;
        };
    }

    /**
     * <code>LootTableInstance.LootInstance</code>: A subclass representing any loot
     * type.
     */
    public abstract class LootInstance {

        /**
         * <code>ImmutableList&lt;TRScriptValue&gt;</code>: The conditions required to
         * apply this <code>LootTableInstance.LootInstance</code> instance.
         */
        private final ImmutableList<TRScriptValue> conditions;

        /**
         * Determines whether or not this <code>LootTableInstance.LootInstance</code>
         * passes the applied conditions.
         * 
         * @return <code>boolean</code>: If this loot passes its conditions.
         */
        public final boolean passes() {

            for (TRScriptValue script : conditions) {

                if (!script.evaluateBoolean(LootTableInstance.this)) {

                    return false;
                }
            }

            return true;
        }

        /**
         * <code>double</code>: The chance value of this
         * <code>LootTableInstance.LootInstance</code> instance, represented as a
         * percentage greater than 0.
         */
        private final double chance;

        /**
         * Retrieves the chance value of this
         * <code>LootTableInstance.LootInstance</code> instance.
         * 
         * @return <code>double</code>: The <code>chance</code> field of this
         *         <code>LootTableInstance.LootInstance</code> instance.
         */
        protected double getChance() {

            return chance;
        }

        private final Range count;

        protected int getCount(World world) {

            return count.getIntegerValue(world.nextRandom());
        }

        /**
         * Creates a new instance of the <code>LootTableInstance.LootInstance</code>
         * subclass.
         * 
         * @param schema <code>LootTableAttributes.LootSchema</code>: The schema
         *               to
         *               create this <code>LootTableInstance.LootInstance</code>
         *               instance from.
         */
        public LootInstance(LootTableAttributes.LootSchema schema) {

            conditions = schema.getConditions();
            chance = schema.getChance();
            count = schema.getCount();
        }

        /**
         * Evaluates this <code>LootTableInstance.LootInstance</code> instance into a
         * set of item contexts.
         * 
         * @param world <code>World</code>: The world to use processing item preset(s).
         * @return <code>List&lt;ItemContext&gt;</code>: The generated item contexts.
         */
        public abstract List<ItemContext> evaluate(World world);
    }

    public final class LootValueInstance extends LootInstance {

        private final AssetPresets item;

        public LootValueInstance(LootTableAttributes.LootValueSchema schema) {

            super(schema);

            item = schema.getItem();
        }

        @Override
        public ArrayList<ItemContext> evaluate(World world) {

            ArrayList<ItemContext> items = new ArrayList<>();
            items.add(new ItemContext(item, world, getCount(world)));

            return items;
        }
    }

    public final class LootPoolInstance extends LootInstance {

        private final ImmutableMap<LootInstance, Integer> pools;

        public LootPoolInstance(LootTableAttributes.LootPoolSchema schema) {

            super(schema);

            this.pools = new ImmutableMap<>(schema.getPools().entrySet().stream().collect(Collectors.toMap(
                    entry -> createLoot(entry.getKey()),
                    entry -> entry.getValue(),
                    (previous, _) -> previous,
                    HashMap::new)));
        }

        @Override
        public ArrayList<ItemContext> evaluate(World world) {

            ArrayList<ItemContext> items = new ArrayList<>();

            HashMap<LootInstance, Integer> available = new HashMap<>(pools);
            int rolled = getCount(world);

            for (int i = 0; i < rolled; i++) {

                if (available.isEmpty()) {

                    break;
                }

                items.addAll(available.entrySet().stream() // Map through all available entries.
                        .map(Map.Entry::getKey) // Retrieve the loot.
                        .filter(loot -> loot.passes() && available.get(loot) != 0 // Ensure the loot passes input
                                                                                  // conditions.
                                && world.nextRandom() * 100 < loot.chance) // Randomly select the loot with its input
                                                                           // percentage.
                        .flatMap(loot -> { // Compute loot values, subtract 1 from the loot.

                            int remainder = available.computeIfPresent(loot, (_, val) -> val - 1);
                            if (remainder == 0) {

                                available.remove(loot);
                            }
                            return loot.evaluate(world).stream();
                        }).collect(Collectors.toList()));
            }

            return items;
        }
    }

    public final class LootSelectionInstance extends LootInstance {

        private final ImmutableMap<LootInstance, Integer> pools;

        public LootSelectionInstance(LootTableAttributes.LootPoolSchema schema) {

            super(schema);

            HashMap<LootInstance, Integer> poolsMap = new HashMap<>();

            for (Map.Entry<LootTableAttributes.LootSchema, Integer> lootEntry : schema.getPools().entrySet()) {

                poolsMap.put(createLoot(lootEntry.getKey()), lootEntry.getValue());
            }

            this.pools = new ImmutableMap<>(poolsMap);
        }

        @Override
        public ArrayList<ItemContext> evaluate(World world) {

            ArrayList<ItemContext> items = new ArrayList<>();

            HashMap<LootInstance, Integer> available = new HashMap<>(pools);

            int rolled = getCount(world);

            for (int i = 0; i < rolled; i++) {

                if (available.isEmpty()) {

                    break;
                }

                WeightedRoll<LootInstance> selector = new WeightedRoll<>(
                        available.keySet().stream().filter(loot -> loot.passes()), loot -> loot.chance);

                LootInstance loot = selector.get(world.nextRandom());

                int remainder = available.computeIfPresent(loot, (_, val) -> val - 1);
                if (remainder == 0) {

                    available.remove(loot);
                }
                items.addAll(loot.evaluate(world));
            }

            return items;
        }
    }
}
