package com.transcendruins.world.assetinstances.structures;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.structures.StructureSchemaAttributes;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.AssetInstance;

/**
 * <code>StructureInstance</code>: A class representing a generated structure
 * instance.
 */
public final class StructureInstance extends AssetInstance {

    /**
     * Creates a new instance of the <code>StructureInstance</code> class.
     * 
     * @param schema <code>StructurePresets</code>: The presets used to generate
     *               this
     *               <code>StructureInstance</code> instance.
     * @param world  <code>World</code>: The world copy to assign to this
     *               <code>StructureInstance</code> instance.
     */
    public StructureInstance(StructurePresets presets, World world) {

        super(presets, world);
    }

    /**
     * Applies an attribute set to this <code>StructureInstance</code> instance.
     * 
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to
     *                     apply.
     */
    @Override
    protected void applyAttributeSet(AssetSchemaAttributes attributeSet) {

        StructureSchemaAttributes attributes = (StructureSchemaAttributes) attributeSet;
    }
}
