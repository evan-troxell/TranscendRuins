package com.transcendruins.world.assetinstances.structures;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.structures.StructureSchema;
import com.transcendruins.packcompiling.assetschemas.structures.StructureSchemaAttributes;
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
     * Applies a attribute set to this <code>StructureInstance</code> instance.
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to apply.
     */
    @Override
    protected void applyAttributeSet(AssetSchemaAttributes attributeSet) {

        StructureSchemaAttributes attributes = (StructureSchemaAttributes) attributeSet;
    }
}
