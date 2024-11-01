package com.transcendruins.world.assetinstances.structures;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.packcompiling.assetschemas.structures.StructureSchema;
import com.transcendruins.packcompiling.assetschemas.structures.StructureSchemaComponents;
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
     * Applies a component set to this <code>StructureInstance</code> instance.
     * @param componentSet <code>AssetSchemaComponents</code>: The component set to apply.
     */
    @Override
    protected void applyComponentSet(AssetSchemaComponents componentSet) {

        StructureSchemaComponents components = (StructureSchemaComponents) componentSet;
    }
}
