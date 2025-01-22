package com.transcendruins.world.assetinstances.animations;

import org.json.simple.JSONObject;

import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.assetinstances.AssetPresets;

/**
 * <code>AnimationPresets</code>: A class representing a the presets
 * of an animation instance.
 */
public final class AnimationPresets extends AssetPresets {

    /**
     * <code>boolean</code>: Whether or not this <code>AnimationPresets</code>
     * instance is reversed.
     */
    private final boolean reversed;

    /**
     * <code>double</code>: The playback speed of this <code>AnimationPresets</code>
     * instance.
     */
    private final double playbackSpeed;

    /**
     * <code>double</code>: The starting timestamp of this
     * <code>AnimationPresets</code> instance.
     */
    private final double startingTimestamp;

    /**
     * Creates a new instance of the <code>AnimationPresets</code> class.
     * 
     * @param identifierEntry <code>TracedEntry&lt;Identifier&gt;</code>: The
     *                        identifier entry of this <code>AnimationPresets</code>
     *                        instance.
     */
    public AnimationPresets(TracedEntry<Identifier> identifierEntry) {

        super(identifierEntry, AssetType.ANIMATION);
        reversed = false;
        playbackSpeed = 1.0;
        startingTimestamp = 0.0;
    }

    /**
     * Creates a new instance of the <code>AnimationPresets</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON of this
     *             <code>AnimationPresets</code> instance.
     * 
     * @throws LoggedException Thrown if an error occurs while creating this
     *                         <code>AnimationPresets</code> instance.
     */
    public AnimationPresets(TracedDictionary json) throws LoggedException {

        super(json, "animation", AssetType.ANIMATION);

        TracedEntry<Boolean> reversedEntry = json.getAsBoolean("reversed", true, false);
        reversed = reversedEntry.getValue();

        TracedEntry<Double> playbackSpeedEntry = json.getAsDouble("playbackSpeed", true, 1.0, 0.0, null);
        playbackSpeed = playbackSpeedEntry.getValue();

        TracedEntry<Double> startingTimestampEntry = json.getAsDouble("startingTimestamp", true, 0.0);
        startingTimestamp = startingTimestampEntry.getValue();
    }

    /**
     * Retrieves whether or not this <code>AnimationPresets</code> instance is
     * reversed.
     * 
     * @return <code>boolean</code>: The <code>reversed</code> field of this
     *         <code>AnimationPresets</code> instance.
     */
    public boolean getReversed() {

        return reversed;
    }

    /**
     * Retrieves the playback speed of this <code>AnimationPresets</code> instance.
     * 
     * @return <code>double</code>: The <code>playbackSpeed</code> field of this
     *         <code>AnimationPresets</code> instance.
     */
    public double getPlaybackSpeed() {

        return playbackSpeed;
    }

    /**
     * Retrieves the starting timestamp of this <code>AnimationPresets</code>
     * instance.
     * 
     * @return <code>double</code>: The <code>startingTimestamp</code> field of this
     *         <code>AnimationPresets</code> instance.
     */
    public double getStartingTimestamp() {

        return startingTimestamp;
    }

    public static AnimationPresets createPresets(TracedCollection collection, Object key, boolean nullCaseAllowed)
            throws LoggedException {

        TracedEntry<?> presetsEntry = collection.get(key, nullCaseAllowed, null, String.class, JSONObject.class);

        return (presetsEntry.containsValue()) ? switch (presetsEntry.getValue()) {

            case String _ -> new AnimationPresets(collection.getAsIdentifier(key, nullCaseAllowed));

            case TracedDictionary json -> new AnimationPresets(json);

            default -> null;
        } : null;
    }
}
