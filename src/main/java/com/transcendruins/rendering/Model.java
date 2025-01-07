package com.transcendruins.rendering;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.transcendruins.graphics3d.Position3D;
import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.Triangle3D;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.graphics3d.interpolation.PositionModifier;
import com.transcendruins.graphics3d.interpolation.RotationModifier;
import com.transcendruins.graphics3d.interpolation.ScaleModifier;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.InvalidKeyException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Model</code>: A class representing a portion of this <code>Model</code> instance.
 */
public final class Model {

    /**
     * <code>ArrayList&lt;WeightedVertex&gt;</code>: The vertices of this <code>Model</code> instance.
     * This <code>ArrayList</code> only represents the initial positions of the vertices before animation transformations have been applied.
     */
    private final ArrayList<WeightedVertex> vertices = new ArrayList<>();

    /**
     * <code>ArrayList&lt;IndexedPolygon&gt;</code>: The polygons of this <code>Model</code> instance.
     */
    private final ArrayList<IndexedPolygon> polygons = new ArrayList<>();

    /**
     * <code>Model.Bone</code>: The bone which contains all vertex structures of this <code>Model</code> instance.
     */
    private final Bone bone;

    /**
     * Creates a new instance of the <code>Model</code> class.
     * @param modelJson <code>TracedDictionary</code>: The dictionary from which to create this <code>Model</code> instance.
     * @param vertices <code>ArrayList&lt;Vector&gt;</code>: The vertices to reference when parsing the <code>modelJson</code> perameter.
     * @throws LoggedException Thrown to represent any exception raised which creating the model.
     */
    public Model(TracedDictionary modelJson) throws LoggedException {

        // Create the vertices which the polygons of this model will be created from.
        TracedEntry<TracedArray> verticesEntry = modelJson.getAsArray("vertices", false);
        TracedArray verticesJson = verticesEntry.getValue();
        if (verticesJson.isEmpty()) {

            throw new ArrayLengthException(verticesEntry);
        }

        for (int i = 0; i < verticesJson.size(); i++) {

            TracedEntry<Vector> vertexIndexEntry = verticesJson.getAsVector(i, false, Vector.DIMENSION_3D);
            vertices.add(new WeightedVertex(vertexIndexEntry.getValue()));
        }

        // Create the polygons to be rendered and assign them with their individual vertices.
        TracedEntry<TracedArray> polygonsEntry = modelJson.getAsArray("polygons", false);
        TracedArray polygonsJson = polygonsEntry.getValue();

        if (polygonsJson != null) {

            for (int polygonIndex = 0; polygonIndex < polygonsJson.size(); polygonIndex ++) {

                TracedEntry<TracedDictionary> polygonEntry = polygonsJson.getAsDictionary(polygonIndex, false);
                TracedDictionary polygonJson = polygonEntry.getValue();

                TracedEntry<TracedArray> polygonVerticesEntry = polygonJson.getAsArray("vertices", false);
                TracedArray polygonVerticesJson = polygonVerticesEntry.getValue();
                if (polygonVerticesJson.size() != Vector.DIMENSION_3D) {

                    throw new ArrayLengthException(polygonVerticesEntry);
                }
                long[] vertexIndices = new long[Vector.DIMENSION_3D];

                // For each vertex of the triangle, retrieve the appropriate vertex from the vertices perameter.
                for (int i = 0; i < vertexIndices.length; i++) {

                    TracedEntry<Long> vertexIndexEntry = polygonVerticesJson.getAsLong(i, false, null, 0l, vertices.size() - 1l);
                    vertexIndices[i] = vertexIndexEntry.getValue();
                }

                // Retrieve the color field of the polygon.
                TracedEntry<TracedArray> colorEntry = polygonJson.getAsArray("color", false);
                TracedArray colorJson = colorEntry.getValue();

                // The color entry should either represent an RGB value or an RGBA value, with a length of 3 or 4.
                if (colorJson.size() != 3 && colorJson.size() != 4) {

                    throw new ArrayLengthException(colorEntry);
                }

                int[] polygonColorArray = new int[4];
                for (int i = 0; i < 4; i++) {

                    polygonColorArray[i] = colorJson.getAsLong(i, (i == 3), 255l, 0l, 255l).getValue().intValue();
                }
                Color polygonColor = new Color(polygonColorArray[0], polygonColorArray[1], polygonColorArray[2], polygonColorArray[3]);

                polygons.add(new IndexedPolygon(vertexIndices, polygonColor));
            }
        }

        bone = new Bone(modelJson, new ArrayList<>());
    }

