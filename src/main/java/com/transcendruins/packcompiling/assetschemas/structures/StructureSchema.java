package com.transcendruins.packcompiling.assetschemas.structures;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>StructureSchema</code>: A class which serves as the schema template for a structure class, created into the parent <code>Pack</code> instance.
 */
public final class StructureSchema extends AssetSchema {

    /**
     * Creates a new instance of the <code>StructureSchema</code> class.
     * @param path <code>TracedPath</code>: The filepath to this <code>StructureSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>StructureSchema</code> instance.
     */
    public StructureSchema(TracedPath path) throws LoggedException {

        super(path, AssetType.STRUCTURE);
    }

    /**
     * Builds the base attribute set of this <code>StructureSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the attribute set.
     * @return <code>StructureSchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the attribute set.
     */
    @Override
    public StructureSchemaAttributes buildBaseAttributeSet(TracedDictionary jsonSchema) throws LoggedException {

        return new StructureSchemaAttributes(this, jsonSchema, true);
    }

    /**
     * Builds a attribute set of this <code>StructureSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the attribute set.
     * @return <code>StructureSchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the attribute set.
     */
    @Override
    public StructureSchemaAttributes buildAttributeSet(TracedDictionary jsonSchema) throws LoggedException {

        return new StructureSchemaAttributes(this, jsonSchema, false);
    }
}
