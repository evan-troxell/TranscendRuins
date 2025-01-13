package com.transcendruins.graphics3d.interpolation;

import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>RotationModifier</code>: A class representing a rotation in space about
 * the origin.
 */
public class RotationModifier {

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
     */
    public RotationModifier() {

        angle = 0;
        axis = Vector.fromUnitSphere(0, 0);
    }

    /**
     * Creates a new instance of the <code>RotationModifier</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON from which to create this
     *             <code>RotationModifier</code> instance.
     * @throws LoggedException
     */
    public RotationModifier(TracedDictionary json) throws MissingPropertyException, PropertyTypeException {

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
     * Applies this <code>RotationModifier</code> instance to another vector.
     * 
     * @param vector <code>Vector</code>: The vector to apply to.
     * @return <code>Vector</code>: The resulting vector.
     */
    public Vector apply(Vector vector) {

        Quaternion quat = Quaternion.fromEulerRotation(angle, axis);
        Quaternion conj = quat.toConjugate();

        return quat.multiply(vector.toQuaternion()).multiply(conj).toVector();
    }

    /**
     * Applies the conjugate of this <code>RotationModifier</code> instance to
     * another vector.
     * 
     * @param vector <code>Vector</code>: The vector to apply to.
     * @return <code>Vector</code>: The resulting vector.
     */
    public Vector applyConj(Vector vector) {

        Quaternion quat = Quaternion.fromEulerRotation(angle, axis);
        Quaternion conj = quat.toConjugate();

        return conj.multiply(vector.toQuaternion()).multiply(quat).toVector();
    }
}
