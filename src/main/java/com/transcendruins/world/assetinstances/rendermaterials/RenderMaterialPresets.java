package com.transcendruins.world.assetinstances.rendermaterials;

import org.json.simple.JSONObject;

import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.assetinstances.AssetPresets;

/**
 * <code>RenderMaterialPresets</code>: A class representing a the presets
 * of a render material instance.
 */
public final class RenderMaterialPresets extends AssetPresets {

    /**
     * Creates a new instance of the <code>RenderMaterialPresets</code> class.
     * 
     * @param identifierEntry <code>TracedEntry&lt;Identifier&gt;</code>: The
     *                        identifier entry of this
     *                        <code>RenderMaterialPresets</code>
     *                        instance.
     */
    public RenderMaterialPresets(TracedEntry<Identifier> identifierEntry) {

        super(identifierEntry, AssetType.RENDER_MATERIAL);
    }

    /**
     * Creates a new instance of the <code>RenderMaterialPresets</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON of this
     *             <code>RenderMaterialPresets</code> instance.
     * 
     * @throws LoggedException Thrown if an error occurs while creating this
     *                         <code>RenderMaterialPresets</code> instance.
     */
    public RenderMaterialPresets(TracedDictionary json) throws LoggedException {

        super(json, "renderMaterial", AssetType.RENDER_MATERIAL);
    }

    public static RenderMaterialPresets createPresets(TracedCollection collection, Object key, boolean nullCaseAllowed)
            throws LoggedException {

        TracedEntry<?> presetsEntry = collection.get(key, nullCaseAllowed, null, String.class, JSONObject.class);

        return (presetsEntry.containsValue()) ? switch (presetsEntry.getValue()) {

            case String _ -> new RenderMaterialPresets(collection.getAsIdentifier(key, nullCaseAllowed));

            case TracedDictionary json -> new RenderMaterialPresets(json);

            default -> null;
        } : null;
    }
}
