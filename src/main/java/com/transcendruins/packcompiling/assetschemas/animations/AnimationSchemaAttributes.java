package com.transcendruins.packcompiling.assetschemas.animations;

import java.util.ArrayList;
import java.util.HashMap;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.animations.interpolation.PermutationFrame;
import com.transcendruins.packcompiling.assetschemas.animations.interpolation.PositionFrame;
import com.transcendruins.packcompiling.assetschemas.animations.interpolation.RotationFrame;
import com.transcendruins.packcompiling.assetschemas.animations.interpolation.ScaleFrame;
import com.transcendruins.utilities.Sorter;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.InvalidKeyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
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

    private final Boolean holdOnFinish;

    public Boolean getHoldOnFinish() {

        return holdOnFinish;
    }

    private final Boolean loopOnFinish;

    public Boolean getLoopOnFinish() {

        return loopOnFinish;
    }

    private final Boolean cycleOnFinish;

    public Boolean getCycleOnFinish() {

        return cycleOnFinish;
    }

    /**
     * <code>FinalizedList&lt;FinalizedMap&lt;String, AnimationSchemaAttributes.KeyFrame&gt;&gt;</code>:
     * The key frame time stamps of this <code>AnimationSchemaAttributes</code>
     * instance paired with the key frames of the bone actors.
     */
    private final FinalizedList<FinalizedMap<String, KeyFrame>> keyframes;

    /**
     * Retrieves the keyframes of this <code>AnimationSchemaAttributes</code>
     * instance.
     * 
     * @return <code>FinalizedList&lt;FinalizedMap&lt;String, KeyFrame&gt;&gt;</code>:
     *         The <code>keyframes</code> field of this
     *         <code>AnimationSchemaAttributes</code> instance.
     */
    public FinalizedList<FinalizedMap<String, KeyFrame>> getKeyframes() {

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

        TracedEntry<String> loopModeEntry = schemaJson.getAsString("loopMode", true, isBase ? "once" : null);
        String loopModeString = loopModeEntry.getValue();

        switch (loopModeString) {

            case "once" -> {

                holdOnFinish = false;
                loopOnFinish = false;
                cycleOnFinish = false;
            }

            case "hold" -> {

                holdOnFinish = true;
                loopOnFinish = false;
                cycleOnFinish = false;
            }

            case "loop" -> {

                holdOnFinish = true;
                loopOnFinish = true;
                cycleOnFinish = false;
            }

            case "cycle" -> {

                holdOnFinish = true;
                loopOnFinish = true;
                cycleOnFinish = true;
            }

            case null -> {

                holdOnFinish = null;
                loopOnFinish = null;
                cycleOnFinish = null;
            }

            default -> throw new UnexpectedValueException(loopModeEntry);
        }

        // Key frames should be required if this attribute set is the base attribute
        // set.
        TracedEntry<TracedDictionary> keyframesEntry = schemaJson.getAsDictionary("keyframes", !isBase);
        animationDefinition = keyframesEntry.containsValue();

        if (animationDefinition) {

            keyframes = new FinalizedList<>();
            bones = new FinalizedSet<>();

            TracedDictionary keyframesJson = keyframesEntry.getValue();

            // If there are keyframe entries, a new length should be defined.
            TracedEntry<Double> lengthEntry = schemaJson.getAsDouble("length", false, null, 0.0, null);
            length = lengthEntry.getValue();

            ArrayList<Double> timestampsList = new ArrayList<>();

            HashMap<Double, FinalizedMap<String, KeyFrame>> tempKeyframes = new HashMap<>();

            for (String keyframe : keyframesJson.getKeys()) {

                // If the keyframe cannot be converted to a double value, raise an exception.
                if (!keyframe.matches(KEYFRAME_PATTERN)) {

                    throw new InvalidKeyException(keyframesEntry, keyframe);
                }

                // Generates the timestamp of the keyframe.
                Double keyframeTimestamp = Double.valueOf(keyframe);

                // If the timestamp of the keyframe is outside of the animation length, raise an
                // exception.
                if (keyframeTimestamp < 0.0 || keyframeTimestamp > length
                        || tempKeyframes.containsKey(keyframeTimestamp)) {

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

                tempKeyframes.put(keyframeTimestamp, boneMap);
            }

            // Sort the list of timestamps and apply it to this attribute set.
            timestampsSorted = new FinalizedList<>(TIMESTAMP_SORTER.sort(timestampsList));

            for (double timestamp : timestampsSorted) {

                keyframes.add(tempKeyframes.get(timestamp));
            }

            for (String bone : bones) {

                for (int t = 0; t < keyframes.size(); t++) {

                    KeyFrame b = keyframes.get(t).computeIfAbsent(bone, (_) -> new KeyFrame());

                    if (b.baseComplete) {

                        continue;
                    }

                    for (int i = 1; i < timestampsSorted.size(); i++) {

                        int iL = t - i;
                        if (!b.last.isComplete() && (iL >= 0 || cycleOnFinish)) {

                            if (iL < 0) {

                                iL += timestampsSorted.size();
                            }

                            if (keyframes.get(iL).containsKey(bone)) {

                                b.last.applyPermutation(keyframes.get(iL).get(bone).base);
                            }
                        }

                        int iN = t + i;
                        if (!b.next.isComplete() && (iN < timestampsSorted.size() || cycleOnFinish)) {

                            if (iN >= timestampsSorted.size()) {

                                iN -= timestampsSorted.size();
                            }

                            if (keyframes.get(iN).containsKey(bone)) {

                                b.next.applyPermutation(keyframes.get(iN).get(bone).base);
                            }
                        }
                    }
                }
            }
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
         * <code>int</code>: An enum constant representing the next keyframe.
         */
        public static final int NEXT = 0;

        /**
         * <code>int</code>: An enum constant representing the last keyframe.
         */
        public static final int LAST = 1;

        private final boolean baseComplete;

        private final PermutationFrame base;

        private final PermutationFrame last = new PermutationFrame();
        private final PermutationFrame next = new PermutationFrame();

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

            TracedEntry<TracedDictionary> positionEntry = keyframeJson.getAsDictionary("position", true);
            PositionFrame position = positionEntry.containsValue()
                    ? new PositionFrame(positionEntry.getValue(), timestamp, length)
                    : null;

            TracedEntry<TracedDictionary> rotationEntry = keyframeJson.getAsDictionary("rotation", true);
            RotationFrame rotation = rotationEntry.containsValue()
                    ? new RotationFrame(rotationEntry.getValue(), timestamp, length)
                    : null;

            TracedEntry<TracedDictionary> scaleEntry = keyframeJson.getAsDictionary("scale", true);
            ScaleFrame scale = scaleEntry.containsValue()
                    ? new ScaleFrame(scaleEntry.getValue(), timestamp, length)
                    : null;

            base = new PermutationFrame(position, rotation, scale);
            baseComplete = base.isComplete();
        }

        private KeyFrame() {

            base = new PermutationFrame();
            baseComplete = false;
        }

        public PositionFrame getPosition(int version) {

            if (baseComplete || base.getPosition() != null) {

                return base.getPosition();
            }

            if ((version == NEXT && next.getPosition() != null) || last.getPosition() == null) {

                return next.getPosition();
            }

            return last.getPosition();
        }

        public RotationFrame getRotation(int version) {

            if (baseComplete || base.getRotation() != null) {

                return base.getRotation();
            }

            if (version == NEXT && next.getRotation() != null) {

                return next.getRotation();
            }

            return last.getRotation();
        }

        public ScaleFrame getScale(int version) {

            if (baseComplete || base.getScale() != null) {

                return base.getScale();
            }

            if (version == NEXT && next.getScale() != null) {

                return next.getScale();
            }

            return last.getScale();
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
