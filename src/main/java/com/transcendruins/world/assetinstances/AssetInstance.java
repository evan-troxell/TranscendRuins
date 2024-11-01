package com.transcendruins.world.assetinstances;

import java.util.ArrayList;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingComponentSetException;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.World;

/**
 * <code>AssetInstance</code>: A class representing a generated instance of any asset type, including but not limited to: structures, elements, entities, items, and more.
 */
public abstract class AssetInstance {

    /**
     * <code>AssetSchema</code>: The schema used to generate this <code>AssetInstance</code> instance.
     */
    private final AssetSchema assetSchema;

    /**
     * <code>ArrayList&lt;String&gt;</code>: The list of currently applied component sets.
     */
    private final ArrayList<String> appliedComponentSets = new ArrayList<>();

    /**
     * Creates a new instance of the <code>AssetInstance</code> class.
     * @param schema <code>AssetSchema</code>: The schema used to generate this <code>AssetInstance</code> instance.
     */
    public AssetInstance(AssetSchema schema) {

        this.assetSchema = schema;
        updateComponents();
    }

    /**
     * Updates the components of this <code>AssetInstance</code> instance.
     */
    public final void updateComponents() {

        componentSetApplier(assetSchema.getComponentSet());

        for (String componentSetKey : appliedComponentSets) {

            AssetSchemaComponents componentSet = assetSchema.getComponentSet(componentSetKey);
            componentSetApplier(componentSet);
        }
    }

    /**
     * Determines whether a component set is present in the schema of this <code>AssetInstance</code> instance.
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The component set to check for.
     * @throws MissingComponentSetException Thrown to indicate a reference to a component group missing from the schema.
     */
    public final void containsComponentSet(TracedEntry<String> entry) throws MissingComponentSetException {

        assetSchema.containsComponentSet(entry);
    }

    /**
     * Applies a component set to this <code>AssetInstance</code> instance.
     * This method allows a first order child of the <code>AssetInstance</code> class to safely apply its own component set before its own child attempts to apply its component set.
     * @param componentSet <code>AssetSchemaComponents</code>: The component set to apply.
     */
    protected void componentSetApplier(AssetSchemaComponents componentSet) {

        applyComponentSet(componentSet);
    }

    /**
     * Retrieves an asset schema from the current environment of the program.
     * @param type <code>AssetType</code>: The type of asset schema to retrieve.
     * @param identifier <code>Identifier</code>: The identifier of the asset schema to retrieve.
     * @return <code>AssetSchema</code>: The asset schema retrieved from the current environment state.
     */
    protected static final AssetSchema getSchema(AssetType type, Identifier identifier) {

        return World.getWorld().getEnvironment().getSchema(type, identifier);
    }

    /**
     * Applies a component set to this <code>AssetInstance</code> instance.
     * @param componentSet <code>AssetSchemaComponents</code>: The component set to apply.
     */
    protected abstract void applyComponentSet(AssetSchemaComponents componentSet);
}
