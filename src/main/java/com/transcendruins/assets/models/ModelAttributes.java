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

import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.extra.BoneActorSet;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.KeyNameException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>ModelAttributes</code>: A class which represents the attributes
 * of a <code>ModelSchema</code> instance.
 */
public final class ModelAttributes extends AssetAttributes {

    /**
     * <code>Integer</code>: The width of the texture of this
     * <code>ModelAttributes</code>
     * instance.
     */
    private final Integer textureWidth;

    /**
     * Retrieves the width of the texture of this <code>ModelAttributes</code>
     * instance.
     * 
     * @return <code>Integer</code>: The <code>textureWidth</code> field of this
     *         <code>ModelAttributes</code> instance.
     */
    public Integer getTextureWidth() {

        return textureWidth;
    }

    /**
     * <code>Integer</code>: The height of the texture of this
     * <code>ModelAttributes</code>
     * instance.
     */
    private final Integer textureHeight;

    /**
     * Retrieves the height of the texture of this
     * <code>ModelAttributes</code> instance.
     * 
     * @return <code>Integer</code>: The <code>textureHeight</code> field of this
     *         <code>ModelAttributes</code> instance.
     */
    public Integer getTextureHeight() {

        return textureHeight;
    }

    /**
     * <code>ImmutableList&lt;WeightedVertex&gt;</code>: The vertices of this
     * <code>ModelAttributes</code> instance.
     * This <code>ImmutableList</code> only represents the initial positions of the
     * vertices before animation transformations have been applied.
     */
    private final ImmutableList<WeightedVertex> vertices;

    public ImmutableList<WeightedVertex> getVertices() {

        return vertices;
    }

    /**
     * <code>ImmutableList&lt;IndexedPolygon&gt;</code>: The polygons of this
     * <code>ModelAttributes</code> instance.
     */
    private final ImmutableList<IndexedPolygon> polygons;

    public ImmutableList<IndexedPolygon> getPolygons() {

        return polygons;
    }

    private final ImmutableMap<String, Bone> bones;

    public ImmutableMap<String, Bone> getBones() {

        return bones;
    }

    private final Vector origin;

    public Vector getOrigin() {

        return origin;
    }

