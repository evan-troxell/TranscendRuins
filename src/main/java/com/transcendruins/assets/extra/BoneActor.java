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

package com.transcendruins.assets.extra;

import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Vector;

public final class BoneActor {

    private final Vector position;

    private final Quaternion rotation;

    private final Matrix scale;

    public BoneActor() {

        position = Vector.IDENTITY_VECTOR;
        rotation = Quaternion.IDENTITY_QUATERNION;
        scale = Matrix.IDENTITY_3X3;
    }

    public BoneActor(Vector position, Quaternion rotation, Matrix scale) {

        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public final BoneActor extend(BoneActor boneActor) {

        if (isDefault()) {

            return boneActor;
        }

        return new BoneActor(
                boneActor.position.add(position),
                boneActor.rotation.multiply(rotation),
                boneActor.scale.multiply(scale));
    }

    public final Vector transform(Vector vector, Vector pivotPoint) {

        if (isDefault()) {

            return vector;
        }

        return vector.subtract(pivotPoint)
                .multiply(scale)
                .rotate(rotation)
                .add(position)
                .add(pivotPoint);
    }

    public final boolean isDefault() {

        return position == Vector.IDENTITY_VECTOR
                && rotation == Quaternion.IDENTITY_QUATERNION
                && scale == Matrix.IDENTITY_3X3;
    }
}
