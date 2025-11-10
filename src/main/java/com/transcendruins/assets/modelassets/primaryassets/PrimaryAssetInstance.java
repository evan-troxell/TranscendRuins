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

package com.transcendruins.assets.modelassets.primaryassets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.transcendruins.assets.animations.boneactors.BoneActor;
import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema;
import com.transcendruins.assets.modelassets.ModelAssetAttributes;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.assets.modelassets.items.ItemInstance;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventorySchema;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventorySlotInstance;
import com.transcendruins.assets.models.ModelAttributes.Bone;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.geometry.Matrix;
import com.transcendruins.geometry.Quaternion;
import com.transcendruins.geometry.Vector;
import com.transcendruins.rendering.renderBuffer.RenderBuffer;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.world.AreaGrid;

/**
 * <code>PrimaryAsset</code>: A class representing an <code>AssetInstance</code>
 * instance which has the capability of being rendered using the standard
 * <code>RenderInstance</code> method.
 */
public abstract class PrimaryAssetInstance extends ModelAssetInstance {

    /**
     * <code>InventoryInstance</code>: The inventory of this
     * <code>PrimaryAssetInstance</code> instance.
     */
    private final InventoryInstance inventory = new InventoryInstance(this);

    /**
     * Retrieves the inventory of this <code>PrimaryAssetInstance</code> instance.
     * 
     * @return <code>InventoryInstance</code>: The <code>inventory</code> field of
     *         this <code>PrimaryAssetInstance</code> instance.
     */
    public final InventoryInstance getInventory() {

        return inventory;
    }

    private InventoryComponentSchema inventoryUi;

    public final InventoryComponentSchema getInventoryUi() {

        return inventoryUi;
    }

    private InventoryComponentSchema privateInventoryUi;

    public final InventoryComponentSchema getPrivateInventoryUi() {

        return privateInventoryUi;
    }

    private ImmutableList<AssetInteractionInstance> interaction;

    private final HashMap<AssetAttributes, ImmutableList<AssetInteractionInstance>> interactionCache = new HashMap<>();

    public final ImmutableList<AssetInteractionInstance> getInteraction() {

        return interaction;
    }

    private boolean hasTileUpdate;

    public final boolean hasTileUpdate() {

        return hasTileUpdate;
    }

    private final HashSet<PrimaryAssetInstance> primaryModelChildren = new HashSet<>();

    private final HashMap<String, HashSet<PrimaryAssetInstance>> primaryModelConnections = new HashMap<>();

    public final boolean containsPrimaryAssetChild(String connection) {

        if (!primaryModelConnections.containsKey(connection)) {

            return false;
        }

        return !primaryModelConnections.get(connection).isEmpty();
    }

    protected final void queueAreaUpdate() {

        hasTileUpdate = true;
        if (!primaryModelChildren.isEmpty()) {

            primaryModelChildren.forEach(PrimaryAssetInstance::queueAreaUpdate);
        }
    }

    protected final void removeAreaUpdate() {

        hasTileUpdate = false;
    }

    public abstract void updateArea(AreaGrid area);

    public abstract void setPosition(int tileX, int tileZ);

    public abstract void translate(int dx, int dz);

    public abstract void rotate(int direction, int areaWidth, int areaLength);

    public final Rectangle getTileBounds() {

        if (hasModelParent()) {

            return getModelParent().getTileBounds();
        }

        return getInternalTileBounds();
    }

    protected abstract Rectangle getInternalTileBounds();

    public final Rectangle getTileBoundsTranslated(int dx, int dz) {

        if (hasModelParent()) {

            return getModelParent().getTileBoundsTranslated(dx, dz);
        }

        return getInternalTileBoundsAt(dx, dz);
    }

    protected abstract Rectangle getInternalTileBoundsTranslated(int dx, int dz);

    public final Rectangle getTileBoundsAt(int tileX, int tileZ) {

        if (hasModelParent()) {

            return getModelParent().getTileBoundsAt(tileX, tileZ);
        }

        return getInternalTileBoundsAt(tileX, tileZ);
    }

    protected abstract Rectangle getInternalTileBoundsAt(int tileX, int tileZ);

    private String modelParentAttachment;

    protected final void setModelParent(PrimaryAssetInstance parent, String modelParentAttachment) {

        setModelParent(parent);
        this.modelParentAttachment = modelParentAttachment;

        queueAreaUpdate();
    }

    @Override
    public final void removeModelParent() {

        setModelParent(null);
        modelParentAttachment = null;

        queueAreaUpdate();
    }

    @Override
    public final String getModelParentAttachment() {

        return modelParentAttachment;
    }

    /**
     * <code>HashSet&lt;ModelAssetInstance</code>: The children models of this
     * <code>PrimaryAssetInstance</code> instance.
     */
    private final HashSet<ModelAssetInstance> modelChildren = new HashSet<>();

