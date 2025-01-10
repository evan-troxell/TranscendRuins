package com.transcendruins.world.assetinstances.models;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.models.ModelSchema;
import com.transcendruins.packcompiling.assetschemas.models.ModelSchemaAttributes;
import com.transcendruins.rendering.Model;
import com.transcendruins.world.assetinstances.AssetInstance;

/**
 * <code>ModelInstance</code>: A class representing a generated model instance.
 */
public final class ModelInstance extends AssetInstance {

    /**
     * <code>Model</code>: The current model which this <code>ModelInstance</code> instance represents.
     */
    private Model model;

    /**
     * Creates a new instance of the <code>ModelInstance</code> class.
     * @param schema <code>ModelSchema</code>: The schema used to generate this <code>ModelInstance</code> instance.
     */
    public ModelInstance(ModelSchema schema) {

        super(schema);
    }

    /**
     * Applies a attribute set to this <code>ModelInstance</code> instance.
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to apply.
     */
    @Override
    protected void applyAttributeSet(AssetSchemaAttributes attributeSet) {

        ModelSchemaAttributes attributes = (ModelSchemaAttributes) attributeSet;
        model = attributes.getModel();
    }

    /**
     * Retrieves the current model of this <code>ModelInstance</code> instance.
     * @return <code>Model</code>: The <code>model</code> field of this <code>ModelInstance</code> instance.
     */
    public Model getModel() {

        return model;
    }
}
