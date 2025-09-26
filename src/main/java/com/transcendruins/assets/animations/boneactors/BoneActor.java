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

package com.transcendruins.assets.animations.boneactors;

import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Vector;

/**
 * <code>BoneActor</code>: A class representing the transformations to apply to
 * a set of polygons in the animation process.
 */
public final class BoneActor {

    /**
     * <code>BoneActor</code>: A bone actor which performs no translation, rotation,
     * or scaling on a bone.
     */
    public static final BoneActor DEFAULT = new BoneActor(Vector.IDENTITY_VECTOR, Quaternion.IDENTITY_QUATERNION,
            Matrix.IDENTITY_3X3);

    /**
     * <code>Vector</code>: The vector to translate bones by.
     */
    private final Vector position;

    /**
     * <code>Quaternion</code>: The quaternion to rotate bones by.
     */
    private final Quaternion rotation;

    /**
     * <code>Matrix</code>: The matrix to scale bones by.
     */
    private final Matrix scale;

    /**
     * Creates a new instance of the <code>BoneActor</code> class.
     * 
     * @param position <code>Vector</code>: The vector to translate bones by.
     * @param rotation <code>Quaternion</code>: The quaternion to rotate bones by.
     * @param scale    <code>Matrix</code>: The matrix to scale bones by.
     */
    public BoneActor(Vector position, Quaternion rotation, Matrix scale) {

        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    /**
     * Extends this <code>BoneActor</code> instance by another.
     * 
     * @param boneActor <code>BoneActor</code>: The bone actor to adjust the
     *                  translation, rotation, and scaling by.
     * 
     * @return <code>BoneActor</code>: The generated bone actor.
     */
    public final BoneActor extend(BoneActor boneActor) {

        if (isDefault()) {

            return boneActor;
        }

        return new BoneActor(boneActor.position.add(position), boneActor.rotation.multiply(rotation),
                boneActor.scale.multiply(scale));
    }

    /**
     * Applies the transformations of this <code>BoneActor</code> instance to a
     * vector about a specific pivot point.
     * 
     * @param vector     <code>Vector</code>: The vector to apply the
     *                   transformations to.
     * @param pivotPoint <code>Vector</code>: The pivot point to operate about.
     * 
     * @return <code>Vector</code>: The resulting vector.
     */
    public final Vector transform(Vector vector, Vector pivotPoint) {

        if (isDefault()) {

            return vector;
        }

        return vector.subtract(pivotPoint).multiply(scale).rotate(rotation).add(position).add(pivotPoint);
    }

    public final boolean isDefault() {

        return position == Vector.IDENTITY_VECTOR && rotation == Quaternion.IDENTITY_QUATERNION
                && scale == Matrix.IDENTITY_3X3;
    }
}