    /**
     * Adds a child model to this <code>PrimaryAssetInstance</code> instance. The
     * child model will be attached to the bone with the specified name.
     * 
     * @param modelChild <code>PrimaryAssetInstance</code>: The child model to add
     *                   to this <code>PrimaryAssetInstance</code> instance.
     * @param connection <code>String</code>: The name of the bone to which the
     *                   child model will be attached.
     * @return <code>boolean</code>: Whether or not the child was successfully
     *         added.
     */
    protected final boolean addModelChild(PrimaryAssetInstance modelChild, String connection) {

        if (!containsBone(connection)) {

            return false;
        }

        if (this != modelChild.getModelParent()) {

            modelChild.removeModelParent();
        } else {

            String prevConnection = modelChild.getModelParentAttachment();
            primaryModelConnections.get(prevConnection).remove(modelChild);
        }

        modelChild.setModelParent(this, connection);
        modelChildren.add(modelChild);

        primaryModelChildren.add(modelChild);
        primaryModelConnections.computeIfAbsent(connection, _ -> new HashSet<>()).add(modelChild);

        return true;
    }

    /**
     * Adds a child model to this <code>PrimaryAssetInstance</code> instance.
     * 
     * @param modelChild <code>ItemInstance</code>: The child model to add to this
     *                   <code>PrimaryAssetInstance</code> instance.
     * @param slot       <code>InventorySlotInstance</code>: The slot to which the
     *                   child model will be attached.
     * @return <code>boolean</code>: Whether or not the child was successfully
     *         added.
     */
    public final boolean addModelChild(ItemInstance modelChild, InventorySlotInstance slot) {

        if (this != modelChild.getModelParent()) {

            modelChild.removeModelParent();
        }

        modelChild.setModelParent(this, slot);
        modelChildren.add(modelChild);
        return true;
    }

    /**
     * Removes a child model from this <code>PrimaryAssetInstance</code> instance.
     * 
     * @param modelChild <code>ModelAssetInstance</code>: The child model to remove
     *                   from this <code>PrimaryAssetInstance</code> instance.
     */
    public final void removeModelChild(ModelAssetInstance modelChild) {

        // If the child is a child of this model, remove it.
        if (modelChild != null && modelChildren.remove(modelChild)) {

            if (modelChild instanceof PrimaryAssetInstance primaryModelChild) {

                String prevConnection = modelChild.getModelParentAttachment();
                primaryModelConnections.get(prevConnection).remove(primaryModelChild);
                primaryModelChildren.remove(primaryModelChild);
            }

            modelChild.removeModelParent();
        }
    }

    @Override
    protected final RenderBuffer getParentPolygons(ModelInstance model, BoneActorSet boneActors, Vector position,
            Quaternion rotation) {

        Bone root = model.getRoot();
        BoneActor boneActor = new BoneActor(position.subtract(root.pivotPoint()), rotation, Matrix.IDENTITY_3X3);

        HashMap<String, List<ModelAssetInstance>> boneMappings = new HashMap<>();

        for (ModelAssetInstance modelChild : modelChildren) {

            String attachment = modelChild.getModelParentAttachment();
            if (attachment == null) {

                continue;
            }

            boneMappings.computeIfAbsent(attachment, _ -> new ArrayList<>()).add(modelChild);
        }

        RenderBuffer[] renders = new RenderBuffer[] { new RenderBuffer() };
        Map<Integer, Map<Vector, Double>> vertices = root.computeVertexWeights(boneActors, boneActor,
                model.getVertices(), (bone, actor, pivotPoint) -> {

                    if (boneMappings.containsKey(bone)) {

                        List<RenderBuffer> newRenders = boneMappings.get(bone).stream()
                                .map(asset -> asset.getPolygons(this)).toList();
                        renders[0].append(newRenders);
                    }

                    renders[0].transform(actor, pivotPoint);
                });

        RenderBuffer render = generatePolygons(vertices);
        render.append(renders[0]);

        return render;
    }

    @Override
    protected final RenderBuffer getChildPolygons(ModelInstance model, BoneActorSet boneActors,
            ModelAssetInstance parent, String attachment) {

        Vector position = model.getPivotPoint(attachment);
        Quaternion rotation = Quaternion.IDENTITY_QUATERNION;

        return getParentPolygons(model, boneActors, position, rotation);
    }

    /**
     * Creates a new instance of the <code>PrimaryAssetInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>PrimaryAssetInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public PrimaryAssetInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);
    }

    @Override
    public void applyModelAssetAttributes(ModelAssetAttributes attributeSet) {

        PrimaryAssetAttributes attributes = (PrimaryAssetAttributes) attributeSet;

        // Updates the inventory field.
        computeAttribute(attributes.getInventory(), inventory::applyAttributes, attributes, InventorySchema.DEFAULT);

        // Updates the inventoryUi field.
        inventoryUi = calculateAttribute(attributes.getInventoryUi(), inventoryUi);

        privateInventoryUi = calculateAttribute(attributes.getPrivateInventoryUi(), privateInventoryUi);

        // Update the interaction.
        interaction = calculateAttribute(attributes.getInteraction(),
                schemas -> interactionCache.computeIfAbsent(attributes,
                        _ -> new ImmutableList<>(
                                schemas.stream().map(AssetInteractionInstance::createInteraction).toList())),
                interaction, attributes, new ImmutableList<>());

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

        // Update the inventory items.
        inventory.getItems().forEach(item -> item.update(time));

        onPrimaryAssetUpdate(time);
    }

    /**
     * Performs the instance update of the class of this
     * <code>PrimaryAssetInstance</code> instance.
     * 
     * @param time <code>double</code>: The time since the world was created.
     */
    protected abstract void onPrimaryAssetUpdate(double time);
}
