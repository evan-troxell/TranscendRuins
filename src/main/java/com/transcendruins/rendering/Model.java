package com.transcendruins.rendering;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.transcendruins.graphics3d.Position3D;
import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Triangle3D;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.InvalidKeyException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Model</code>: A class representing a portion of this <code>Model</code>
 * instance.
 */
public final class Model {

    /**
     * <code>ArrayList&lt;WeightedVertex&gt;</code>: The vertices of this
     * <code>Model</code> instance.
     * This <code>ArrayList</code> only represents the initial positions of the
     * vertices before animation transformations have been applied.
     */
    private final ArrayList<WeightedVertex> vertices = new ArrayList<>();

    /**
     * <code>ArrayList&lt;IndexedPolygon&gt;</code>: The polygons of this
     * <code>Model</code> instance.
     */
    private final ArrayList<IndexedPolygon> polygons = new ArrayList<>();

    /**
     * <code>Model.Bone</code>: The bone which contains all vertex structures of
     * this <code>Model</code> instance.
     */
    private final Bone bone;

    /**
     * Creates a new instance of the <code>Model</code> class.
     * 
     * @param modelJson <code>TracedDictionary</code>: The dictionary from which to
     *                  create this <code>Model</code> instance.
     * @param vertices  <code>ArrayList&lt;Vector&gt;</code>: The vertices to
     *                  reference when parsing the <code>modelJson</code> perameter.
     * @throws LoggedException Thrown to represent any exception raised which
     *                         creating the model.
     */
    public Model(TracedDictionary modelJson) throws LoggedException {

        // Create the vertices which the polygons of this model will be created from.
        TracedEntry<TracedArray> verticesEntry = modelJson.getAsArray("vertices", false);
        TracedArray verticesJson = verticesEntry.getValue();
        if (verticesJson.isEmpty()) {

            throw new ArrayLengthException(verticesEntry);
        }

        for (int i : verticesJson.getIndices()) {

            TracedEntry<Vector> vertexIndexEntry = verticesJson.getAsVector(i, false, Vector.DIMENSION_3D);
            vertices.add(new WeightedVertex(vertexIndexEntry.getValue()));
        }

        // Create the polygons to be rendered and assign them with their individual
        // vertices.
        TracedEntry<TracedArray> polygonsEntry = modelJson.getAsArray("polygons", false);

        if (polygonsEntry.containsValue()) {

            TracedArray polygonsJson = polygonsEntry.getValue();

            for (int polygonIndex : polygonsJson.getIndices()) {

                TracedEntry<TracedDictionary> polygonEntry = polygonsJson.getAsDictionary(polygonIndex, false);
                TracedDictionary polygonJson = polygonEntry.getValue();

                TracedEntry<TracedArray> polygonVerticesEntry = polygonJson.getAsArray("vertices", false);
                TracedArray polygonVerticesJson = polygonVerticesEntry.getValue();
                if (polygonVerticesJson.size() != Vector.DIMENSION_3D) {

                    throw new ArrayLengthException(polygonVerticesEntry);
                }
                int[] vertexIndices = new int[Vector.DIMENSION_3D];

                // For each vertex of the triangle, retrieve the appropriate vertex from the
                // vertices perameter.
                for (int i = 0; i < vertexIndices.length; i++) {

                    TracedEntry<Long> vertexIndexEntry = polygonVerticesJson.getAsLong(i, false, null, 0l,
                            vertices.size() - 1l);
                    vertexIndices[i] = vertexIndexEntry.getValue().intValue();
                }

                // Retrieve the color field of the polygon.
                TracedEntry<TracedArray> colorEntry = polygonJson.getAsArray("color", false);
                TracedArray colorJson = colorEntry.getValue();

                // The color entry should either represent an RGB value or an RGBA value, with a
                // length of 3 or 4.
                if (colorJson.size() != 3 && colorJson.size() != 4) {

                    throw new ArrayLengthException(colorEntry);
                }

                int[] polygonColorArray = new int[4];
                for (int i = 0; i < 4; i++) {

                    polygonColorArray[i] = colorJson.getAsLong(i, (i == 3), 255l, 0l, 255l).getValue().intValue();
                }
                Color polygonColor = new Color(polygonColorArray[0], polygonColorArray[1], polygonColorArray[2],
                        polygonColorArray[3]);

                polygons.add(new IndexedPolygon(vertexIndices, polygonColor));
            }
        }

        bone = new Bone(modelJson, new ArrayList<>());
    }

