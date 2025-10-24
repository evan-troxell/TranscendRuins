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

import java.util.Collections;
import java.util.HashMap;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.animations.AnimationAttributes.KeyFrame;
import com.transcendruins.assets.animations.boneactors.BoneActor;
import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.animations.interpolation.PositionFrame;
import com.transcendruins.assets.animations.interpolation.RotationFrame;
import com.transcendruins.assets.animations.interpolation.ScaleFrame;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;

/**
 * <code>AnimationInstance</code>: A class representing a generated animation
 * instance.
 */
public final class AnimationInstance extends AssetInstance {

    /**
     * <code>double</code>: The playback speed of this
     * <code>AnimationInstance</code> instance.
     */
    private double playbackSpeed;

    /**
     * <code>double>/code>: The starting timestamp of this <code>AnimationInstance</code>
     * instance.
     */
    private double startingTimestamp;

    /**
     * <code>boolean>/code>: Whether or not this <code>AnimationInstance</code>
     * instance is playing in reverse.
     */
    private boolean reversed;

    /**
     * <code>double</code>: The animation length of this
     * <code>AnimationInstance</code> instance.
     */
    private double length;

    /**
     * Retrieves the length of this <code>AnimationInstance</code> instance.
     * 
     * @return <code>double</code>: The <code>length</code> field of this
     *         <code>AnimationInstance</code> instance.
     */
    public final double getLength() {

        return length;
    }

    private boolean holdOnFinish;

    public final boolean getHoldOnFinish() {

        return holdOnFinish;
    }

    private boolean loopOnFinish;

    public final boolean getLoopOnFinish() {

        return loopOnFinish;
    }

    private boolean cycleOnFinish;

    public final boolean getCycleOnFinish() {

        return cycleOnFinish;
    }

    /**
     * <code>ImmutableList&lt;ImmutableMap&lt;String, KeyFrame&gt;&gt;</code>: The
     * key frames of this <code>AnimationInstance</code> instance.
     */
    private ImmutableList<ImmutableMap<String, KeyFrame>> keyframes;

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The set of all bones in this
     * <code>AnimationInstance</code> instance.
     */
    private ImmutableList<String> bones;

    /**
     * <code>ImmutableList&lt;Double&gt;</code>: The list of sorted timestamps of
     * this <code>AnimationInstance</code> instance.
     */
    private ImmutableList<Double> timestampsSorted;

    /**
     * Creates a new instance of the <code>AnimationInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>AnimationInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public AnimationInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        AnimationContext context = (AnimationContext) assetContext;
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        AnimationAttributes attributes = (AnimationAttributes) attributeSet;

        // Updates the startingTimestamp field.
        startingTimestamp = calculateAttribute(attributes.getStartingTimestamp(), startingTimestamp, attributes, 0.0);
        setProperty("startingTimestamp", startingTimestamp);

        // Updates the reversed field.
        reversed = calculateAttribute(attributes.getReversed(), reversed, attributes, false);
        setProperty("reversed", reversed);

        // Updates the playbackSpeed field.
        playbackSpeed = calculateAttribute(attributes.getPlaybackSpeed(), playbackSpeed, attributes, 1.0);
        setProperty("playbackSpeed", playbackSpeed);

        // Updates the holdOnFinish field.
        holdOnFinish = calculateAttribute(attributes.getHoldOnFinish(), holdOnFinish, attributes, false);
        setProperty("holdOnFinish", holdOnFinish);

        // Updates the loopOnFinish field.
        loopOnFinish = calculateAttribute(attributes.getLoopOnFinish(), loopOnFinish, attributes, false);
        setProperty("loopOnFinish", loopOnFinish);

        // Updates the cycleOnFinish field.
        cycleOnFinish = calculateAttribute(attributes.getCycleOnFinish(), cycleOnFinish, attributes, false);
        setProperty("cycleOnFinish", cycleOnFinish);

        // Updates the length field.
        length = calculateAttribute(attributes.getLength(), length);
        setProperty("length", length);

        // Updates the keyframes field.
        keyframes = calculateAttribute(attributes.getKeyframes(), keyframes);
        setProperty("keyframes", keyframes);

        // Updates the bones field.
        bones = calculateAttribute(attributes.getBones(), bones);
        setProperty("bones", bones);

        // Updates the timestampsSorted field.
        timestampsSorted = calculateAttribute(attributes.getTimestamps(), timestampsSorted);
        setProperty("timestampsSorted", timestampsSorted);
    }

    @Override
    protected void onUpdate(double time) {
    }

    /**
     * Retrieves the timestamp of this <code>AnimationInstance</code> instance at a
     * certain time.
     * 
     * @param time <code>double</code>: The time to convert, assuming 0.0 is the
     *             start of the animation.
     * @return <code>double</code>: The created timestamp.
     */
    public double getTimestamp(double time) {

        double timestamp = time * playbackSpeed * ((reversed) ? -1 : 1) + startingTimestamp;

        double loopTimestamp = timestamp % length;

        if (timestamp > length) {

            return loopOnFinish ? ((loopTimestamp == 0) ? length : loopTimestamp) : length;
        }

        if (timestamp < 0) {

            return loopOnFinish ? loopTimestamp : 0;
        }

        return timestamp;
    }

