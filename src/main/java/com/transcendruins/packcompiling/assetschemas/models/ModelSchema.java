package com.transcendruins.packcompiling.assetschemas.models;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>ModelSchema</code>: A class which serves as the schema template for a model class, created into the parent <code>Pack</code> instance.
 */
public final class ModelSchema extends AssetSchema {

    /**
     * Creates a new instance of the <code>ModelSchema</code> class.
     * @param path <code>TracedPath</code>: The filepath to this <code>ModelSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>ModelSchema</code> instance.
     */
    public ModelSchema(TracedPath path) throws LoggedException {

        super(path, AssetType.MODEL);
    }

    /**
     * Builds the base module set of this <code>ModelSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the module set.
     * @return <code>ModelSchemaModules</code>: The generated module set.
     * @throws LoggedException Thrown if any exception is raised while building the module set.
     */
    @Override
    public ModelSchemaModules buildBaseModuleSet(TracedDictionary jsonSchema) throws LoggedException {

        return new ModelSchemaModules(this, jsonSchema, true);
    }

    /**
     * Builds a module set of this <code>ModelSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the module set.
     * @return <code>ModelSchemaModules</code>: The generated module set.
     * @throws LoggedException Thrown if any exception is raised while building the module set.
     */
    @Override
    public ModelSchemaModules buildModuleSet(TracedDictionary jsonSchema) throws LoggedException {

        return new ModelSchemaModules(this, jsonSchema, false);
    }
}
