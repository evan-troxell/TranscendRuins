package com.transcendruins.world.assetinstances.models;

import org.json.simple.JSONObject;

import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.assetinstances.AssetPresets;

/**
 * <code>ModelPresets</code>: A class representing a the presets
 * of a model instance.
 */
public final class ModelPresets extends AssetPresets {

    /**
     * Creates a new instance of the <code>ModelPresets</code> class.
     * 
     * @param identifierEntry <code>TracedEntry&lt;Identifier&gt;</code>: The
     *                        identifier entry of this <code>ModelPresets</code>
     *                        instance.
     */
    public ModelPresets(TracedEntry<Identifier> identifierEntry) {

        super(identifierEntry, AssetType.MODEL);
    }

    /**
     * Creates a new instance of the <code>ModelPresets</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON of this
     *             <code>ModelPresets</code> instance.
     * 
     * @throws LoggedException Thrown if an error occurs while creating this
     *                         <code>ModelPresets</code> instance.
     */
    public ModelPresets(TracedDictionary json) throws LoggedException {

        super(json, "model", AssetType.MODEL);
    }

    public static ModelPresets createPresets(TracedCollection collection, Object key, boolean nullCaseAllowed)
            throws LoggedException {

        TracedEntry<?> presetsEntry = collection.get(key, nullCaseAllowed, null, String.class, JSONObject.class);

        return (presetsEntry.containsValue()) ? switch (presetsEntry.getValue()) {

            case String _ -> new ModelPresets(collection.getAsIdentifier(key, nullCaseAllowed));

            case TracedDictionary json -> new ModelPresets(json);

            default -> null;
        } : null;
    }
}