    /**
     * Retrieves the keyframes of this <code>AnimationInstance</code> instance at a
     * specified timestamp.
     * 
     * @param timestamp <code>double</code>: The timestamp whose keyframes to find.
     * @return <code>BoneActorSet</code>: The retrieved bone actors of the
     *         keyframes.
     */
    public BoneActorSet getKeyFrames(double timestamp) {

        timestamp = getTimestamp(timestamp);

        if (timestampsSorted == null) {

            return new BoneActorSet();
        }
        Integer higherIndex = findTimestampIndex(timestamp);
        Integer lowerIndex = higherIndex - 1;

        if (higherIndex >= timestampsSorted.size()) {

            higherIndex = cycleOnFinish ? 0 : null;
        }

        if (lowerIndex < 0) {

            lowerIndex = cycleOnFinish ? timestampsSorted.size() - 1 : null;
        }

        ImmutableMap<String, KeyFrame> higherKeyframes = higherIndex == null ? null : keyframes.get(higherIndex);

        ImmutableMap<String, KeyFrame> lowerKeyframes = lowerIndex == null ? null : keyframes.get(lowerIndex);

        // Retrieve all bone keys retrieved while processing the key frames.

        HashMap<String, BoneActor> animationNodeMap = new HashMap<>();

        for (String bone : bones) {

            KeyFrame nextFrame = higherKeyframes == null ? null : higherKeyframes.get(bone);
            KeyFrame lastFrame = lowerKeyframes == null ? null : lowerKeyframes.get(bone);

            Vector position = PositionFrame.interpolate(lastFrame, nextFrame, timestamp);
            Quaternion rotation = RotationFrame.interpolate(lastFrame, nextFrame, timestamp);
            Matrix scale = ScaleFrame.interpolate(lastFrame, nextFrame, timestamp);

            animationNodeMap.put(bone, new BoneActor(position, rotation, scale));
        }

        return new BoneActorSet(animationNodeMap);
    }

    /**
     * Finds the first timestamp higher than the input timestamp in this
     * <code>AnimationInstance</code> instance.
     * 
     * @param timestamp <code>double</code>: The timestamp to search for.
     * @return <code>int</code>: The index of the first timestamp greater than the
     *         <code>timestamp</code> field If the index is the length of the
     *         timestamps list, then there are no timestamps greater than the
     *         <code>timestamp</code> perameter.
     */
    private int findTimestampIndex(double timestamp) {

        // The timestamps list is already sorted, so it is a candidate for a binary
        // sort.
        int higherIndex = Collections.binarySearch(timestampsSorted, timestamp);

        // A positive index is returned if the timestamp is found, in which case it can
        // be returned.
        if (higherIndex >= 0) {

            return higherIndex;
        } else {

            // Calculate for the expected index of the timestamp in the list if the
            // timestamp is not found.
            higherIndex = -(higherIndex + 1);
        }

        return higherIndex;
    }
}
