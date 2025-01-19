package com.transcendruins.packcompiling.assetschemas.animations.interpolation;

import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchemaAttributes.KeyFrame;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

public final class RotationFrame extends RotationModifier {

    private final Interpolation interpolation;

    private final double animationLength;

    public RotationFrame(TracedDictionary json, double timestamp, double animationLength) throws LoggedException {

        super(json);
        interpolation = new Interpolation(json, timestamp);
        this.animationLength = animationLength;
    }

    public RotationModifier interpolate(RotationFrame next, double t) {

        double inter = Interpolation.getInter(interpolation, next.interpolation, t, animationLength);

        double angle = Interpolation.lerp(getAngle(), next.getAngle(), inter);

        com.transcendruins.graphics3d.geometry.Vector axis = Interpolation.slerp(getAxis(), next.getAxis(), inter);

        return new RotationModifier(angle, axis);
    }

    /**
     * Interpolates between two <code>KeyFrame</code> instances.
     * 
     * @param lastFrame <code>KeyFrame</code>: The last frame to
     *                  interpolate at.
     * @param nextFrame <code>KeyFrame</code>: The next frame to
     *                  interpolate at.
     * @param timestamp <code>double</code>: The timestamp to interpolate at.
     * @return <code>RotationModifier</code>: The resulting rotation modifier.
     */
    public static RotationModifier interpolate(KeyFrame lastFrame, KeyFrame nextFrame, double timestamp) {

        RotationFrame last = lastFrame == null ? null : lastFrame.getRotation(KeyFrame.LAST);
        RotationFrame next = nextFrame == null ? null : nextFrame.getRotation(KeyFrame.NEXT);

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
