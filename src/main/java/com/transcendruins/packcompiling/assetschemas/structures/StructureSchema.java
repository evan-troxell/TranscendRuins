package com.transcendruins.packcompiling.assetschemas.structures;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>StructureSchema</code>: A class which serves as the schema template for
 * a structure class, created into the parent <code>Pack</code> instance.
 */
public final class StructureSchema extends AssetSchema {

    /**
     * Creates a new instance of the <code>StructureSchema</code> class.
     * 
     * @param path <code>TracedPath</code>: The filepath to this
     *             <code>StructureSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>StructureSchema</code> instance.
     */
    public StructureSchema(TracedPath path) throws LoggedException {

        super(path, AssetType.STRUCTURE);
    }

    @Override
    public StructureSchemaAttributes buildAttributeSet(TracedDictionary jsonSchema, boolean isBase)
            throws LoggedException {

        return new StructureSchemaAttributes(this, jsonSchema, isBase);
    }
}
