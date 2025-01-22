package com.transcendruins.world.assetinstances.structures;

import org.json.simple.JSONObject;

import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.assetinstances.AssetPresets;

/**
 * <code>StructurePresets</code>: A class representing a the presets
 * of a structure instance.
 */
public final class StructurePresets extends AssetPresets {

    /**
     * Creates a new instance of the <code>StructurePresets</code> class.
     * 
     * @param identifierEntry <code>TracedEntry&lt;Identifier&gt;</code>: The
     *                        identifier entry of this <code>StructurePresets</code>
     *                        instance.
     */
    public StructurePresets(TracedEntry<Identifier> identifierEntry) {

        super(identifierEntry, AssetType.STRUCTURE);
    }

    /**
     * Creates a new instance of the <code>StructurePresets</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON of this
     *             <code>StructurePresets</code> instance.
     * 
     * @throws LoggedException Thrown if an error occurs while creating this
     *                         <code>StructurePresets</code> instance.
     */
    public StructurePresets(TracedDictionary json) throws LoggedException {

        super(json, "structure", AssetType.STRUCTURE);
    }

    public static StructurePresets createPresets(TracedCollection collection, Object key, boolean nullCaseAllowed)
            throws LoggedException {

        TracedEntry<?> presetsEntry = collection.get(key, nullCaseAllowed, null, String.class, JSONObject.class);

        return (presetsEntry.containsValue()) ? switch (presetsEntry.getValue()) {

            case String _ -> new StructurePresets(collection.getAsIdentifier(key, nullCaseAllowed));

            case TracedDictionary json -> new StructurePresets(json);

            default -> null;
        } : null;
    }
}
