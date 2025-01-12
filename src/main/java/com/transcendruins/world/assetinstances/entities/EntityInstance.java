package com.transcendruins.world.assetinstances.entities;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.entities.EntitySchema;
import com.transcendruins.packcompiling.assetschemas.entities.EntitySchemaAttributes;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.ModelAssetInstance;

/**
 * <code>EntityInstance</code>: A class representing a generated entity instance.
 */
public final class EntityInstance extends ModelAssetInstance {

    /**
     * Creates a new instance of the <code>EntityInstance</code> class.
     * @param schema <code>EntitySchema</code>: The schema used to generate this <code>EntityInstance</code> instance.
     * @param world <code>World</code>: The world copy to assign to this <code>EntityInstance</code> instance.
     * @param tileX <code>long</code>: The X coordinate of the tile to assign to this <code>EntityInstance</code> instance.
     * @param tileZ <code>long</code>: The Z coordinate of the tile to assign to this <code>EntityInstance</code> instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to combine with the heading, represented by the cardinal direction enums of the <code>World</code> class.
     * @param tileOffset <code>Vector</code>: The tile offset to assign to this <code>EntityInstance</code> instance.
     */
    public EntityInstance(EntitySchema schema, World world, long tileX, long tileZ, int cardinalDirection, Vector tileOffset) {

        super(schema, world, tileX, tileZ, cardinalDirection, tileOffset);
    }

    /**
     * Applies a attribute set to this <code>EntityInstance</code> instance.
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to apply.
     */
    @Override
    protected void applyAttributeSet(AssetSchemaAttributes attributeSet) {

        EntitySchemaAttributes attributes = (EntitySchemaAttributes) attributeSet;

    }
}
