package com.transcendruins.packcompiling.assetschemas.entities;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>EntitySchema</code>: A class which serves as the schema template for an entity class, created into the parent <code>Pack</code> instance.
 */
public final class EntitySchema extends AssetSchema {

    /**
     * Creates a new instance of the <code>EntitySchema</code> class.
     * @param path <code>TracedPath</code>: The filepath to this <code>EntitySchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>EntitySchema</code> instance.
     */
    public EntitySchema(TracedPath path) throws LoggedException {

        super(path, AssetType.ENTITY);
    }

    /**
     * Builds the base attribute set of this <code>EntitySchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the attribute set.
     * @return <code>EntitySchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the attribute set.
     */
    @Override
    public EntitySchemaAttributes buildBaseAttributeSet(TracedDictionary jsonSchema) throws LoggedException {

        return new EntitySchemaAttributes(this, jsonSchema, true);
    }

    /**
     * Builds a attribute set of this <code>EntitySchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the attribute set.
     * @return <code>EntitySchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the attribute set.
     */
    @Override
    public EntitySchemaAttributes buildAttributeSet(TracedDictionary jsonSchema) throws LoggedException {

        return new EntitySchemaAttributes(this, jsonSchema, false);
    }
}
