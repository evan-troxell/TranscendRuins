package com.transcendruins.packcompiling.assetschemas.items;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaModules;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
* <code>ItemSchemaModules</code>: A class which represents the modules of an <code>ItemSchema</code> instance.
*/
public final class ItemSchemaModules extends AssetSchemaModules {

    /**
     * Compiles this <code>ItemSchemaModules</code> instance into a completed instance.
     * @param schema <code>ItemSchema</code>: The schema which created this <code>ItemSchemaModules</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>ItemSchemaModules</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>ItemSchemaModules</code> instance is the base module set of an <code>ItemSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>ItemSchemaModules</code> instance.
     */
    public ItemSchemaModules(ItemSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);
    }
}
