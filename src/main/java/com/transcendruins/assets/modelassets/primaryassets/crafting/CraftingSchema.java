package com.transcendruins.assets.modelassets.primaryassets.crafting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.transcendruins.assets.modelassets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventorySlotInstance;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class CraftingSchema {

    private final String category;

    public final String getCategory() {

        return category;
    }

    private final ImmutableList<SlotReference> input;

    public final ImmutableList<SlotReference> getInput() {

        return input;
    }

    private final SlotReference output;

    public final SlotReference getOutput() {

        return output;
    }

    private final SlotReference fuel;

    public final SlotReference getFuel() {

        return fuel;
    }

    @FunctionalInterface
    public interface SlotReference {

        public InventorySlotInstance apply(InventoryInstance inventory);
    }

    public static final SlotReference createSlotReference(TracedCollection collection, Object key,
            boolean nullCaseAllowed, HashSet<Integer> grid, HashSet<String> named) throws LoggedException {

        ArrayList<TracedCollection.TypeCase<?, SlotReference>> cases = new ArrayList<>();
        cases.add(collection.intCase(entry -> {

            int slot = entry.getValue();
            if (grid.contains(slot)) {

                throw new UnexpectedValueException(entry);
            }
            grid.add(slot);

            return inventory -> inventory.getSlot(slot);
        }, num -> num >= 0));

        cases.add(collection.stringCase(entry -> {

            String slot = entry.getValue();
            if (named.contains(slot)) {
                throw new UnexpectedValueException(entry);
            }
            named.add(slot);

            return inventory -> inventory.getSlot(slot);
        }));

        if (nullCaseAllowed) {

            cases.add(collection.nullCase(_ -> null));
        }

        return collection.get(key, cases);
    }

    public CraftingSchema(TracedDictionary json) throws LoggedException {

        TracedEntry<String> categoryEntry = json.getAsString("category", false, null);
        category = categoryEntry.getValue();

        HashSet<Integer> grid = new HashSet<>();
        HashSet<String> named = new HashSet<>();

        input = json.get("input", List.of(json.arrayCase(entry -> {

            TracedArray inputJson = entry.getValue();
            ArrayList<SlotReference> slots = new ArrayList<>();
            for (int i : inputJson) {

                slots.add(createSlotReference(inputJson, i, false, grid, named));
            }

            return new ImmutableList<>(slots);
        }), json.defaultCase(_ -> new ImmutableList<>(createSlotReference(json, "input", false, grid, named)))));

        output = createSlotReference(json, "output", false, grid, named);
        fuel = createSlotReference(json, "fuel", true, grid, named);
    }
}
