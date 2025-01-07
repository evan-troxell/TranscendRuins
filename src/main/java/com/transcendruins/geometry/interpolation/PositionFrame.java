package com.transcendruins.geometry.interpolation;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

public final class PositionFrame extends PositionModifier {
    
    private final Interpolation interpolation;

    public PositionFrame(TracedDictionary json, double timestamp) throws LoggedException {

        super(json);
        interpolation = new Interpolation(json, timestamp);
    }

    public PositionModifier interpolate(PositionFrame next, double t) {

        double inter = interpolation.getInter(next.interpolation.getTimestamp(), t);

        com.transcendruins.geometry.Vector position = Interpolation.interpolate(getPosition(), next.getPosition(), inter);
        
        double rotationAngle = Interpolation.interpolate(getRotation().getAngle(), next.getRotation().getAngle(), inter);
        com.transcendruins.geometry.Vector rotationAxis = Interpolation.sphericalInterpolate(getRotation().getAxis(), next.getRotation().getAxis(), inter);

        return new PositionModifier(position, new RotationModifier(rotationAngle, rotationAxis));
    }
}
