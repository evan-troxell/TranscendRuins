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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
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

    /**
     * <code>int</code>: The width of the texture used by this
     * <code>ModelInstance</code> instance.
     */
    private int textureWidth;

    /**
     * Retrieves the width of the texture used by this <code>ModelInstance</code>
     * instance.
     * 
     * @return <code>int</code>: The <code>textureWidth</code> field of this
     *         <code>ModelInstance</code> instance.
     */
    public int getTextureWidth() {

        return textureWidth;
    }

    /**
     * <code>int</code>: The height of the texture used by this
     * <code>ModelInstance</code> instance.
     */
    private int textureHeight;

    /**
     * Retrieves the height of the texture used by this <code>ModelInstance</code>
     * instance.
     * 
     * @return <code>int</code>: The <code>textureHeight</code> field of this
     *         <code>ModelInstance</code> instance.
     */
    public int getTextureHeight() {

        return textureHeight;
    }

    /**
     * <code>Vector</code>: The origin of this <code>ModelInstance</code> instance.
     * This is the pivot point of the model, which is used to center the model at
     * the origin when rendering.
     */
    private Vector origin;

    /**
     * <code>ImmutableList&lt;ModelAttributes.WeightedVertex&gt;</code>: The
     * vertices of this <code>ModelInstance</code> instance. These vertices are
     * weighted by the bones of the model, allowing for smooth animations and
     * deformations.
     */
    private ImmutableList<ModelAttributes.WeightedVertex> vertices;

    /**
     * <code>ImmutableList&lt;ModelAttributes.IndexedPolygon&gt;</code>: The
     * polygons of this <code>ModelInstance</code> instance. These polygons are used
     * to render the model, and are defined by the vertices of the model.
     */
    private ImmutableList<ModelAttributes.IndexedPolygon> polygons;

    /**
     * <code>ImmutableMap&lt;String, ModelAttributes.Bone&gt;</code>: The bones of
     * this <code>ModelInstance</code> instance. These bones are used to animate the
     * model, allowing for complex movements and deformations.
     */
    private ImmutableMap<String, ModelAttributes.Bone> bones;

    /**
     * Retrieves a bone from this <code>ModelInstance</code> instance by its name.
     * 
     * @param bone <code>String</code>: The name of the bone to retrieve.
     * @return <code>ModelAttributes.Bone</code>: The bone retrieved from this
     *         <code.ModelInstance</code> instance.
     */
    public ModelAttributes.Bone getBone(String bone) {

        return bones.get(bone);
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The list of bones to hide when
     * rendering this <code>ModelInstance</code> instance.
     */
    private ImmutableList<String> hideBones;

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The list of bone tags to hide when
     * rendering this <code>ModelInstance</code> instance.
     */
    private ImmutableList<String> hideTags;

    /**
     * Creates a new instance of the <code>ModelInstance</code> class.
     * 
     * @param context <code>ModelContext</code>: The context used to generate this
     *                <code>ModelInstance</code> instance.
     */
    public ModelInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        ModelContext context = (ModelContext) assetContext;
    }

    /**
     * Retrieves the vertices of this <code>ModelInstance</code> instance.
     * 
     * @param boneActors <code>BoneActorSet</code>: The bone actors used to model
     *                   the bones of this <code>ModelInstance</code> instance.
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

            verticesModified.add(vertices.get(i) // Retrieve the raw vertex.
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
     * @param bone             <code>ModelAttributes.Bone</code>: The bone at which
     *                         to position this <code>ModelInstance</code> instance.
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

                if (bones.containsKey(boneActor)) {

                    verticesModified = parentBoneActors.apply(verticesModified, boneActor,
                            bones.get(boneActor).getPivotPoint());
                }
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
     * @param position <code>Vector</code>: The position to center the polygons at.
     * @param rotation <code>Quaternion</code>: The rotation to apply to the
     *                 polygons.
     * @return <code>HashMap&lt;Triangle, Triangle&gt;</code>: The generated map of
     *         polygons and UVs.
     */
    private HashMap<Triangle, Triangle> getPolygons(List<Vector> vertices, Vector position, Quaternion rotation) {

        ArrayList<Vector> verticesModified = new ArrayList<>(vertices.size());

        HashSet<Integer> hideVertices = new HashSet<>();

        for (String boneName : hideBones) {

            if (bones.containsKey(boneName)) {

                ModelAttributes.Bone bone = bones.get(boneName);
                Set<Integer> boneVertices = bone.getVertexWeights().keySet();

                hideVertices.addAll(boneVertices);
            }
        }

        for (Map.Entry<String, ModelAttributes.Bone> boneEntry : bones.entrySet()) {

            if (hideBones.contains(boneEntry.getKey())) {

                continue; // Skip hidden bones.
            }

            ModelAttributes.Bone bone = boneEntry.getValue();

            ImmutableList<String> tags = bone.getTags();

            // If there any any hidden tags in this bone, hide the vertices.
            if (!tags.isEmpty() && !Collections.disjoint(hideTags, tags)) {

                Set<Integer> boneVertices = bone.getVertexWeights().keySet();
                hideVertices.addAll(boneVertices);
            }
        }

        for (Vector vertex : vertices) {

            verticesModified.add(vertex.rotate(rotation) // Rotate the model to the input rotation.
                    .add(position) // Adjust so the model is centered at the input position.
            );
        }

        HashMap<Triangle, Triangle> finalizedPolygons = new HashMap<>(polygons.size());
        for (IndexedPolygon polygon : polygons) {

            Triangle rendered = polygon.getPolygon(verticesModified, hideVertices);

            // Skip polygons which are hidden.
            if (rendered != null) {

                finalizedPolygons.put(rendered, polygon.getUvs());
            }
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

        hideBones = calculateAttribute(attributes.getHideBones(), hideBones, attributes, new ImmutableList<>());
        setProperty("hideBones", hideBones);

        hideTags = calculateAttribute(attributes.getHideTags(), hideTags, attributes, new ImmutableList<>());
        setProperty("hideTags", hideTags);
    }

    @Override
    protected void onUpdate(double time) {

    }
}
