package com.transcendruins.world.assetinstances.items;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaModules;
import com.transcendruins.packcompiling.assetschemas.items.ItemSchema;
import com.transcendruins.packcompiling.assetschemas.items.ItemSchemaModules;
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
     * Applies a module set to this <code>ItemInstance</code> instance.
     * @param moduleSet <code>AssetSchemaModules</code>: The module set to apply.
     */
    @Override
    protected void applyModuleSet(AssetSchemaModules moduleSet) {

        ItemSchemaModules modules = (ItemSchemaModules) moduleSet;
    }
}
