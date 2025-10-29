/* Copyright 2025 Evan Troxell
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
import com.transcendruins.geometry.Matrix;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

public final class ScaleFrame extends ScaleModifier {

    private final Interpolation interpolation;

    private final double animationLength;

    public ScaleFrame(TracedDictionary json, double timestamp, double animationLength) throws LoggedException {

        super(json);
        interpolation = Interpolation.createInterpolation(json, "interpolation", timestamp);
        this.animationLength = animationLength;
    }

    public ScaleModifier interpolate(ScaleFrame next, double t) {

        double inter = Interpolation.getInter(interpolation, next.interpolation, t, animationLength);

        com.transcendruins.geometry.Vector scale = Interpolation.lerp(getScale(), next.getScale(), inter);

        double rotationAngle = Interpolation.lerp(getRotation().getAngle(), next.getRotation().getAngle(), inter);
        com.transcendruins.geometry.Vector rotationAxis = Interpolation.slerp(getRotation().getAxis(),
                next.getRotation().getAxis(), inter);

        return new ScaleModifier(scale, new RotationModifier(rotationAngle, rotationAxis));
    }

    /**
     * Interpolates between two <code>KeyFrame</code> instances.
     * 
     * @param lastFrame <code>KeyFrame</code>: The last frame to interpolate at.
     * @param nextFrame <code>KeyFrame</code>: The next frame to interpolate at.
     * @param timestamp <code>double</code>: The timestamp to interpolate at.
     * @return <code>Matrix</code>: The resulting scale modifier.
     */
    public static Matrix interpolate(KeyFrame lastFrame, KeyFrame nextFrame, double timestamp) {

        ScaleFrame last = lastFrame == null ? null : lastFrame.getScale(KeyFrame.LAST);
        ScaleFrame next = nextFrame == null ? null : nextFrame.getScale(KeyFrame.NEXT);

        if (last != null && next != null) {

            return last.interpolate(next, timestamp).getTransform();
        }

        if (next != null) {

            return next.getTransform();
        }

        if (last != null) {

            return last.getTransform();
        }

        return Matrix.IDENTITY_3X3;
    }
}
