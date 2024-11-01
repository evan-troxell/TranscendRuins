package com.transcendruins.packcompiling.assetschemas;

import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
* <code>AssetSchemaComponents</code>: A class which represents the components of an <code>AssetSchema</code> instance.
*/
public abstract class AssetSchemaComponents {

    /**
     * <code>AssetSchema</code>: The asset schema which created this <code>AssetSchemaComponents</code> instance.
     */
    public final AssetSchema schema;

    /**
     * <code>TracedDictionary</code>: The schema JSON used to compile this <code>AssetSchemaComponents</code> instance.
     */
    public final TracedDictionary schemaJson;

    /**
     * <code>boolean</code>: Whether or not this <code>AssetSchemaComponents</code> instance is the base component layer of an <code>AssetSchema</code> instance.
     */
    public final boolean isBase;

    /**
     * Creates a new instance of the <code>AssetSchemaComponents</code> class.
     * @param schema <code>AssetSchema</code>: The schema which created this <code>AssetSchemaComponents</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>AssetSchemaComponents</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>AssetSchemaComponents</code> instance is the base component set of an <code>AssetSchema</code> instance.
     */
    public AssetSchemaComponents(AssetSchema schema, TracedDictionary schemaJson, boolean isBase) {

        this.schema = schema;
        this.schemaJson = schemaJson;
        this.isBase = isBase;
    };

    /**
     * Adds a asset dependency to the <code>AssetSchema</code> instance which created this <code>AssetSchemaComponents</code> instance.
     * @param type <code>AssetType</code>: The type of dependency to be added.
     * @param dependency <code>TracedEntry&lt;Identifier&gt;</code>: The identifier of the dependency to be added.
     */
    protected final void addElementDependency(AssetType type, TracedEntry<Identifier> dependency) {

        schema.addElementDependency(type, dependency);
    }
}
