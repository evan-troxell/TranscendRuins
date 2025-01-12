package com.transcendruins.world.assetinstances.animations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.transcendruins.graphics3d.interpolation.PositionFrame;
import com.transcendruins.graphics3d.interpolation.PositionModifier;
import com.transcendruins.graphics3d.interpolation.RotationFrame;
import com.transcendruins.graphics3d.interpolation.RotationModifier;
import com.transcendruins.graphics3d.interpolation.ScaleFrame;
import com.transcendruins.graphics3d.interpolation.ScaleModifier;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchema;
import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchemaAttributes;
import com.transcendruins.rendering.Model.BoneActor;
import com.transcendruins.utilities.finalize.FinalizedList;
import com.transcendruins.utilities.finalize.FinalizedMap;
import com.transcendruins.utilities.finalize.FinalizedSet;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.AssetInstance;

/**
 * <code>AnimationInstance</code>: A class representing a generated animation instance.
 */
public final class AnimationInstance extends AssetInstance {

    /**
     * <code>double</code>: The animation length of this <code>AnimationInstance</code> instance.
     */
    private double length;

    /**
     * <code>boolean</code>: Whether or not this <code>AnimationInstance</code> is looping.
     */
    private boolean looping = false;

    /**
     * <code>FinalizedMap&lt;Double, FinalizedMap&lt;String, AnimationSchemaAttributes.KeyFrame&gt;&gt;</code>: The key frames of this <code>AnimationInstance</code> instance.
     */
    private FinalizedMap<Double, FinalizedMap<String, AnimationSchemaAttributes.KeyFrame>> keyframes;

    /**
     * <code>FinalizedSet&lt;String&gt;</code>: The set of all bones in this <code>AnimationInstance</code> instance.
     */
    private FinalizedSet<String> bones;

    /**
     * <code>FinalizedList&lt;Double&gt;</code>: The list of sorted timestamps of this <code>AnimationInstance</code> instance.
     */
    private FinalizedList<Double> timestampsSorted;

    /**
     * Creates a new instance of the <code>AnimationInstance</code> class.
     * @param schema <code>AnimationSchema</code>: The schema used to generate this <code>AnimationInstance</code> instance.
     * @param world <code>World</code>: The world copy to assign to this <code>AnimationInstance</code> instance.
     */
    public AnimationInstance(AnimationSchema schema, World world) {

        super(schema, world);
    }

    /**
     * Applies a attribute set to this <code>AnimationInstance</code> instance.
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to apply.
     */
    @Override
    protected void applyAttributeSet(AssetSchemaAttributes attributeSet) {

        AnimationSchemaAttributes attributes = (AnimationSchemaAttributes) attributeSet;

        if (attributes.getLooping() != null) {

            looping = attributes.getLooping();
        }

        if (attributes.getAnimationDefinition()) {

            length = attributes.getLength();
            keyframes = attributes.getKeyFrames();
            bones = attributes.getBones();
            timestampsSorted = attributes.getTimeStampsSorted();
        }
    }

    /**
     * Retrieves the length of this <code>AnimationInstance</code> instance.
     * @return <code>double</code>: The <code>length</code> field of this <code>AnimationInstance</code> instance.
     */
    public double getLength() {

        return length;
    }

    /**
     * Retrieves whether or not this <code>AnimationInstance</code> instance is looping.
     * @return <code>boolean</code>: The <code>looping</code> field of this <code>AnimationInstance</code> instance.
     */
    public boolean getLooping() {

        return looping;
    }

    /**
     * Retrieves the keyframes of this <code>AnimationInstance</code> instance at a specified timestamp.
     * @param timestamp <code>double</code>: The timestamp whose keyframes to find.
     * @return <code>HashMap&lt;String, BoneActor&gt;</code>: The retrieved bone actors of the keyframes.
     */
    public HashMap<String, BoneActor> getKeyFrames(double timestamp) {

        if (timestampsSorted == null) {

            return new HashMap<>();
        }
        Integer higherTimestamp = findTimestampIndex(timestamp);

        // Retrieves the timestamps lower than the input timestamp. Because they would sorted from lowest to highest, it should be reversed.
        ArrayList<Double> lowerTimestamps = new ArrayList<>(timestampsSorted.subList(0, higherTimestamp));
        Collections.reverse(lowerTimestamps);
        HashMap<String, BoneKeyFrame> lowerBoneMap = buildKeyFrames(lowerTimestamps);

        ArrayList<Double> higherTimestamps = new ArrayList<>(timestampsSorted.subList(higherTimestamp, timestampsSorted.size()));
        HashMap<String, BoneKeyFrame> higherBoneMap = buildKeyFrames(higherTimestamps);

        // Retrieve all bone keys retrieved while processing the key frames.

        HashMap<String, BoneActor> boneActorMap = new HashMap<>();

        for (String bone : bones) {

            BoneKeyFrame prevFrame = lowerBoneMap.get(bone);
            BoneKeyFrame nextFrame = higherBoneMap.get(bone);

            boneActorMap.put(bone, buildBoneActor(prevFrame, nextFrame, timestamp));
        }

        return boneActorMap;
    }

