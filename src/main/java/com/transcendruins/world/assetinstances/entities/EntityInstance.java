package com.transcendruins.world.assetinstances.entities;

import com.transcendruins.geometry.Position3D;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.packcompiling.assetschemas.entities.EntitySchema;
import com.transcendruins.packcompiling.assetschemas.entities.EntitySchemaComponents;
import com.transcendruins.world.assetinstances.ModelAssetInstance;

/**
 * <code>EntityInstance</code>: A class representing a generated entity instance.
 */
public final class EntityInstance extends ModelAssetInstance {

    /**
     * Creates a new instance of the <code>EntityInstance</code> class.
     * @param schema <code>EntitySchema</code>: The schema used to generate this <code>EntityInstance</code> instance.
     * @param tileX <code>long</code>: The X coordinate of the tile to assign to this <code>EntityInstance</code> instance.
     * @param tileZ <code>long</code>: The Z coordinate of the tile to assign to this <code>EntityInstance</code> instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to combine with the heading, represented by the cardinal direction enums of the <code>World</code> class.
     * @param tileOffset <code>Position3D</code>: The tile offset to assign to this <code>EntityInstance</code> instance.
     */
    public EntityInstance(EntitySchema schema, long tileX, long tileZ, int cardinalDirection, Position3D tileOffset) {

        super(schema, tileX, tileZ, cardinalDirection, tileOffset);
    }

    /**
     * Applies a component set to this <code>EntityInstance</code> instance.
     * @param componentSet <code>AssetSchemaComponents</code>: The component set to apply.
     */
    @Override
    protected void applyComponentSet(AssetSchemaComponents componentSet) {

        EntitySchemaComponents components = (EntitySchemaComponents) componentSet;

    }
}
