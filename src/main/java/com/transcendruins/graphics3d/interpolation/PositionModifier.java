package com.transcendruins.graphics3d.interpolation;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>PositionModifier</code>: A class representing a position vector rotated
 * in space
 * about the origin.
 */
public class PositionModifier {

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
     * position of
     * this <code>PositionModifier</code> instance.
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

            TracedEntry<TracedDictionary> rotationEntry = json.getAsDictionary("rotation", false);
            rotation = new RotationModifier(rotationEntry.getValue());
        } else {

            rotation = new RotationModifier();
        }
    }

    /**
     * Creates a new instance of the <code>PositionModifier</code> class.
     * 
     * @param position <code>Vector</code>: The position of this
     *                 <code>PositionModifier</code> instance.
     * @param rotation <code>RotationModifier</code>: The rotation modifier to be
     *                 applied to
     *                 the position of this <code>PositionModifier</code> instance.
     */
    public PositionModifier(Vector position, RotationModifier rotation) {

        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Applies this <code>PositionModifier</code> instance to another vector.
     * 
     * @param vector <code>Vector</code>: The vector to apply to.
     * @return <code>Vector</code>: The resulting vector.
     */
    public Vector apply(Vector vector) {

        return vector.addVector(rotation.apply(vector));
    }
}
