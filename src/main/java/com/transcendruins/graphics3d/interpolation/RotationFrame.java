package com.transcendruins.graphics3d.interpolation;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

public final class RotationFrame extends RotationModifier {

    private final Interpolation interpolation;

    public RotationFrame(TracedDictionary json, double timestamp) throws LoggedException {

        super(json);
        interpolation = new Interpolation(json, timestamp);
    }

    public RotationModifier interpolate(RotationFrame next, double t) {

        double inter = interpolation.getInter(next.interpolation.getTimestamp(), t);

        double angle = Interpolation.lerp(getAngle(), next.getAngle(), inter);
        com.transcendruins.graphics3d.geometry.Vector axis = Interpolation.slerp(getAxis(), next.getAxis(), inter);

        return new RotationModifier(angle, axis);
    }
}
