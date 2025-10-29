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

import com.transcendruins.geometry.Matrix;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>ScaleModifier</code>: A class representing a scaling in space about an
 * axis.
 */
sealed public class ScaleModifier permits ScaleFrame {

    /**
     * <code>Vector</code>: The scale values of this <code>ScaleModifier</code>
     * instance.
     */
    private final Vector scale;

    /**
     * Retrieves the scale values of this <code>ScaleModifier</code> instance.
     * 
     * @return <code>Vector</code>: The <code>scale</code> field of this
     *         <code>ScaleModifier</code> instance.
     */
    public Vector getScale() {

        return scale;
    }

    /**
     * <code>RotationModifier</code>: The rotation modifier to be applied to the
     * scale of this <code>ScaleModifier</code> instance.
     */
    private final RotationModifier rotation;

    /**
     * Retrieves the rotation modifier of this <code>ScaleModifier</code> instance.
     * 
     * @return <code>RotationModifier</code>: The <code>rotation</code> field of
     *         this <code>ScaleModifier</code> instance.
     */
    public RotationModifier getRotation() {

        return rotation;
    }

    /**
     * Creates a new instance of the <code>ScaleModifier</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON from which to create this
     *             <code>ScaleModifier</code> instance.
     * @throws NumberBoundsException
     * @throws LoggedException
     */
    public ScaleModifier(TracedDictionary json)
            throws MissingPropertyException, PropertyTypeException, CollectionSizeException, NumberBoundsException {

        TracedEntry<Vector> scaleEntry = json.getAsVector("scale", false, 3);
        scale = scaleEntry.getValue();

        TracedEntry<TracedDictionary> rotationEntry = json.getAsDict("rotation", true);
        rotation = (rotationEntry.containsValue()) ? new RotationModifier(rotationEntry.getValue())
                : new RotationModifier();
    }

    /**
     * Creates a new instance of the <code>ScaleModifier</code> class.
     */
    public ScaleModifier() {

        this.scale = new Vector(1, 1, 1);
        this.rotation = new RotationModifier();
    }

    /**
     * Creates a new instance of the <code>ScaleModifier</code> class.
     * 
     * @param scale    <code>Vector</code>: The scale values of this
     *                 <code>ScaleModifier</code> instance.
     * @param rotation <code>RotationModifier</code>: The rotation modifier to be
     *                 applied to the scale of this <code>ScaleModifier</code>
     *                 instance.
     */
    public ScaleModifier(Vector scale, RotationModifier rotation) {

        this.scale = scale;
        this.rotation = rotation;
    }

    /**
     * Retrieves the transformation of this <code>ScaleModifier</code> instance.
     * 
     * @return <code>Matrix</code>: The transformation matrix.
     */
    public Matrix getTransform() {

        return rotation.getTransform().rotate(Matrix.getScaledMatrix3X3(scale.getX(), scale.getY(), scale.getZ()));
    }
}