    /**
     * Retrieves the polygons of this <code>Model</code> instance.
     * @param boneActors <code>HashMap&lt;String, Model.BoneActor&gt;</code>: The bone actors used to model the bones of this <code>Model</code> instance.
     * @param offset <code>Position3D</code>: The offset at which to render this <code>Model</code> instance from.
     * @param rotationOffset <code>Vector</code>: The rotation offset of the model, represented as a vector.
     * @return <code>ArrayList&lt;Triangle3D&gt;</code>: The retrieved polygons of this <code>ModelSchema</code> instance.
     */
    public ArrayList<Triangle3D> getPolygons(HashMap<String, BoneActor> boneActors, Position3D offset, double angle, double heading, double pitch) {

        PositionModifier positionModifier = new PositionModifier(offset.getPosition(), new RotationModifier(0, Vector.DEFAULT_VECTOR));
        RotationModifier rotationModifier = new RotationModifier(angle, Vector.fromUnitSphere(heading, pitch));
        BoneActor boneActor = new BoneActor(positionModifier, rotationModifier, null);

        HashMap<Long, HashMap<String, Vector>> boneWeights = bone.getVertexWeights(boneActors);
        ArrayList<Vector> verticesModified = new ArrayList<>(vertices.size());

        Matrix cardinalDirection = Matrix.getRotationalMatrix3X3(offset.getHeading(), Matrix.Y_AXIS);

        for (int i = 0; i < vertices.size(); i++) {

            verticesModified.add(
                boneActor.apply(
                    vertices.get(i).getWeightedVertex(boneWeights.get((long) i)), bone.pivotPoint
                ).multiplyMatrix(cardinalDirection)
            );
        }

        ArrayList<Triangle3D> finalizedPolygons = new ArrayList<>(polygons.size());
        for (IndexedPolygon polygon : polygons) {

            finalizedPolygons.add(polygon.getPolygon(verticesModified));
        }

        return finalizedPolygons;
    }

    /**
     * <code>Model.WeightedVertex</code>: A subclass representing a vertex of a parent <code>Model</code> instance which can be modified using a weighting system.
     */
    private final class WeightedVertex {

        /**
         * <code>Vector</code>: The initial position of this <code>WeightedVertex</code> instance.
         */
        private final Vector baseVertex;

        /**
         * <code>HashMap&lt;String, Double&gt;</code>: The map of all bones to their specific weights in this <code>WeightedVertex</code> instance.
         */
        private final HashMap<String, Double> weights = new HashMap<>();

        /**
         * <code>double</code>: The total weight of this <code>WeightedVertex</code> instance.
         */
        private double weight;

        /**
         * Creates a new instance of the <code>WeightedVertex</code> class.
         * @param baseVertex <code>Vector</code>: The initial position of this <code>WeightedVertex</code> instance, and the vertex which will be returned if no weights are added.
         */
        private WeightedVertex(Vector baseVertex) {

            this.baseVertex = baseVertex;
        }

        /**
         * Adds a weight to this <code>Model.WeightedVertex</code> instance.
         * @param newBone <code>String</code>: The bone assigning the weight.
         * @param newWeight <code>double</code>: The weight to be asigned.
         */
        private void addWeight(String newBone, double newWeight) {

            weights.put(newBone, newWeight);
            this.weight += weight;
        }

        /**
         * Retrieves the weighted vertex of this <code>Model.WeightedVertex</code> instance.
         * @param boneVertices <code>HashMap&lt;String, Vector&gt;</code>: The map of bones to their respective vertex which should be applied.
         * @return <code>Vector<d/code>: The generated vertex.
         */
        private Vector getWeightedVertex(HashMap<String, Vector> boneVertices) {

            if (weight <= 0) {

                return baseVertex;
            }
            Vector returnVertex = new Vector(0, 0, 0);

            for (Map.Entry<String, Vector> vertexEntry : boneVertices.entrySet()) {

                returnVertex = returnVertex.addVector(vertexEntry.getValue().multiplyScalar(weights.get(vertexEntry.getKey())));
            }

            return returnVertex.multiplyScalar(1.0 / weight);
        }
    }

