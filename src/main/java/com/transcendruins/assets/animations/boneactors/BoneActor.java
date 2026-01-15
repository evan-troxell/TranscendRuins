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

package com.transcendruins.assets.animations.boneactors;

import java.util.HashMap;
import java.util.Map;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * <code>BoneActor</code>: A class representing the transformations to apply to
 * a set of polygons in the animation process.
 */
public final class BoneActor {

    /**
     * <code>BoneActor</code>: A bone actor which performs no translation, rotation,
     * or scaling on a bone.
     */
    public static final BoneActor DEFAULT = new BoneActor(null, null, null);

    /**
     * <code>Vector3f</code>: The vector to translate bones by.
     */
    private final Vector3f position;

    /**
     * <code>Quaternion</code>: The quaternion to rotate bones by.
     */
    private final Quaternion rotation;

    /**
     * <code>Matrix3f</code>: The matrix to scale bones by.
     */
    private final Matrix3f scale;

    /**
     * Creates a new instance of the <code>BoneActor</code> class.
     * 
     * @param position <code>Vector3f</code>: The vector to translate bones by.
     * @param rotation <code>Quaternion</code>: The quaternion to rotate bones by.
     * @param scale    <code>Matrix3f</code>: The matrix to scale bones by.
     */
    public BoneActor(Vector3f position, Quaternion rotation, Matrix3f scale) {

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

        if (this == DEFAULT) {

            return boneActor;
        }

        return new BoneActor(
                boneActor.position == null ? position
                        : position == null ? boneActor.position : boneActor.position.add(position),
                boneActor.rotation == null ? rotation
                        : rotation == null ? boneActor.rotation : boneActor.rotation.mult(rotation),
                boneActor.scale == null ? scale : scale == null ? boneActor.scale : boneActor.scale.mult(scale));
    }

    /**
     * Applies the transformations of this <code>BoneActor</code> instance to a
     * vector about a specific pivot point. This <b>will</b> mutate the input
     * vector.
     * 
     * @param vector     <code>Vector3f</code>: The vector to apply the
     *                   transformations to.
     * @param pivotPoint <code>Vector3f</code>: The pivot point to operate about.
     */
    public final void transform(Vector3f vector, Vector3f pivotPoint) {

        vector.subtractLocal(pivotPoint);
        if (scale != null) {

            scale.multLocal(vector);
        }

        if (rotation != null) {

            rotation.multLocal(vector);
        }

        if (position != null) {

            vector.addLocal(position);
        }
        vector.addLocal(pivotPoint);
    }

    /**
     * Applies this <code>BoneActor</code> instance to a set of vertices.
     * 
     * @param vertices   <code>HashMap&lt;Integer, HashMap&lt;Vector3f, Float&gt;&gt;</code>:
     *                   The vertices to apply this <code>BoneActor</code> instance
     *                   to.
     * @param pivotPoint <code>Vector3f</code>: The origin about which to perform
     *                   all relevant transformations.
     */
    public final void apply(HashMap<Integer, HashMap<Vector3f, Float>> vertices, Vector3f pivotPoint) {

        if (this == DEFAULT) {

            return;
        }

        // If the bone actor does not exist, a new set should still be created - create
        // a default bone actor used only to copy over vertices.

        for (Map.Entry<Integer, HashMap<Vector3f, Float>> boneWeightsEntry : vertices.entrySet()) {

            for (Vector3f vertex : boneWeightsEntry.getValue().keySet()) {

                transform(vertex, pivotPoint);
            }
        }
    }
}
