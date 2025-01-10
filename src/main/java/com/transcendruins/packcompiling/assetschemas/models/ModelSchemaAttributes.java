package com.transcendruins.packcompiling.assetschemas.models;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.rendering.Model;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
* <code>ModelSchemaAttributes</code>: A class which represents the attributes of a <code>ModelSchema</code> instance.
*/
public final class ModelSchemaAttributes extends AssetSchemaAttributes {

    /**
     * <code>Model</code>: The model which represents all polygons and bones of this <code>ModelSchema</code> instance.
     */
    private final Model model;

    /**
     * Compiles this <code>ModelSchemaAttributes</code> instance into a completed instance.
     * @param schema <code>ModelSchema</code>: The schema which created this <code>ModelSchemaAttributes</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>schemaJson</code> perameter.
     * @param isBase <code>boolean</code>: Whether or not this <code>ModelSchemaAttributes</code> instance is the base attribute set of a <code>ModelSchemaAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>ModelSchemaAttributes</code> instance.
     */
    public ModelSchemaAttributes(ModelSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);

        model = new Model(schemaJson);
    }

    /**
     * Retrieves the model of this <code>ModelSchemaAttributes</code> instance.
     * @return <code>Identifier</code>: The <code>model</code> field of this <code>ModelSchemaAttributes</code> instance.
     */
    public Model getModel() {

        return model;
    }
}
