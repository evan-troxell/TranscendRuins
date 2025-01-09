package com.transcendruins.packcompiling.assetschemas.animationcontrollers;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>AnimationControllerSchema</code>: A class which serves as the schema template for an animation controller class, created into the parent <code>Pack</code> instance.
 */
public final class AnimationControllerSchema extends AssetSchema {

    /**
     * Creates a new instance of the <code>AnimationControllerSchema</code> class.
     * @param path <code>TracedPath</code>: The filepath to this <code>AnimationControllerSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while creating this <code>AnimationControllerSchema</code> instance.
     */
    public AnimationControllerSchema(TracedPath path) throws LoggedException {

        super(path, AssetType.ANIMATION_CONTROLLER);
    }

    /**
     * Builds the base module set of this <code>AnimationControllerSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the module set.
     * @return <code>AnimationControllerSchemaModules</code>: The generated module set.
     * @throws LoggedException Thrown if any exception is raised while building the module set.
     */
    @Override
    public AnimationControllerSchemaModules buildBaseModuleSet(TracedDictionary jsonSchema) throws LoggedException {

        return new AnimationControllerSchemaModules(this, jsonSchema, true);
    }

    /**
     * Builds a module set of this <code>AnimationControllerSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the module set.
     * @return <code>AnimationControllerSchemaModules</code>: The generated module set.
     * @throws LoggedException Thrown if any exception is raised while building the module set.
     */
    @Override
    public AnimationControllerSchemaModules buildModuleSet(TracedDictionary jsonSchema) throws LoggedException {

        return new AnimationControllerSchemaModules(this, jsonSchema, false);
    }
}
