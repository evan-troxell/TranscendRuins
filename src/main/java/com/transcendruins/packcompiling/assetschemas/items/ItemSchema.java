package com.transcendruins.packcompiling.assetschemas.items;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>ItemSchema</code>: A class which serves as the schema template for an item class, created into the parent <code>Pack</code> instance.
 */
public final class ItemSchema extends AssetSchema {

    /**
     * Creates a new instance of the <code>ItemSchema</code> class.
     * @param path <code>TracedPath</code>: The filepath to this <code>ItemSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>ItemSchema</code> instance.
     */
    public ItemSchema(TracedPath path) throws LoggedException {

        super(path, AssetType.ITEM);
    }

    /**
     * Builds the base component set of this <code>ItemSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the component set.
     * @return <code>ItemSchemaComponents</code>: The generated component set.
     * @throws LoggedException Thrown if any exception is raised while building the component set.
     */
    @Override
    public ItemSchemaComponents buildBaseComponentSet(TracedDictionary jsonSchema) throws LoggedException {

        return new ItemSchemaComponents(this, jsonSchema, true);
    }

    /**
     * Builds a component set of this <code>ItemSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the component set.
     * @return <code>ItemSchemaComponents</code>: The generated component set.
     * @throws LoggedException Thrown if any exception is raised while building the component set.
     */
    @Override
    public ItemSchemaComponents buildComponentSet(TracedDictionary jsonSchema) throws LoggedException {

        return new ItemSchemaComponents(this, jsonSchema, false);
    }
}
