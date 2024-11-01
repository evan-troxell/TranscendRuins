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
     * Builds the base component set of this <code>StructureSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the component set.
     * @return <code>StructureSchemaComponents</code>: The generated component set.
     * @throws LoggedException Thrown if any exception is raised while building the component set.
     */
    @Override
    public StructureSchemaComponents buildBaseComponentSet(TracedDictionary jsonSchema) throws LoggedException {

        return new StructureSchemaComponents(this, jsonSchema, true);
    }

    /**
     * Builds a component set of this <code>StructureSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the component set.
     * @return <code>StructureSchemaComponents</code>: The generated component set.
     * @throws LoggedException Thrown if any exception is raised while building the component set.
     */
    @Override
    public StructureSchemaComponents buildComponentSet(TracedDictionary jsonSchema) throws LoggedException {

        return new StructureSchemaComponents(this, jsonSchema, false);
    }
}
