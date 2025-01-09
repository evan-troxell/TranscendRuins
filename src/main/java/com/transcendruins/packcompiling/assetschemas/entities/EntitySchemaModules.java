package com.transcendruins.packcompiling.assetschemas.entities;

import com.transcendruins.packcompiling.assetschemas.ModelAssetSchemaModules;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
* <code>EntitySchemaModules</code>: A class which represents the modules of an <code>EntitySchema</code> instance.
*/
public final class EntitySchemaModules extends ModelAssetSchemaModules {

    /**
     * Compiles this <code>EntitySchemaModules</code> instance into a completed instance.
     * @param schema <code>EntitySchema</code>: The schema which created this <code>EntitySchemaModules</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>EntitySchemaModules</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>EntitySchemaModules</code> instance is the base module set of an <code>EntitySchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>EntitySchemaModules</code> instance.
     */
    public EntitySchemaModules(EntitySchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);
    }
}
