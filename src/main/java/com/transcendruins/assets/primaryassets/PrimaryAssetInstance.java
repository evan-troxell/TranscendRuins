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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.transcendruins.assets.AssetType.ELEMENT;
import com.transcendruins.assets.animations.boneactors.BoneActor;
import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.elements.ElementInstance;
import com.transcendruins.assets.entities.EntityInstance;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema;
import com.transcendruins.assets.items.ItemInstance;
import com.transcendruins.assets.modelassets.ModelAssetAttributes;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.assets.models.ModelAttributes.Bone;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.assets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.assets.primaryassets.inventory.InventorySchema;
import com.transcendruins.assets.primaryassets.inventory.InventorySlotInstance;
import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.rendering.renderBuffer.RenderBuffer;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.AreaTile;
import com.transcendruins.world.Player;
import com.transcendruins.world.World;

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

    private AssetInteraction interaction;

    private ImmutableSet<AreaTile> tiles = new ImmutableSet<>();

    private boolean hasTileUpdate;

    protected void queueTileUpdate() {

        hasTileUpdate = true;
    }

    public final void updateTiles(AreaGrid area) {

        if (!hasTileUpdate) {

            return;
        }

        hasTileUpdate = false;

        Rectangle tileBounds = getTileBounds();
        AreaTile[] areaTiles = area.getArea(tileBounds.x, tileBounds.y, tileBounds.width, tileBounds.height);
        updateTiles(Arrays.stream(areaTiles).toList());
    }

    private void updateTiles(Collection<AreaTile> newTiles) {

        // Find the tiles that should remain the same.
        HashSet<AreaTile> intersect = new HashSet<>(tiles);
        intersect.retainAll(newTiles);

        boolean isElement = getType() == ELEMENT;

        HashSet<AreaTile> remove = new HashSet<>(tiles);
        remove.removeAll(intersect);
        // Update the tiles that should be removed from.
        if (!remove.isEmpty()) {

            if (isElement) {

                ElementInstance element = (ElementInstance) this;

                for (AreaTile tile : remove) {

                    // tile.removeElement(element);
                }
            } else {

                EntityInstance entity = (EntityInstance) this;

                for (AreaTile tile : remove) {

                    // tile.removeEntity(entity);
                }
            }
        }

        HashSet<AreaTile> add = new HashSet<>(newTiles);
        add.removeAll(intersect);

        // Update the tiles that should be added to.
        if (!add.isEmpty()) {

            if (isElement) {

                ElementInstance element = (ElementInstance) this;

                for (AreaTile tile : add) {

                    // tile.addElement(element);
                }
            } else {

                EntityInstance entity = (EntityInstance) this;

                for (AreaTile tile : add) {

                    // tile.addEntity(entity);
                }
            }
        }

        tiles = new ImmutableSet<>(newTiles);
    }

    public final Rectangle getTileBounds() {

        if (hasModelParent()) {

            return getModelParent().getTileBounds();
        }

        return getInternalTileBounds();
    }

    protected abstract Rectangle getInternalTileBounds();

    private Vector hitbox;

    public final Vector getHitbox() {

        return hitbox;
    }

    private String modelParentAttachment;

    protected final void setModelParent(PrimaryAssetInstance parent, String modelParentAttachment) {

        setModelParent(parent);
        this.modelParentAttachment = modelParentAttachment;

        queueTileUpdate();
    }

    @Override
    public final void removeModelParent() {

        setModelParent(null);
        modelParentAttachment = null;

        queueTileUpdate();
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
     * @param attachment <code>String</code>: The name of the bone to which the
     *                   child model will be attached.
     * @return <code>boolean</code>: Whether or not the child was successfully
     *         added.
     */
    protected final boolean addModelChild(PrimaryAssetInstance modelChild, String attachment) {

        if (!containsBone(attachment)) {

            return false;
        }

        if (this != modelChild.getModelParent()) {

            modelChild.removeModelParent();
        }

        modelChild.setModelParent(this, attachment);
        modelChildren.add(modelChild);
        return true;
    }

    /**
     * Adds a child model to this <code>PrimaryAssetInstance</code> instance.
     * 
     * @param modelChild <code>ItemInstance</code>: The child model to add to this
     *                   <code>PrimaryAssetInstance</code> instance.
     * @param attachment <code>String</code>: The name of the bone to which the
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
    protected final void removeModelChild(ModelAssetInstance modelChild) {

        // If the child is a child of this model, remove it.
        if (modelChildren.remove(modelChild)) {

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
        interaction = calculateAttribute(attributes.getInteraction(), interaction, attributes, null);

        hitbox = calculateAttribute(attributes.getHitbox(), hitbox, attributes, World.UNIT_TILE_VECTOR);

        applyPrimaryAssetAttributes(attributes);
    }

    public final boolean interact(long time, Player caller) {

        if (interaction == null) {

            return false;
        }
        return interaction.call(this, time, caller);
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
     * Performs the instance update of the class of this
     * <code>PrimaryAssetInstance</code> instance.
     * 
     * @param time <code>double</code>: The time since the world was created.
     */
    protected abstract void onPrimaryAssetUpdate(double time);
}