    /**
     * Compiles this <code>ModelAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>ModelAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to
     *               compile this <code>json</code> perameter.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>ModelAttributes</code> instance is the base
     *               attribute set of a <code>ModelAttributes</code>
     *               instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>ModelAttributes</code> instance.
     */
    public ModelAttributes(AssetSchema schema, TracedDictionary json, boolean isBase)
            throws LoggedException {

        super(schema, json, isBase);

        TracedEntry<TracedDictionary> modelEntry = json.getAsDict("model", !isBase);

        if (modelEntry.containsValue()) {

            if (!isBase) {

                throw new KeyNameException("model", modelEntry);
            }

            TracedDictionary modelJson = modelEntry.getValue();

            TracedEntry<Integer> textureWidthEntry = modelJson.getAsInteger("textureWidth", false, null,
                    num -> num >= 1);
            textureWidth = textureWidthEntry.getValue();

            TracedEntry<Integer> textureHeightEntry = modelJson.getAsInteger("textureHeight", false, null,
                    num -> num >= 1);
            textureHeight = textureHeightEntry.getValue();

            // Create the vertices which the polygons of this model will be created from.
            TracedEntry<TracedArray> verticesEntry = modelJson.getAsArray("vertices", false);
            TracedArray verticesJson = verticesEntry.getValue();
            if (verticesJson.isEmpty()) {

                throw new CollectionSizeException(verticesEntry, verticesJson);
            }

            ArrayList<WeightedVertex> verticesList = new ArrayList<>();

            for (int i : verticesJson.getIndices()) {

                TracedEntry<Vector> vertexIndexEntry = verticesJson.getAsVector(i, false, 3);
                verticesList.add(new WeightedVertex(vertexIndexEntry.getValue()));
            }

            vertices = new ImmutableList<>(verticesList);

            // Create the polygons to be rendered and assign them with their individual
            // vertices.
            TracedEntry<TracedArray> polygonsEntry = modelJson.getAsArray("polygons", false);
            TracedArray polygonsJson = polygonsEntry.getValue();

            ArrayList<IndexedPolygon> polygonsList = new ArrayList<>();

            for (int polygonIndex : polygonsJson.getIndices()) {

                TracedEntry<TracedDictionary> polygonEntry = polygonsJson.getAsDict(polygonIndex, false);
                TracedDictionary polygonJson = polygonEntry.getValue();

                TracedEntry<TracedArray> polygonVerticesEntry = polygonJson.getAsArray("vertices", false);
                TracedArray polygonVerticesJson = polygonVerticesEntry.getValue();
                if (polygonVerticesJson.size() != 3) {

                    throw new CollectionSizeException(polygonVerticesEntry, polygonVerticesJson);
                }
                int[] vertexIndices = new int[3];

                // For each vertex of the triangle, retrieve the appropriate vertex from the
                // vertices perameter.
                for (int i = 0; i < vertexIndices.length; i++) {

                    TracedEntry<Integer> vertexIndexEntry = polygonVerticesJson.getAsInteger(i, false, null,
                            num -> 0 <= num && num < vertices.size());
                    vertexIndices[i] = vertexIndexEntry.getValue();
                }

                // Retrieve the color field of the polygon.
                TracedEntry<TracedArray> uvsEntry = polygonJson.getAsArray("uvs", false);
                TracedArray uvsJson = uvsEntry.getValue();

                if (uvsJson.size() != 3) {

                    throw new CollectionSizeException(uvsEntry, uvsJson);
                }

                Vector minUvBounds = new Vector(0, 0);
                Vector maxUvBounds = new Vector(textureWidth, textureHeight);

                Vector[] uvsArray = new Vector[3];
                for (int i : uvsJson.getIndices()) {

                    TracedEntry<Vector> uvEntry = uvsJson.getAsVector(i, false, 2, minUvBounds, maxUvBounds);
                    uvsArray[i] = uvEntry.getValue();
                }

                Triangle uvs = new Triangle(uvsArray[0], uvsArray[1], uvsArray[2]);

                polygonsList.add(new IndexedPolygon(vertexIndices, uvs));
            }

            polygons = new ImmutableList<>(polygonsList);

            TracedEntry<Vector> pivotPointEntry = modelJson.getAsVector("pivotPoint", false, 3);
            origin = pivotPointEntry.getValue();

            HashMap<String, Bone> bonesMap = new HashMap<>();

            TracedEntry<TracedDictionary> bonesEntry = modelJson.getAsDict("bones", true);
            if (bonesEntry.containsValue()) {

                TracedDictionary bonesJson = bonesEntry.getValue();
                for (String boneKey : bonesJson.getKeys()) {

                    TracedEntry<TracedDictionary> boneEntry = bonesJson.getAsDict(boneKey, false);
                    TracedDictionary boneJson = boneEntry.getValue();

                    // If any other bones in the model have the same key, raise an exception.
                    if (bonesMap.containsKey(boneKey)) {

                        throw new KeyNameException(boneKey, boneEntry);
                    }

                    bonesMap.put(boneKey, new Bone(boneJson, new ArrayList<>(), boneKey));
                }

            }

            bones = new ImmutableMap<>(bonesMap);
        } else {

            textureWidth = null;
            textureHeight = null;

            origin = null;
            vertices = null;
            polygons = null;
            bones = null;
        }
    }

    /**
     * <code>ModelAttributes.WeightedVertex</code>: A subclass representing a
     * vertex of a
     * parent <code>ModelAttributes</code> instance which can be modified
     * using a weighting
     * system.
     */
    public final class WeightedVertex {

        /**
         * <code>Vector</code>: The initial position of this
         * <code>ModelAttributes.WeightedVertex</code> instance.
         */
        private final Vector baseVertex;

        /**
         * Creates a new instance of the
         * <code>ModelAttributes.WeightedVertex</code> class.
         * 
         * @param baseVertex <code>Vector</code>: The initial position of this
         *                   <code>ModelAttributes.WeightedVertex</code> instance,
         *                   and the vertex
         *                   which will be returned if no weights are added.
         */
        private WeightedVertex(Vector baseVertex) {

            this.baseVertex = baseVertex;
        }

