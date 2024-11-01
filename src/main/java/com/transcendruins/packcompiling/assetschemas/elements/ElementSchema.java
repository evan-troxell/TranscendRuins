package com.transcendruins.packcompiling.assetschemas.elements;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>ElementSchema</code>: A class which serves as the schema template for an element class, created into the parent <code>Pack</code> instance.
 */
public final class ElementSchema extends AssetSchema {

    /**
     * Creates a new instance of the <code>ElementSchema</code> class.
     * @param path <code>TracedPath</code>: The filepath to this <code>ElementSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>ElementSchema</code> instance.
     */
    public ElementSchema(TracedPath path) throws LoggedException {

        super(path, AssetType.ELEMENT);
    }

    /**
     * Builds the base component set of this <code>ElementSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the component set.
     * @return <code>ElementSchemaComponents</code>: The generated component set.
     * @throws LoggedException Thrown if any exception is raised while building the component set.
     */
    @Override
    public ElementSchemaComponents buildBaseComponentSet(TracedDictionary jsonSchema) throws LoggedException {

        return new ElementSchemaComponents(this, jsonSchema, true);
    }

    /**
     * Builds a component set of this <code>ElementSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the component set.
     * @return <code>ElementSchemaComponents</code>: The generated component set.
     * @throws LoggedException Thrown if any exception is raised while building the component set.
     */
    @Override
    public ElementSchemaComponents buildComponentSet(TracedDictionary jsonSchema) throws LoggedException {

        return new ElementSchemaComponents(this, jsonSchema, false);
    }
}
