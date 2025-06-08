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

package com.transcendruins.assets.primaryassets.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.Instance;
import com.transcendruins.assets.items.ItemInstance;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.world.World;

/**
 * <code>InventoryInstance</code>: A class representing an instatiated
 * inventory.
 */
public final class InventoryInstance extends Instance {

    private int gridSlots;

    /**
     * <code>InventorySet[]</code>: The grid of indexed inventory slots in this
     * <code>InventoryInstance</code> instance.
     */
    private InventorySlotInstance[] grid = new InventorySlotInstance[0];

    /**
     * Retrieves an item from the grid slots of this <code>InventoryInstance</code>
     * instance.
     * 
     * @param index <code>int</code>: The index of the slot whose item will be
     *              retrieved.
     * @return <code>ItemInstance</code>: The retrieved item, or <code>null</code>
     *         if the item or slot could not be found.
     */
    public ItemInstance getItem(int index) {

        return (grid.length > index) ? grid[index].getItem() : null;
    }

    /**
     * Resizes the grid slots of this <code>InventoryInstance</code> instance to a
     * set size.
     * 
     * @param size <code> The grid size to resize to.
     * 
     * @return <code>boolean</code>: Whether or not the <code>grid</code> field of
     *             this <code>InventoryInstance</code> instance could be resized.
     */
    public boolean resize(int size) {

        gridSlots = size;

        // If the new size is the same as the old size, ignore.
        if (grid.length == size) {

            return true;
        }

        // If the new size is greater than the old size or the removed slots are empty,
        // make the resize.
        if (size > grid.length || isEmpty(size)) {

            grid = copyContents(size);
            return true;
        }

        return false;
    }

    /**
     * Determines whether or not the inventory grid of this
     * <code>InventoryInstance</code> is empty starting at a certain index.
     * 
     * @param start <code>int</code>: The index to begin checking at.
     * @return <code>boolean</code>: Whether or not the inventory grid is empty past
     *         the set index.
     */
    public boolean isEmpty(int start) {

        for (int i = start; i < grid.length; i++) {

            if (grid[i].containsItem()) {

                return false;
            }
        }

        return true;
    }

    public ArrayList<InventorySlotInstance> getEmptyGridSlots() {

        ArrayList<InventorySlotInstance> empty = new ArrayList<>();

        for (InventorySlotInstance slot : grid) {

            if (slot.isEmpty()) {

                empty.add(slot);
            }
        }

        return empty;
    }

    /**
     * Copies the contents of the inventory grid of this
     * <code>InventoryInstance</code> instance to another grid of set size.
     * 
     * @param gridSlots <code>int</code>: The grid size to copy.
     * @return <code>InventorySlot[]</code>: The resized grid.
     */
    public InventorySlotInstance[] copyContents(int gridSlots) {

        InventorySlotInstance[] newGrid = new InventorySlotInstance[gridSlots];

        for (int i = 0; i < gridSlots; i++) {

            newGrid[i] = (i < grid.length) ? grid[i] : new InventorySlotInstance(this, i);
        }

        return newGrid;
    }

    private ImmutableSet<String> namedSet;

    /**
     * <code>HashMap&lt;String, InventorySlot&gt;</code>: The map of named inventory
     * slots in this <code>InventoryInstance</code> instance.
     */
    private final HashMap<String, InventorySlotInstance> named = new HashMap<>();

    /**
     * Retrieves an item from the named slots of this <code>InventoryInstance</code>
     * instance.
     * 
     * @param slot <code>String</code>: The name of the slot whose item will be
     *             retrieved.
     * @return <code>ItemInstance</code>: The retrieved item, or <code>null</code>
     *         if the item or slot could not be found.
     */
    public ItemInstance getItem(String slot) {

        return (named.containsKey(slot)) ? named.get(slot).getItem() : null;
    }

