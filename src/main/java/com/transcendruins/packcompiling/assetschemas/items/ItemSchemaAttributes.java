package com.transcendruins.packcompiling.assetschemas.items;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
* <code>ItemSchemaAttributes</code>: A class which represents the attributes of an <code>ItemSchema</code> instance.
*/
public final class ItemSchemaAttributes extends AssetSchemaAttributes {

    /**
     * Compiles this <code>ItemSchemaAttributes</code> instance into a completed instance.
     * @param schema <code>ItemSchema</code>: The schema which created this <code>ItemSchemaAttributes</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>ItemSchemaAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>ItemSchemaAttributes</code> instance is the base attribute set of an <code>ItemSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>ItemSchemaAttributes</code> instance.
     */
    public ItemSchemaAttributes(ItemSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);
    }
}
