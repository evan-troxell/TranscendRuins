package com.transcendruins.packcompiling.assetschemas.structures;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
* <code>StructureSchemaAttributes</code>: A class which represents the attributes of a <code>StructureSchema</code> instance.
*/
public final class StructureSchemaAttributes extends AssetSchemaAttributes {

    /**
     * Compiles this <code>StructureSchemaAttributes</code> instance into a completed instance.
     * @param structureSchema <code>StructureSchema</code>: The schema which created this <code>StructureSchemaAttributes</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>StructureSchemaAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>StructureSchemaAttributes</code> instance is the base attribute set of a <code>StructureSchemaAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>StructureSchemaAttributes</code> instance.
     */
    public StructureSchemaAttributes(StructureSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);

        finalizeData();
    }

    @Override
    public void finalizeData() {
        
    }
}
