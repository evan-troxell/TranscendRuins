package com.transcendruins.world.assetinstances;

import java.util.ArrayList;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingAttributeSetException;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
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
     * @param schema <code>AssetSchema</code>: The schema used to generate this
     *               <code>AssetInstance</code> instance.
     * @param world  <code>World</code>: The world copy to assign to this
     *               <code>AssetInstance</code> instance.
     */
    public AssetInstance(AssetSchema schema, World world) {

        this.assetSchema = schema;
        this.world = world;
        updateAttributes();
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
     * Determines whether a attribute set is present in the schema of this
     * <code>AssetInstance</code> instance.
     * 
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The attribute set to
     *              check for.
     * @throws MissingAttributeSetException Thrown to indicate a reference to a
     *                                      attribute group missing from the schema.
     */
    public final void containsAttributeSet(TracedEntry<String> entry) throws MissingAttributeSetException {

        assetSchema.containsAttributeSet(entry);
    }

    /**
     * Applies a attribute set to this <code>AssetInstance</code> instance.
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
     * Retrieves an asset schema from the world copy of this
     * <code>AssetInstance</code> instance.
     * 
     * @param type       <code>AssetType</code>: The type of asset schema to
     *                   retrieve.
     * @param identifier <code>Identifier</code>: The identifier of the asset schema
     *                   to retrieve.
     * @return <code>AssetSchema</code>: The asset schema retrieved from the current
     *         environment state.
     */
    public final AssetSchema getSchema(AssetType type, Identifier identifier) {

        return world.getEnvironment().getSchema(type, identifier);
    }

    /**
     * Applies a attribute set to this <code>AssetInstance</code> instance.
     * 
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to
     *                     apply.
     */
    protected abstract void applyAttributeSet(AssetSchemaAttributes attributeSet);
}
