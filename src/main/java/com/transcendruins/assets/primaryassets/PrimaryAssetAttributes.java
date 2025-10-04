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

package com.transcendruins.assets.primaryassets;

import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema;
import com.transcendruins.assets.modelassets.ModelAssetAttributes;
import com.transcendruins.assets.primaryassets.inventory.InventorySchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>PrimaryAssetAttributes</code>: A class which represents the attributes
 * of an <code>AssetSchema</code> instance which has the capability of being
 * rendered using the standard <code>RenderInstance</code> method.
 */
public abstract class PrimaryAssetAttributes extends ModelAssetAttributes {

    /**
     * <code>InventorySchema</code>: The inventory schema of this
     * <code>PrimaryAssetAttributes</code> instance.
     */
    private final InventorySchema inventory;

    /**
     * Retrieves the inventory schema of this <code>PrimaryAssetAttributes</code>
     * instance.
     * 
     * @return <code>InventorySchema</code>: The <code>inventory</code> field of
     *         this <code>PrimaryAssetAttributes</code> instance.
     */
    public final InventorySchema getInventory() {

        return inventory;
    }

    private final InventoryComponentSchema inventoryUi;

    public final InventoryComponentSchema getInventoryUi() {

        return inventoryUi;
    }

    private final AssetInteraction interaction;

    public final AssetInteraction getInteraction() {

        return interaction;
    }

    /**
     * Compiles this <code>PrimaryAssetAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>PrimaryAssetAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>PrimaryAssetAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>PrimaryAssetAttributes</code> instance is the base
     *               attribute set of an <code>AssetSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while creating this
     *                         <code>PrimaryAssetAttributes</code> instance.
     */
    public PrimaryAssetAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        TracedEntry<TracedDictionary> inventoryEntry = json.getAsDict("inventory", true);
        if (inventoryEntry.containsValue()) {

            TracedDictionary inventoryJson = inventoryEntry.getValue();

            inventory = new InventorySchema(inventoryJson, isBase);

            TracedEntry<TracedDictionary> inventoryUiEntry = inventoryJson.getAsDict("ui", true);
            if (inventoryUiEntry.containsValue()) {

                TracedDictionary inventoryUiJson = inventoryUiEntry.getValue();
                inventoryUi = new InventoryComponentSchema(inventoryUiJson, this::addAssetDependency);
            } else {

                inventoryUi = null;
            }
        } else {

            inventory = null;
            inventoryUi = null;
        }

        TracedEntry<TracedDictionary> interactionEntry = json.getAsDict("interaction", true);
        if (interactionEntry.containsValue()) {

            TracedDictionary interactionJson = interactionEntry.getValue();
            interaction = AssetInteraction.createInteraction(interactionJson);
        } else {

            interaction = null;
        }
    }
}