    /**
     * Retrieves the polygons of this <code>Model</code> instance.
     * 
     * @param boneActors     <code>HashMap&lt;String, Model.BoneActor&gt;</code>:
     *                       The bone actors used to model the bones of this
     *                       <code>Model</code> instance.
     * @param offset         <code>Position3D</code>: The offset at which to render
     *                       this <code>Model</code> instance from.
     * @param rotationOffset <code>Vector</code>: The rotation offset of the model,
     *                       represented as a vector.
     * @return <code>ArrayList&lt;Triangle3D&gt;</code>: The retrieved polygons of
     *         this <code>ModelSchema</code> instance.
     */
    public ArrayList<Triangle3D> getPolygons(HashMap<String, BoneActor> boneActors, Position3D offset, double angle,
            double heading, double pitch) {

        BoneActor boneActor = new BoneActor();
        boneActor.updatePosition(offset.getPosition());
        boneActor.updateRotation(Quaternion.fromEulerRotation(angle, Vector.fromUnitSphere(heading, pitch)));

        HashMap<Integer, HashMap<Vector, Double>> boneWeights = bone.getVertexWeights(boneActors);
        ArrayList<Vector> verticesModified = new ArrayList<>(vertices.size());

        Matrix cardinalDirection = Matrix.getRotationalMatrix3X3(offset.getHeading(), Matrix.Y_AXIS);

        for (int i = 0; i < vertices.size(); i++) {

            verticesModified.add(
                    boneActor.transform(
                            vertices.get(i).getWeightedVertex(boneWeights.get(i)),
                            bone.pivotPoint).multiplyMatrix(cardinalDirection));
        }

        ArrayList<Triangle3D> finalizedPolygons = new ArrayList<>(polygons.size());
        for (IndexedPolygon polygon : polygons) {

            finalizedPolygons.add(polygon.getPolygon(verticesModified));
        }

        return finalizedPolygons;
    }

    /**
     * <code>Model.WeightedVertex</code>: A subclass representing a vertex of a
     * parent <code>Model</code> instance which can be modified using a weighting
     * system.
     */
    private final class WeightedVertex {

        /**
         * <code>Vector</code>: The initial position of this
         * <code>Model.WeightedVertex</code> instance.
         */
        private final Vector baseVertex;

        /**
         * Creates a new instance of the <code>Model.WeightedVertex</code> class.
         * 
         * @param baseVertex <code>Vector</code>: The initial position of this
         *                   <code>Model.WeightedVertex</code> instance, and the vertex
         *                   which will be returned if no weights are added.
         */
        private WeightedVertex(Vector baseVertex) {

            this.baseVertex = baseVertex;
        }

        /**
         * Retrieves the weighted vertex of this <code>Model.WeightedVertex</code>
         * instance.
         * 
         * @param weights <code>HashMap&lt;String, Vector&gt;</code>: The map of
         *                bones to their respective vertex which should be applied.
         * @return <code>Vector</code>: The generated vertex.
         */
        private Vector getWeightedVertex(HashMap<Vector, Double> vertices) {

            if (vertices == null || vertices.isEmpty()) {

                return baseVertex;
            }

            Vector returnVertex = Vector.DEFAULT_VECTOR;
            double weightSum = 0.0;

            for (Map.Entry<Vector, Double> vertexEntry : vertices.entrySet()) {

                returnVertex = returnVertex
                        .addVector(vertexEntry.getKey().multiplyScalar(vertexEntry.getValue()));

                weightSum += vertexEntry.getValue();
            }

            if (weightSum == 0) {

                return baseVertex;
            }

            return returnVertex.multiplyScalar(1.0 / weightSum);
        }
    }

    /**
     * <code>Model.IndexedPolygon</code>: A subclass representing a polygon which is
     * defined only with the indices of vertices in another list.
     */
    private final class IndexedPolygon {

        /**
         * <code>int[3]</code>: The vertex indices of this
         * <code>Model.IndexedPolygon</code> instance.
         */
        private final int[] vertexIndices;

        /**
         * <code>Color</code>: The color of this <code>Model.IndexedPolygon</code>
         * instance.
         */
        private final Color color;

