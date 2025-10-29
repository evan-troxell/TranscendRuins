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

package com.transcendruins.assets.animations.interpolation;

import com.transcendruins.geometry.Quaternion;
import com.transcendruins.geometry.Vector;
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
     * <code>double</code>: The angle of rotation of this
     * <code>RotationModifier</code> instance.
     */
    private final double angle;

    /**
     * Retrieves the angle of rotation of this <code>RotationModifier</code>
     * instance.
     * 
     * @return <code>double</code>: The <code>angle</code> field of this
     *         <code>RotationModifier</code> instance.
     */
    public double getAngle() {

        return angle;
    }

    /**
     * <code>Vector</code>: The axis of rotation of this
     * <code>RotationModifier</code> instance.
     */
    private final Vector axis;

    /**
     * Retrieves the axis of rotation of this <code>RotationModifier</code>
     * instance.
     * 
     * @return <code>Vector</code>: The <code>axis</code> field of this
     *         <code>RotationModifier</code> instance.
     */
    public Vector getAxis() {

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

            TracedEntry<Double> angleEntry = json.getAsDouble("angle", false, null);
            angle = Math.toRadians(angleEntry.getValue());

            TracedEntry<Double> axisHeadingEntry = json.getAsDouble("axisHeading", true, 0.0);
            double axisHeading = Math.toRadians(axisHeadingEntry.getValue());

            TracedEntry<Double> axisPitchEntry = json.getAsDouble("axisPitch", true, 0.0);
            double axisPitch = Math.toRadians(axisPitchEntry.getValue());

            axis = Vector.fromUnitSphere(axisHeading, axisPitch);
        } else {

            angle = 0;
            axis = Vector.fromUnitSphere(0, 0);
        }
    }

    /**
     * Creates a new instance of the <code>RotationModifier</code> class.
     */
    public RotationModifier() {

        angle = 0;
        axis = Vector.IDENTITY_VECTOR;
    }

    /**
     * Creates a new instance of the <code>RotationModifier</code> class.
     * 
     * @param angle <code>double</code>: The angle of rotation of this
     *              <code>RotationModifier</code> instance.
     * @param axis  <code>Vector</code>: The axis of rotation of this
     *              <code>RotationModifier</code> instance.
     */
    public RotationModifier(double angle, Vector axis) {

        this.angle = angle;
        this.axis = axis;
    }

    /**
     * Retrieves the transformation of this <code>RotationModifier</code> instance.
     * 
     * @return <code>Quaternion</code>: The transformation quaternion.
     */
    public Quaternion getTransform() {

        return Quaternion.fromEulerRotation(angle, axis);
    }
}
