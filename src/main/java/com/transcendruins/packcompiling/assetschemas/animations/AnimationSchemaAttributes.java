package com.transcendruins.packcompiling.assetschemas.animations;

import java.util.ArrayList;

import com.transcendruins.graphics3d.interpolation.PositionFrame;
import com.transcendruins.graphics3d.interpolation.RotationFrame;
import com.transcendruins.graphics3d.interpolation.ScaleFrame;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.utilities.Sorter;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.InvalidKeyException;
import com.transcendruins.utilities.finalize.FinalizedList;
import com.transcendruins.utilities.finalize.FinalizedMap;
import com.transcendruins.utilities.finalize.FinalizedSet;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>AnimationSchemaAttributes</code>: A class which represents the
 * attributes of an <code>AnimationSchema</code> instance.
 */
public final class AnimationSchemaAttributes extends AssetSchemaAttributes {

    /**
     * <code>String</code>: The regular expression used to ensure all key frames are
     * of the expected pattern.
     */
    private static final String KEYFRAME_PATTERN = "[-+]?\\d*\\.\\d+";

    /**
     * <code>Sorter&lt;Double&gt;</code>: A sorter which takes an input of any
     * <code>Collection&lt;Double&gt;</code> composed of timestamps and outputs an
     * <code>ArrayList&lt;Double&gt;</code> composed of timestamps from lowest to
     * highest.
     */
    private static final Sorter<Double> TIMESTAMP_SORTER = new Sorter<Double>() {

        @Override
        public Double sortSelector(Double newEntry, Double oldEntry) {

            return (newEntry < oldEntry) ? newEntry : oldEntry;
        }
    };

    /**
     * <code>Double</code>: The animation length of this
     * <code>AnimationSchemaAttributes</code> instance in seconds.
     */
    private final Double length;

    /**
     * Retrieves the length of this <code>AnimationSchemaAttributes</code> instance.
     * 
     * @return <code>Double</code>: The <code>length</code> field of this
     *         <code>AnimationSchemaAttributes</code> instance.
     */
    public Double getLength() {

        return length;
    }

    /**
     * <code>Boolean</code>: Whether or not this
     * <code>AnimationSchemaAttributes</code> instance is a looping animation.
     */
    private final Boolean looping;

    /**
     * Retrieves whether or not this <code>AnimationSchemaAttributes</code> instance
     * is looping.
     * 
     * @return <code>Boolean</code>: The <code>looping</code> field of this
     *         <code>AnimationSchemaAttributes</code> instance.
     */
    public Boolean getLooping() {

        return looping;
    }

    /**
     * <code>FinalizedMap&lt;Double, FinalizedMap&lt;String, AnimationSchemaAttributes.KeyFrame&gt;&gt;</code>:
     * The key frame time stamps of this <code>AnimationSchemaAttributes</code>
     * instance paired with the key frames of the bone actors.
     */
    private final FinalizedMap<Double, FinalizedMap<String, KeyFrame>> keyframes;

    /**
     * Retrieves the keyframes of this <code>AnimationSchemaAttributes</code>
     * instance.
     * 
     * @return <code>FinalizedMap&lt;Double, FinalizedMap&lt;String, KeyFrame&gt;&gt;</code>:
     *         The <code>keyframes</code> field of this
     *         <code>AnimationSchemaAttributes</code> instance.
     */
    public FinalizedMap<Double, FinalizedMap<String, KeyFrame>> getKeyframes() {

        return keyframes;
    }

    /**
     * <code>FinalizedList&lt;Double&gt;</code>: The sorted list of timestamps of
     * this <code>AnimationSchemaAttributes</code> instance.
     */
    private final FinalizedList<Double> timestampsSorted;

    /**
     * Retrieves the sorted timestamps of this
     * <code>AnimationSchemaAttributes</code> instance.
     * 
     * @return <code>FinalizedList&lt;Double&gt;</code>: The
     *         <code>timestampsSorted</code> field of this
     *         <code>AnimationSchemaAttributes</code> instance.
     */
    public FinalizedList<Double> getTimestampsSorted() {

        return timestampsSorted;
    }

