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

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.transcendruins.assets.animations.AnimationAttributes.KeyFrame;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

public final class ScaleFrame extends ScaleModifier {

    private final Interpolation interpolation;

    private final float animationLength;

    public ScaleFrame(TracedDictionary json, float timestamp, float animationLength) throws LoggedException {

        super(json);
        interpolation = Interpolation.createInterpolation(json, "interpolation", timestamp);
        this.animationLength = animationLength;
    }

    public ScaleModifier interpolate(ScaleFrame next, float t) {

        float inter = Interpolation.getInter(interpolation, next.interpolation, t, animationLength);

        Vector3f scale = Interpolation.lerp(getScale(), next.getScale(), inter);

        float rotationAngle = Interpolation.lerp(getRotation().getAngle(), next.getRotation().getAngle(), inter);
        Vector3f rotationAxis = Interpolation.slerp(getRotation().getAxis(), next.getRotation().getAxis(), inter);

        return new ScaleModifier(scale, new RotationModifier(rotationAngle, rotationAxis));
    }

    /**
     * Interpolates between two <code>KeyFrame</code> instances.
     * 
     * @param lastFrame <code>KeyFrame</code>: The last frame to interpolate at.
     * @param nextFrame <code>KeyFrame</code>: The next frame to interpolate at.
     * @param timestamp <code>float</code>: The timestamp to interpolate at.
     * @return <code>Matrix3f</code>: The resulting scale modifier.
     */
    public static Matrix3f interpolate(KeyFrame lastFrame, KeyFrame nextFrame, float timestamp) {

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

        return Matrix3f.IDENTITY;
    }
}
