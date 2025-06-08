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

import java.util.ArrayList;

import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.modelassets.ModelAssetAttributes;
import com.transcendruins.assets.primaryassets.inventory.InventorySchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.ReferenceWithoutDefinitionException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>PrimaryAssetAttributes</code>: A class which represents the attributes
 * of an <code>AssetSchema</code> instance which has the capability of being
 * rendered using the standard <code>RenderInstance</code> method.
 */
public abstract class PrimaryAssetAttributes extends ModelAssetAttributes {

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The asset category types of this
     * <code>PrimaryAssetAttributes</code> instance.
     */
    private final ImmutableList<String> categories;

    /**
     * Retrieves the asset category types of this
     * <code>PrimaryAssetAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The <code>categories</code>
     *         field of this <code>PrimaryAssetAttributes</code> instance.
     */
    public ImmutableList<String> getCategories() {

        return categories;
    }

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

    private final InteractionType interactionType;

    public final InteractionType getInteractionType() {

        return interactionType;
    }

    private final Double interactionCooldown;

    public final Double getInteractionCooldown() {

        return interactionCooldown;
    }

    private final ImmutableList<String> interactionEvents;

    public final ImmutableList<String> getInteractionEvents() {

        return interactionEvents;
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

        TracedEntry<TracedArray> categoriesEntry = json.getAsArray("categories", true);
        if (categoriesEntry.containsValue()) {

            ArrayList<String> categoriesList = new ArrayList<>();

            TracedArray categoriesJson = categoriesEntry.getValue();
            if (categoriesJson.isEmpty()) {

                throw new CollectionSizeException(categoriesEntry, categoriesJson);
            }

            for (int i : categoriesJson) {

                categoriesList.add(categoriesJson.getAsString(i, false, null).getValue());
            }

            categories = new ImmutableList<>(categoriesList);
        } else {

            categories = null;
        }

        TracedEntry<TracedDictionary> inventoryEntry = json.getAsDict("inventory", true);
        if (inventoryEntry.containsValue()) {

            inventory = new InventorySchema(inventoryEntry.getValue(), isBase);
        } else {

            inventory = null;
        }

        TracedEntry<TracedDictionary> interactionEntry = json.getAsDict("interaction", true);
        if (interactionEntry.containsValue()) {

            TracedDictionary interactionJson = interactionEntry.getValue();

            interactionType = InteractionType.parseInteractionType(interactionJson, "type");

            TracedEntry<Double> interactionCooldownEntry = json.getAsDouble("interactionCooldown", true, null);
            interactionCooldown = interactionCooldownEntry.getValue();

            ArrayList<String> interactionEventsList = new ArrayList<>();

            TracedEntry<TracedArray> interactionEventsEntry = json.getAsArray("events", true);
            if (interactionEventsEntry.containsValue()) {

                TracedArray interactionEventsJson = interactionEventsEntry.getValue();
                for (int i : interactionEventsJson) {

                    TracedEntry<String> interactionEventEntry = interactionEventsJson.getAsString(i, false, null);
                    String interactionEvent = interactionEventEntry.getValue();

                    if (!schema.containsEvent(interactionEvent)) {

                        throw new ReferenceWithoutDefinitionException(interactionEventEntry, "Event");
                    }

                    interactionEventsList.add(interactionEvent);
                }
            }

            interactionEvents = new ImmutableList<>(interactionEventsList);
        } else {

            interactionType = null;
            interactionCooldown = null;
            interactionEvents = null;
        }
    }
}
