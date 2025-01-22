package com.transcendruins.world.assetinstances.items;

import org.json.simple.JSONObject;

import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.assetinstances.AssetPresets;

/**
 * <code>ItemPresets</code>: A class representing a the presets
 * of an item instance.
 */
public final class ItemPresets extends AssetPresets {

    /**
     * Creates a new instance of the <code>ItemPresets</code> class.
     * 
     * @param identifierEntry <code>TracedEntry&lt;Identifier&gt;</code>: The
     *                        identifier entry of this <code>ItemPresets</code>
     *                        instance.
     */
    public ItemPresets(TracedEntry<Identifier> identifierEntry) {

        super(identifierEntry, AssetType.ITEM);
    }

    /**
     * Creates a new instance of the <code>ItemPresets</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON of this
     *             <code>ItemPresets</code> instance.
     * 
     * @throws LoggedException Thrown if an error occurs while creating this
     *                         <code>ItemPresets</code> instance.
     */
    public ItemPresets(TracedDictionary json) throws LoggedException {

        super(json, "item", AssetType.ITEM);
    }

    public static ItemPresets createPresets(TracedCollection collection, Object key, boolean nullCaseAllowed)
            throws LoggedException {

        TracedEntry<?> presetsEntry = collection.get(key, nullCaseAllowed, null, String.class, JSONObject.class);

        return (presetsEntry.containsValue()) ? switch (presetsEntry.getValue()) {

            case String _ -> new ItemPresets(collection.getAsIdentifier(key, nullCaseAllowed));

            case TracedDictionary json -> new ItemPresets(json);

            default -> null;
        } : null;
    }
}
