package com.transcendruins.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.transcendruins.packcompiling.Pack;
import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>EnvironmentState</code>: A class representing a the environment of a world.
 */
public final class EnvironmentState {

    /**
     * <code>HashMap&lt;AssetType, HashMap&lt;Identifier, AssetSchema&gt;&gt;</code>: The merged asset schemas of this <code>EnvironmentState</code> instance.
     */
    private final HashMap<AssetType, HashMap<Identifier, AssetSchema>> mergedAssets;

    /**
     * Creates a new instance of the <code>EnvironmentState</code> class.
     * @param packs <code>ArrayList&lt;Pack&gt;</code>: The packs used to create this <code>EnvironmentState</code> instance.
     */
    EnvironmentState(ArrayList<Pack> packs) {

        Iterator<Pack> iterator = packs.iterator();

        mergedAssets = new HashMap<>(iterator.next().getAssetMap());
        while (iterator.hasNext()) {

            for (Map.Entry<AssetType, HashMap<Identifier, AssetSchema>> assetTypeEntry : iterator.next().getAssetMap().entrySet()) {

                mergedAssets.get(assetTypeEntry.getKey()).putAll(assetTypeEntry.getValue());
            }
        }
    }

    /**
     * Retrieves an asset schema from this <code>EnvironmentState</code> instance.
     * @param type <code>AssetType</code>: The type of asset schema to retrieve.
     * @param identifier <code>Identifier</code>: The identifier of the asset schema to retrieve.
     * @return <code>AssetSchema</code>: The asset schema retrieved from the <code>mergedAssets</code> property of this <code>EnvironmentState</code> instance.
     */
    public AssetSchema getSchema(AssetType type, Identifier identifier) {

        if (!mergedAssets.containsKey(type)) {

            return null;
        }

        return mergedAssets.get(type).get(identifier);
    }

    /**
     * Retrieves the environment state of the current world.
     * @return <code>EnvironmentState</code>: The <code>environment</code> property of the <code>world</code> property of the <code>World</code> class.
     */
    public static EnvironmentState getEnvironment() {

        return World.getWorld().getEnvironment();
    }
}