    /**
     * Resizes the named slots of this <code>InventoryInstance</code> instance to a
     * set size.
     * 
     * @param slots <code>Set&lt;String&gt;</code>: The slots to resize to.
     * 
     * @return <code>boolean</code>: Whether or not the <code>slots</code> field of
     *         this <code>InventoryInstance</code> instance could be resized.
     */
    public boolean resize(Set<String> slots) {

        namedSet = new ImmutableSet<>(slots);
        Set<String> oldSlots = named.keySet();

        // If the old slots are the same as the new slots, ignore.
        if (oldSlots.equals(slots)) {

            return true;
        }

        // Copy the positive slots (slots which are contained in the new slots but not
        // in the old).
        Set<String> positiveSlots = new HashSet<>(slots);
        positiveSlots.removeAll(oldSlots);

        positiveSlots.forEach(slot -> named.put(slot, new InventorySlotInstance(this, slot)));

        // Copy the negative slots (slots which are contained in the old slots but not
        // in the new slots).
        Set<String> negativeSlots = new HashSet<>(oldSlots);
        negativeSlots.removeAll(slots);

        // If there are no slots or only empty slots contained in the old slots which
        // are not contained in the new ones, completely copy the old slots.
        if (negativeSlots.isEmpty() || isEmpty(negativeSlots)) {

            named.keySet().removeAll(slots);
            return true;
        }

        // If some slots are not empty, iterate through and remove only the empty ones.
        for (String removeSlot : negativeSlots) {

            if (named.get(removeSlot).isEmpty()) {

                named.remove(removeSlot);
            }
        }

        return false;
    }

    /**
     * Determines whether or not the named slot set of this
     * <code>InventoryInstance</code> is empty.
     * 
     * @param checks <code>Set&lt;String&gt;</code>: The inventory slots to check
     * @return <code>boolean</code>: Whether or not the inventory slots are empty.
     */
    public boolean isEmpty(Set<String> checks) {

        for (String slot : checks) {

            if (named.containsKey(slot) && named.get(slot).containsItem()) {

                return false;
            }
        }

        return true;
    }

    /**
     * Copies the contents of the named slots of this <code>InventoryInstance</code>
     * instance to another slot set of specified contents.
     * 
     * @param slotNames <code>Set&lt;String&gt;</code>: The slot set to resize to.
     * @return <code>HashMap&lt;String, InventorySlot&gt;</code>: The resized slot
     *         set.
     */
    public HashMap<String, InventorySlotInstance> copyContents(Set<String> slotNames) {

        HashMap<String, InventorySlotInstance> newSlots = new HashMap<>();

        for (String slotName : slotNames) {

            newSlots.put(slotName, named.getOrDefault(slotName, new InventorySlotInstance(this, slotName)));
        }

        return newSlots;
    }

    public HashSet<InventorySlotInstance> getEmptyNamedSlots() {

        HashSet<InventorySlotInstance> empty = new HashSet<>();

        for (InventorySlotInstance slot : named.values()) {

            if (slot.isEmpty()) {

                empty.add(slot);
            }
        }

        return empty;
    }

    /**
     * Retrieves the map of items of this <code>InventoryInstance</code> instance.
     * 
     * @return <code>HashMap&lt;String, ItemInstance&gt;</code>: The map of
     *         inventory slots to their items.
     */
    public HashMap<String, ItemInstance> getItems() {

        HashMap<String, ItemInstance> items = new HashMap<>();

        for (Map.Entry<String, InventorySlotInstance> slotEntry : named.entrySet()) {

            items.put(slotEntry.getKey(), slotEntry.getValue().getItem());
        }

        for (int i = 0; i < grid.length; i++) {

            items.put(String.valueOf(i), grid[i].getItem());
        }

        return items;
    }

