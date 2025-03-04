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

public final class PermutationFrame {

    private PositionFrame position;

    private RotationFrame rotation;

    private ScaleFrame scale;

    public PermutationFrame() {
    }

    public PermutationFrame(PositionFrame position, RotationFrame rotation, ScaleFrame scale) {

        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void applyPermutation(PermutationFrame permutation) {

        if (position == null) {

            position = permutation.position;
        }

        if (rotation == null) {

            rotation = permutation.rotation;
        }

        if (scale == null) {

            scale = permutation.scale;
        }
    }

    public boolean isComplete() {

        return position != null && rotation != null && scale != null;
    }

    public PositionFrame getPosition() {

        return position;
    }

    public RotationFrame getRotation() {

        return rotation;
    }

    public ScaleFrame getScale() {

        return scale;
    }
}