    /**
     * <code>Model.IndexedPolygon</code>: A subclass representing a polygon which is defined only with the indices of vertices in another list.
     */
    private final class IndexedPolygon {

        /**
         * <code>long[3]</code>: The vertex indices of this <code>Model.IndexedPolygon</code> instance.
         */
        private final long[] vertexIndices;

        /**
         * <code>Color</code>: The color of this <code>Model.IndexedPolygon</code> instance.
         */
        private final Color color;

        /**
         * Creates a new instance of the <code>Model.IndexedPolygon</code> class.
         * @param vertexIndices <code>long[3]</code>: The vertex indices to assign to this <code>Model.IndexedPolygon</code> instance.
         * @param color <code>Color</code>: The color to assign to this <code>Model.IndexedPolygon</code> instance.
         */
        private IndexedPolygon(long[] vertexIndices, Color color) {

            this.vertexIndices = vertexIndices;
            this.color = color;
        }

        /**
         * Retrieves the polygon of this <code>Model.IndexedPolygon</code> instance.
         * @param modelledVertices <code>ArrayList&lt;Vector&gt;/code>: The modelled vertices to index when creating the polygon.
         * @return <code>Triangle3D</code>: The generated polygon.
         */
        private Triangle3D getPolygon(ArrayList<Vector> modelledVertices) {

            return new Triangle3D(modelledVertices.get((int) vertexIndices[0]), modelledVertices.get((int) vertexIndices[1]), modelledVertices.get((int) vertexIndices[2]), color);
        }
    }

    /**
     * <code>Model.Bone</code>: A subclass representing a polygon structure of a <code>Model</code> instance.
     */
    public final class Bone {

        /**
         * <code>String</code>: The regular expression used to ensure all vertex indices are of the expected pattern.
         */
        private static final String INDEX_PATTERN = "[+]?\\d+";

        /**
         * <code>HashMap&lt;Long, Double&gt;</code>: The vertex weights of this <code>Model.Bone</code> instance.
         */
        private final HashMap<Long, Double> vertexWeights = new HashMap<>();

         /**
         * <code>HashMap&lgt;String, Bone&gt;</code>: The bones of this <code>Model.Bone</code> instance.
         */
        private final HashMap<String, Bone> bones = new HashMap<>();

        /**
         * <code>Vector</code>: The pivot point around which this <code>Model.Bone</code> should rotate and scale about during animations.
         * Note that this value is in relative space, assuming the model is centered at (0, 0, 0).
         */
        private final Vector pivotPoint;

        /**
         * Creates a new instance of the <code>Model.Bone</code> class.
         * @param modelJson <code>TracedDictionary</code>: The JSON from which to create this <code>Model.Bone</code> instance.
         * @param boneKeys <code>ArrayList&lt;String&gt;</code>: The list of all previous bone keys.
         * @throws LoggedException Thrown to represent any exception raised which creating the bone.
         */
        private Bone(TracedDictionary modelJson, ArrayList<String> boneKeys) throws LoggedException {

            TracedEntry<TracedDictionary> vertexWeightsEntry = modelJson.getAsDictionary("vertexWeights", true);
            TracedDictionary vertexWeightsJson = vertexWeightsEntry.getValue();

            if (vertexWeightsJson != null) {

                for (String vertexKey : vertexWeightsJson.getKeys()) {

                    if (vertexKey.matches(INDEX_PATTERN)) {

                        throw new InvalidKeyException(vertexWeightsEntry, vertexKey);
                    }
                    long vertexIndex = Long.parseLong(vertexKey);

                    if (vertexWeights.containsKey(vertexIndex) || vertexIndex >= vertices.size()) {

                        throw new InvalidKeyException(vertexWeightsEntry, vertexKey);
                    }

                    TracedEntry<Double> weightEntry = vertexWeightsJson.getAsDouble(vertexKey, false, null, 0.01, null);
                    double weight = weightEntry.getValue();

                    vertexWeights.put(vertexIndex, weight);
                }
            }

            TracedEntry<TracedDictionary> bonesEntry = modelJson.getAsDictionary("bones", true);
            TracedDictionary bonesJson = bonesEntry.getValue();

            if (bonesJson != null) {

                for (String boneKey : bonesJson.getKeys()) {

                    // If any other bones in the model have the same key, raise an exception.
                    if (boneKeys.contains(boneKey)) {

                        throw new InvalidKeyException(bonesEntry, boneKey);
                    }
                    boneKeys.add(boneKey);

                    TracedEntry<TracedDictionary> boneEntry = bonesJson.getAsDictionary(boneKey, false);
                    TracedDictionary boneJson = boneEntry.getValue();
                    Bone newBone = new Bone(boneJson, boneKeys);

                    for (Map.Entry<Long, Double> vertexWeightEntry : newBone.vertexWeights.entrySet()) {

                        vertices.get(vertexWeightEntry.getKey().intValue()).addWeight(boneKey, vertexWeightEntry.getValue());
                    }

                    bones.put(boneKey, newBone);
                }
            }

            TracedEntry<Vector> pivotPointEntry = modelJson.getAsVector("pivotPoint", false, Vector.DIMENSION_3D);
            pivotPoint = pivotPointEntry.getValue();
        }

