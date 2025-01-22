package com.transcendruins.world.assetinstances.animationcontrollers;

import org.json.simple.JSONObject;

import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.assetinstances.AssetPresets;

/**
 * <code>AnimationControllerPresets</code>: A class representing a the presets
 * of an animation controller instance.
 */
public final class AnimationControllerPresets extends AssetPresets {

    /**
     * Creates a new instance of the <code>AnimationControllerPresets</code> class.
     * 
     * @param identifierEntry <code>TracedEntry&lt;Identifier&gt;</code>: The
     *                        identifier entry of this
     *                        <code>AnimationControllerPresets</code>
     *                        instance.
     */
    public AnimationControllerPresets(TracedEntry<Identifier> identifierEntry) {

        super(identifierEntry, AssetType.ANIMATION_CONTROLLER);
    }

    /**
     * Creates a new instance of the <code>AnimationControllerPresets</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON of this
     *             <code>AnimationControllerPresets</code> instance.
     * 
     * @throws LoggedException Thrown if an error occurs while creating this
     *                         <code>AnimationControllerPresets</code> instance.
     */
    public AnimationControllerPresets(TracedDictionary json) throws LoggedException {

        super(json, "animationController", AssetType.ANIMATION_CONTROLLER);
    }

    public static AnimationControllerPresets createPresets(TracedCollection collection, Object key,
            boolean nullCaseAllowed) throws LoggedException {

        TracedEntry<?> presetsEntry = collection.get(key, nullCaseAllowed, null, String.class, JSONObject.class);

        return (presetsEntry.containsValue()) ? switch (presetsEntry.getValue()) {

            case String _ -> new AnimationControllerPresets(collection.getAsIdentifier(key, nullCaseAllowed));

            case TracedDictionary json -> new AnimationControllerPresets(json);

            default -> null;
        } : null;
    }
}
