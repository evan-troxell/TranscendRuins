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

package com.transcendruins.assets.elements;

import java.util.HashMap;

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.extra.BoneActorSet;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.assets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.assets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.world.AreaTile;
import com.transcendruins.world.World;

/**
 * <code>ElementInstance</code>: A class representing a generated element
 * instance.
 */
public final class ElementInstance extends PrimaryAssetInstance {

    /**
     * <code>int</code>: The X coordinate of the tile of this
     * <code>ElementInstance</code> instance.
     */
    private int tileX;

    /**
     * Sets the X coordinate of this <code>ElementInstance</code> instance.
     * 
     * @param tileX <code>int</code>: The X coordinate to apply to this
     *              <code>ElementInstance</code> instance.
     */
    public void setTileX(int tileX) {

        this.tileX = tileX;
    }

    /**
     * Retrieves the X coordinate of this <code>ElementInstance</code> instance.
     * 
     * @return <code>int</code>: The <code>tileX</code> field of this
     *         <code>ElementInstance</code> instance.
     */
    public int getTileX() {

        return tileX;
    }

    /**
     * <code>int</code>: The Z coordinate of the tile of this
     * <code>ElementInstance</code> instance.
     */
    private int tileZ;

    /**
     * Sets the Z coordinate of this <code>ElementInstance</code> instance.
     * 
     * @param tileZ <code>int</code>: The Z coordinate to apply to this
     *              <code>ElementInstance</code> instance.
     */
    public void setTileZ(int tileZ) {

        this.tileZ = tileZ;
    }

    /**
     * Retrieves the Z coordinate of this <code>ElementInstance</code> instance.
     * 
     * @return <code>int</code>: The <code>tileZ</code> field of this
     *         <code>ElementInstance</code> instance.
     */
    public int getTileZ() {

        return tileZ;
    }

    /**
     * <code>Vector</code>: The tile offset of this <code>ElementInstance</code>
     * instance.
     */
    private Vector tileOffset;

    public void setTileOffset(Vector tileOffset) {

        this.tileOffset = tileOffset;
    }

    /**
     * Retrieves the tile offset of this <code>ElementInstance</code> instance.
     * 
     * @return <code>Vector</code>: The <code>tileOffset</code> field of this
     *         <code>ElementInstance</code> instance.
     */
    public Vector getTileOffset() {

        return tileOffset;
    }

    /**
     * <code>double</code>: The orientation of this <code>ElementInstance</code>
     * instance in degrees.
     */
    private double orientation;

    public void setOrientation(double orientation) {

        this.orientation = orientation;
    }

    /**
     * Retrieves the orientation of this <code>ElementInstance</code> instance.
     * 
     * @return <code>double</code>: The <code>orientation</code> field of this
     *         <code>ElementInstance</code> instance.
     */
    public double getOrientation() {

        return orientation;
    }

    private AreaTile[] tiles;

    public void remove(AreaTile[] tiles) {

        for (AreaTile tile : tiles) {

            tile.removeElement(this);
        }
    }

    public void apply(AreaTile[] tiles) {

        if (this.tiles != null) {

            remove(this.tiles);
        }

        this.tiles = tiles;
        for (AreaTile tile : tiles) {

            tile.addElement(this);
        }
    }

    /**
     * Creates a new instance of the <code>ElementInstance</code> class.
     * 
     * @param context <code>ElementContext</code>: The context used to generate this
     *                <code>ElementInstance</code> instance.
     */
    public ElementInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        ElementContext context = (ElementContext) assetContext;

        setTileOffset(context.getTileOffset());
        setOrientation(context.getOrientation());
    }

    @Override
    public void applyPrimaryAssetAttributes(PrimaryAssetAttributes attributeSet) {

        ElementAttributes attributes = (ElementAttributes) attributeSet;
    }

    @Override
    protected void onPrimaryAssetUpdate(double time) {
    }

    @Override
    public Vector getPosition() {

        return new Vector(tileX + 0.5, 0, tileZ + 0.5).multiply(World.UNIT_TILE).add(tileOffset);
    }

    @Override
    public Quaternion getRotation() {

        return Quaternion.fromHeading(orientation);
    }

    @Override
    protected HashMap<Triangle, Triangle> getPolygons(BoneActorSet boneActors, ModelInstance model, Vector position,
            Quaternion rotation) {

        return model.getPolygons(boneActors, position, rotation);
    }
}