    /**
     * <code>FinalizedSet&lt;String&gt;</code>: The set of all bones of this
     * <code>AnimationSchemaAttributes</code> instance.
     */
    private final FinalizedSet<String> bones;

    /**
     * Retrieves the bones of this <code>AnimationSchemaAttributes</code> instance.
     * 
     * @return <code>FinalizedSet&lt;String&gt;</code>: The <code>bones</code> field
     *         of this <code>AnimationSchemaAttributes</code> instance.
     */
    public FinalizedSet<String> getBones() {

        return bones;
    }

    /**
     * <code>boolean</code>: Whether or not this
     * <code>AnimationSchemaAttributes</code> defines a new animation or only
     * properties of the animation.
     */
    private final boolean animationDefinition;

    /**
     * Retrieves whether or not this <code>AnimationSchemaAttributes</code> instance
     * is an animation definition.
     * 
     * @return <code>boolean</code>: The <code>animationDefinition</code> field of
     *         this <code>AnimationSchemaAttributes</code> instance.
     */
    public boolean getAnimationDefinition() {

        return animationDefinition;
    }

    /**
     * Compiles this <code>AnimationSchemaAttributes</code> instance into a
     * completed instance.
     * 
     * @param schema     <code>AnimationSchema</code>: The schema which created this
     *                   <code>AnimationSchemaAttributes</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to
     *                   compile this <code>AnimationSchemaAttributes</code>
     *                   instance.
     * @param isBase     <code>boolean</code>: Whether or not this
     *                   <code>AnimationSchemaAttributes</code> instance is the base
     *                   attribute set of an <code>AnimationSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>AnimationSchemaAttributes</code> instance.
     */
    public AnimationSchemaAttributes(AnimationSchema schema, TracedDictionary schemaJson, boolean isBase)
            throws LoggedException {

        super(schema, schemaJson, isBase);

        TracedEntry<Boolean> loopingEntry = schemaJson.getAsBoolean("looping", true, isBase ? false : null);
        looping = loopingEntry.getValue();

        // Key frames should be required if this attribute set is the base attribute
        // set.
        TracedEntry<TracedDictionary> keyframesEntry = schemaJson.getAsDictionary("keyframes", !isBase);
        animationDefinition = keyframesEntry.containsValue();

        if (animationDefinition) {

            keyframes = new FinalizedMap<>();
            bones = new FinalizedSet<>();

            TracedDictionary keyframesJson = keyframesEntry.getValue();

            // If there are keyframe entries, a new length should be defined.
            TracedEntry<Double> lengthEntry = schemaJson.getAsDouble("length", false, null, 0.0, null);
            length = lengthEntry.getValue();

            ArrayList<Double> timestampsList = new ArrayList<>();

            for (String keyframe : keyframesJson.getKeys()) {

                // If the keyframe cannot be converted to a double value, raise an exception.
                if (!keyframe.matches(KEYFRAME_PATTERN)) {

                    throw new InvalidKeyException(keyframesEntry, keyframe);
                }

                // Generates the timestamp of the keyframe.
                Double keyframeTimestamp = Double.valueOf(keyframe);

                // If the timestamp of the keyframe is outside of the animation length, raise an
                // exception.
                if (keyframeTimestamp < 0 || keyframeTimestamp > length || keyframes.containsKey(keyframeTimestamp)) {

                    throw new InvalidKeyException(keyframesEntry, keyframe);
                }

                // Adds the timestamp to the sorted list of timestamps
                timestampsList.add(keyframeTimestamp);

                // Retrieve the map of bones to their key frames from the key frame.
                TracedEntry<TracedDictionary> keyframeBoneMapEntry = keyframesJson.getAsDictionary(keyframe, false);
                TracedDictionary keyframeBoneMapJson = keyframeBoneMapEntry.getValue();
                FinalizedMap<String, KeyFrame> boneMap = new FinalizedMap<>();

                bones.addAll(keyframeBoneMapJson.getKeys());

                // Iterate through each bone and process its key frame.
                for (String bone : keyframeBoneMapJson.getKeys()) {

                    TracedEntry<TracedDictionary> keyframeEntry = keyframeBoneMapJson.getAsDictionary(bone, false);
                    TracedDictionary keyframeJson = keyframeEntry.getValue();
                    boneMap.put(bone, new KeyFrame(keyframeJson, keyframeTimestamp));
                }

                keyframes.put(keyframeTimestamp, boneMap);
            }

            // Sort the list of timestamps and apply it to this attribute set.
            timestampsSorted = new FinalizedList<>(TIMESTAMP_SORTER.sort(timestampsList));
        } else {

            length = null;
            keyframes = null;
            bones = null;
            timestampsSorted = null;
        }

        finalizeData();
    }

