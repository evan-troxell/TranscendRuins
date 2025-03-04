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

package com.transcendruins.assets.modelassets.elements;

import java.util.HashMap;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.extra.BoneActorSet;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.world.World;

/**
 * <code>ElementInstance</code>: A class representing a generated element
 * instance.
 */
public final class ElementInstance extends ModelAssetInstance {

    /**
     * <code>long</code>: The X coordinate of the tile of this
     * <code>ElementInstance</code> instance.
     */
    private final long tileX;

    /**
     * Retrieves the X coordinate of this <code>ElementInstance</code> instance.
     * 
     * @return <code>long</code>: The <code>tileX</code> field of this
     *         <code>ElementInstance</code> instance.
     */
    public long getTileX() {

        return tileX;
    }

    /**
     * <code>long</code>: The Z coordinate of the tile of this
     * <code>ElementInstance</code> instance.
     */
    private final long tileZ;

    /**
     * Retrieves the Z coordinate of this <code>ElementInstance</code> instance.
     * 
     * @return <code>long</code>: The <code>tileZ</code> field of this
     *         <code>ElementInstance</code> instance.
     */
    public long getTileZ() {

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
     * Retrieves the orientation of this <code>ElementInstance</code>
     * instance.
     * 
     * @return <code>double</code>: The <code>orientation</code> field of this
     *         <code>ElementInstance</code> instance.
     */
    public double getOrientation() {

        return orientation;
    }

    /**
     * Creates a new instance of the <code>ElementInstance</code> class.
     * 
     * @param context <code>ElementContext</code>: The context used to
     *                generate this <code>ElementInstance</code> instance.
     */
    public ElementInstance(ElementContext context) {

        super(context);

        tileX = context.getTileX();
        tileZ = context.getTileZ();

        setTileOffset(context.getTileOffset());
        setOrientation(context.getOrientation());
    }

    /**
     * Applies an attribute set to this <code>ElementInstance</code> instance.
     * 
     * @param attributeSet <code>AssetAttributes</code>: The attribute set to
     *                     apply.
     */
    @Override
    public void applyAttributes(Attributes attributeSet) {

        super.applyAttributes(attributeSet);
        ElementAttributes attributes = (ElementAttributes) attributeSet;
    }

    @Override
    protected void onModelAssetUpdate(double time) {
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
