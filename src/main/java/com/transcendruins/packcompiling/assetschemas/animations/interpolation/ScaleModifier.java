package com.transcendruins.packcompiling.assetschemas.animations.interpolation;

import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>ScaleModifier</code>: A class representing a scaling in space about
 * an axis.
 */
public class ScaleModifier {

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
     * scale of
     * this <code>ScaleModifier</code> instance.
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
     * @throws LoggedException
     */
    public ScaleModifier(TracedDictionary json)
            throws MissingPropertyException, PropertyTypeException, ArrayLengthException {

        TracedEntry<Vector> scaleEntry = json.getAsVector("scale", false, 3);
        scale = scaleEntry.getValue();

        if (json.containsKey("rotation")) {

            TracedEntry<TracedDictionary> rotationEntry = json.getAsDictionary("rotation", false);
            rotation = new RotationModifier(rotationEntry.getValue());
        } else {

            rotation = new RotationModifier();
        }
    }

    /**
     * Creates a new instance of the <code>ScaleModifier</code> class.
     * 
     * @param scale    <code>Vector</code>: The scale values of this
     *                 <code>ScaleModifier</code> instance.
     * @param rotation <code>RotationModifier</code>: The rotation modifier to be
     *                 applied to the scale of
     *                 this <code>ScaleModifier</code> instance.
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

        Matrix rQuat = rotation.getTransform().toMatrix();

        return rQuat.multiplyMatrix(Matrix.getScaledMatrix3X3(scale.getX(), scale.getY(), scale.getZ()))
                .multiplyMatrix(rQuat.transpose());
    }
}