    /**
     * Adds a list of items to this <code>InventoryInstance</code> instance.
     * 
     * @param items     <code>List&lt;ItemInstance&gt;</code>: The items to fill
     *                  this <code>InventoryInstance</code> instance with.
     * @param randomize <code>boolean</code>: Whether or not the order of insertion
     *                  should be randomized.
     * @param world     <code>World</code>: The world copy to assist filling using.
     * @return <code>List&lt;ItemInstance&gt;</code>: The leftover items which could
     *         not be added.
     */
    public List<ItemInstance> fill(List<ItemInstance> items, boolean randomize, World world) {

        // Retrieve the list of all available slots and randomize if necessary
        ArrayList<InventorySlotInstance> availableSlots = new ArrayList<>(List.of(grid));
        if (randomize) {

            world.shuffle(availableSlots);
        }

        ListIterator<ItemInstance> itemIt = items.listIterator();

        while (itemIt.hasNext() && !availableSlots.isEmpty()) {

            ItemInstance item = itemIt.next();

            Iterator<InventorySlotInstance> slotIt = availableSlots.iterator();
            while (slotIt.hasNext()) {

                InventorySlotInstance slot = slotIt.next();

                if (!slot.isEmpty()) {

                    // If the slot is full, remove it and skip to the next slot.
                    if (slot.getItem().atCapacity()) {

                        slotIt.remove();
                        continue;
                    }

                    // If the item cannot be added because it is not the same as the existing item,
                    // skip to the next slot.
                    if (!slot.getItem().isLikeAsset(item)) {

                        continue;
                    }
                } else {

                    // If the item cannot be added because of its type, skip to the next slot.
                    if (!slot.isAcceptedType(item)) {

                        continue;
                    }
                }

                // If the item has passed criteria thus far, remove it from the list of unadded
                // and put the item in the slot.
                itemIt.remove();

                item = slot.putItem(item);

                // If there is any remainder, add it to the item iterator.
                if (item != null) {

                    itemIt.add(item);
                }

                break;
            }
        }

        return items;
    }

    public void setMarkedForRemoval(int gridSlot, boolean marked) {

        if (gridSlot >= grid.length) {

            return;
        }

        InventorySlotInstance slot = grid[gridSlot];
        slot.setMarkedForRemoval(marked);
    }

    public void setMarkedForRemoval(String namedSlot, boolean marked) {

        if (named.containsKey(namedSlot)) {

            InventorySlotInstance slot = named.get(namedSlot);
            slot.setMarkedForRemoval(marked);
        }
    }

    public void updateMarkedForRemoval() {

        resize(gridSlots);

        resize(namedSet);
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        InventorySchema attributes = (InventorySchema) attributeSet;

        gridSlots = calculateAttribute(attributes.getGridSlots(), grid.length, attributes, 0);
        resize(gridSlots);

        for (int i = 0; i < grid.length; i++) {

            InventorySlotInstance slot = grid[i];

            if (i < gridSlots) {

                // Apply the default grid schema if not defined.
                computeAttribute(attributes.getGrid(), gridSchema -> {

                    slot.applyAttributes(gridSchema);
                }, attributes, InventorySlotSchema.DEFAULT);
                setMarkedForRemoval(i, false);
            } else {

                // Reset to default and mark for removal if above the grid size.
                slot.applyAttributes(InventorySlotSchema.DEFAULT);
                setMarkedForRemoval(i, true);
            }
        }

        computeAttribute(attributes.getNamed(), namedSchemas -> {

            resize(namedSchemas.keySet());

            for (Map.Entry<String, InventorySlotInstance> namedEntry : named.entrySet()) {

                String slotName = namedEntry.getKey();
                InventorySlotInstance slot = namedEntry.getValue();

                // Reset to default and mark for removal if not defined.
                computeAttribute(namedSchemas.get(slotName), slotSchema -> {

                    slot.applyAttributes(slotSchema);
                    setMarkedForRemoval(slotName, slotSchema == InventorySlotSchema.DEFAULT);
                }, InventorySlotSchema.DEFAULT);
            }
        }, new ImmutableMap<String, InventorySlotSchema>());
    }
}