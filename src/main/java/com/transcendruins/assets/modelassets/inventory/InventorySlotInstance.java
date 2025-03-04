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

package com.transcendruins.assets.modelassets.inventory;

import java.util.Collections;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.Instance;
import com.transcendruins.assets.modelassets.items.ItemInstance;
import com.transcendruins.utilities.immutable.ImmutableList;

public final class InventorySlotInstance extends Instance {

    private final InventoryInstance parent;

    private final String name;

    public String getName() {

        return name;
    }

    private boolean markedForRemoval;

    public void setMarkedForRemoval(boolean markedForRemoval) {

        this.markedForRemoval = markedForRemoval;
    }

    public boolean getMarkedForRemoval() {

        return markedForRemoval;
    }

    private ItemInstance item;

    public boolean isEmpty() {

        return item == null;
    }

    private void setItem(ItemInstance item) {

        this.item = item;
    }

    public ItemInstance putItem(ItemInstance item) {

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

    public boolean containsItem() {

        return item != null;
    }

    public boolean containsItem(ItemInstance item) {

        return this.item.isLikeAsset(item);
    }

    public ItemInstance getItem() {

        return item;
    }

    private ImmutableList<String> acceptedTypes;

    public boolean isAcceptedType(ItemInstance item) {

        return acceptedTypes.contains("any") || !Collections.disjoint(acceptedTypes, item.getCategories());
    }

    private String modelSocket;

    public String getModelSocket() {

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
    public void applyAttributes(Attributes attributeSet) {

        InventorySlotSchema attributes = (InventorySlotSchema) attributeSet;

        acceptedTypes = calculateAttribute(attributes.getAcceptedTypes(), acceptedTypes, attributes,
                new ImmutableList<>("any"));
        modelSocket = calculateAttribute(attributes.getModelSocket(), modelSocket, attributes, null);
    }
}
