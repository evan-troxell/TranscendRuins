package com.transcendruins.packcompiling.assetschemas.structures;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
* <code>StructureSchemaComponents</code>: A class which represents the components of a <code>StructureSchema</code> instance.
*/
public class StructureSchemaComponents extends AssetSchemaComponents {

    /**
     * Compiles this <code>StructureSchemaComponents</code> instance into a completed instance.
     * @param structureSchema <code>StructureSchema</code>: The schema which created this <code>StructureSchemaComponents</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>StructureSchemaComponents</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>StructureSchemaComponents</code> instance is the base component set of a <code>StructureSchemaComponents</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>StructureSchemaComponents</code> instance.
     */
    public StructureSchemaComponents(StructureSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);
    }
}
