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

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.modelassets.ModelAssetAttributes;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.assets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.assets.primaryassets.inventory.InventorySchema;
import com.transcendruins.utilities.immutable.ImmutableList;

/**
 * <code>PrimaryAsset</code>: A class representing an <code>AssetInstance</code>
 * instance which has the capability of being rendered using the standard
 * <code>RenderInstance</code> method.
 */
public abstract class PrimaryAssetInstance extends ModelAssetInstance {

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The asset category types of this
     * <code>PrimaryAssetInstance</code> instance.
     */
    private ImmutableList<String> categories;

    /**
     * Retrieves the asset category types of this <code>PrimaryAssetInstance</code>
     * instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The <code>categories</code>
     *         field of this <code>PrimaryAssetInstance</code> instance.
     */
    public final ImmutableList<String> getCategories() {

        return categories;
    }

    /**
     * <code>InventoryInstance</code>: The inventory of this
     * <code>PrimaryAssetInstance</code> instance.
     */
    private final InventoryInstance inventory = new InventoryInstance();

    /**
     * Retrieves the inventory of this <code>PrimaryAssetInstance</code> instance.
     * 
     * @return <code>InventoryInstance</code>: The <code>inventory</code> field of
     *         this <code>PrimaryAssetInstance</code> instance.
     */
    public InventoryInstance getInventory() {

        return inventory;
    }

    private InteractionType interactionType;

    private double interactionCooldown;

    private ImmutableList<String> interactionEvents;

    /**
     * Creates a new instance of the <code>PrimaryAssetInstance</code> class.
     * 
     * @param context <code>AssetContext</code>: The context used to generate this
     *                <code>PrimaryAssetInstance</code> instance.
     */
    public PrimaryAssetInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);
    }

    @Override
    public void applyModelAssetAttributes(ModelAssetAttributes attributeSet) {

        PrimaryAssetAttributes attributes = (PrimaryAssetAttributes) attributeSet;

        categories = calculateAttribute(attributes.getCategories(), categories, attributes, new ImmutableList<>());
        setProperty("categories", categories);

        // Updates the inventory field.
        computeAttribute(attributes.getInventory(), inventory::applyAttributes, attributes, InventorySchema.DEFAULT);

        // Update the interaction type.
        interactionType = calculateAttribute(attributes.getInteractionType(), interactionType, attributes,
                InteractionType.NONE);
        setProperty("interactionType", interactionType.toString());

        // Update the interaction cooldown time.
        interactionCooldown = calculateAttribute(attributes.getInteractionCooldown(), interactionCooldown, attributes,
                0.0);
        setProperty("interactionCooldown", interactionCooldown);

        // Update the interaction events.
        interactionEvents = calculateAttribute(attributes.getInteractionEvents(), interactionEvents, attributes,
                new ImmutableList<>());
        setProperty("interactionEvents", interactionEvents);

        applyPrimaryAssetAttributes(attributes);
    }

    /**
     * Applies an attribute set to this <code>PrimaryAssetInstance</code> instance.
     * 
     * @param attributeSet <code>ModelAssetAttributes</code>: The attributes to
     *                     apply.
     */
    public abstract void applyPrimaryAssetAttributes(PrimaryAssetAttributes attributeSet);

    @Override
    protected void onModelAssetUpdate(double time) {

        onPrimaryAssetUpdate(time);
    }

    /**
     * Performs the instance update of the subclass of this
     * <code>PrimaryAssetInstance</code> instance.
     * 
     * @param time <code>double</code>: The time since the world was created.
     */
    protected abstract void onPrimaryAssetUpdate(double time);

    public abstract int getTileWidth();

    public abstract int getTileLength();
}
