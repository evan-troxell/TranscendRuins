package com.transcendruins.packcompiling.assetschemas.entities;

import com.transcendruins.packcompiling.assetschemas.ModelAssetSchemaComponents;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
* <code>EntitySchemaComponents</code>: A class which represents the components of an <code>EntitySchema</code> instance.
*/
public final class EntitySchemaComponents extends ModelAssetSchemaComponents {

    /**
     * Compiles this <code>EntitySchemaComponents</code> instance into a completed instance.
     * @param schema <code>EntitySchema</code>: The schema which created this <code>EntitySchemaComponents</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>EntitySchemaComponents</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>EntitySchemaComponents</code> instance is the base component set of an <code>EntitySchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>EntitySchemaComponents</code> instance.
     */
    public EntitySchemaComponents(EntitySchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);
    }
}
