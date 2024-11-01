package com.transcendruins.packcompiling.assetschemas.items;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
* <code>ItemSchemaComponents</code>: A class which represents the components of an <code>ItemSchema</code> instance.
*/
public final class ItemSchemaComponents extends AssetSchemaComponents {

    /**
     * Compiles this <code>ItemSchemaComponents</code> instance into a completed instance.
     * @param schema <code>ItemSchema</code>: The schema which created this <code>ItemSchemaComponents</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>ItemSchemaComponents</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>ItemSchemaComponents</code> instance is the base component set of an <code>ItemSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>ItemSchemaComponents</code> instance.
     */
    public ItemSchemaComponents(ItemSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);
    }
}
