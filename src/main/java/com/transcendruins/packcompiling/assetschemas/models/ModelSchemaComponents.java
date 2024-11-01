package com.transcendruins.packcompiling.assetschemas.models;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.rendering.Model;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
* <code>ModelSchemaComponents</code>: A class which represents the components of a <code>ModelSchema</code> instance.
*/
public final class ModelSchemaComponents extends AssetSchemaComponents {

    /**
     * <code>Model</code>: The model which represents all polygons and bones of this <code>ModelSchema</code> instance.
     */
    private final Model model;

    /**
     * Compiles this <code>ModelSchemaComponents</code> instance into a completed instance.
     * @param schema <code>ModelSchema</code>: The schema which created this <code>ModelSchemaComponents</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>schemaJson</code> perameter.
     * @param isBase <code>boolean</code>: Whether or not this <code>ModelSchemaComponents</code> instance is the base component set of a <code>ModelSchemaComponents</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>ModelSchemaComponents</code> instance.
     */
    public ModelSchemaComponents(ModelSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);

        model = new Model(schemaJson);
    }

    /**
     * Retrieves the model of this <code>ModelSchemaComponents</code> instance.
     * @return <code>Identifier</code>: The <code>model</code> property of this <code>ModelSchemaComponents</code> instance.
     */
    public Model getModel() {

        return model;
    }
}
