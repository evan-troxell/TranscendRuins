package com.transcendruins.geometry.interpolation;

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
        
        double angle = Interpolation.interpolate(getAngle(), next.getAngle(), inter);
        com.transcendruins.geometry.Vector axis = Interpolation.sphericalInterpolate(getAxis(), next.getAxis(), inter);

        return new RotationModifier(angle, axis);
    }
}
