package com.transcendruins.world.assetinstances.items;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.items.ItemSchemaAttributes;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.AssetInstance;

/**
 * <code>ItemInstance</code>: A class representing a generated item instance.
 */
public final class ItemInstance extends AssetInstance {

    /**
     * Creates a new instance of the <code>ItemInstance</code> class.
     * 
     * @param presets <code>ItemPresets</code>: The presets used to generate this
     *                <code>ItemInstance</code> instance.
     * @param world   <code>World</code>: The world copy to assign to this
     *                <code>ItemInstance</code> instance.
     */
    public ItemInstance(ItemPresets presets, World world) {

        super(presets, world);
    }

    /**
     * Applies an attribute set to this <code>ItemInstance</code> instance.
     * 
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to
     *                     apply.
     */
    @Override
    protected void applyAttributeSet(AssetSchemaAttributes attributeSet) {

        ItemSchemaAttributes attributes = (ItemSchemaAttributes) attributeSet;
    }
}
