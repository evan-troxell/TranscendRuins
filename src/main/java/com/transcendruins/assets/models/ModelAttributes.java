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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.math.Vector2f;
import com.transcendruins.assets.animations.boneactors.BoneActor;
import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.geometry.Quaternion;
import com.transcendruins.geometry.Vector;
import com.transcendruins.rendering.renderbuffer.LightData;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.KeyNameException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>ModelAttributes</code>: A class which represents the attributes of a
 * <code>ModelSchema</code> instance.
 */
public final class ModelAttributes extends AssetAttributes {

    /**
     * <code>Integer</code>: The width of the texture of this
     * <code>ModelAttributes</code> instance.
     */
    private final Integer textureWidth;

    /**
     * Retrieves the width of the texture of this <code>ModelAttributes</code>
     * instance.
     * 
     * @return <code>Integer</code>: The <code>textureWidth</code> field of this
     *         <code>ModelAttributes</code> instance.
     */
    public final Integer getTextureWidth() {

        return textureWidth;
    }

    /**
     * <code>Integer</code>: The height of the texture of this
     * <code>ModelAttributes</code> instance.
     */
    private final Integer textureHeight;

    /**
     * Retrieves the height of the texture of this <code>ModelAttributes</code>
     * instance.
     * 
     * @return <code>Integer</code>: The <code>textureHeight</code> field of this
     *         <code>ModelAttributes</code> instance.
     */
    public final Integer getTextureHeight() {

        return textureHeight;
    }

    /**
     * <code>ImmutableList&lt;WeightedVertex&gt;</code>: The vertices of this
     * <code>ModelAttributes</code> instance. This <code>ImmutableList</code> only
     * represents the initial positions of the vertices before animation
     * transformations have been applied.
     */
    private final ImmutableList<WeightedVertex> vertices;

    /**
     * Retrieves the vertices of this <code>ModelAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;WeightedVertex&gt;</code>: The
     *         <code>vertices</code> field of this <code>ModelAttributes</code>
     *         instance.
     */
    public final ImmutableList<WeightedVertex> getVertices() {

        return vertices;
    }

    private final ImmutableList<Vector2f> uvs;

    public final ImmutableList<Vector2f> getUvs() {

        return uvs;
    }

    /**
     * <code>ImmutableList&lt;Integer&gt;</code>: The polygons of this
     * <code>ModelAttributes</code> instance.
     */
    private final ImmutableList<Integer> polygons;

    /**
     * Retrieves the polygons of this <code>ModelAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;Integer&gt;</code>: The <code>polygons</code>
     *         field of this <code>ModelAttributes</code> instance.
     */
    public ImmutableList<Integer> getPolygons() {

        return polygons;
    }

    private final ImmutableList<LightData> lights;

    public final ImmutableList<LightData> getLights() {

        return lights;
    }

    private final Bone root;

    public final Bone getRoot() {

        return root;
    }

    /**
     * <code>ImmutableSet&lt;String&gt;</code>: The bones of this
     * <code>ModelAttributes</code> instance.
     */
    private final ImmutableMap<String, Bone> allBones;

