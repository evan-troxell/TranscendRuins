package com.transcendruins.packcompiling.assetschemas.entities;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>EntitySchema</code>: A class which serves as the schema template for an
 * entity class, created into the parent <code>Pack</code> instance.
 */
public final class EntitySchema extends AssetSchema {

    /**
     * Creates a new instance of the <code>EntitySchema</code> class.
     * 
     * @param path <code>TracedPath</code>: The filepath to this
     *             <code>EntitySchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>EntitySchema</code> instance.
     */
    public EntitySchema(TracedPath path) throws LoggedException {

        super(path, AssetType.ENTITY);
    }

    @Override
    public EntitySchemaAttributes buildAttributeSet(TracedDictionary jsonSchema, boolean isBase)
            throws LoggedException {

        return new EntitySchemaAttributes(this, jsonSchema, isBase);
    }
}
