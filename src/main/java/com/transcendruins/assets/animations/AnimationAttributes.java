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

package com.transcendruins.assets.animations;

import java.util.ArrayList;
import java.util.HashMap;

import com.transcendruins.assets.animations.interpolation.PermutationFrame;
import com.transcendruins.assets.animations.interpolation.PositionFrame;
import com.transcendruins.assets.animations.interpolation.RotationFrame;
import com.transcendruins.assets.animations.interpolation.ScaleFrame;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.KeyNameException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>AnimationAttributes</code>: A class which represents the attributes of
 * an <code>AnimationSchema</code> instance.
 */
public final class AnimationAttributes extends AssetAttributes {

    /**
     * <code>String</code>: The regular expression used to ensure all key frames are
     * of the expected pattern.
     */
    private static final String KEYFRAME_PATTERN = "[-+]?\\d*\\.\\d+";

    /**
     * <code>Double</code>: The starting timestamp of this
     * <code>AnimationAttributes</code> instance, in seconds.
     */
    private final Double startingTimestamp;

    /**
     * Retrieves the starting timestamp of this <code>AnimationAttributes</code>
     * instance.
     * 
     * @return <code>Double</code>: The <code>startingTimestamp</code> field of this
     *         <code>AnimationAttributes</code> instance.
     */
    public Double getStartingTimestamp() {

        return startingTimestamp;
    }

    /**
     * <code>Boolean</code>: Whether or not this <code>AnimationAttributes</code>
     * instance plays in reverse.
     */
    private final Boolean reversed;

    /**
     * Retrieves whether or not this <code>AnimationAttributes</code> instance plays
     * in reverse.
     * 
     * @return <code>Boolean</code>: The <code>reversed</code> field of this
     *         <code>AnimationAttributes</code> instance.
     */
    public Boolean getReversed() {

        return reversed;
    }

    /**
     * <code>Double</code>: The playback speed of this
     * <code>AnimationAttributes</code> instance.
     */
    private final Double playbackSpeed;

    /**
     * Retrieves the playback speed of this <code>AnimationAttributes</code>
     * instance.
     * 
     * @return <code>Double</code>: The <code>playbackSpeed</code> field of this
     *         <code>AnimationAttributes</code> instance.
     */
    public Double getPlaybackSpeed() {

        return playbackSpeed;
    }

    /**
     * <code>Double</code>: The animation length of this
     * <code>AnimationAttributes</code> instance in seconds.
     */
    private final Double length;

    /**
     * Retrieves the length of this <code>AnimationAttributes</code> instance.
     * 
     * @return <code>Double</code>: The <code>length</code> field of this
     *         <code>AnimationAttributes</code> instance.
     */
    public Double getLength() {

        return length;
    }

    /**
     * <code>Boolean</code>: Whether or not this <code>AnimationAttributes</code>
     * instance should hold on its final frame upon finishing.
     */
    private final Boolean holdOnFinish;

    /**
     * Retrieves whether or not this <code>AnimationAttributes</code> instance
     * should hold on its final frame upon finishing.
     * 
     * @return <code>Boolean</code>: The <code>holdOnFinish</code> property of this
     *         <code>AnimationAttributes</code> instance.
     */
    public Boolean getHoldOnFinish() {

        return holdOnFinish;
    }

    /**
     * <code>Boolean</code>: Whether or not this <code>AnimationAttributes</code>
     * instance should loop upon finishing.
     */
    private final Boolean loopOnFinish;

    /**
     * Retrieves whether or not this <code>AnimationAttributes</code> instance
     * should loop upon finishing.
     * 
     * @return <code>Boolean</code>: The <code>loopOnFinish</code> property of this
     *         <code>AnimationAttributes</code> instance.
     */
    public Boolean getLoopOnFinish() {

        return loopOnFinish;
    }

    /**
     * <code>Boolean</code>: Whether or not this <code>AnimationAttributes</code>
     * instance should cycle upon finishing.
     */
    private final Boolean cycleOnFinish;