    /**
     * Retrieves the bones of this <code>ModelAttributes</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, Bone&gt;</code>: The
     *         <code>pivotPoints</code> field of this <code>ModelAttributes</code>
     *         instance.
     */
    public ImmutableMap<String, Bone> getAllBones() {

        return allBones;
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The bones which should be hidden
     * when rendering this <code>ModelAttributes</code> instance.
     */
    private final ImmutableList<String> disableByBone;

    /**
     * Retrieves the bones which should be hidden when rendering this
     * <code>ModelAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The
     *         <code>disableByBone</code> field of this <code>ModelAttributes</code>
     *         instance.
     */
    public ImmutableList<String> getDisableByBone() {

        return disableByBone;
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The bones which should be unhidden
     * when rendering this <code>ModelAttributes</code> instance.
     */
    private final ImmutableList<String> enableByBone;

    /**
     * Retrieves the bones which should be unhidden when rendering this
     * <code>ModelAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The
     *         <code>enableByBone</code> field of this <code>ModelAttributes</code>
     *         instance.
     */
    public ImmutableList<String> getEnableByBone() {

        return enableByBone;
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The bone tags which should be
     * hidden when rendering this <code>ModelAttributes</code> instance.
     */
    private final ImmutableList<String> disableByTag;

    /**
     * Retrieves the bone tags which should be hidden when rendering this
     * <code>ModelAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The
     *         <code>disableByTag</code> field of this <code>ModelAttributes</code>
     *         instance.
     */
    public ImmutableList<String> getDisableByTag() {

        return disableByTag;
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The bone tags which should be
     * unhidden when rendering this <code>ModelAttributes</code> instance.
     */
    private final ImmutableList<String> enableByTag;

    /**
     * Retrieves the bone tags which should be unhidden when rendering this
     * <code>ModelAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The
     *         <code>enableByTag</code> field of this <code>ModelAttributes</code>
     *         instance.
     */
    public ImmutableList<String> getEnableByTag() {

        return enableByTag;
    }

    /**
     * Compiles this <code>ModelAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>ModelAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>json</code> perameter.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>ModelAttributes</code> instance is the base attribute set
     *               of a <code>ModelAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>ModelAttributes</code> instance.
     */
    public ModelAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        // The model should only be defined once.
        if (isBase) {

            TracedEntry<TracedDictionary> modelEntry = json.getAsDict("model", false);
            TracedDictionary modelJson = modelEntry.getValue();

            TracedEntry<Integer> textureWidthEntry = modelJson.getAsInteger("textureWidth", false, null,
                    num -> num > 0);
            textureWidth = textureWidthEntry.getValue();

            TracedEntry<Integer> textureHeightEntry = modelJson.getAsInteger("textureHeight", false, null,
                    num -> num > 0);
            textureHeight = textureHeightEntry.getValue();

            // Create the vertices which the polygons of this model will be created from.
            TracedEntry<TracedArray> verticesEntry = modelJson.getAsArray("vertices", false);
            TracedArray verticesJson = verticesEntry.getValue();
            if (verticesJson.isEmpty()) {

                throw new CollectionSizeException(verticesEntry, verticesJson);
            }

            ArrayList<WeightedVertex> verticesList = new ArrayList<>();
            for (int i : verticesJson) {

                TracedEntry<Vector> vertexIndexEntry = verticesJson.getAsVector(i, false, 3);
                verticesList.add(new WeightedVertex(vertexIndexEntry.getValue()));
            }

            vertices = new ImmutableList<>(verticesList);

            TracedEntry<TracedArray> uvsEntry = modelJson.getAsArray("uvs", false);
            TracedArray uvsJson = uvsEntry.getValue();
            if (uvsJson.size() != vertices.size()) {

                throw new CollectionSizeException(uvsEntry, uvsJson);
            }

            ArrayList<Vector2f> uvsList = new ArrayList<>();
            for (int i : uvsJson) {

                TracedEntry<TracedArray> uvEntry = uvsJson.getAsArray(i, false);
                TracedArray uvJson = uvEntry.getValue();

                if (uvJson.size() != 2) {

                    throw new CollectionSizeException(uvEntry, uvJson);
                }

                TracedEntry<Float> uEntry = uvJson.getAsFloat(0, false, null, num -> 0 <= num && num <= textureWidth);
                float u = uEntry.getValue() / textureWidth;

                TracedEntry<Float> vEntry = uvJson.getAsFloat(1, false, null, num -> 0 <= num && num <= textureHeight);
                float v = vEntry.getValue() / textureHeight;

                uvsList.add(new Vector2f(u, v));
            }

            uvs = new ImmutableList<>(uvsList);

            // Create the polygons to be rendered and assign them with their individual
            // vertices.
            TracedEntry<TracedArray> polygonsEntry = modelJson.getAsArray("polygons", false);
            TracedArray polygonsJson = polygonsEntry.getValue();

            ArrayList<Integer> polygonsList = new ArrayList<>();

            for (int polygonIndex : polygonsJson) {

                TracedEntry<TracedArray> polygonEntry = polygonsJson.getAsArray(polygonIndex, false);
                TracedArray polygonJson = polygonEntry.getValue();

                if (polygonJson.size() != 3) {

                    throw new CollectionSizeException(polygonEntry, polygonJson);
                }

                // For each vertex of the triangle, retrieve the appropriate vertex from the
                // vertices perameter.
                for (int i = 0; i < polygonJson.size(); i++) {

                    TracedEntry<Integer> vertexIndexEntry = polygonJson.getAsInteger(i, false, null,
                            num -> 0 <= num && num < vertices.size());
                    polygonsList.add(vertexIndexEntry.getValue());
                }
            }

            polygons = new ImmutableList<>(polygonsList);

            ArrayList<LightData> lightsArray = new ArrayList<>();
            TracedEntry<TracedArray> lightsEntry = modelJson.getAsArray("lights", true);
            if (lightsEntry.containsValue()) {

                TracedArray lightsJson = lightsEntry.getValue();
                for (int i : lightsJson) {

                    lightsArray.add(LightData.createLightData(lightsJson, i, vertices.size()));
                }
            }
            lights = new ImmutableList<>(lightsArray);

            HashMap<String, Bone> allBonesMap = new HashMap<>();
            root = Bone.createBone(modelJson, allBonesMap, vertices.size());

            allBones = new ImmutableMap<>(allBonesMap);
        } else {

            textureWidth = null;
            textureHeight = null;

            vertices = null;
            uvs = null;
            polygons = null;
            lights = null;

            root = null;
            allBones = null;
        }

        TracedEntry<TracedArray> disableByBoneEntry = json.getAsArray("disableByBone", true);
        if (disableByBoneEntry.containsValue()) {

            ArrayList<String> disableByBoneList = new ArrayList<>();

            TracedArray disableByBoneJson = disableByBoneEntry.getValue();
            for (int i : disableByBoneJson) {

                TracedEntry<String> hideBoneEntry = disableByBoneJson.getAsString(i, false, null);
                String hideBone = hideBoneEntry.getValue();

                disableByBoneList.add(hideBone);

            }

            disableByBone = new ImmutableList<>(disableByBoneList);
        } else {

            disableByBone = null;
        }

        TracedEntry<TracedArray> enableByBoneEntry = json.getAsArray("enableByBone", true);
        if (enableByBoneEntry.containsValue()) {

            ArrayList<String> enableByBoneList = new ArrayList<>();

            TracedArray enableByBoneJson = enableByBoneEntry.getValue();
            for (int i : enableByBoneJson) {

                TracedEntry<String> showBoneEntry = enableByBoneJson.getAsString(i, false, null);
                String showBone = showBoneEntry.getValue();

                enableByBoneList.add(showBone);

            }

            enableByBone = new ImmutableList<>(enableByBoneList);
        } else {

            enableByBone = null;
        }

        TracedEntry<TracedArray> disableByTagEntry = json.getAsArray("disableByTag", true);
        if (disableByTagEntry.containsValue()) {

            ArrayList<String> disableByTagList = new ArrayList<>();

            TracedArray disableByTagJson = disableByTagEntry.getValue();
            for (int i : disableByTagJson) {

                TracedEntry<String> hideTagEntry = disableByTagJson.getAsString(i, false, null);
                String hideTag = hideTagEntry.getValue();

                disableByTagList.add(hideTag);
            }

            disableByTag = new ImmutableList<>(disableByTagList);
        } else {

            disableByTag = null;
        }

        TracedEntry<TracedArray> enableByTagEntry = json.getAsArray("enableByTag", true);
        if (enableByTagEntry.containsValue()) {

            ArrayList<String> enableByTagList = new ArrayList<>();

            TracedArray enableByTagJson = enableByTagEntry.getValue();
            for (int i : enableByTagJson) {

                TracedEntry<String> showTagEntry = enableByTagJson.getAsString(i, false, null);
                String showTag = showTagEntry.getValue();

                enableByTagList.add(showTag);
            }

            enableByTag = new ImmutableList<>(enableByTagList);
        } else {

            enableByTag = null;
        }
    }

    /**
     * <code>WeightedVertex</code>: A class representing a vertex of a parent
     * <code>ModelAttributes</code> instance which can be modified using a weighting
     * system.
     */
    public static final class WeightedVertex {

        /**
         * <code>Vector</code>: The initial position of this <code>WeightedVertex</code>
         * instance.
         */
        private final Vector baseVertex;

        /**
         * Creates a new instance of the <code>WeightedVertex</code> class.
         * 
         * @param baseVertex <code>Vector</code>: The initial position of this
         *                   <code>WeightedVertex</code> instance, and the vertex which
         *                   will be returned if no weights are added.
         */
        private WeightedVertex(Vector baseVertex) {

            this.baseVertex = baseVertex;
        }

        /**
         * Retrieves the weighted vertex of this <code>WeightedVertex</code> instance.
         * 
         * @param weights <code>Map&lt;String, Vector&gt;</code>: The map of bones to
         *                their respective vertex which should be applied.
         * @return <code>Vector</code>: The generated vertex.
         */
        public final Vector getWeightedVertex(Map<Vector, Double> vertices) {

            Vector returnVertex = Vector.IDENTITY_VECTOR;
            double weightSum = 0.0;

            for (Map.Entry<Vector, Double> vertexEntry : vertices.entrySet()) {

                returnVertex = returnVertex.add(vertexEntry.getKey().multiply(vertexEntry.getValue()));

                weightSum += vertexEntry.getValue();
            }

            if (weightSum == 0) {

                return baseVertex;
            }

            return returnVertex.multiply(1.0 / weightSum);
        }

        public static final List<Vector> getWeightedVertices(List<WeightedVertex> weightedVertices,
                Map<Integer, Map<Vector, Double>> vertices, Vector position, Quaternion rotation, Vector pivotPoint) {

            int verticesCount = weightedVertices.size();
            ArrayList<Vector> newVertices = new ArrayList<>(verticesCount);

            for (int i = 0; i < verticesCount; i++) {

                WeightedVertex weighted = weightedVertices.get(i);
                Vector v = (vertices.containsKey(i)) ? weighted.getWeightedVertex(vertices.get(i))
                        : weighted.baseVertex;
                newVertices.add(v.subtract(pivotPoint).rotate(rotation).add(position));
            }

            return newVertices;
        }
    }

    /**
     * <code>Bone</code>: A class representing a polygon layout of a
     * <code>ModelAttributes</code> instance.
     */
    public static final record Bone(ImmutableList<String> tags, ImmutableMap<Integer, Double> vertexWeights,
            Vector pivotPoint, ImmutableMap<String, Bone> bones) {

        /**
         * <code>String</code>: The regular expression used to ensure all vertex indices
         * are of the expected pattern.
         */
        private static final String INDEX_PATTERN = "[+]?\\d+";

        /**
         * Creates a new instance of the <code>Bone</code> class.
         * 
         * @param modelJson <code>TracedDictionary</code>: The JSON from which to create
         *                  this <code>Bone</code> instance.
         * @throws LoggedException Thrown to represent any exception raised which
         *                         creating the bone.
         */
        public static Bone createBone(TracedDictionary modelJson, HashMap<String, Bone> allBonesMap, int vertices)
                throws LoggedException {

            ImmutableList<String> tags;

            TracedEntry<TracedArray> tagsEntry = modelJson.getAsArray("tags", true);
            if (tagsEntry.containsValue()) {

                ArrayList<String> tagsList = new ArrayList<>();

                TracedArray tagsJson = tagsEntry.getValue();
                for (int i : tagsJson) {

                    TracedEntry<String> tagEntry = tagsJson.getAsString(i, false, null);
                    String tag = tagEntry.getValue();

                    tagsList.add(tag);
                }

                tags = new ImmutableList<>(tagsList);
            } else {

                tags = new ImmutableList<>();
            }

            HashMap<Integer, Double> vertexWeightsMap = new HashMap<>();

            TracedEntry<TracedDictionary> vertexWeightsEntry = modelJson.getAsDict("vertexWeights", true);
            if (vertexWeightsEntry.containsValue()) {

                TracedDictionary vertexWeightsJson = vertexWeightsEntry.getValue();

                for (String vertexKey : vertexWeightsJson) {

                    if (!vertexKey.matches(INDEX_PATTERN)) {

                        throw new KeyNameException(vertexWeightsJson, vertexKey);
                    }

                    int vertexIndex = Integer.parseInt(vertexKey);

                    if (vertexWeightsMap.containsKey(vertexIndex) || vertexIndex >= vertices || vertexIndex < 0) {

                        throw new KeyNameException(vertexWeightsJson, vertexKey);
                    }

                    TracedEntry<Double> chanceEntry = vertexWeightsJson.getAsDouble(vertexKey, false, null,
                            num -> num > 0);
                    double weight = chanceEntry.getValue();

                    vertexWeightsMap.put(vertexIndex, weight);
                }
            }

            ImmutableMap<Integer, Double> vertexWeights = new ImmutableMap<>(vertexWeightsMap);

            TracedEntry<Vector> pivotPointEntry = modelJson.getAsVector("pivotPoint", false, 3);
            Vector pivotPoint = pivotPointEntry.getValue();

            HashMap<String, Bone> bonesMap = new HashMap<>();
            TracedEntry<TracedDictionary> bonesEntry = modelJson.getAsDict("bones", true);
            if (bonesEntry.containsValue()) {

                TracedDictionary bonesJson = bonesEntry.getValue();
                for (String boneKey : bonesJson) {

                    TracedEntry<TracedDictionary> boneEntry = bonesJson.getAsDict(boneKey, false);
                    TracedDictionary boneJson = boneEntry.getValue();

                    Bone bone = createBone(boneJson, allBonesMap, vertices);

                    // If any other bones in the model have the same key, raise an exception.
                    if (allBonesMap.containsKey(boneKey)) {

                        throw new KeyNameException(bonesJson, boneKey);
                    }

                    allBonesMap.put(boneKey, bone);
                    bonesMap.put(boneKey, bone);
                }
            }

            ImmutableMap<String, Bone> bones = new ImmutableMap<>(bonesMap);

            return new Bone(tags, vertexWeights, pivotPoint, bones);
        }

        @FunctionalInterface
        public interface BoneConsumer {

            public void accept(String bone, BoneActor boneActor, Vector pivotPoint);
        }

        /**
         * Retrieves the vertex weights of this <code>Bone</code> instance.
         * 
         * @param boneActors <code>List&lt;BoneActorSet&gt;</code>: The bone actors used
         *                   to model the bones of this <code>Bone</code> instance.
         * @return <code>Map&lt;Integer, Map&lt;Vector, Double&gt;&gt;</code>: The
         *         retrieved vertex weights of this <code>Bone</code> instance.
         */
        public final Map<Integer, Map<Vector, Double>> computeVertexWeights(BoneActorSet boneActors,
                BoneActor boneActor, ImmutableList<WeightedVertex> vertices, BoneConsumer operator) {

            Map<Integer, Map<Vector, Double>> boneVertices = new HashMap<>();

            for (Map.Entry<Integer, Double> vertexWeightEntry : vertexWeights.entrySet()) {

                HashMap<Vector, Double> vertexWeight = new HashMap<>();

                int index = vertexWeightEntry.getKey();
                vertexWeight.put(vertices.get(index).baseVertex, vertexWeightEntry.getValue());

                boneVertices.put(index, vertexWeight);
            }

            for (Map.Entry<String, Bone> boneEntry : bones.entrySet()) {

                String boneName = boneEntry.getKey();
                if (operator != null) {

                    operator.accept(boneName, boneActor, pivotPoint);
                }

                Bone bone = boneEntry.getValue();

                for (Map.Entry<Integer, Map<Vector, Double>> boneVerticesEntry : bone
                        .computeVertexWeights(boneActors, boneActors.getBoneActor(boneName), vertices, operator)
                        .entrySet()) {

                    int index = boneVerticesEntry.getKey();
                    boneVertices.computeIfAbsent(index, _ -> new HashMap<>()).putAll(boneVerticesEntry.getValue());
                }
            }

            if (boneActor == null || boneActor == BoneActor.DEFAULT) {

                return boneVertices;
            }

            return boneActor.apply(boneVertices, pivotPoint);
        }
    }
}