        /**
         * Retrieves the weighted vertex of this
         * <code>ModelAttributes.WeightedVertex</code>
         * instance.
         * 
         * @param weights <code>HashMap&lt;String, Vector&gt;</code>: The map of
         *                bones to their respective vertex which should be applied.
         * @return <code>Vector</code>: The generated vertex.
         */
        public Vector getWeightedVertex(HashMap<Vector, Double> vertices) {

            if (vertices == null || vertices.isEmpty()) {

                return baseVertex;
            }

            Vector returnVertex = Vector.IDENTITY_VECTOR;
            double weightSum = 0.0;

            for (Map.Entry<Vector, Double> vertexEntry : vertices.entrySet()) {

                returnVertex = returnVertex
                        .add(vertexEntry.getKey().multiply(vertexEntry.getValue()));

                weightSum += vertexEntry.getValue();
            }

            if (weightSum == 0) {

                return baseVertex;
            }

            return returnVertex.multiply(1.0 / weightSum);
        }
    }

    /**
     * <code>ModelAttributes.IndexedPolygon</code>: A subclass representing a
     * polygon which is
     * defined only with the indices of vertices in another list.
     */
    public final class IndexedPolygon {

        /**
         * <code>int[3]</code>: The vertex indices of this
         * <code>ModelAttributes.IndexedPolygon</code> instance.
         */
        private final int[] vertexIndices;

        /**
         * <code>Triangle</code>: The texture UVs of this
         * <code>ModelAttributes.IndexedPolygon</code> instance.
         */
        private final Triangle uvs;

        /**
         * Creates a new instance of the
         * <code>ModelAttributes.IndexedPolygon</code> class.
         * 
         * @param vertexIndices <code>int[3]</code>: The vertex index of this
         *                      <code>ModelAttributes.IndexedPolygon</code>
         *                      instance.
         * @param uvs           <code>Triangle</code>: The texture UVs of this
         *                      <code>ModelAttributes.IndexedPolygon</code>
         *                      instance.
         */
        private IndexedPolygon(int[] vertexIndices, Triangle uvs) {

            this.vertexIndices = vertexIndices;
            this.uvs = uvs;
        }

        /**
         * Retrieves the polygon of this
         * <code>ModelAttributes.IndexedPolygon</code> instance.
         * 
         * @param modelledVertices <code>List&lt;Vector&gt;</code>: The modelled
         *                         vertices to index when creating the polygon.
         * @return <code>Triangle</code>: The generated polygon, split between the
         *         model vertices in the first 3 indices and the UV vertices in the last
         *         3.
         */
        public Triangle getPolygon(List<Vector> modelledVertices) {

            return new Triangle(
                    modelledVertices.get(vertexIndices[0]),
                    modelledVertices.get(vertexIndices[1]),
                    modelledVertices.get(vertexIndices[2]));
        }

        /**
         * Retrieves the texture UVs of this
         * <code>ModelAttributes.IndexedPolygon</code> instance.
         * 
         * @return <code>Triangle</code>: The <code>uvs</code> field of this
         *         <code>ModelAttributes.IndexedPolygon</code> instance.
         */
        public Triangle getUvs() {

            return uvs;
        }
    }

    /**
     * <code>ModelAttributes.Bone</code>: A subclass representing a polygon
     * layout of a <code>ModelAttributes</code> instance.
     */
    public final class Bone {

        /**
         * <code>String</code>: The regular expression used to ensure all vertex indices
         * are of the expected pattern.
         */
        private static final String INDEX_PATTERN = "[+]?\\d+";

        /**
         * <code>ImmutableMap&lt;Integer, Double&gt;</code>: The vertex weights of this
         * <code>ModelAttributes.Bone</code> instance.
         */
        private final ImmutableMap<Integer, Double> vertexWeights;

        /**
         * <code>ImmutableList&lt;String&gt;</code>: The pathway of bones traced to
         * arrive at this <code>ModelAttributes.Bone</code> instance.
         */
        private final ImmutableList<String> bonePathway;

        /**
         * Retrieves the pathway of bones traced to arrive at this
         * <code>ModelAttributes.Bone</code> instance.
         * 
         * @return <code>ImmutableList&lt;String&gt;</code>: The
         *         <code>bonePathway</code> field of this
         *         <code>ModelAttributes.Bone</code> instance.
         */
        public ImmutableList<String> getBonePathway() {

            return bonePathway;
        }

