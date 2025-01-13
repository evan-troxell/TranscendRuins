package com.transcendruins.graphics3d.interpolation;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

public final class ScaleFrame extends ScaleModifier {

    private final Interpolation interpolation;

    public ScaleFrame(TracedDictionary json, double timestamp) throws LoggedException {

        super(json);
        interpolation = new Interpolation(json, timestamp);
    }

    public ScaleModifier interpolate(ScaleFrame next, double t) {

        double inter = interpolation.getInter(next.interpolation.getTimestamp(), t);

        com.transcendruins.graphics3d.geometry.Vector scale = Interpolation.lerp(getScale(), next.getScale(), inter);

        double rotationAngle = Interpolation.lerp(getRotation().getAngle(), next.getRotation().getAngle(), inter);
        com.transcendruins.graphics3d.geometry.Vector rotationAxis = Interpolation.slerp(getRotation().getAxis(), next.getRotation().getAxis(), inter);

        return new ScaleModifier(scale, new RotationModifier(rotationAngle, rotationAxis));
    }
}