        /**
         * Retrieves the vertex weights of this <code>Model.Bone</code> instance.
         * @param boneActors <code>HashMap&lt;String, Model.BoneActor&gt;</code>: The bone actors used to model the bones of this <code>Model.Bone</code> instance.
         * @return <code>HashMap&lt;Long, HashMap&lt;String, Vector&gt;&gt;</code>: The retrieved vertex weights of this <code>Model.Bone</code> instance.
         */
        private HashMap<Long, HashMap<String, Vector>> getVertexWeights(HashMap<String, BoneActor> boneActors) {

            HashMap<Long, HashMap<String, Vector>> vertexBoneWeights = new HashMap<>();

            // Retrieve all bones in this model and apply bone actors.
            for (Map.Entry<String, Bone> boneEntry : bones.entrySet()) {

                BoneActor boneActor = boneActors.containsKey(boneEntry.getKey()) ? boneActors.get(boneEntry.getKey()) : null;
                Bone newBone = boneEntry.getValue();

                for (Map.Entry<Long, HashMap<String, Vector>> boneVertexBoneWeights : newBone.getVertexWeights(boneActors).entrySet()) {

                    HashMap<String, Vector> boneWeights = new HashMap<>();
                    for (Map.Entry<String, Vector> boneWeightsEntry : boneVertexBoneWeights.getValue().entrySet()) {

                        boneWeights.put(boneWeightsEntry.getKey(), boneActor == null ? boneWeightsEntry.getValue() : boneActor.apply(boneWeightsEntry.getValue(), newBone.pivotPoint));
                    }

                    vertexBoneWeights.get(boneVertexBoneWeights.getKey()).putAll(boneWeights);
                }
            }

            return vertexBoneWeights;
        }
    }

    /**
     * <code>Model.BoneActor</code>: A class representing a bone actor (a collection of operations to perform on a bone when modelling its vertices).
     */
    public static final class BoneActor {

        /**
         * <code>PositionModifier</code>: The operator used to perform all matrix adding on the polygons of a <code>Model</code> instance.
         */
        private final PositionModifier positionOperator;

        /**
         * <code>RotationModifier</code>: The operator used to perform all matrix rotations on the polygons of a <code>Model</code> instance.
         */
        private final RotationModifier rotationOperator;

        /**
         * <code>ScaleModifier</code>: The operator used to perform all matrix scaling on the polygons of a <code>Model</code> instance.
         */
        private final ScaleModifier scaleOperator;

        public BoneActor(PositionModifier positionOperator, RotationModifier rotationOperator, ScaleModifier scaleOperator) {

            this.positionOperator = positionOperator;
            this.rotationOperator = rotationOperator;
            this.scaleOperator = scaleOperator;
        }

        public Vector apply(Vector vector, Vector pivotPoint) {

            vector = vector.subtractVector(pivotPoint);

            if (scaleOperator != null) {

                vector = scaleOperator.apply(vector);
            }

            if (rotationOperator != null) {

                vector = rotationOperator.apply(vector);
            }

            if (positionOperator != null) {

                vector = positionOperator.apply(vector);
            }

            vector = vector.addVector(pivotPoint);

            return vector;
        }
    }
}
