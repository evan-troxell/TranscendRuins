package com.transcendruins.world.assetinstances.entities;

import org.json.simple.JSONObject;

import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.assetinstances.AssetPresets;

/**
 * <code>EntityPresets</code>: A class representing a the presets
 * of an entity instance.
 */
public final class EntityPresets extends AssetPresets {

    /**
     * Creates a new instance of the <code>EntityPresets</code> class.
     * 
     * @param identifierEntry <code>TracedEntry&lt;Identifier&gt;</code>: The
     *                        identifier entry of this <code>EntityPresets</code>
     *                        instance.
     */
    public EntityPresets(TracedEntry<Identifier> identifierEntry) {

        super(identifierEntry, AssetType.ENTITY);
    }

    /**
     * Creates a new instance of the <code>EntityPresets</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON of this
     *             <code>EntityPresets</code> instance.
     * 
     * @throws LoggedException Thrown if an error occurs while creating this
     *                         <code>EntityPresets</code> instance.
     */
    public EntityPresets(TracedDictionary json) throws LoggedException {

        super(json, "entity", AssetType.ENTITY);
    }

    public static EntityPresets createPresets(TracedCollection collection, Object key, boolean nullCaseAllowed)
            throws LoggedException {

        TracedEntry<?> presetsEntry = collection.get(key, nullCaseAllowed, null, String.class, JSONObject.class);

        return (presetsEntry.containsValue()) ? switch (presetsEntry.getValue()) {

            case String _ -> new EntityPresets(collection.getAsIdentifier(key, nullCaseAllowed));

            case TracedDictionary json -> new EntityPresets(json);

            default -> null;
        } : null;
    }
}