        /**
         * <code>Vector</code>: The pivot point around which this
         * <code>ModelAttributes.Bone</code> should rotate and scale about during
         * animations. Note that this value is in relative space, assuming the model is
         * centered at (0, 0, 0).
         */
        private final Vector pivotPoint;

        public Vector getPivotPoint() {

            return pivotPoint;
        }

        /**
         * Creates a new instance of the <code>ModelAttributes.Bone</code> class.
         * 
         * @param modelJson <code>TracedDictionary</code>: The JSON from which to create
         *                  this <code>ModelAttributes.Bone</code> instance.
         * @throws LoggedException Thrown to represent any exception raised which
         *                         creating the bone.
         */
        private Bone(TracedDictionary modelJson, List<String> bonePathway, String boneName) throws LoggedException {

            ArrayList<String> bonePathwayList = new ArrayList<>(bonePathway);
            bonePathwayList.add(0, boneName);
            this.bonePathway = new ImmutableList<>(bonePathwayList);

            HashMap<Integer, Double> vertexWeightsMap = new HashMap<>();

            TracedEntry<TracedDictionary> vertexWeightsEntry = modelJson.getAsDict("vertexWeights", true);
            if (vertexWeightsEntry.containsValue()) {

                TracedDictionary vertexWeightsJson = vertexWeightsEntry.getValue();

                for (String vertexKey : vertexWeightsJson.getKeys()) {

                    TracedEntry<Double> chanceEntry = vertexWeightsJson.getAsDouble(vertexKey, false, null,
                            num -> num > 0);
                    double weight = chanceEntry.getValue();

                    if (!vertexKey.matches(INDEX_PATTERN)) {

                        throw new KeyNameException(vertexKey, chanceEntry);
                    }

                    int vertexIndex = Integer.parseInt(vertexKey);

                    if (vertexWeightsMap.containsKey(vertexIndex) || vertexIndex >= vertices.size()) {

                        throw new KeyNameException(vertexKey, chanceEntry);
                    }

                    vertexWeightsMap.put(vertexIndex, weight);
                }
            }

            vertexWeights = new ImmutableMap<>(vertexWeightsMap);

            TracedEntry<TracedDictionary> bonesEntry = modelJson.getAsDict("bones", true);
            if (bonesEntry.containsValue()) {

                TracedDictionary bonesJson = bonesEntry.getValue();

                for (String boneKey : bonesJson.getKeys()) {

                    TracedEntry<TracedDictionary> boneEntry = bonesJson.getAsDict(boneKey, false);
                    TracedDictionary boneJson = boneEntry.getValue();

                    // If any other bones in the model have the same key, raise an exception.
                    if (bones.containsKey(boneKey)) {

                        throw new KeyNameException(boneKey, boneEntry);
                    }

                    bones.put(boneKey, new Bone(boneJson, this.bonePathway, boneKey));
                }
            }

            TracedEntry<Vector> pivotPointEntry = modelJson.getAsVector("pivotPoint", false, 3);
            pivotPoint = pivotPointEntry.getValue();
        }

        /**
         * Retrieves the vertex weights of this <code>ModelAttributes.Bone</code>
         * instance.
         * 
         * @param boneActors <code>List&lt;BoneActorSet&gt;</code>: The bone actors used
         *                   to model the bones of this
         *                   <code>ModelAttributes.Bone</code> instance.
         * @return <code>HashMap&lt;Integer, HashMap&lt;Vector, Double&gt;&gt;</code>:
         *         The retrieved vertex weights of this
         *         <code>ModelAttributes.Bone</code> instance.
         */
        public HashMap<Integer, HashMap<Vector, Double>> getVertexWeights(BoneActorSet boneActors) {

            HashMap<Integer, HashMap<Vector, Double>> boneVertices = new HashMap<>();

            for (Map.Entry<Integer, Double> vertexWeight : vertexWeights.entrySet()) {

                int index = vertexWeight.getKey();
                Vector vertex = vertices.get(index).baseVertex;
                double weight = vertexWeight.getValue();

                boneVertices.computeIfAbsent(index, _ -> new HashMap<>()).put(vertex, weight);
            }

            for (String boneName : bonePathway) {

                Vector bonePivot = bones.get(boneName).pivotPoint;
                boneVertices = boneActors.apply(boneVertices, boneName, bonePivot);
            }

            return boneVertices;
        }
    }
}
