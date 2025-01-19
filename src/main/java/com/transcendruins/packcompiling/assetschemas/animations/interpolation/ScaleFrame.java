package com.transcendruins.packcompiling.assetschemas.animations.interpolation;

import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchemaAttributes.KeyFrame;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

public final class ScaleFrame extends ScaleModifier {

    private final Interpolation interpolation;

    private final double animationLength;

    public ScaleFrame(TracedDictionary json, double timestamp, double animationLength) throws LoggedException {

        super(json);
        interpolation = new Interpolation(json, timestamp);
        this.animationLength = animationLength;
    }

    public ScaleModifier interpolate(ScaleFrame next, double t) {

        double inter = Interpolation.getInter(interpolation, next.interpolation, t, animationLength);

        com.transcendruins.graphics3d.geometry.Vector scale = Interpolation.lerp(getScale(), next.getScale(), inter);

        double rotationAngle = Interpolation.lerp(getRotation().getAngle(), next.getRotation().getAngle(), inter);
        com.transcendruins.graphics3d.geometry.Vector rotationAxis = Interpolation.slerp(getRotation().getAxis(),
                next.getRotation().getAxis(), inter);

        return new ScaleModifier(scale, new RotationModifier(rotationAngle, rotationAxis));
    }

    /**
     * Interpolates between two <code>KeyFrame</code> instances.
     * 
     * @param lastFrame <code>KeyFrame</code>: The last frame to
     *                  interpolate at.
     * @param nextFrame <code>KeyFrame</code>: The next frame to
     *                  interpolate at.
     * @param timestamp <code>double</code>: The timestamp to interpolate at.
     * @return <code>ScaleModifier</code>: The resulting scale modifier.
     */
    public static ScaleModifier interpolate(KeyFrame lastFrame, KeyFrame nextFrame, double timestamp) {

        ScaleFrame last = lastFrame == null ? null : lastFrame.getScale(KeyFrame.LAST);
        ScaleFrame next = nextFrame == null ? null : nextFrame.getScale(KeyFrame.NEXT);

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
