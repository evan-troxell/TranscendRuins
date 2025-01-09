package com.transcendruins.world.assetinstances.models;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaModules;
import com.transcendruins.packcompiling.assetschemas.models.ModelSchema;
import com.transcendruins.packcompiling.assetschemas.models.ModelSchemaModules;
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
     * Applies a module set to this <code>ModelInstance</code> instance.
     * @param moduleSet <code>AssetSchemaModules</code>: The module set to apply.
     */
    @Override
    protected void applyModuleSet(AssetSchemaModules moduleSet) {

        ModelSchemaModules modules = (ModelSchemaModules) moduleSet;
        model = modules.getModel();
    }

    /**
     * Retrieves the current model of this <code>ModelInstance</code> instance.
     * @return <code>Model</code>: The <code>model</code> field of this <code>ModelInstance</code> instance.
     */
    public Model getModel() {

        return model;
    }
}
