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

import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.primaryassets.PrimaryAssetContext;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.world.World;

/**
 * <code>ElementContext</code>: A class representing the instantiation context
 * of an element.
 */
public final class ElementContext extends PrimaryAssetContext {

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
     * radians. <code>double</code>: The <code>orientation</code> field of this
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
     * @param presets     <code>AssetPresets</code>: The presets containing schema
     *                    and instantiation information of this
     *                    <code>ElementContext</code> instance.
     * @param world       <code>World</code>: The world copy of this
     *                    <code>EntityContext</code> instance.
     * @param parent      <code>AssetInstance</code>: The parent to assign to this
     *                    <code>ElementContext</code> instance.
     * @param orientation <code>double</code>: The orientation of this
     *                    <code>ElementContext</code> instance in degrees.
     */
    public ElementContext(AssetPresets presets, World world, AssetInstance parent, double orientation) {

        super(presets, world, parent);

        this.orientation = orientation;
    }
}
