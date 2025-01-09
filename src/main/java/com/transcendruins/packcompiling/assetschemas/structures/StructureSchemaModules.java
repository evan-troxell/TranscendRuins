package com.transcendruins.packcompiling.assetschemas.structures;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaModules;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
* <code>StructureSchemaModules</code>: A class which represents the modules of a <code>StructureSchema</code> instance.
*/
public class StructureSchemaModules extends AssetSchemaModules {

    /**
     * Compiles this <code>StructureSchemaModules</code> instance into a completed instance.
     * @param structureSchema <code>StructureSchema</code>: The schema which created this <code>StructureSchemaModules</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>StructureSchemaModules</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>StructureSchemaModules</code> instance is the base module set of a <code>StructureSchemaModules</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>StructureSchemaModules</code> instance.
     */
    public StructureSchemaModules(StructureSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);
    }
}
