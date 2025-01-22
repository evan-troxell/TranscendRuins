package com.transcendruins.packcompiling.assetschemas;

import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.world.assetinstances.AssetPresets;

/**
 * <code>AssetSchemaAttributes</code>: A class which represents the attributes
 * of an <code>AssetSchema</code> instance.
 */
public abstract class AssetSchemaAttributes {

    /**
     * <code>AssetSchema</code>: The asset schema which created this
     * <code>AssetSchemaAttributes</code> instance.
     */
    private final AssetSchema schema;

    /**
     * Creates a new instance of the <code>AssetSchemaAttributes</code> class.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>AssetSchemaAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to
     *               compile this <code>AssetSchemaAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>AssetSchemaAttributes</code> instance is the base
     *               attribute set of an <code>AssetSchema</code> instance.
     */
    public AssetSchemaAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) {

        this.schema = schema;
    };

    /**
     * Adds a asset dependency to the <code>AssetSchema</code> instance which
     * created this <code>AssetSchemaAttributes</code> instance.
     * 
     * @param dependency <code>AssetPresets</code>: The presets of the dependency to
     *                   be added.
     */
    protected final void addElementDependency(AssetPresets dependency) {

        schema.addElementDependency(dependency);
    }

    /**
     * Finalizes the data of this <code>AssetSchemaAttributes</code> instance.
     */
    protected abstract void finalizeData();
}
