package com.transcendruins.packcompiling.assetschemas.rendermaterials;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>RenderMaterialSchema</code>: A class which serves as the schema template for a render material class, created into the parent <code>Pack</code> instance.
 */
public final class RenderMaterialSchema extends AssetSchema {

    /**
     * Creates a new instance of the <code>RenderMaterialSchema</code> class.
     * @param RenderMaterialSchema <code>TracedPath</code>: The filepath to this <code>RenderMaterialSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>RenderMaterialSchema</code> instance.
     */
    public RenderMaterialSchema(TracedPath path) throws LoggedException {

        super(path, AssetType.RENDER_MATERIAL);
    }

    /**
     * Builds the base module set of this <code>RenderMaterialSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the module set.
     * @return <code>RenderMaterialSchemaModules</code>: The generated module set.
     * @throws LoggedException Thrown if any exception is raised while building the module set.
     */
    @Override
    public RenderMaterialSchemaModules buildBaseModuleSet(TracedDictionary jsonSchema) throws LoggedException {

        return new RenderMaterialSchemaModules(this, jsonSchema, true);
    }

    /**
     * Builds a module set of this <code>RenderMaterialSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the module set.
     * @return <code>RenderMaterialSchemaModules</code>: The generated module set.
     * @throws LoggedException Thrown if any exception is raised while building the module set.
     */
    @Override
    public RenderMaterialSchemaModules buildModuleSet(TracedDictionary jsonSchema) throws LoggedException {

        return new RenderMaterialSchemaModules(this, jsonSchema, false);
    }
}
