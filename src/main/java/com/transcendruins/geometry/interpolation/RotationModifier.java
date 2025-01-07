package com.transcendruins.geometry.interpolation;

import com.transcendruins.geometry.Quaternion;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public class RotationModifier {

    private final double angle;

    private final Vector axis;

    public RotationModifier() {

        angle = 0;
        axis = Vector.fromUnitSphere(0, 0);
    }

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

    public RotationModifier(double angle, Vector axis) {

        this.angle = angle;
        this.axis = axis;
    }

    public double getAngle() {

        return angle;
    }

    public Vector getAxis() {

        return axis;
    }

    public Vector apply(Vector vector) {

        Quaternion quat = Quaternion.fromEulerRotation(angle, axis);
        Quaternion conj = quat.toConjugate();

        return quat.multiply(vector.toQuaternion()).multiply(conj).toVector();
    }

    public Vector applyConj(Vector vector) {

        Quaternion quat = Quaternion.fromEulerRotation(angle, axis);
        Quaternion conj = quat.toConjugate();

        return conj.multiply(vector.toQuaternion()).multiply(quat).toVector();
    }
}
