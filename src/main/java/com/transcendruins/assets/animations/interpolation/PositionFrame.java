/* Copyright 2026 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins.assets.animations.interpolation;

import com.transcendruins.assets.animations.AnimationAttributes.KeyFrame;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

public final class PositionFrame extends PositionModifier {

    private final Interpolation interpolation;

    private final double animationLength;

    public PositionFrame(TracedDictionary json, double timestamp, double animationLength) throws LoggedException {

        super(json);
        interpolation = Interpolation.createInterpolation(json, "interpolation", timestamp);
        this.animationLength = animationLength;
    }

    public PositionModifier interpolate(PositionFrame next, double t) {

        double inter = Interpolation.getInter(interpolation, next.interpolation, t, animationLength);

        Vector position = Interpolation.lerp(getPosition(), next.getPosition(), inter);

        double rotationAngle = Interpolation.lerp(getRotation().getAngle(), next.getRotation().getAngle(), inter);
        Vector rotationAxis = Interpolation.slerp(getRotation().getAxis(), next.getRotation().getAxis(), inter);

        return new PositionModifier(position, new RotationModifier(rotationAngle, rotationAxis));
    }

    /**
     * Interpolates between two <code>KeyFrame</code> instances.
     * 
     * @param lastFrame <code>KeyFrame</code>: The last frame to interpolate at.
     * @param nextFrame <code>KeyFrame</code>: The next frame to interpolate
     *                  between.
     * @param timestamp <code>double</code>: The timestamp to interpolate at.
     * @return <code>Vector</code>: The resulting position modifier.
     */
    public static Vector interpolate(KeyFrame lastFrame, KeyFrame nextFrame, double timestamp) {

        PositionFrame last = lastFrame == null ? null : lastFrame.getPosition(KeyFrame.LAST);
        PositionFrame next = nextFrame == null ? null : nextFrame.getPosition(KeyFrame.NEXT);

        if (last != null && next != null) {

            return last.interpolate(next, timestamp).getTransform();
        }

        if (next != null) {

            return next.getTransform();
        }

        if (last != null) {

            return last.getTransform();
        }

        return Vector.IDENTITY_VECTOR;
    }
}
