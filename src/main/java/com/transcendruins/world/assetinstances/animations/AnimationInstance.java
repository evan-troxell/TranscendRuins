package com.transcendruins.world.assetinstances.animations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.transcendruins.geometry.interpolation.PositionFrame;
import com.transcendruins.geometry.interpolation.PositionModifier;
import com.transcendruins.geometry.interpolation.RotationFrame;
import com.transcendruins.geometry.interpolation.RotationModifier;
import com.transcendruins.geometry.interpolation.ScaleFrame;
import com.transcendruins.geometry.interpolation.ScaleModifier;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchema;
import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchemaComponents;
import com.transcendruins.rendering.Model.BoneActor;
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
     * <code>HashMap&lt;Double, HashMap&lt;String, AnimationSchemaComponents.KeyFrame&gt;&gt;</code>: The key frames of this <code>AnimationInstance</code> instance.
     */
    private HashMap<Double, HashMap<String, AnimationSchemaComponents.KeyFrame>> keyFrames;

    /**
     * <code>HashSet&lt;String&gt;</code>: The set of all bones in this <code>AnimationInstance</code> instance.
     */
    private HashSet<String> bones;

    /**
     * <code>ArrayList&lt;Double&gt;</code>: The list of sorted timestamps of this <code>AnimationInstance</code> instance.
     */
    private ArrayList<Double> timestampsSorted;

    /**
     * Creates a new instance of the <code>AnimationInstance</code> class.
     * @param schema <code>AnimationSchema</code>: The schema used to generate this <code>AnimationInstance</code> instance.
     */
    public AnimationInstance(AnimationSchema schema) {

        super(schema);
    }

    /**
     * Applies a component set to this <code>AnimationInstance</code> instance.
     * @param componentSet <code>AssetSchemaComponents</code>: The component set to apply.
     */
    @Override
    protected void applyComponentSet(AssetSchemaComponents componentSet) {

        AnimationSchemaComponents components = (AnimationSchemaComponents) componentSet;

        if (components.getAnimationDefinition()) {

            length = components.getLength();
            keyFrames = components.getKeyFrames();
            bones = components.getBones();
            timestampsSorted = components.getTimeStampsSorted();
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
     * @param timestamps <code>ArrayList&lt;Double&gt</code>: The timestamps to build using.
     * @return <code>HashMap&lt;String, BoneKeyFrame&gt;</code>: The generated map of bones to their key frames.
     */
    private HashMap<String, BoneKeyFrame> buildKeyFrames(ArrayList<Double> timestamps) {

        HashMap<String, BoneKeyFrame> boneKeyFrames = new HashMap<>();

        for (double newTimestamp : timestamps) {

            HashMap<String, AnimationSchemaComponents.KeyFrame> bonesTimestampMap = keyFrames.get(newTimestamp);

            for (Map.Entry<String, AnimationSchemaComponents.KeyFrame> boneEntry : bonesTimestampMap.entrySet()) {

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
         * @param keyframe <code>AnimationSchemaComponents.KeyFrame</code>: The keyframe to assign.
         */
        private void update(AnimationSchemaComponents.KeyFrame keyframe) {

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