    /**
     * Retrieves whether or not this <code>AnimationAttributes</code> instance
     * should cycle upon finishing.
     * 
     * @return <code>Boolean</code>: The <code>cycleOnFinish</code> property of this
     *         <code>AnimationAttributes</code> instance.
     */
    public Boolean getCycleOnFinish() {

        return cycleOnFinish;
    }

    /**
     * <code>ImmutableList&lt;ImmutableMap&lt;String, AnimationAttributes.KeyFrame&gt;&gt;</code>:
     * The key frame time stamps of this <code>AnimationAttributes</code> instance
     * paired with the key frames of the bone actors.
     */
    private final ImmutableList<ImmutableMap<String, KeyFrame>> keyframes;

    /**
     * Retrieves the keyframes of this <code>AnimationAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;ImmutableMap&lt;String, KeyFrame&gt;&gt;</code>:
     *         The <code>keyframes</code> field of this
     *         <code>AnimationAttributes</code> instance.
     */
    public ImmutableList<ImmutableMap<String, KeyFrame>> getKeyframes() {

        return keyframes;
    }

    /**
     * <code>ImmutableList&lt;Double&gt;</code>: The sorted list of timestamps of
     * this <code>AnimationAttributes</code> instance.
     */
    private final ImmutableList<Double> timestamps;

