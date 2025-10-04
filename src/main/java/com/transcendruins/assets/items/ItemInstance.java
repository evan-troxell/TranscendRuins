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

package com.transcendruins.assets.items;

import java.util.HashMap;

import javax.swing.ImageIcon;

import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.modelassets.ModelAssetAttributes;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.assets.models.ModelAttributes;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.assets.primaryassets.inventory.InventorySlotInstance;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;

/**
 * <code>ItemInstance</code>: A class representing a generated item instance.
 */
public final class ItemInstance extends ModelAssetInstance {

    /**
     * <code>int</code>: The maximum stack size of this <code>ItemInstance</code>
     * instance.
     */
    private int maxStackSize;

    /**
     * Retrieves the maximum stack size of this <code>ItemInstance</code> instance.
     * 
     * @return <code>int</code>: The <code>maxStackSize</code> field of this
     *         <code>ItemInstance</code> instance.
     */
    public final int getMaxStackSize() {

        return maxStackSize;
    }

    /**
     * <code>int</code>: The stack size of this <code>ItemInstance</code> instance.
     */
    private int stackSize;

    /**
     * Retrieves the stack size of this <code>ItemInstance</code> instance.
     * 
     * @return <code>int</code>: The <code>stackSize</code> field of this
     *         <code>ItemInstance</code> instance.
     */
    public final int getStackSize() {

        return stackSize;
    }

    /**
     * Determines whether or not this <code>ItemInstance</code> instance is at
     * capacity.
     * 
     * @return <code>boolean</code>: Whether or not the <code>stackSize</code> and
     *         </code>maxStackSize</code> fields of this <code>ItemInstance</code>
     *         instance are equal.
     */
    public final boolean atCapacity() {

        return stackSize >= maxStackSize;
    }

    /**
     * Sets the stack size of this <code>ItemInstance</code> instance.
     * 
     * @param stackSize <code>int</code>: The stack size of this
     *                  <code>ItemInstance</code> instance. Note that if this value
     *                  is greater than the <code>maxStackSize</code> field of this
     *                  <code>ItemInstance</code> instance, only the maximum
     *                  capacity will be applied.
     * @return <code>int</code>: The leftover capacity which could not be applied.
     *         This value will be <code>0</code> if the <code>stackSize</code>
     *         parameter was less than or equal to the <code>maxStackSize</code>
     *         field of this <code>ItemInstance</code> instance, but will be equal
     *         to the difference between the <code>stackSize</code> parameter and
     *         the <code>maxStackSize</code> field of this <code>ItemInstance</code>
     *         instance otherwise.
     */
    public final int setStackSize(int stackSize) {

        this.stackSize = Math.min(stackSize, maxStackSize);

        return stackSize - this.stackSize;
    }

    private String iconPath;

    private ImageIcon icon;

    public final ImageIcon getIcon() {

        return icon;
    }

    /**
     * <code>PrimaryAssetInstance</code>: The parent inventory to this
     * <code>ItemInstance</code> instance.
     */
    private ModelAssetInstance parent;

    /**
     * <code>InventorySlotInstance</code>: The inventory slot containing this
     * <code>ItemInstance</code> instance.
     */
    private InventorySlotInstance slot;

    /**
     * Sets the inventory slot of this <code>ItemInstance</code> instance.
     * 
     * @param inventory <code>PrimaryAssetInstance</code>: The parent inventory to
     *                  this <code>ItemInstance</code> instance.
     * @param slot      <code>InventorySlotInstance</code>: The inventory slot
     *                  containing this <code>ItemInstance</code> instance.
     */
    public final void setSlot(ModelAssetInstance inventory, InventorySlotInstance slot) {

        setParent(inventory);

        this.parent = inventory;
        this.slot = slot;
    }

    /**
     * Retrieves the bone socket of this <code>ItemInstance</code> instance.
     * 
     * @return <code>Bone</code>: The retrieved model socket.
     */
    public final ModelAttributes.Bone getSocket() {

        return parent.getModel().getBone(slot.getModelSocket());
    }

    /**
     * Creates a new instance of the <code>ItemInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>ItemInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public ItemInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        ItemContext context = (ItemContext) assetContext;
        stackSize = context.getStackSize();
    }

    @Override
    public final void applyModelAssetAttributes(ModelAssetAttributes attributeSet) {

        ItemAttributes attributes = (ItemAttributes) attributeSet;

        // Updates the maxStackSize field.
        maxStackSize = calculateAttribute(attributes.getStackSize(), maxStackSize, attributes, 20);
        setProperty("maxStackSize", maxStackSize);

        // Updates the icon field.
        iconPath = calculateAttribute(attributes.getIcon(), val -> {

            icon = getInstanceTexture(val);

            return val;
        }, iconPath);
        setProperty("icon", iconPath);
    }

    @Override
    protected void onModelAssetUpdate(double time) {
    }

    /**
     * Combines the stack size of this <code>ItemInstance</code> instance with
     * another. If this <code>ItemInstance</code> instance is empty, it will add the
     * item and return null; otherwise, the item already in this
     * <code>ItemInstance</code> instance will be combined with the input item if
     * they are alike or the 2 items will be swapped if they are not alike.
     * 
     * @param item <code>ItemInstance</code>: The item whose stack size should be
     *             combined with this <code>ItemInstance</code> instance.
     * @return <code>ItemInstance</code>: The leftover item. If the combined stack
     *         size is greater than the <code>maxStackSize</code> field of this
     *         <code>ItemInstance</code> instance, then this will be the
     *         <code>item</code> parameter minus the removed stack; otherwise, this
     *         will be <code>null</code>.
     */
    public final ItemInstance combine(ItemInstance item) {

        if (!isLikeAsset(item)) {

            return item;
        }

        int inventorySum = stackSize + item.getStackSize();
        int difference = setStackSize(inventorySum);

        if (difference == 0) {

            return null;
        }

        item.setStackSize(difference);
        return item;
    }

    @Override
    public final Vector getPosition() {

        return parent.getPosition();
    }

    @Override
    public final Quaternion getRotation() {

        return parent.getRotation();
    }

    @Override
    protected final HashMap<Triangle, Triangle> getPolygons(BoneActorSet boneActors, ModelInstance model,
            Vector position, Quaternion rotation) {

        // BoneActorSet parentBoneActors = parent.getBoneActors();
        // BoneActorSet mergedBoneActors = new BoneActorSet(boneActors,
        // parentBoneActors);

        // TODO this part should be rebuilt, focus on model to figure out what
        // parameters are necessary to draw children assets.
        // model.getPolygons(mergedBoneActors, parentBoneActors, getSocket(), position,
        // rotation)
        return model.getPolygons(boneActors, position, rotation);
    }
}
