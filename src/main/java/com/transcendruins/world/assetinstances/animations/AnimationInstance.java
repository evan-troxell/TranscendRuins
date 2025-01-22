package com.transcendruins.world.assetinstances.animations;

import java.util.Collections;
import java.util.HashMap;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchemaAttributes.KeyFrame;
import com.transcendruins.packcompiling.assetschemas.animations.interpolation.PositionFrame;
import com.transcendruins.packcompiling.assetschemas.animations.interpolation.PositionModifier;
import com.transcendruins.packcompiling.assetschemas.animations.interpolation.RotationFrame;
import com.transcendruins.packcompiling.assetschemas.animations.interpolation.RotationModifier;
import com.transcendruins.packcompiling.assetschemas.animations.interpolation.ScaleFrame;
import com.transcendruins.packcompiling.assetschemas.animations.interpolation.ScaleModifier;
import com.transcendruins.rendering.Model.BoneActor;
import com.transcendruins.utilities.finalize.FinalizedList;
import com.transcendruins.utilities.finalize.FinalizedMap;
import com.transcendruins.utilities.finalize.FinalizedSet;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.AssetInstance;

/**
 * <code>AnimationInstance</code>: A class representing a generated animation
 * instance.
 */
public final class AnimationInstance extends AssetInstance {

    /**
     * <code>double</code>: The playback speed of this
     * <code>AnimationInstance</code> instance.
     */
    private final double playbackSpeed;

    /**
     * <code>double>/code>: The starting timestamp of this <code>AnimationInstance</code>
     * instance.
     */
    private final double startingTimestamp;

    /**
     * <code>boolean>/code>: Whether or not this <code>AnimationInstance</code>
     * instance is playing in reverse.
     */
    private final boolean reversed;

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
    public double getLength() {

        return length;
    }

    private boolean holdOnFinish;

    public boolean getHoldOnFinish() {

        return holdOnFinish;
    }

    private boolean loopOnFinish;

    public boolean getLoopOnFinish() {

        return loopOnFinish;
    }

    private boolean cycleOnFinish;

    public boolean getCycleOnFinish() {

        return cycleOnFinish;
    }

    /**
     * <code>FinalizedList&lt;FinalizedMap&lt;String, AnimationSchemaAttributes.KeyFrame&gt;&gt;</code>:
     * The key frames of this <code>AnimationInstance</code> instance.
     */
    private FinalizedList<FinalizedMap<String, AnimationSchemaAttributes.KeyFrame>> keyframes;

    /**
     * <code>FinalizedSet&lt;String&gt;</code>: The set of all bones in this
     * <code>AnimationInstance</code> instance.
     */
    private FinalizedSet<String> bones;

    /**
     * <code>FinalizedList&lt;Double&gt;</code>: The list of sorted timestamps of
     * this <code>AnimationInstance</code> instance.
     */
    private FinalizedList<Double> timestampsSorted;

    /**
     * Creates a new instance of the <code>AnimationInstance</code> class.
     * 
     * @param presets <code>AnimationPresets</code>: The presets used to
     *                generate this
     *                <code>AnimationInstance</code> instance.
     * @param world   <code>World</code>: The world copy to assign to this
     *                <code>AnimationInstance</code> instance.
     */
    public AnimationInstance(AnimationPresets presets, World world) {

        super(presets, world);

        this.startingTimestamp = presets.getStartingTimestamp();
        this.reversed = presets.getReversed();
        this.playbackSpeed = presets.getPlaybackSpeed();
    }

    /**
     * Applies an attribute set to this <code>AnimationInstance</code> instance.
     * 
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to
     *                     apply.
     */
    @Override
    protected void applyAttributeSet(AssetSchemaAttributes attributeSet) {

        AnimationSchemaAttributes attributes = (AnimationSchemaAttributes) attributeSet;

        if (attributes.getHoldOnFinish() != null) {

            holdOnFinish = attributes.getHoldOnFinish();
        }

        if (attributes.getLoopOnFinish() != null) {

            loopOnFinish = attributes.getLoopOnFinish();
        }

        if (attributes.getCycleOnFinish() != null) {

            cycleOnFinish = attributes.getCycleOnFinish();
        }

        if (attributes.getAnimationDefinition()) {

            length = attributes.getLength();
            keyframes = attributes.getKeyframes();
            bones = attributes.getBones();
            timestampsSorted = attributes.getTimestampsSorted();
        }
    }

