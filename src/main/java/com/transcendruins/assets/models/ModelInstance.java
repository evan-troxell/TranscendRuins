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

package com.transcendruins.assets.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.extra.BoneActorSet;
import com.transcendruins.assets.models.ModelAttributes.IndexedPolygon;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;

/**
 * <code>ModelInstance</code>: A class representing a generated model instance.
 */
public final class ModelInstance extends AssetInstance {

    private int textureWidth;

    public int getTextureWidth() {

        return textureWidth;
    }

    private int textureHeight;

    public int getTextureHeight() {

        return textureHeight;
    }

    private Vector origin;

    private ImmutableList<ModelAttributes.WeightedVertex> vertices;

    private ImmutableList<ModelAttributes.IndexedPolygon> polygons;

    private ImmutableMap<String, ModelAttributes.Bone> bones;

    public ModelAttributes.Bone getBone(String bone) {

        return bones.get(bone);
    }

    /**
     * Creates a new instance of the <code>ModelInstance</code> class.
     * 
     * @param context <code>ModelContext</code>: The context used to generate this
     *                <code>ModelInstance</code> instance.
     */
    public ModelInstance(ModelContext context) {

        super(context);
        setParent(context.getParent());
    }

    /**
     * Retrieves the vertices of this <code>ModelInstance</code> instance.
     * 
     * @param boneActors <code>BoneActorSet</code>: The bone actors used
     *                   to model the bones of this <code>ModelInstance</code>
     *                   instance.
     * @return <code>ArrayList&lt;Vector&gt;</code>: The retrieved vertices of this
     *         <code>ModelSchema</code> instance.
     */
    public ArrayList<Vector> getVertices(BoneActorSet boneActors) {

        HashMap<Integer, HashMap<Vector, Double>> boneWeights = new HashMap<>();
        for (Map.Entry<String, ModelAttributes.Bone> bone : bones.entrySet()) {

            for (Map.Entry<Integer, HashMap<Vector, Double>> indexEntry : bone.getValue().getVertexWeights(boneActors)
                    .entrySet()) {

                HashMap<Vector, Double> indexVertices = boneWeights.computeIfAbsent(indexEntry.getKey(),
                        _ -> new HashMap<>());
                indexVertices.putAll(indexEntry.getValue());
            }
        }

        ArrayList<Vector> verticesModified = new ArrayList<>(vertices.size());

        for (int i = 0; i < vertices.size(); i++) {

            verticesModified
                    .add(vertices.get(i) // Retrieve the raw vertex.
                            .getWeightedVertex(boneWeights.get(i)) // Apply the vertex weights.
                            .subtract(origin) // Adjust so the pivot point of the model is the origin.
                    );
        }

        return verticesModified;
    }

    /**
     * Retrieves the polygons of this <code>ModelInstance</code> instance and
     * translates it to a specified position and orientation.
     * 
     * @param boneActors <code>BoneActorSet</code>: The bone actors to animate this
     *                   <code>ModelInstance</code> with.
     * @param position   <code>Vector</code>: The position to center the vertices
     *                   at.
     * @param rotation   <code>Quaternion</code>: The rotation to apply to the
     *                   vertices.
     * @return <code>HashMap&lt;Triangle, Triangle&gt;</code>: The generated
     *         polygons.
     */
    public HashMap<Triangle, Triangle> getPolygons(BoneActorSet boneActors, Vector position, Quaternion rotation) {

        return getPolygons(getVertices(boneActors), position, rotation);
    }

