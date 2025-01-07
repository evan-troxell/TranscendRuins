package com.transcendruins.graphics3d.interpolation;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public class PositionModifier {

    private final Vector position;

    private final RotationModifier rotation;
    
    public PositionModifier(TracedDictionary json) throws LoggedException {

        TracedEntry<Vector> positionEntry = json.getAsVector("position", false, 3);
        position = positionEntry.getValue();

        if (json.containsKey("rotation")) {

            TracedEntry<TracedDictionary> rotationEntry = json.getAsDictionary("rotation", false);
            rotation = new RotationModifier(rotationEntry.getValue());
        } else {

            rotation = new RotationModifier();
        }
    }

    public PositionModifier(Vector position, RotationModifier rotation) {

        this.position = position;
        this.rotation = rotation;
    }

    public Vector getPosition() {

        return position;
    }

    public RotationModifier getRotation() {
        
        return rotation;
    }

    public Vector apply(Vector vector) {

        return vector.addVector(rotation.apply(vector));
    }
}
