package com.transcendruins.assets.modelassets.primaryassets.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.transcendruins.assets.AssetType;
import static com.transcendruins.assets.AssetType.ITEM;
import static com.transcendruins.assets.AssetType.LOOT_TABLE;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.loottables.LootTableContext;
import com.transcendruins.assets.loottables.LootTableInstance;
import com.transcendruins.assets.modelassets.items.ItemContext;
import com.transcendruins.assets.modelassets.items.ItemInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.utilities.selection.DiscreteRange;
import com.transcendruins.utilities.selection.WeightedRoll;
import com.transcendruins.world.World;

public final class InventoryContents {

    private final ImmutableMap<String, NamedInventoryContents> named;

    private final GridInventoryContents grid;

    public InventoryContents(TracedDictionary json, PrimaryAssetAttributes parent) throws LoggedException {

        HashMap<String, NamedInventoryContents> namedMap = new HashMap<>();

        TracedEntry<TracedDictionary> namedEntry = json.getAsDict("named", true);
        if (namedEntry.containsValue()) {

            TracedDictionary namedJson = namedEntry.getValue();

            for (String key : namedJson) {

                TracedEntry<TracedDictionary> nameEntry = namedJson.getAsDict(key, false);
                TracedDictionary nameJson = nameEntry.getValue();

                namedMap.put(key, new NamedInventoryContents(nameJson, parent));
            }
        }

        named = new ImmutableMap<>(namedMap);

        TracedEntry<TracedDictionary> gridEntry = json.getAsDict("grid", true);
        if (gridEntry.containsValue()) {

            TracedDictionary gridJson = gridEntry.getValue();
            grid = new GridInventoryContents(gridJson, parent);
        } else {

            grid = null;
        }
    }

    private abstract class AssetContent {

        private final WeightedRoll<AssetPresets> asset;

        private final double chance;

        public AssetContent(TracedDictionary json, AssetType type, String typeKey, PrimaryAssetAttributes parent)
                throws LoggedException {

            asset = json.getAsRoll(typeKey, false, null, typeKey, json.presetsCase(entry -> {

                AssetPresets presets = entry.getValue();
                parent.addAssetDependency(presets);

                return presets;
            }, type));

            TracedEntry<Double> chanceEntry = json.getAsDouble("chance", true, 100.0, num -> 0 < num && num <= 100);
            chance = chanceEntry.getValue();
        }

        public final boolean passes(DeterministicRandom random) {

            return DeterministicRandom.toDouble(random.next()) * 100.0 < chance;
        }

        public final AssetPresets get(DeterministicRandom random) {

            return asset.get(random.next());
        }
    }

    private final class NamedInventoryContents extends AssetContent {

        private final DiscreteRange count;

        public NamedInventoryContents(TracedDictionary json, PrimaryAssetAttributes parent) throws LoggedException {

            super(json, ITEM, "item", parent);

            count = DiscreteRange.createRange(json, "count", true, 1, num -> num > 0);
        }
    }

    private final class GridInventoryContents extends AssetContent {

        private final boolean shuffle;

        public GridInventoryContents(TracedDictionary json, PrimaryAssetAttributes parent) throws LoggedException {

            super(json, LOOT_TABLE, "lootTable", parent);

            TracedEntry<Boolean> shuffleEntry = json.getAsBoolean("shuffle", true, true);
            shuffle = shuffleEntry.getValue();
        }
    }

    public final void generate(DeterministicRandom random, PrimaryAssetInstance asset) {

        World world = asset.getWorld();
        GlobalLocationInstance location = asset.getLocation();
        InventoryInstance inventory = asset.getInventory();

        for (Map.Entry<String, NamedInventoryContents> namedEntry : named.entrySet()) {

            InventorySlotInstance slot = inventory.getSlot(namedEntry.getKey());
            if (slot == null) {

                continue;
            }

            NamedInventoryContents inventoryContents = namedEntry.getValue();

            if (!inventoryContents.passes(random)) {

                continue;
            }

            AssetPresets itemPresets = inventoryContents.get(random);
            int count = inventoryContents.count.get(random.next());

            ItemContext itemContext = new ItemContext(itemPresets, world, count);
            ItemInstance item = itemContext.instantiate();
            slot.putItem(item);
        }

        if (grid != null && grid.passes(random)) {

            AssetPresets lootTablePresets = grid.get(random);
            LootTableContext lootTableContext = new LootTableContext(lootTablePresets, world, asset);
            LootTableInstance lootTable = lootTableContext.instantiate();

            List<ItemInstance> items = lootTable.generate(location);
            inventory.fill(items, grid.shuffle, random);
        }
    }
}