        /**
         * Creates a new instance of the <code>Model.IndexedPolygon</code> class.
         * 
         * @param vertexIndices <code>int[3]</code>: The vertex indices to assign to
         *                      this <code>Model.IndexedPolygon</code> instance.
         * @param color         <code>Color</code>: The color to assign to this
         *                      <code>Model.IndexedPolygon</code> instance.
         */
        private IndexedPolygon(int[] vertexIndices, Color color) {

            this.vertexIndices = vertexIndices;
            this.color = color;
        }

        /**
         * Retrieves the polygon of this <code>Model.IndexedPolygon</code> instance.
         * 
         * @param modelledVertices <code>ArrayList&lt;Vector&gt;</code>: The modelled
         *                         vertices to index when creating the polygon.
         * @return <code>Triangle3D</code>: The generated polygon.
         */
        private Triangle3D getPolygon(ArrayList<Vector> modelledVertices) {

            return new Triangle3D(modelledVertices.get((int) vertexIndices[0]),
                    modelledVertices.get((int) vertexIndices[1]), modelledVertices.get((int) vertexIndices[2]), color);
        }
    }

    /**
     * <code>Model.Bone</code>: A subclass representing a polygon structure of a
     * <code>Model</code> instance.
     */
    public final class Bone {

        /**
         * <code>String</code>: The regular expression used to ensure all vertex indices
         * are of the expected pattern.
         */
        private static final String INDEX_PATTERN = "[+]?\\d+";

        /**
         * <code>HashMap&lt;Integer, Double&gt;</code>: The vertex weights of this
         * <code>Model.Bone</code> instance.
         */
        private final HashMap<Integer, Double> vertexWeights = new HashMap<>();

        /**
         * <code>HashMap&lt;String, Bone&gt;</code>: The bones of this
         * <code>Model.Bone</code> instance.
         */
        private final HashMap<String, Bone> bones = new HashMap<>();

        /**
         * <code>Vector</code>: The pivot point around which this
         * <code>Model.Bone</code> should rotate and scale about during animations.
         * Note that this value is in relative space, assuming the model is centered at
         * (0, 0, 0).
         */
        private final Vector pivotPoint;

        /**
         * Creates a new instance of the <code>Model.Bone</code> class.
         * 
         * @param modelJson <code>TracedDictionary</code>: The JSON from which to create
         *                  this <code>Model.Bone</code> instance.
         * @param boneKeys  <code>ArrayList&lt;String&gt;</code>: The list of all
         *                  previous bone keys.
         * @throws LoggedException Thrown to represent any exception raised which
         *                         creating the bone.
         */
        private Bone(TracedDictionary modelJson, ArrayList<String> boneKeys) throws LoggedException {

            TracedEntry<TracedDictionary> vertexWeightsEntry = modelJson.getAsDictionary("vertexWeights", true);

            if (vertexWeightsEntry.containsValue()) {

                TracedDictionary vertexWeightsJson = vertexWeightsEntry.getValue();

                for (String vertexKey : vertexWeightsJson.getKeys()) {

                    if (!vertexKey.matches(INDEX_PATTERN)) {

                        throw new InvalidKeyException(vertexWeightsEntry, vertexKey);
                    }
                    int vertexIndex = Integer.parseInt(vertexKey);

                    if (vertexWeights.containsKey(vertexIndex) || vertexIndex >= vertices.size()) {

                        throw new InvalidKeyException(vertexWeightsEntry, vertexKey);
                    }

                    TracedEntry<Double> weightEntry = vertexWeightsJson.getAsDouble(vertexKey, false, null, 0.01, null);
                    double weight = weightEntry.getValue();

                    vertexWeights.put(vertexIndex, weight);
                }
            }

            TracedEntry<TracedDictionary> bonesEntry = modelJson.getAsDictionary("bones", true);

            if (bonesEntry.containsValue()) {

                TracedDictionary bonesJson = bonesEntry.getValue();

                for (String boneKey : bonesJson.getKeys()) {

                    // If any other bones in the model have the same key, raise an exception.
                    if (boneKeys.contains(boneKey)) {

                        throw new InvalidKeyException(bonesEntry, boneKey);
                    }
                    boneKeys.add(boneKey);

                    TracedEntry<TracedDictionary> boneEntry = bonesJson.getAsDictionary(boneKey, false);
                    TracedDictionary boneJson = boneEntry.getValue();
                    Bone newBone = new Bone(boneJson, boneKeys);

                    bones.put(boneKey, newBone);
                }
            }

            TracedEntry<Vector> pivotPointEntry = modelJson.getAsVector("pivotPoint", false, Vector.DIMENSION_3D);
            pivotPoint = pivotPointEntry.getValue();
        }

