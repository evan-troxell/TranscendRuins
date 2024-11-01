package com.transcendruins.packcompiling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>PackCompiler</code>: A class which is used to assist in the compiling of a pack by storing dependencies of the pack.
 */
public final class PackCompiler {

    /**
     * <code>HashMap&lt;AssetType, ArrayList&lt;Identifiergt;&gt;</code>: A map of all dependency asset configurations in this <code>PackCompiler</code>.
     */
    private final HashMap<AssetType, ArrayList<Identifier>> dependencyAssets = new HashMap<>();

    /**
     * Creates a new instance of the <code>PackCompiler</code> class.
     */
    public PackCompiler(Pack pack) {

        // Iterate through all dependencies of this pack.
        for (Collection<Identifier> dependency : pack.filteredDependencies.values()) {

            // The retainer map of all dependency assets present in all satisfying packs.
            HashMap<AssetType, ArrayList<Identifier>> dependencyAssetsMerged = new HashMap<>();

            // Iterate through all satisfying packs.
            for (Identifier acceptedDependency : dependency) {

                Pack retrievedPack = Pack.PACKS.get(acceptedDependency);
                HashMap<AssetType, HashMap<Identifier, AssetSchema>> retrievedAssetMap = retrievedPack.getAssetMap();

                // Iterate through all asset types.
                for (Map.Entry<AssetType, HashMap<Identifier, AssetSchema>> packAsset : retrievedAssetMap.entrySet()) {

                    // If the asset type is not present in the retainer map, set the map to this one.
                    if (!dependencyAssetsMerged.containsKey(packAsset.getKey())) {

                        dependencyAssetsMerged.put(packAsset.getKey(), new ArrayList<>(packAsset.getValue().keySet()));

                    // Retain only the assets in both the merged dependency assets and the assets of the merged pack.
                    } else {

                        dependencyAssetsMerged.get(packAsset.getKey()).retainAll(packAsset.getValue().keySet());
                    }
                }
            }

            for (Map.Entry<AssetType, ArrayList<Identifier>> packAsset : dependencyAssetsMerged.entrySet()) {

                if (!dependencyAssets.containsKey(packAsset.getKey())) {

                       dependencyAssets.put(packAsset.getKey(),packAsset.getValue());
                } else {

                    dependencyAssets.get(packAsset.getKey()).addAll(packAsset.getValue());
                }
            }
        }
    }

    /**
     * Checks whether or not a specific asset is contained within this <code>PackCompiler</code> instance.
     * @param type <code>AssetType</code>: The type of asset to check for.
     * @param identifier <code>Identifier</code>: The identifier of the asset to check for.
     * @return <code>boolean</code>: Whether the asset was present in the <code>dependencyAssets</code> property of this <code>PackCompiler</code> instance.
     */
    public boolean containsAsset(AssetType type, Identifier identifier) {

        if (!dependencyAssets.containsKey(type)) {

            return false;
        }
        return dependencyAssets.get(type).contains(identifier);
    }
}
