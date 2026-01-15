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

package com.transcendruins.assets.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.models.ModelAttributes.Bone;
import com.transcendruins.assets.models.ModelAttributes.WeightedVertex;
import com.transcendruins.rendering.renderbuffer.LightData;
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
     * <code>ImmutableList&lt;WeightedVertex&gt;</code>: The vertices of this
     * <code>ModelInstance</code> instance. These vertices are weighted by the bones
     * of the model, allowing for smooth animations and deformations.
     */
    private ImmutableList<WeightedVertex> vertices;

    public final ImmutableList<WeightedVertex> getVertices() {

        return vertices;
    }

    private Bone root;

    public final Bone getRoot() {

        return root;
    }

    /**
     * <code>ImmutableSet&lt;String&gt;</code>: The bones of this
     * <code>ModelInstance</code> instance.
     */
    private ImmutableMap<String, Bone> allBones;

    /**
     * Determines whether or not this <code>ModelInstance</code> instance has a bone
     * by the given name.
     * 
     * @param bone <code>String</code>: The name of the bone to check for.
     * @return <code>boolean</code>: Whether or not the <code>bones</code> field of
     *         this <code>ModelInstance</code> instance contains a bone with the
     *         given name.
     */
    public final boolean containsBone(String bone) {

        return allBones.containsKey(bone);
    }

    public final Vector3f getPivotPoint(String bone) {

        return allBones.get(bone).pivotPoint();
    }

    /**
     * <code>ImmutableList&lt;Vector2f&gt;</code>: The uvs of this
     * <code>ModelInstance</code> instance. Each UV vector corresponds to the
     * respective vertex in the model.
     */
    private ImmutableList<Vector2f> uvs;

    public final ImmutableList<Vector2f> getUvs() {

        return uvs;
    }

    /**
     * <code>ImmutableList&lt;Integer</code>: The polygons of this
     * <code>ModelInstance</code> instance. These polygons are used to render the
     * model, and are defined by the vertices of the model.
     */
    private ImmutableList<Integer> polygons;

    public final ImmutableList<Integer> getPolygons() {

        return polygons;
    }

    private ImmutableList<LightData> lights;

    public final ImmutableList<LightData> getLights() {

        return lights;
    }

    /**
     * <code>ArrayList&lt;String&gt;</code>: The list of bones to hide when
     * rendering this <code>ModelInstance</code> instance.
     */
    private final ArrayList<String> disableByBone = new ArrayList<>();

    /**
     * <code>ArrayList&lt;String&gt;</code>: The list of bone tags to hide when
     * rendering this <code>ModelInstance</code> instance.
     */
    private final ArrayList<String> disableByTag = new ArrayList<>();

    public final HashSet<Integer> getDisabledVertices() {

        HashSet<Integer> hideVertices = new HashSet<>();

        // Find each vertex which is hidden by a bone or tag.
        for (String boneName : disableByBone) {

            if (allBones.containsKey(boneName)) {

                Bone bone = allBones.get(boneName);
                Set<Integer> boneVertices = bone.vertexWeights().keySet();

                hideVertices.addAll(boneVertices);
            }
        }

        // Find each vertex which is hidden by a tag.
        for (Map.Entry<String, Bone> boneEntry : allBones.entrySet()) {

            if (disableByBone.contains(boneEntry.getKey())) {

                continue; // Skip hidden bones.
            }

            Bone bone = boneEntry.getValue();
            ImmutableList<String> tags = bone.tags();

            // If there any any hidden tags in this bone, hide the vertices.
            if (!tags.isEmpty() && !Collections.disjoint(disableByTag, tags)) {

                Set<Integer> boneVertices = bone.vertexWeights().keySet();
                hideVertices.addAll(boneVertices);
            }
        }

        return hideVertices;
    }

    /**
     * Creates a new instance of the <code>ModelInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>ModelInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public ModelInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        ModelContext context = (ModelContext) assetContext;
    }

    @Override
    public final void applyAttributes(Attributes attributeSet) {

        ModelAttributes attributes = (ModelAttributes) attributeSet;

        if (attributes.isBase()) {

            // Updates the textureWidth field.
            textureWidth = attributes.getTextureWidth();
            setProperty("textureWidth", textureWidth);

            // Updates the textureHeight field.
            textureHeight = attributes.getTextureHeight();
            setProperty("textureHeight", textureHeight);

            root = attributes.getRoot();

            allBones = attributes.getAllBones();

            vertices = attributes.getVertices();
            uvs = attributes.getUvs();
            polygons = attributes.getPolygons();

            lights = attributes.getLights();

            disableByBone.clear();
            disableByTag.clear();
        }

        computeAttribute(attributes.getDisableByBone(), disableByBone::addAll);
        computeAttribute(attributes.getEnableByBone(), disableByBone::removeAll);
        setProperty("disableByBone", disableByBone);

        computeAttribute(attributes.getDisableByTag(), disableByTag::addAll);
        computeAttribute(attributes.getEnableByTag(), disableByTag::removeAll);
        setProperty("disableByTag", disableByTag);
    }

    @Override
    protected final void onUpdate(double time) {

    }
}
