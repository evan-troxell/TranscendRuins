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

package com.transcendruins.assets.entities;

import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.primaryassets.PrimaryAssetContext;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.world.World;

/**
 * <code>EntityContext</code>: A class representing the instantiation context of
 * an entity.
 */
public final class EntityContext extends PrimaryAssetContext {

    /**
     * <code>Vector</code>: The position of this <code>EntityContext</code>
     * instance.
     */
    private final Vector position;

    /**
     * Retrieves the position of this <code>EntityContext</code> instance.
     * 
     * @return <code>Vector</code>: The <code>position</code> field of this
     *         <code>EntityContext</code> instance.
     */
    public Vector getPosition() {

        return position;
    }

    /**
     * <code>double</code>: The heading of this <code>EntityContext</code> instance
     * in degrees.
     */
    private double heading = 0.0;

    /**
     * Retrieves the heading of this <code>EntityContext</code> instance in radians.
     * <code>double</code>: The <code>heading</code> field of this
     * <code>EntityContext</code> instance in radians.
     */
    public double getHeading() {

        return Math.toRadians(heading);
    }

    /**
     * <code>double</code>: The pitch of this <code>EntityContext</code> instance in
     * degrees.
     */
    private double pitch = 0.0;

    /**
     * Retrieves the pitch of this <code>EntityContext</code> instance in radians.
     * <code>double</code>: The <code>pitch</code> field of this
     * <code>EntityContext</code> instance in radians.
     */
    public double getPitch() {

        return Math.toRadians(pitch);
    }

    /**
     * Sets the rotation of this <code>EntityContext</code> instance.
     * 
     * @param heading <code>double</code>: The heading of this
     *                <code>EntityContext</code> instance in degrees.
     * @param pitch   <code>double</code>: The pitch of this
     *                <code>EntityContext</code> instance in degrees.
     */
    public void setRotation(double heading, double pitch) {

        this.heading = heading;
        this.pitch = pitch;
    }

    /**
     * Creates a new instance of the <code>EntityContext</code> class.
     * 
     * @param presets  <code>AssetPresets</code>: The presets containing schema and
     *                 instantiation information of this <code>EntityContext</code>
     *                 instance.
     * @param world    <code>World</code>: The world copy of this
     *                 <code>EntityContext</code> instance.
     * @param parent   <code>AssetInstance</code>: The parent to assign to this
     *                 <code>EntityContext</code> instance.
     * @param position <code>Vector</code>: The position of this
     *                 <code>EntityContext</code> instance.
     */
    public EntityContext(AssetPresets presets, World world, AssetInstance parent, Vector position) {

        super(presets, world, parent);

        this.position = position;
    }
}
