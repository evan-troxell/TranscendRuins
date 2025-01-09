package com.transcendruins.world.assetinstances.structures;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaModules;
import com.transcendruins.packcompiling.assetschemas.structures.StructureSchema;
import com.transcendruins.packcompiling.assetschemas.structures.StructureSchemaModules;
import com.transcendruins.world.assetinstances.AssetInstance;

/**
 * <code>StructureInstance</code>: A class representing a generated structure instance.
 */
public final class StructureInstance extends AssetInstance {

    /**
     * Creates a new instance of the <code>StructureInstance</code> class.
     * @param schema <code>StructureSchema</code>: The schema used to generate this <code>StructureInstance</code> instance.
     */
    public StructureInstance(StructureSchema schema) {

        super(schema);
    }

    /**
     * Applies a module set to this <code>StructureInstance</code> instance.
     * @param moduleSet <code>AssetSchemaModules</code>: The module set to apply.
     */
    @Override
    protected void applyModuleSet(AssetSchemaModules moduleSet) {

        StructureSchemaModules modules = (StructureSchemaModules) moduleSet;
    }
}