        /**
         * Retrieves the vertex weights of this <code>Model.Bone</code> instance.
         * 
         * @param boneActors <code>HashMap&lt;String, Model.BoneActor&gt;</code>:
         *                   The
         *                   bone actors used to model the bones of this
         *                   <code>Model.Bone</code> instance.
         * @return <code>HashMap&lt;Integer, HashMap&lt;String, Vector&gt;&gt;</code>:
         *         The retrieved vertex weights of this <code>Model.Bone</code>
         *         instance.
         */
        private HashMap<Integer, HashMap<Vector, Double>> getVertexWeights(HashMap<String, BoneActor> boneActors) {

            HashMap<Integer, HashMap<Vector, Double>> vertexBoneWeights = new HashMap<>();

            // Retrieve all bones in this model and apply bone actors.
            for (Map.Entry<String, Bone> boneEntry : bones.entrySet()) {

                String boneName = boneEntry.getKey();
                BoneActor boneActor = boneActors.get(boneName);
                Bone newBone = boneEntry.getValue();

                HashMap<Integer, HashMap<Vector, Double>> boneVertices = new HashMap<>();

                for (Map.Entry<Integer, Double> vertexWeight : newBone.vertexWeights.entrySet()) {

                    int index = vertexWeight.getKey();
                    Vector vertex = vertices.get(index).baseVertex;

                    addVertexWeight(index, vertex, vertexWeight.getValue(), boneVertices);
                }

                for (Map.Entry<Integer, HashMap<Vector, Double>> boneVertexBoneWeights : newBone
                        .getVertexWeights(boneActors).entrySet()) {

                    int index = boneVertexBoneWeights.getKey();

                    for (Map.Entry<Vector, Double> boneWeightEntry : boneVertexBoneWeights.getValue().entrySet()) {

                        addVertexWeight(index, boneWeightEntry.getKey(), boneWeightEntry.getValue(), boneVertices);
                    }
                }

                for (Map.Entry<Integer, HashMap<Vector, Double>> boneWeightsEntry : boneVertices.entrySet()) {

                    int index = boneWeightsEntry.getKey();
                    if (!vertexBoneWeights.containsKey(index)) {

                        vertexBoneWeights.put(index, new HashMap<>());
                    }

                    HashMap<Vector, Double> verticesMap = vertexBoneWeights.get(index);

                    if (boneActor == null) {

                        for (Map.Entry<Vector, Double> vertexEntry : boneWeightsEntry.getValue().entrySet()) {

                            verticesMap.put(vertexEntry.getKey(), vertexEntry.getValue());
                        }
                    } else {

                        for (Map.Entry<Vector, Double> vertexEntry : boneWeightsEntry.getValue().entrySet()) {

                            verticesMap.put(boneActor.transform(vertexEntry.getKey(), newBone.pivotPoint),
                                    vertexEntry.getValue());
                        }
                    }
                }
            }

            return vertexBoneWeights;
        }
    }

    private void addVertexWeight(int index, Vector vertex, double weight,
            HashMap<Integer, HashMap<Vector, Double>> map) {

        if (!map.containsKey(index)) {

            map.put(index, new HashMap<>());
        }

        map.get(index).put(vertex, weight);
    }

    public static final class BoneActor {

        private Vector position = null;

        private Quaternion rotation = null;

        private Matrix scale = null;

        public void updatePosition(Vector p) {

            position = (position == null) ? p : position.addVector(p);
        }

        public void updateRotation(Quaternion r) {

            rotation = (rotation == null) ? r : rotation.multiply(r);
        }

        public void updateScale(Matrix s) {

            scale = (scale == null) ? s : scale.multiplyMatrix(s);
        }

        public Vector transform(Vector vector, Vector pivotPoint) {

            vector = vector.subtractVector(pivotPoint);

            if (scale != null) {

                vector = vector.multiplyMatrix(scale);
            }

            if (rotation != null) {

                vector = rotation.multiply(vector.toQuaternion()).multiply(rotation.toConjugate()).toVector();
            }

            if (position != null) {

                vector = vector.addVector(position);
            }

            vector = vector.addVector(pivotPoint);

            return vector;
        }
    }
}
