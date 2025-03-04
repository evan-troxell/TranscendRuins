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

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.world.World;

/**
 * <code>ElementContext</code>: A class representing the instantiation context
 * of an element.
 */
public final class ElementContext extends AssetContext {

    /**
     * <code>long</code>: The X coordinate of this <code>ElementContext</code>
     * instance.
     */
    private final long tileX;

    /**
     * Retrieves the X coordinate of this <code>ElementContext</code> instance.
     * 
     * @return <code>long</code>: The <code>tileX</code> field of this
     *         <code>ElementContext</code> instance.
     */
    public long getTileX() {

        return tileX;
    }

    /**
     * <code>long</code>: The Z coordinate of this <code>ElementContext</code>
     * instance.
     */
    private final long tileZ;

    /**
     * Retrieves the Z coordinate of this <code>ElementContext</code> instance.
     * 
     * @return <code>long</code>: The <code>tileZ</code> field of this
     *         <code>ElementContext</code> instance.
     */
    public long getTileZ() {

        return tileZ;
    }

    /**
     * <code>double</code>: The orientation of this <code>ElementContext</code>
     * instance in degrees.
     */
    private double orientation = 0.0;

    /**
     * Sets the orientation of this <code>ElementContext</code> instance in degrees.
     * 
     * @param orientation <code>double</code>: The value to assign to the
     *                    <code>orientation</code> field of this
     *                    <code>ElementContext</code> instance.
     */
    public void setOrientation(double orientation) {

        this.orientation = orientation;
    }

    /**
     * Retrieves the orientation of this <code>ElementContext</code> instance in
     * radians.
     * <code>double</code>: The <code>orientation</code> field of this
     * <code>ElementContext</code> instance converted to radians.
     */
    public double getOrientation() {

        return Math.toRadians(orientation);
    }

    /**
     * <code>Vector</code>: The offset from the center of the tile of this
     * <code>ElementContext</code> instance.
     */
    private Vector tileOffset = Vector.IDENTITY_VECTOR;

    /**
     * Sets the offset from the center of the tile of this
     * <code>ElementContext</code> instance.
     * 
     * @param tileOffset <code>Vector</code>: The value to assign to the
     *                   <code>tileOffset</code> field of this
     *                   <code>ElementContext</code> instance.
     */
    public void setTileOffset(Vector tileOffset) {

        this.tileOffset = tileOffset;
    }

    /**
     * Retrieves the offset from the center of the tile of this
     * <code>ElementContext</code> instance.
     * 
     * @return <code>Vector</code>: The <code>tileOffset</code> field of this
     *         <code>ElementContext</code> instance.
     */
    public Vector getTileOffset() {

        return tileOffset;
    }

    /**
     * Creates a new instance of the <code>ElementContext</code> class.
     * 
     * @param presets     <code>ElementPresets</code>: The presets containing schema
     *                    and
     *                    instantiation information of this
     *                    <code>ElementContext</code>
     *                    instance.
     * @param world       <code>World</code>: The <code>World</code> instance to
     *                    assign
     *                    to this <code>ElementContext</code> instance.
     * @param tileX       <code>long</code>: The X coordinate of this
     *                    <code>ElementContext</code> instance.
     * @param tileZ       <code>long</code>: The Z coordinate of this
     *                    <code>ElementContext</code> instance.
     * @param orientation <code>double</code>: The orientation of this
     *                    <code>ElementContext</code> instance in degrees.
     */
    public ElementContext(ElementPresets presets, World world, long tileX, long tileZ, double orientation) {

        super(presets, world);

        this.tileX = tileX;
        this.tileZ = tileZ;
        this.orientation = orientation;
    }
}
