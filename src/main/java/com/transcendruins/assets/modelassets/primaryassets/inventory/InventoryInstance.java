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

package com.transcendruins.assets.modelassets.primaryassets.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.Instance;
import com.transcendruins.assets.modelassets.items.ItemInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.immutable.ImmutableSet;

/**
 * <code>InventoryInstance</code>: A class representing an instatiated
 * inventory.
 */
public final class InventoryInstance extends Instance {

    public static final String MAINHAND = "mainhand";

    private final PrimaryAssetInstance asset;

    public final PrimaryAssetInstance getInventoryParent() {

        return asset;
    }

    public InventoryInstance(PrimaryAssetInstance asset) {

        this.asset = asset;
    }

    public final int getItemCount(ItemInstance item) {

        int count = 0;

        for (InventorySlotInstance slot : grid) {

            ItemInstance slotItem = slot.getItem();
            if (slotItem.isLikeAsset(item)) {

                count += slotItem.getStackSize();
            }
        }

        for (InventorySlotInstance slot : named.values()) {

            ItemInstance slotItem = slot.getItem();
            if (slotItem.isLikeAsset(item)) {

                count += slotItem.getStackSize();
            }
        }

        return count;
    }

    public final int consume(ItemInstance item) {

        int count = item.getStackSize();
        for (InventorySlotInstance slot : grid) {

            ItemInstance slotItem = slot.getItem();
            if (slotItem.isLikeAsset(item)) {

                count = slot.remove(count);
                if (count == 0) {

                    return 0;
                }
            }
        }

        for (InventorySlotInstance slot : named.values()) {

            ItemInstance slotItem = slot.getItem();
            if (slotItem.isLikeAsset(item)) {

                count = slot.remove(count);
                if (count == 0) {

                    return 0;
                }
            }
        }

        return count;
    }

    private int gridSlots;

    /**
     * <code>InventorySet[]</code>: The grid of indexed inventory slots in this
     * <code>InventoryInstance</code> instance.
     */
    private InventorySlotInstance[] grid = new InventorySlotInstance[0];

    public final int getGridSize() {

        return grid.length;
    }

    /**
     * Retrieves an item from the grid slots of this <code>InventoryInstance</code>
     * instance.
     * 
     * @param index <code>int</code>: The index of the slot whose item will be
     *              retrieved.
     * @return <code>ItemInstance</code>: The retrieved item, or <code>null</code>
     *         if the item or slot could not be found.
     */
    public final ItemInstance getItem(int index) {

        return (0 <= index && index < grid.length) ? grid[index].getItem() : null;
    }

    public final InventorySlotInstance getSlot(int index) {

        if (grid.length > index) {

            return grid[index];
        }
        return null;
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
    public final boolean resize(int size) {

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
    public final boolean isEmpty(int start) {

        for (int i = start; i < grid.length; i++) {

            if (grid[i].containsItem()) {

                return false;
            }
        }

        return true;
    }

    public final ArrayList<InventorySlotInstance> getEmptyGridSlots() {

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
    public final InventorySlotInstance[] copyContents(int gridSlots) {

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

    public final HashSet<String> getNamedSlots() {

        return new HashSet<>(named.keySet());
    }

    /**
     * Retrieves an item from the named slots of this <code>InventoryInstance</code>
     * instance.
     * 
     * @param slot <code>String</code>: The name of the slot whose item will be
     *             retrieved.
     * @return <code>ItemInstance</code>: The retrieved item, or <code>null</code>
     *         if the item or slot could not be found.
     */
    public final ItemInstance getItem(String slot) {

        return (named.containsKey(slot)) ? named.get(slot).getItem() : null;
    }

    public final InventorySlotInstance getSlot(String slot) {

        return named.get(slot);
    }

    public final HashSet<ItemInstance> getItems() {

        HashSet<InventorySlotInstance> slots = new HashSet<>();
        slots.addAll(Arrays.asList(grid));
        slots.addAll(named.values());

        return new HashSet<>(slots.stream().map(InventorySlotInstance::getItem).filter(Objects::nonNull).toList());
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
    public final boolean resize(Set<String> slots) {

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

            named.keySet().removeAll(negativeSlots);
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
    public final boolean isEmpty(Set<String> checks) {

        for (String slot : checks) {

            if (named.containsKey(slot) && named.get(slot).containsItem()) {

                return false;
            }
        }

        return true;
    }

    /**
     * Adds a list of items to this <code>InventoryInstance</code> instance.
     * 
     * @param items     <code>List&lt;ItemInstance&gt;</code>: The items to fill
     *                  this <code>InventoryInstance</code> instance with.
     * @param randomize <code>boolean</code>: Whether or not the order of insertion
     *                  should be randomized.
     * @param asset     <code>PrimaryAssetInstance</code>: The asset used to
     *                  randomize cell layout.
     * @return <code>List&lt;ItemInstance&gt;</code>: The leftover items which could
     *         not be added.
     */
    public final List<ItemInstance> fill(List<ItemInstance> items, boolean randomize, PrimaryAssetInstance asset) {

        // Retrieve the list of all available slots and randomize if necessary.
        ArrayList<InventorySlotInstance> availableSlots = new ArrayList<>(List.of(grid));
        if (randomize) {

            asset.shuffle(availableSlots);
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

    public final void setMarkedForRemoval(int gridSlot, boolean marked) {

        if (gridSlot >= grid.length) {

            return;
        }

        InventorySlotInstance slot = grid[gridSlot];
        slot.setMarkedForRemoval(marked);
    }

    public final void setMarkedForRemoval(String namedSlot, boolean marked) {

        if (named.containsKey(namedSlot)) {

            InventorySlotInstance slot = named.get(namedSlot);
            slot.setMarkedForRemoval(marked);
        }
    }

    public final void updateMarkedForRemoval() {

        resize(gridSlots);

        resize(namedSet);
    }

    @Override
    public final void applyAttributes(Attributes attributeSet) {

        InventorySchema attributes = (InventorySchema) attributeSet;

        gridSlots = calculateAttribute(attributes.getGridSlots(), grid.length, attributes, 0);
        resize(gridSlots);

        InventorySlotSchema gridSchema = attributes.getGrid();
        for (int i = 0; i < grid.length; i++) {

            InventorySlotInstance slot = grid[i];

            if (i < gridSlots) {

                // Apply the default grid schema if not defined.
                computeAttribute(gridSchema, slot::applyAttributes, attributes, InventorySlotSchema.DEFAULT);
                setMarkedForRemoval(i, false);
            } else {

                // Reset to default and mark for removal if above the grid size.
                slot.applyAttributes(InventorySlotSchema.DEFAULT);
                setMarkedForRemoval(i, true);
            }
        }

        computeAttribute(attributes.getNamed(), namedSchemas -> {

            namedSet = new ImmutableSet<>(namedSchemas.keySet());
            resize(namedSet);

            for (Map.Entry<String, InventorySlotInstance> namedEntry : named.entrySet()) {

                String slotName = namedEntry.getKey();
                InventorySlotInstance slot = namedEntry.getValue();

                // Reset to default and mark for removal if not defined.
                computeAttribute(namedSchemas.get(slotName), slotSchema -> {

                    slot.applyAttributes(slotSchema);
                    setMarkedForRemoval(slotName, slotSchema == InventorySlotSchema.DEFAULT);
                }, InventorySlotSchema.DEFAULT);
            }
        }, attributes, new ImmutableMap<String, InventorySlotSchema>());
    }
}
