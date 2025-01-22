package com.transcendruins.world.assetinstances.elements;

import org.json.simple.JSONObject;

import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.assetinstances.AssetPresets;

/**
 * <code>ElementPresets</code>: A class representing a the presets
 * of an element instance.
 */
public final class ElementPresets extends AssetPresets {

    /**
     * Creates a new instance of the <code>ElementPresets</code> class.
     * 
     * @param identifierEntry <code>TracedEntry&lt;Identifier&gt;</code>: The
     *                        identifier entry of this <code>ElementPresets</code>
     *                        instance.
     */
    public ElementPresets(TracedEntry<Identifier> identifierEntry) {

        super(identifierEntry, AssetType.ELEMENT);
    }

    /**
     * Creates a new instance of the <code>ElementPresets</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON of this
     *             <code>ElementPresets</code> instance.
     * 
     * @throws LoggedException Thrown if an error occurs while creating this
     *                         <code>ElementPresets</code> instance.
     */
    public ElementPresets(TracedDictionary json) throws LoggedException {

        super(json, "element", AssetType.ELEMENT);
    }

    public static ElementPresets createPresets(TracedCollection collection, Object key, boolean nullCaseAllowed)
            throws LoggedException {

        TracedEntry<?> presetsEntry = collection.get(key, nullCaseAllowed, null, String.class, JSONObject.class);

        return (presetsEntry.containsValue()) ? switch (presetsEntry.getValue()) {

            case String _ -> new ElementPresets(collection.getAsIdentifier(key, nullCaseAllowed));

            case TracedDictionary json -> new ElementPresets(json);

            default -> null;
        } : null;
    }
}
