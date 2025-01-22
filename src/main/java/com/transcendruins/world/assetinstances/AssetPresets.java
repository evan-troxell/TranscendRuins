package com.transcendruins.world.assetinstances;

import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.finalize.FinalizedList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>AssetPresets</code>: A class representing the presets of any
 * asset type, including but not limited to: structures, elements, entities,
 * items, and more.
 */
public abstract class AssetPresets {

    /**
     * <code>TracedEntry&lt;Identifier&gt;</code>: The identifier of this
     * <code>AssetPresets</code>
     * instance.
     */
    private final TracedEntry<Identifier> identifierEntry;

    /**
     * <code>AssetType</code>: The asset type of this <code>AssetPresets</code>
     * instance.
     */
    private final AssetType type;

    /**
     * <code>FinalizedList&lt;TracedEntry&lt;String&gt;&gt;</code>: A list of the
     * initial attribute sets of this <code>AssetPresets</code> instance.
     */
    private final FinalizedList<TracedEntry<String>> attributeSets = new FinalizedList<>();

    /**
     * Creates a new instance of the <code>AssetPresets</code> class.
     * 
     * @param identifierEntry <code>TracedEntry&lt;Identifier&gt;</code>: The
     *                        identifier entry of this <code>AssetPresets</code>
     *                        instance.
     * @param type            <code>AssetType</code>: The asset type of this
     *                        <code>AssetPresets</code> instance.
     */
    public AssetPresets(TracedEntry<Identifier> identifierEntry, AssetType type) {

        this.identifierEntry = identifierEntry;
        this.type = type;

        attributeSets.finalizeData();
    }

    /**
     * Creates a new instance of the <code>AssetPresets</code> class.
     * 
     * @param json          <code>TracedDictionary</code>: The JSON of this
     *                      <code>AssetPresets</code> instance.
     * @param identifierKey <code>String</code>: The key from which to retrieve the
     *                      identifier entry of this <code>AssetPreset</code>
     *                      instance.
     * @param type          <code>AssetType</code>: The asset type of this
     *                      <code>AssetPresets</code> instance.
     */
    public AssetPresets(TracedDictionary json, String identifierKey, AssetType type) throws LoggedException {

        this.identifierEntry = json.getAsIdentifier(identifierKey, false);
        this.type = type;

        TracedEntry<TracedArray> attributeSetsEntry = json.getAsArray("attributeSets", true);

        if (attributeSetsEntry.containsValue()) {

            TracedArray attributeSetsJson = attributeSetsEntry.getValue();

            for (int i : attributeSetsJson.getIndices()) {

                attributeSets.add(attributeSetsJson.getAsString(i, false, null));
            }
        }

        attributeSets.finalizeData();
    }

    /**
     * Retrieves the identifier entry of this <code>AssetPresets</code> instance.
     * 
     * @return <code>TracedEntry&lt;Identifier&gt;</code>: The
     *         <code>identifierEntry</code> field of this
     *         <code>AssetPresets</code> instance.
     */
    public TracedEntry<Identifier> getIdentifierEntry() {

        return identifierEntry;
    }

    /**
     * Retrieves the identifier of this <code>AssetPresets</code> instance.
     * 
     * @return <code>Identifier</code>: The value of the
     *         <code>identifierEntry</code> field of this
     *         <code>AssetPresets</code> instance.
     */
    public Identifier getIdentifier() {

        return identifierEntry.getValue();
    }

    /**
     * Retrieves the asset type of this <code>AssetPresets</code> instance.
     * 
     * @return <code>AssetType</code>: The <code>type</code> field of this
     *         <code>AssetPresets</code> instance.
     */
    public AssetType getType() {

        return type;
    }

    /**
     * Retrieves the initial attribute sets of this <code>AssetPresets</code>
     * instance.
     * 
     * @return <code>FinalizedList&lt;TracedEntry&lt;String&gt;&gt;</code>: The
     *         <code>attributeSets</code> field of this <code>AssetPresets</code>
     *         instance.
     */
    public FinalizedList<TracedEntry<String>> getAttributeSets() {

        return attributeSets;
    }
}
