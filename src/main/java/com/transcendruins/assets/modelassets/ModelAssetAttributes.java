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

package com.transcendruins.assets.modelassets;

import java.util.HashSet;

import static com.transcendruins.assets.AssetType.ANIMATION_CONTROLLER;
import static com.transcendruins.assets.AssetType.MODEL;
import static com.transcendruins.assets.AssetType.RENDER_MATERIAL;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.modelassets.inventory.InventorySchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>ModelAssetAttributes</code>: A class which represents the
 * attributes of an <code>AssetSchema</code> instance which has the capability
 * of being rendered using the standard <code>RenderInstance</code> method.
 */
public abstract class ModelAssetAttributes extends AssetAttributes {

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The asset category types of this
     * <code>ModelAssetAttributes</code> instance.
     */
    private final ImmutableList<String> categories;

    /**
     * Retrieves the asset category types of this
     * <code>ModelAssetAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The <code>categories</code>
     *         field of this <code>ModelAssetAttributes</code> instance.
     */
    public ImmutableList<String> getCategories() {

        return categories;
    }

    /**
     * <code>String</code>: The texture of this
     * <code>ModelAssetAttributes</code> instance.
     */
    private final String texture;

    /**
     * Retrieves the texture of this <code>ModelAssetAttributes</code>
     * instance.
     * 
     * @return <code>String</code>: The <code>texture</code> field of this
     *         <code>ModelAssetAttributes</code> instance.
     */
    public final String getTexture() {

        return texture;
    }

    /**
     * <code>AssetPresets</code>: The model presets of this
     * <code>ModelAssetAttributes</code> instance.
     */
    private final AssetPresets model;

    /**
     * Retrieves the model presets of this
     * <code>ModelAssetAttributes</code> instance.
     * 
     * @return <code>AssetPresets</code>: The <code>model</code> field of
     *         this <code>ModelAssetAttributes</code> instance.
     */
    public final AssetPresets getModel() {

        return model;
    }

    /**
     * <code>AssetPresets</code>: The render material presets of this
     * <code>ModelAssetAttributes</code> instance.
     */
    private final AssetPresets renderMaterial;

    /**
     * Retrieves the render material presets of this
     * <code>ModelAssetAttributes</code> instance.
     * 
     * @return <code>AssetPresets</code>: The <code>renderMaterial</code>
     *         field of this <code>ModelAssetAttributes</code> instance.
     */
    public final AssetPresets getRenderMaterial() {

        return renderMaterial;
    }

    /**
     * <code>AssetPresets</code>: The animation controller presets of
     * this <code>ModelAssetAttributes</code> instance.
     */
    private final AssetPresets animationController;

    /**
     * Retrieves the animation controller presets of this
     * <code>ModelAssetAttributes</code> instance.
     * 
     * @return <code>AssetPresets</code>: The
     *         <code>animationController</code> field of this
     *         <code>ModelAssetAttributes</code> instance.
     */
    public final AssetPresets getAnimationController() {

        return animationController;
    }

    /**
     * <code>InventorySchema</code>: The inventory schema of this
     * <code>ModelAssetAttributes</code> instance.
     */
    private final InventorySchema inventory;

    /**
     * Retrieves the inventory schema of this
     * <code>ModelAssetAttributes</code> instance.
     * 
     * @return <code>InventorySchema</code>: The <code>inventory</code> field of
     *         this <code>ModelAssetAttributes</code> instance.
     */
    public final InventorySchema getInventory() {

        return inventory;
    }

    /**
     * Compiles this <code>ModelAssetAttributes</code> instance into a
     * completed instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>ModelAssetAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to
     *               compile this <code>ModelAssetAttributes</code>
     *               instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>ModelAssetAttributes</code> instance is the
     *               base attribute set of an <code>AssetSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while creating this
     *                         <code>ModelAssetAttributes</code> instance.
     */
    public ModelAssetAttributes(AssetSchema schema, TracedDictionary json, boolean isBase)
            throws LoggedException {

        super(schema, json, isBase);

        HashSet<String> categoriesList = new HashSet<>();

        TracedEntry<TracedArray> categoriesEntry = json.getAsArray("categories", true);
        if (categoriesEntry.containsValue()) {

            TracedArray categoriesJson = categoriesEntry.getValue();
            if (categoriesJson.isEmpty()) {

                throw new CollectionSizeException(categoriesEntry, categoriesJson);
            }

            for (int i : categoriesJson.getIndices()) {

                categoriesList.add(categoriesJson.getAsString(i, false, null).getValue());
            }
        }

        categories = new ImmutableList<>(categoriesList);

        TracedEntry<String> textureEntry = json.getAsString("texture", !isBase, null);
        texture = textureEntry.getValue();

        model = MODEL.createPresets(json, "model", !isBase);
        if (model != null) {

            addAssetDependency(model);
        }

        renderMaterial = RENDER_MATERIAL.createPresets(json, "renderMaterial", !isBase);
        if (renderMaterial != null) {

            addAssetDependency(renderMaterial);
        }

        animationController = ANIMATION_CONTROLLER.createPresets(json, "animationController", true);
        if (animationController != null) {

            addAssetDependency(animationController);
        }

        TracedEntry<TracedDictionary> inventoryEntry = json.getAsDict("inventory", true);
        if (inventoryEntry.containsValue()) {

            inventory = new InventorySchema(inventoryEntry.getValue(), isBase);
        } else {

            inventory = null;
        }
    }
}
