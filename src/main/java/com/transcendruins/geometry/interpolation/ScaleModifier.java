package com.transcendruins.geometry.interpolation;

import com.transcendruins.geometry.Matrix;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public class ScaleModifier {

    private final Vector scale;

    private final RotationModifier rotation;
    
    public ScaleModifier(TracedDictionary json) throws MissingPropertyException, PropertyTypeException, ArrayLengthException {

        TracedEntry<Vector> scaleEntry = json.getAsVector("scale", false, 3);
        scale = scaleEntry.getValue();

        if (json.containsKey("rotation")) {

            TracedEntry<TracedDictionary> rotationEntry = json.getAsDictionary("rotation", false);
            rotation = new RotationModifier(rotationEntry.getValue());
        } else {

            rotation = new RotationModifier();
        }
    }

    public ScaleModifier(Vector scale, RotationModifier rotation) {

        this.scale = scale;
        this.rotation = rotation;
    }

    public Vector getScale() {

        return scale;
    }

    public RotationModifier getRotation() {
        
        return rotation;
    }

    public Vector apply(Vector vector) {

        Vector unscaled = rotation.applyConj(vector);
        Vector scaled = unscaled.multiplyMatrix(Matrix.getScaledMatrix3X3(vector.getX(), vector.getY(), vector.getZ()));

        return rotation.apply(scaled);
    }
}
