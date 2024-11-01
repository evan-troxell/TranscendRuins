package com.transcendruins.packcompiling.assetschemas.animations;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>AnimationSchema</code>: A class which serves as the schema template for an animation class, created into the parent <code>Pack</code> instance.
 */
public final class AnimationSchema extends AssetSchema {

    /**
     * Creates a new instance of the <code>AnimationSchema</code> class.
     * @param path <code>TracedPath</code>: The filepath to this <code>AnimationSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while creating this <code>AnimationSchema</code> instance.
     */
    public AnimationSchema(TracedPath path) throws LoggedException {

        super(path, AssetType.ANIMATION);
    }

    /**
     * Builds the base component set of this <code>AnimationSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the component set.
     * @return <code>AnimationSchemaComponents</code>: The generated component set.
     * @throws LoggedException Thrown if any exception is raised while building the component set.
     */
    @Override
    public AnimationSchemaComponents buildBaseComponentSet(TracedDictionary jsonSchema) throws LoggedException {

        return new AnimationSchemaComponents(this, jsonSchema, true);
    }

    /**
     * Builds a component set of this <code>AnimationSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the component set.
     * @return <code>AnimationSchemaComponents</code>: The generated component set.
     * @throws LoggedException Thrown if any exception is raised while building the component set.
     */
    @Override
    public AnimationSchemaComponents buildComponentSet(TracedDictionary jsonSchema) throws LoggedException {

        return new AnimationSchemaComponents(this, jsonSchema, false);
    }
}
