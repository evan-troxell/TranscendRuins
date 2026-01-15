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

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>RotationModifier</code>: A class representing a rotation in space about
 * the origin.
 */
sealed public class RotationModifier permits RotationFrame {

    /**
     * <code>float</code>: The angle of rotation of this
     * <code>RotationModifier</code> instance.
     */
    private final float angle;

    /**
     * Retrieves the angle of rotation of this <code>RotationModifier</code>
     * instance.
     * 
     * @return <code>float</code>: The <code>angle</code> field of this
     *         <code>RotationModifier</code> instance.
     */
    public float getAngle() {

        return angle;
    }

    /**
     * <code>Vector3f</code>: The axis of rotation of this
     * <code>RotationModifier</code> instance.
     */
    private final Vector3f axis;

    /**
     * Retrieves the axis of rotation of this <code>RotationModifier</code>
     * instance.
     * 
     * @return <code>Vector3f</code>: The <code>axis</code> field of this
     *         <code>RotationModifier</code> instance.
     */
    public Vector3f getAxis() {

        return axis;
    }

    /**
     * Creates a new instance of the <code>RotationModifier</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON from which to create this
     *             <code>RotationModifier</code> instance.
     * @throws NumberBoundsException
     * @throws LoggedException
     */
    public RotationModifier(TracedDictionary json)
            throws MissingPropertyException, PropertyTypeException, NumberBoundsException {

        if (json.containsKey("angle")) {

            TracedEntry<Float> angleEntry = json.getAsFloat("angle", false, null);
            angle = FastMath.DEG_TO_RAD * angleEntry.getValue();

            TracedEntry<Float> axisHeadingEntry = json.getAsFloat("axisHeading", true, 0.0f);
            float axisHeading = FastMath.DEG_TO_RAD * axisHeadingEntry.getValue();

            TracedEntry<Float> axisPitchEntry = json.getAsFloat("axisPitch", true, 0.0f);
            float axisPitch = FastMath.DEG_TO_RAD * axisPitchEntry.getValue();

            float sinPitch = FastMath.sin(axisPitch);

            float x = sinPitch * FastMath.cos(axisHeading);
            float y = FastMath.cos(axisPitch);
            float z = sinPitch * FastMath.sin(axisHeading);

            axis = new Vector3f(x, y, z);
        } else {

            angle = 0;
            axis = null;
        }
    }

    /**
     * Creates a new instance of the <code>RotationModifier</code> class.
     */
    public RotationModifier() {

        angle = 0;
        axis = null;
    }

    /**
     * Creates a new instance of the <code>RotationModifier</code> class.
     * 
     * @param angle <code>float</code>: The angle of rotation of this
     *              <code>RotationModifier</code> instance.
     * @param axis  <code>Vector3f</code>: The axis of rotation of this
     *              <code>RotationModifier</code> instance.
     */
    public RotationModifier(float angle, Vector3f axis) {

        this.angle = angle;
        this.axis = axis;
    }

    /**
     * Retrieves the transformation of this <code>RotationModifier</code> instance.
     * 
     * @return <code>Quaternion</code>: The transformation quaternion.
     */
    public final Quaternion getTransform() {

        if (angle == 0 || axis == null) {

            return Quaternion.IDENTITY;
        }

        return new Quaternion().fromAngleAxis(angle, axis);
    }
}
