package com.transcendruins.packcompiling.assetschemas.animations.interpolation;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchemaAttributes.KeyFrame;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

public final class PositionFrame extends PositionModifier {

    private final Interpolation interpolation;

    private final double animationLength;

    public PositionFrame(TracedDictionary json, double timestamp, double animationLength) throws LoggedException {

        super(json);
        interpolation = new Interpolation(json, timestamp);
        this.animationLength = animationLength;
    }

    public PositionModifier interpolate(PositionFrame next, double t) {

        double inter = Interpolation.getInter(interpolation, next.interpolation, t, animationLength);

        Vector position = Interpolation.lerp(getPosition(), next.getPosition(),
                inter);

        double rotationAngle = Interpolation.lerp(getRotation().getAngle(), next.getRotation().getAngle(), inter);
        Vector rotationAxis = Interpolation.slerp(getRotation().getAxis(),
                next.getRotation().getAxis(), inter);

        return new PositionModifier(position, new RotationModifier(rotationAngle, rotationAxis));
    }

    /**
     * Interpolates between two <code>KeyFrame</code> instances.
     * 
     * @param lastFrame <code>KeyFrame</code>: The last frame to
     *                  interpolate at.
     * @param nextFrame <code>KeyFrame</code>: The next frame to
     *                  interpolate between.
     * @param timestamp <code>double</code>: The timestamp to interpolate at.
     * @return <code>PositionModifier</code>: The resulting position modifier.
     */
    public static PositionModifier interpolate(KeyFrame lastFrame, KeyFrame nextFrame, double timestamp) {

        PositionFrame last = lastFrame == null ? null : lastFrame.getPosition(KeyFrame.LAST);
        PositionFrame next = nextFrame == null ? null : nextFrame.getPosition(KeyFrame.NEXT);

        if (last == null && next == null) {

            return null;
        }

        if (last == null) {

            return next;
        }

        if (next == null) {

            return last;
        }

        return last.interpolate(next, timestamp);
    }
}