    /**
     * Retrieves the sorted timestamps of this <code>AnimationAttributes</code>
     * instance.
     * 
     * @return <code>ImmutableList&lt;Double&gt;</code>: The
     *         <code>timestampsSorted</code> field of this
     *         <code>AnimationAttributes</code> instance.
     */
    public ImmutableList<Double> getTimestamps() {

        return timestamps;
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The set of all bones of this
     * <code>AnimationAttributes</code> instance.
     */
    private final ImmutableList<String> bones;

    /**
     * Retrieves the bones of this <code>AnimationAttributes</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The <code>bones</code>
     *         field of this <code>AnimationAttributes</code> instance.
     */
    public ImmutableList<String> getBones() {

        return bones;
    }

    /**
     * Compiles this <code>AnimationAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>AnimationAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>AnimationAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>AnimationAttributes</code> instance is the base attribute
     *               set of an <code>AnimationSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>AnimationAttributes</code> instance.
     */
    public AnimationAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        TracedEntry<Double> startingTimestampEntry = json.getAsDouble("startingTimestamp", true, null);
        startingTimestamp = startingTimestampEntry.getValue();

        TracedEntry<Boolean> reversedEntry = json.getAsBoolean("reversed", true, null);
        reversed = reversedEntry.getValue();

        TracedEntry<Double> playbackSpeedEntry = json.getAsDouble("playbackSpeed", true, null, num -> num >= 0.0);
        playbackSpeed = playbackSpeedEntry.getValue();

        TracedEntry<String> loopModeEntry = json.getAsString("loopMode", true, null);

        if (loopModeEntry.containsValue()) {

            switch (loopModeEntry.getValue()) {

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

            default -> throw new UnexpectedValueException(loopModeEntry);
            }
        } else {

            holdOnFinish = null;
            loopOnFinish = null;
            cycleOnFinish = null;
        }

        // The keyframes should only be defined once.
        if (isBase) {

            TracedEntry<TracedDictionary> keyframesEntry = json.getAsDict("keyframes", false);
            TracedDictionary keyframesJson = keyframesEntry.getValue();

            ArrayList<String> bonesList = new ArrayList<>();

            // If there are keyframe entries, a new length should be defined.
            TracedEntry<Double> lengthEntry = json.getAsDouble("length", false, null, num -> num >= 0.0);
            length = lengthEntry.getValue();

            ArrayList<Double> timestampsList = new ArrayList<>();

            HashMap<Double, ImmutableMap<String, KeyFrame>> tempKeyframes = new HashMap<>();

            for (String keyframe : keyframesJson) {

                // If the keyframe cannot be converted to a double value, raise an exception.
                if (!keyframe.matches(KEYFRAME_PATTERN)) {

                    throw new KeyNameException(keyframesJson, keyframe);
                }

                // Generates the timestamp of the keyframe.
                Double keyframeTimestamp = Double.valueOf(keyframe);

                // If the timestamp of the keyframe is outside of the animation length, raise an
                // exception.
                if (keyframeTimestamp < 0.0 || keyframeTimestamp > length
                        || tempKeyframes.containsKey(keyframeTimestamp)) {

                    throw new KeyNameException(keyframesJson, keyframe);
                }

                TracedEntry<TracedDictionary> keyframeBoneMapEntry = keyframesJson.getAsDict(keyframe, false);
                TracedDictionary keyframeBoneMapJson = keyframeBoneMapEntry.getValue();

                // Adds the timestamp to the sorted list of timestamps
                timestampsList.add(keyframeTimestamp);

                // Retrieve the map of bones to their key frames from the key frame.
                HashMap<String, KeyFrame> boneMap = new HashMap<>();

                bonesList.addAll(keyframeBoneMapJson.getKeys());

                // Iterate through each bone and process its key frame.
                for (String bone : keyframeBoneMapJson) {

                    TracedEntry<TracedDictionary> keyframeEntry = keyframeBoneMapJson.getAsDict(bone, false);
                    TracedDictionary keyframeJson = keyframeEntry.getValue();
                    boneMap.put(bone, new KeyFrame(keyframeJson, keyframeTimestamp));
                }

                tempKeyframes.put(keyframeTimestamp, new ImmutableMap<>(boneMap));
            }

            bones = new ImmutableList<>(bonesList);

            // Sort the list of timestamps and apply it to this attribute set.
            timestamps = new ImmutableList<>(timestampsList.stream().sorted().toList());

            ArrayList<ImmutableMap<String, KeyFrame>> keyframesList = new ArrayList<>();
            for (double timestamp : timestamps) {

                keyframesList.add(tempKeyframes.get(timestamp));
            }
            keyframes = new ImmutableList<>(keyframesList);

            for (String bone : bones) {

                for (int t = 0; t < keyframes.size(); t++) {

                    KeyFrame b = keyframes.get(t).computeIfAbsent(bone, (_) -> new KeyFrame());

                    if (b.baseComplete) {

                        continue;
                    }

                    for (int i = 1; i < timestamps.size(); i++) {

                        int iL = t - i;
                        if (!b.last.isComplete() && (iL >= 0 || cycleOnFinish)) {

                            if (iL < 0) {

                                iL += timestamps.size();
                            }

                            if (keyframes.get(iL).containsKey(bone)) {

                                b.last.applyPermutation(keyframes.get(iL).get(bone).base);
                            }
                        }

                        int iN = t + i;
                        if (!b.next.isComplete() && (iN < timestamps.size() || cycleOnFinish)) {

                            if (iN >= timestamps.size()) {

                                iN -= timestamps.size();
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
            timestamps = null;
        }
    }

    /**
     * <code>AnimationAttributes.KeyFrame</code>: A class representing a keyframe in
     * an animation.
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
         * Creates a new instance of the <code>AnimationAttributes.KeyFrame</code>
         * class.
         * 
         * @param keyframeJson <code>TracedDictionary</code>: The JSON from which this
         *                     <code>AnimationAttributes.KeyFrame</code> instance should
         *                     be constructed.
         * @param timestamp    <code>double</code>: The timestamp of this
         *                     <code>AnimationAttributes.KeyFrame</code> instance.
         * @throws LoggedException Thrown to indicate any raised exception while
         *                         creating this key frame.
         */
        private KeyFrame(TracedDictionary keyframeJson, double timestamp) throws LoggedException {

            TracedEntry<TracedDictionary> positionEntry = keyframeJson.getAsDict("position", true);
            PositionFrame position = positionEntry.containsValue()
                    ? new PositionFrame(positionEntry.getValue(), timestamp, length)
                    : null;

            TracedEntry<TracedDictionary> rotationEntry = keyframeJson.getAsDict("rotation", true);
            RotationFrame rotation = rotationEntry.containsValue()
                    ? new RotationFrame(rotationEntry.getValue(), timestamp, length)
                    : null;

            TracedEntry<TracedDictionary> scaleEntry = keyframeJson.getAsDict("scale", true);
            ScaleFrame scale = scaleEntry.containsValue() ? new ScaleFrame(scaleEntry.getValue(), timestamp, length)
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
}
