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
     * Builds the base attribute set of this <code>ItemSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the attribute set.
     * @return <code>ItemSchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the attribute set.
     */
    @Override
    public ItemSchemaAttributes buildBaseAttributeSet(TracedDictionary jsonSchema) throws LoggedException {

        return new ItemSchemaAttributes(this, jsonSchema, true);
    }

    /**
     * Builds a attribute set of this <code>ItemSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the attribute set.
     * @return <code>ItemSchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the attribute set.
     */
    @Override
    public ItemSchemaAttributes buildAttributeSet(TracedDictionary jsonSchema) throws LoggedException {

        return new ItemSchemaAttributes(this, jsonSchema, false);
    }
}
