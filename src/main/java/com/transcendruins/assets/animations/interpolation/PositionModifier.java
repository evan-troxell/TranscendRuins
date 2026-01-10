/* Copyright 2026 Evan Troxell
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

package com.transcendruins.assets.animations.interpolation;

import com.transcendruins.geometry.Quaternion;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>PositionModifier</code>: A class representing a position vector rotated
 * in space about the origin.
 */
sealed public class PositionModifier permits PositionFrame {

    /**
     * <code>Vector</code>: The position of this <code>PositionModifier</code>
     * instance.
     */
    private final Vector position;

    /**
     * Retrieves the position of this <code>PositionModifier</code> instance.
     * 
     * @return <code>Vector</code>: The <code>position</code> field of this
     *         <code>PositionModifier</code> instance.
     */
    public Vector getPosition() {

        return position;
    }

    /**
     * <code>RotationModifier</code>: The rotation modifier to be applied to the
     * position of this <code>PositionModifier</code> instance.
     */
    private final RotationModifier rotation;

    /**
     * Retrieves the rotation modifier of this <code>PositionModifier</code>
     * instance.
     * 
     * @return <code>RotationModifier</code>: The <code>rotation</code> field of
     *         this <code>PositionModifier</code> instance.
     */
    public RotationModifier getRotation() {

        return rotation;
    }

    /**
     * Creates a new instance of the <code>PositionModifier</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON from which to create this
     *             <code>PositionModifier</code> instance.
     * @throws LoggedException
     */
    public PositionModifier(TracedDictionary json) throws LoggedException {

        TracedEntry<Vector> positionEntry = json.getAsVector("position", false, 3);
        position = positionEntry.getValue();

        if (json.containsKey("rotation")) {

            TracedEntry<TracedDictionary> rotationEntry = json.getAsDict("rotation", false);
            rotation = new RotationModifier(rotationEntry.getValue());
        } else {

            rotation = new RotationModifier();
        }
    }

    /**
     * Creates a new instance of the <code>PositionModifier</code> class.
     */
    public PositionModifier() {

        this(Vector.IDENTITY_VECTOR, new RotationModifier());
    }

    /**
     * Creates a new instance of the <code>PositionModifier</code> class.
     * 
     * @param position <code>Vector</code>: The position of this
     *                 <code>PositionModifier</code> instance.
     * @param rotation <code>RotationModifier</code>: The rotation modifier to be
     *                 applied to the position of this <code>PositionModifier</code>
     *                 instance.
     */
    public PositionModifier(Vector position, RotationModifier rotation) {

        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Retrieves the transformation of this <code>PositionModifier</code> instance.
     * 
     * @return <code>Vector</code>: The transformation vector.
     */
    public Vector getTransform() {

        if (rotation == null) {

            return position;
        }
        Quaternion quat = rotation.getTransform();

        return quat.rotate(position);
    }
}