    /**
     * <code>AnimationSchemaAttributes.KeyFrame</code>: A subclass representing a
     * keyframe in an animation.
     */
    public final class KeyFrame {

        /**
         * <code>RotationFrame</code>: The rotation frame of this
         * <code>AnimationSchemaAttributes.KeyFrame</code> instance.
         */
        private final RotationFrame rotation;

        /**
         * <code>PositionFrame</code>: The position frame of this
         * <code>AnimationSchemaAttributes.KeyFrame</code> instance.
         */
        private final PositionFrame position;

        /**
         * <code>ScaleFrame</code>: The scale frame of this
         * <code>AnimationSchemaAttributes.KeyFrame</code> instance.
         */
        private final ScaleFrame scale;

        /**
         * Creates a new instance of the <code>AnimationSchemaAttributes.KeyFrame</code>
         * class.
         * 
         * @param keyframeJson <code>TracedDictionary</code>: The json from which this
         *                     <code>AnimationSchemaAttributes.KeyFrame</code> instance
         *                     should be constructed.
         * @param timestamp    <code>double</code>: The timestamp of this
         *                     <code>AnimationSchemaAttributes.KeyFrame</code> instance.
         * @throws LoggedException Thrown to indicate any raised exception while
         *                         building this key frame.
         */
        private KeyFrame(TracedDictionary keyframeJson, double timestamp) throws LoggedException {

            TracedEntry<TracedDictionary> rotationEntry = keyframeJson.getAsDictionary("rotation", true);
            TracedDictionary rotationJson = rotationEntry.getValue();
            rotation = rotationEntry.containsValue() ? new RotationFrame(rotationJson, timestamp) : null;

            TracedEntry<TracedDictionary> positionEntry = keyframeJson.getAsDictionary("position", true);
            TracedDictionary positionJson = positionEntry.getValue();
            position = positionEntry.containsValue() ? new PositionFrame(positionJson, timestamp) : null;

            TracedEntry<TracedDictionary> scaleEntry = keyframeJson.getAsDictionary("scale", true);
            TracedDictionary scaleJson = scaleEntry.getValue();
            scale = scaleEntry.containsValue() ? new ScaleFrame(scaleJson, timestamp) : null;
        }

        /**
         * Retrieves the position of this
         * <code>AnimationSchemaAttributes.KeyFrame</code> instance.
         * 
         * @return <code>PositionFrame</code>: The <code>position</code> field of this
         *         <code>AnimationSchemaAttributes.KeyFrame</code> instance.
         */
        public PositionFrame getPosition() {

            return position;
        }

        /**
         * Retrieves the rotation of this
         * <code>AnimationSchemaAttributes.KeyFrame</code> instance.
         * 
         * @return <code>RotationFrame</code>: The <code>rotation</code> field of this
         *         <code>AnimationSchemaAttributes.KeyFrame</code> instance.
         */
        public RotationFrame getRotation() {

            return rotation;
        }

        /**
         * Retrieves the scale of this <code>AnimationSchemaAttributes.KeyFrame</code>
         * instance.
         * 
         * @return <code>ScaleFrame</code>: The <code>scale</code> field of this
         *         <code>AnimationSchemaAttributes.KeyFrame</code> instance.
         */
        public ScaleFrame getScale() {

            return scale;
        }
    }

    @Override
    protected void finalizeData() {

        if (keyframes != null) {

            keyframes.finalizeData();
        }

        if (timestampsSorted != null) {

            timestampsSorted.finalizeData();
        }

        if (bones != null) {

            bones.finalizeData();
        }
    }
}