    /**
     * Retrieves the polygons of this <code>ModelInstance</code> instance, adjusts
     * it to the position of a parent asset (like a glove being adjusted to the
     * position of a person's hand), and translates it to a specified position and
     * orientation.
     * 
     * @param boneActors       <code>BoneActorSet</code>: The bone actors to animate
     *                         this <code>ModelInstance</code> with.
     * @param parentBoneActors <code>BoneActorSet</code>: The bone actors of the
     *                         parent animation. These will <b>NOT</b>
     * @param bone             <code>ModelAttributes.Bone</code>: The bone at
     *                         which to position this <code>ModelInstance</code>
     *                         instance.
     * @param position         <code>Vector</code>: The position to center the
     *                         vertices at. This should effectively be the position
     *                         of the parent which this <code>ModelInstance</code>
     *                         is being rendered off of. If this model is a glove
     *                         and the parent bone is the hand of a person, then the
     *                         glove will already be adjusted onto the person, so
     *                         the position should be the spacial position of the
     *                         person.
     * @param rotation         <code>Quaternion</code>: The rotation to apply to the
     *                         polygons. As with the position, this should be the
     *                         rotation of the parent (as this model will already
     *                         have been adjusted to the position of the parent
     *                         model).
     * @return <code>HashMap&lt;Triangle, Triangle&gt;</code>: The generated
     *         polygons.
     */
    public HashMap<Triangle, Triangle> getPolygons(BoneActorSet boneActors, BoneActorSet parentBoneActors,
            ModelAttributes.Bone parent, Vector position, Quaternion rotation) {

        if (parent != null) {

            ArrayList<Vector> verticesModified = new ArrayList<>(vertices.size());

            Vector pivotPoint = parent.getPivotPoint();

            for (Vector vertex : getVertices(boneActors)) {

                verticesModified.add(vertex.add(pivotPoint));
            }

            for (String boneActor : parent.getBonePathway()) {

                if (!bones.containsKey(boneActor)) {

                    continue;
                }

                verticesModified = parentBoneActors.apply(verticesModified, boneActor,
                        bones.get(boneActor).getPivotPoint());
            }

            return getPolygons(verticesModified, position, rotation);
        }

        return getPolygons(getVertices(boneActors), position, rotation);
    }

    /**
     * Retrieves the polygons of this <code>ModelInstance</code> from provided
     * vertices.
     * 
     * @param vertices <code>List&lt;Vector&gt;</code>: The vertices to model using.
     * @param position <code>Vector</code>: The position to center the polygons
     *                 at.
     * @param rotation <code>Quaternion</code>: The rotation to apply to the
     *                 polygons.
     * @return <code>HashMap&lt;Triangle, Triangle&gt;</code>: The generated map of
     *         polygons and UVs.
     */
    private HashMap<Triangle, Triangle> getPolygons(List<Vector> vertices, Vector position, Quaternion rotation) {

        ArrayList<Vector> verticesModified = new ArrayList<>(vertices.size());

        for (Vector vertex : vertices) {

            verticesModified.add(vertex.rotate(rotation) // Rotate the model to the input rotation.
                    .add(position) // Adjust so the model is centered at the input position.
            );
        }

        HashMap<Triangle, Triangle> finalizedPolygons = new HashMap<>(polygons.size());
        for (IndexedPolygon polygon : polygons) {

            finalizedPolygons.put(polygon.getPolygon(verticesModified), polygon.getUvs());
        }

        return finalizedPolygons;
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        ModelAttributes attributes = (ModelAttributes) attributeSet;

        // Updates the textureWidth field.
        textureWidth = calculateAttribute(attributes.getTextureWidth(), textureWidth);
        setProperty("textureWidth", textureWidth);

        // Updates the textureHeight field.
        textureHeight = calculateAttribute(attributes.getTextureHeight(), textureHeight);
        setProperty("textureHeight", textureHeight);

        // Updates the origin field.
        origin = calculateAttribute(attributes.getOrigin(), origin);
        setProperty("origin", new ImmutableList<>(origin.getX(), origin.getY(), origin.getZ()));

        // Updates the bones field.
        bones = calculateAttribute(attributes.getBones(), bones);
        setProperty("bones", bones);

        polygons = calculateAttribute(attributes.getPolygons(), polygons);
        vertices = calculateAttribute(attributes.getVertices(), vertices);
    }

    @Override
    protected void onUpdate(double time) {

    }
}
