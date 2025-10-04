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

import java.util.Collections;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.Instance;
import com.transcendruins.assets.items.ItemInstance;
import com.transcendruins.utilities.immutable.ImmutableList;

public final class InventorySlotInstance extends Instance {

    private final InventoryInstance parent;

    private final String name;

    public final String getName() {

        return name;
    }

    private boolean markedForRemoval;

    public final void setMarkedForRemoval(boolean markedForRemoval) {

        this.markedForRemoval = markedForRemoval;
    }

    public final boolean getMarkedForRemoval() {

        return markedForRemoval;
    }

    private ItemInstance item;

    public final boolean isEmpty() {

        return item == null;
    }

    private void setItem(ItemInstance item) {

        this.item = item;
    }

    public final void swap(InventorySlotInstance other) {

        ItemInstance newItem = other.putItem(getItem());
        setItem(newItem);
    }

    public final ItemInstance putItem(ItemInstance item) {

        if (!isAcceptedType(item)) {

            return item;
        }

        // If the slot is marked for removal and an item is being put in the slot, don't
        // allow the transfer.
        if (getMarkedForRemoval()) {

            if (item == null) {

                return item;
            }

            // If an item is being remove from the slot, alert the parent.
            item = getItem();
            setItem(null);

            parent.updateMarkedForRemoval();
            return item;
        }

        if (isEmpty()) {

            setItem(item);
            item = null;
        } else {

            if (this.item.isLikeAsset(item)) {

                item = this.item.combine(item);
            } else {

                ItemInstance temp = getItem();
                setItem(item);
                item = temp;
            }
        }

        return item;
    }

    public final int remove(int count) {

        if (item == null) {

            return count;
        }

        int quantity = item.getStackSize();
        if (quantity > count) {

            item.setStackSize(quantity - count);
            return 0;
        }

        item = null;
        return count - quantity;
    }

    public final boolean containsItem() {

        return item != null;
    }

    public final boolean containsItem(ItemInstance item) {

        return this.item.isLikeAsset(item);
    }

    public final ItemInstance getItem() {

        return item;
    }

    private ImmutableList<String> acceptedTypes;

    public final boolean isAcceptedType(ItemInstance item) {

        return acceptedTypes == null || !Collections.disjoint(acceptedTypes, item.getCategories());
    }

    private String modelSocket;

    public final String getModelSocket() {

        return modelSocket;
    }

    public InventorySlotInstance(InventoryInstance parent, int index) {

        this.parent = parent;
        name = String.valueOf(index);
    }

    public InventorySlotInstance(InventoryInstance parent, String name) {

        this.parent = parent;
        this.name = name;
    }

    @Override
    public final void applyAttributes(Attributes attributeSet) {

        InventorySlotSchema attributes = (InventorySlotSchema) attributeSet;

        acceptedTypes = calculateAttribute(attributes.getAcceptedTypes(), acceptedTypes, attributes, null);
        modelSocket = calculateAttribute(attributes.getModelSocket(), modelSocket, attributes, null);
    }
}