    /**
     * Retrieves the keyframes of this <code>AnimationInstance</code> instance at a
     * specified timestamp.
     * 
     * @param timestamp <code>double</code>: The timestamp whose keyframes to find.
     * @return <code>HashMap&lt;String, AnimationNode&gt;</code>: The retrieved bone
     *         actors of the keyframes.
     */
    public HashMap<String, AnimationNode> getKeyFrames(double timestamp) {

        timestamp *= playbackSpeed;

        if (reversed) {

            timestamp *= -1;
        }

        timestamp += startingTimestamp;

        double loopTimestamp = timestamp % length;

        if (timestamp > length) {

            timestamp = loopOnFinish ? ((loopTimestamp == 0) ? length : loopTimestamp) : length;
        }

        if (timestamp < 0) {

            timestamp = loopOnFinish ? loopTimestamp : 0;
        }

        if (timestampsSorted == null) {

            return new HashMap<>();
        }
        Integer higherIndex = findTimestampIndex(timestamp);
        Integer lowerIndex = higherIndex - 1;

        if (higherIndex >= timestampsSorted.size()) {

            higherIndex = cycleOnFinish ? 0 : null;
        }

        if (lowerIndex < 0) {

            lowerIndex = cycleOnFinish ? timestampsSorted.size() - 1 : null;
        }

        FinalizedMap<String, KeyFrame> higherKeyframes = higherIndex == null ? null
                : keyframes.get(higherIndex);

        FinalizedMap<String, KeyFrame> lowerKeyframes = lowerIndex == null ? null
                : keyframes.get(lowerIndex);

        // Retrieve all bone keys retrieved while processing the key frames.

        HashMap<String, AnimationNode> animationNodeMap = new HashMap<>();

        for (String bone : bones) {

            KeyFrame nextFrame = higherKeyframes == null ? null : higherKeyframes.get(bone);
            KeyFrame lastFrame = lowerKeyframes == null ? null : lowerKeyframes.get(bone);

            animationNodeMap.put(bone, new AnimationNode(lastFrame, nextFrame, timestamp));
        }

        return animationNodeMap;
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

    /**
     * <code>AnimationInstance.AnimationNode</code>: A class representing a bone
     * actor (a
     * collection
     * of operations to perform on a bone when modelling its vertices).
     */
    public static final class AnimationNode {

        /**
         * <code>PositionModifier</code>: The operator used to perform matrix adding
         * on the polygons of a <code>Model</code> instance.
         */
        private final PositionModifier position;

        /**
         * <code>RotationModifier</code>: The operator used to perform matrix
         * rotations on the polygons of a <code>Model</code> instance.
         */
        private final RotationModifier rotation;

        /**
         * <code>ScaleModifier</code>: The operator used to perform matrix scaling
         * on the polygons of a <code>Model</code> instance.
         */
        private final ScaleModifier scale;

        /**
         * Creates a new instance of the <code>AnimationInstance.AnimationNode</code>
         * class.
         * 
         * @param prevFrame <code>KeyFrame</code>: The previous key frame to
         *                  interpolate from.
         * @param nextFrame <code>KeyFrame</code>: The next key frame to interpolate
         *                  to.
         * @param timestamp <code>double</code>: The timestamp to interpolate at.
         */
        public AnimationNode(KeyFrame prevFrame, KeyFrame nextFrame, double timestamp) {

            position = PositionFrame.interpolate(prevFrame, nextFrame, timestamp);
            rotation = RotationFrame.interpolate(prevFrame, nextFrame, timestamp);
            scale = ScaleFrame.interpolate(prevFrame, nextFrame, timestamp);
        }

        public void apply(BoneActor boneActor) {

            if (position != null) {

                boneActor.updatePosition(position.getTransform());
            }

            if (rotation != null) {

                boneActor.updateRotation(rotation.getTransform());
            }

            if (scale != null) {

                boneActor.updateScale(scale.getTransform());
            }
        }
    }
}
