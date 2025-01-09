package com.transcendruins.world.assetinstances;

import java.util.ArrayList;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaModules;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingModuleSetException;
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
     * <code>ArrayList&lt;String&gt;</code>: The list of currently applied module sets.
     */
    private final ArrayList<String> appliedModuleSets = new ArrayList<>();

    /**
     * Creates a new instance of the <code>AssetInstance</code> class.
     * @param schema <code>AssetSchema</code>: The schema used to generate this <code>AssetInstance</code> instance.
     */
    public AssetInstance(AssetSchema schema) {

        this.assetSchema = schema;
        updateModules();
    }

    /**
     * Updates the modules of this <code>AssetInstance</code> instance.
     */
    public final void updateModules() {

        moduleSetApplier(assetSchema.getModuleSet());

        for (String moduleSetKey : appliedModuleSets) {

            AssetSchemaModules moduleSet = assetSchema.getModuleSet(moduleSetKey);
            moduleSetApplier(moduleSet);
        }
    }

    /**
     * Determines whether a module set is present in the schema of this <code>AssetInstance</code> instance.
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The module set to check for.
     * @throws MissingModuleSetException Thrown to indicate a reference to a module group missing from the schema.
     */
    public final void containsModuleSet(TracedEntry<String> entry) throws MissingModuleSetException {

        assetSchema.containsModuleSet(entry);
    }

    /**
     * Applies a module set to this <code>AssetInstance</code> instance.
     * This method allows a first order child of the <code>AssetInstance</code> class to safely apply its own module set before its own child attempts to apply its module set.
     * @param moduleSet <code>AssetSchemaModules</code>: The module set to apply.
     */
    protected void moduleSetApplier(AssetSchemaModules moduleSet) {

        applyModuleSet(moduleSet);
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
     * Applies a module set to this <code>AssetInstance</code> instance.
     * @param moduleSet <code>AssetSchemaModules</code>: The module set to apply.
     */
    protected abstract void applyModuleSet(AssetSchemaModules moduleSet);
}
