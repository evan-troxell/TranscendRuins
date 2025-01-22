package com.transcendruins.world.assetinstances;

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.world.World;

/**
 * <code>AssetInstance</code>: A class representing a generated instance of any
 * asset type, including but not limited to: structures, elements, entities,
 * items, and more.
 */
public abstract class AssetInstance {

    /**
     * <code>AssetSchema</code>: The schema used to generate this
     * <code>AssetInstance</code> instance.
     */
    private final AssetSchema assetSchema;

    /**
     * <code>World</code>: The world copy of this <code>AssetInstance</code>
     * instance.
     */
    private final World world;

    /**
     * Retrieves the world copy of this <code>AssetInstance</code> instance.
     * 
     * @return <code>World</code>: The <code>world</code> field of this
     *         <code>AssetInstance</code> instance.
     */
    public final World getWorld() {

        return world;
    }

    /**
     * <code>ArrayList&lt;String&gt;</code>: The list of currently applied attribute
     * sets.
     */
    private final ArrayList<String> appliedAttributeSets = new ArrayList<>();

    /**
     * Creates a new instance of the <code>AssetInstance</code> class.
     * 
     * @param presets <code>AssetPresets</code>: The presets used to generate this
     *                <code>AssetInstance</code> instance.
     * @param world   <code>World</code>: The world copy to assign to this
     *                <code>AssetInstance</code> instance.
     */
    public AssetInstance(AssetPresets presets, World world) {

        this.world = world;
        this.assetSchema = world.getEnvironment().getSchema(presets.getType(), presets.getIdentifier());

        for (TracedEntry<String> attributeSet : presets.getAttributeSets()) {

            assetSchema.getAttributeSet(attributeSet.getValue());
        }

        addAttributeSets(TracedEntry.unboxValues(presets.getAttributeSets()));
        updateAttributes();
    }

    /**
     * Adds a list of attribute sets to this <code>AssetInstance</code> instance.
     * 
     * @param attributeSets <code>List&lt;String&gt;</code>: The attribute sets to
     *                      add.
     */
    public final void addAttributeSets(List<String> attributeSets) {

        appliedAttributeSets.removeAll(attributeSets);
        appliedAttributeSets.addAll(attributeSets);
    }

    /**
     * Removes a list of attribute sets from this <code>AssetInstance</code>
     * instance.
     * 
     * @param attributeSets <code>List&lt;String&gt;</code>: The attribute sets to
     *                      remove.
     */
    public final void removeAttributeSets(List<String> attributeSets) {

        appliedAttributeSets.removeAll(attributeSets);
    }

    /**
     * Updates the attributes of this <code>AssetInstance</code> instance.
     */
    public final void updateAttributes() {

        attributeSetApplier(assetSchema.getAttributeSet());

        for (String attributeSetKey : appliedAttributeSets) {

            AssetSchemaAttributes attributeSet = assetSchema.getAttributeSet(attributeSetKey);
            attributeSetApplier(attributeSet);
        }
    }

    /**
     * Applies an attribute set to this <code>AssetInstance</code> instance.
     * This method allows a first order child of the <code>AssetInstance</code>
     * class to safely apply its own attribute set before its own child attempts to
     * apply its attribute set.
     * 
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to
     *                     apply.
     */
    protected void attributeSetApplier(AssetSchemaAttributes attributeSet) {

        applyAttributeSet(attributeSet);
    }

    /**
     * Applies an attribute set to this <code>AssetInstance</code> instance.
     * 
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to
     *                     apply.
     */
    protected abstract void applyAttributeSet(AssetSchemaAttributes attributeSet);
}