    /**
     * Finds the first timestamp higher than the input timestamp in this <code>AnimationInstance</code> instance.
     * @param timestamp <code>double</code>: The timestamp to search for.
     * @return <code>int</code>: The index of the first timestamp greater than the <code>timestamp</code> field If the index is the length of the timestamps list, then there are no timestamps greater than the <code>timestamp</code> perameter.
     */
    private Integer findTimestampIndex(double timestamp) {

        // The timestamps list is already sorted, so it is a candidate for a binary sort.
        int higherIndex = Collections.binarySearch(timestampsSorted, timestamp);

        // A positive index is returned if the timestamp is found, in which case it can be returned.
        if (higherIndex >= 0) {

            return higherIndex;
        } else {

            // Calculate for the expected index of the timestamp in the list if the timestamp is not found.
            higherIndex = -(higherIndex + 1);
        }

        return higherIndex;
    }

    /**
     * Builds the key frames of this <code>AnimationInstance</code> instance.
     * @param timestamps <code>ArrayList&lt;Double&gt;</code>: The timestamps to build using.
     * @return <code>HashMap&lt;String, BoneKeyFrame&gt;</code>: The generated map of bones to their key frames.
     */
    private HashMap<String, BoneKeyFrame> buildKeyFrames(ArrayList<Double> timestamps) {

        HashMap<String, BoneKeyFrame> boneKeyFrames = new HashMap<>();

        for (double newTimestamp : timestamps) {

            HashMap<String, AnimationSchemaAttributes.KeyFrame> bonesTimestampMap = keyframes.get(newTimestamp);

            for (Map.Entry<String, AnimationSchemaAttributes.KeyFrame> boneEntry : bonesTimestampMap.entrySet()) {

                String bone = boneEntry.getKey();

                if (!boneKeyFrames.containsKey(bone)) {

                    boneKeyFrames.put(bone, new BoneKeyFrame());
                }
                BoneKeyFrame boneKeyFrame = boneKeyFrames.get(bone);

                if (boneKeyFrame.complete()) {

                    continue;
                }
                boneKeyFrame.update(boneEntry.getValue());
            }
        }

        return boneKeyFrames;
    }

    /**
     * Generates a bone actor given a set of previous and next keyframes.
     * @param prevFrame <code>BoneKeyFrame</code>: The previous key frame to interpolate from.
     * @param nextFrame <code>BoneKeyFrame</code>: The next key frame to interpolate to.
     * @param timestamp <code>double</code>: The timestamp to interpolate at.
     * @return <code>Model.BoneActor</code>: The generated bone actor.
     */
    private BoneActor buildBoneActor(BoneKeyFrame prevFrame, BoneKeyFrame nextFrame, double timestamp) {


        PositionModifier position = prevFrame.position.interpolate(nextFrame.position, timestamp);

        RotationModifier rotation = prevFrame.rotation.interpolate(nextFrame.rotation, timestamp);

        ScaleModifier scale = prevFrame.scale.interpolate(nextFrame.scale, timestamp);

        return new BoneActor(position, rotation, scale);
    }

    /**
     * <code>AnimationInstance.BoneKeyFrame</code>: The keyframe of a bone at a specific
     */
    private class BoneKeyFrame {

        /**
         * <code>PositionFrame</code>: The nearest position keyframe of a bone to a specified timestamp.
         */
        private PositionFrame position = null;

        /**
         * <code>RotationFrame</code>: The nearest rotation keyframe of a bone to a specified timestamp.
         */
        private RotationFrame rotation = null;

        /**
         * <code>ScaleFrame</code>: The nearest scale keyframe of a bone to a specified timestamp.
         */
        private ScaleFrame scale = null;

        /**
         * Updates this <code>AnimationInstance.BoneKeyFrame</code> instance to a new keyframe.
         * @param keyframe <code>AnimationSchemaAttributes.KeyFrame</code>: The keyframe to assign.
         */
        private void update(AnimationSchemaAttributes.KeyFrame keyframe) {

            if (rotation == null && keyframe.rotation != null) {

                rotation = keyframe.rotation;
            }

            if (position == null && keyframe.position != null) {

                position = keyframe.position;
            }

            if (scale == null && keyframe.scale != null) {

                scale = keyframe.scale;
            }
        }

        /**
         * Determines whether or not this <code>AnimationInstance.BoneKeyFrame</code> instance is complete.
         * @return
         */
        private boolean complete() {

            return (position != null && rotation != null && scale != null);
        }
    }
}
