package com.transcendruins.world.assetinstances.items;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.packcompiling.assetschemas.items.ItemSchema;
import com.transcendruins.packcompiling.assetschemas.items.ItemSchemaComponents;
import com.transcendruins.world.assetinstances.AssetInstance;

/**
 * <code>ItemInstance</code>: A class representing a generated item instance.
 */
public final class ItemInstance extends AssetInstance {

    /**
     * Creates a new instance of the <code>ItemInstance</code> class.
     * @param schema <code>ItemSchema</code>: The schema used to generate this <code>ItemInstance</code> instance.
     */
    public ItemInstance(ItemSchema schema) {

        super(schema);
    }

    /**
     * Applies a component set to this <code>ItemInstance</code> instance.
     * @param componentSet <code>AssetSchemaComponents</code>: The component set to apply.
     */
    @Override
    protected void applyComponentSet(AssetSchemaComponents componentSet) {

        ItemSchemaComponents components = (ItemSchemaComponents) componentSet;
    }
}
