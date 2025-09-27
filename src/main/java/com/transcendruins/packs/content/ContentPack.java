/* Copyright 2025 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins.packs.content;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.catalogue.AssetCatalogue;
import com.transcendruins.packs.Pack;
import com.transcendruins.resources.ResourceSet;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.ReferenceWithoutDefinitionException;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>ContentPack</code>: A class representing the parsed JSON information of
 * a pack.
 */
public final class ContentPack extends Pack {

    /**
     * <code>HashMap&lt;Identifier, HashMap&lt;Identifier, Pack&gt;&gt;</code>: The
     * set of all packs stored within the program.
     */
    private static final HashMap<Identifier, HashMap<Identifier, ContentPack>> PACKS = new HashMap<>();

    /**
     * Adds a pack to the set of all packs.
     * 
     * @param pack <code>ContentPack</code>: The pack to add.
     */
    public static void addPack(ContentPack pack) {

        Identifier identifier = pack.getIdentifier();

        HashMap<Identifier, ContentPack> packs = PACKS.computeIfAbsent(identifier.toGeneric(), _ -> new HashMap<>());
        packs.putIfAbsent(identifier, pack);
    }

    /**
     * Retrieves the set of all packs of a generic ID.
     * 
     * @param id <code>Identifier</code>: The generic id to check for.
     * @return <code>HashSet&lt;Identifier&gt;</code>: The pack identifiers which
     *         match the <code>id</code> parameter.
     */
    public static HashSet<Identifier> getPacks(Identifier id) {

        return new HashSet<>(PACKS.getOrDefault(id.toGeneric(), new HashMap<>()).keySet());
    }

    public static ContentPack getPack(Identifier id) {

        return PACKS.getOrDefault(id.toGeneric(), new HashMap<>()).get(id);
    }

    /**
     * <code>ImmutableMap&lt;AssetType, ImmutableSet&lt;Identifier&gt;&gt;</code>:
     * The set of all asset dependencies referenced by this <code>ContentPack</code>
     * instance which must be fulfilled by its content pack dependencies.
     */
    private final ImmutableMap<AssetType, ImmutableSet<Identifier>> missingAssets;

    /**
     * Determines which assets will still need to be supplemented by the content
     * pack dependencies of this <code>ContentPack</code> instance after a set of
     * packs has been applied.
     * 
     * @param packs <code>Collection&lt;ContentPack&gt;</code>: The subset of packs
     *              to check for.
     * @return <code>HashMap&lt;AssetType, HashSet&lt;Identifier&gt;&gt;</code>: The
     *         set of asset dependencies remaining after the packs have been
     *         applied.
     */
    public HashMap<AssetType, HashSet<Identifier>> getRemainingMissing(Collection<ContentPack> packs) {

        HashMap<AssetType, HashSet<Identifier>> remaining = new HashMap<>();

        for (Map.Entry<AssetType, ImmutableSet<Identifier>> typeEntry : missingAssets.entrySet()) {

            AssetType type = typeEntry.getKey();

            // Begin with all existing depnedencies of the asset type.
            HashSet<Identifier> typeSet = new HashSet<>(typeEntry.getValue());

            boolean isEmpty = false;
            for (ContentPack pack : packs) {

                // Remove the asset dependencies fulfilled by the pack.
                typeSet.removeAll(pack.getAssets().get(type).keySet());

                if (typeSet.isEmpty()) {

                    isEmpty = true;
                    break;
                }
            }

            // If there are any asset dependencies remaining, add them to the map.
            if (!isEmpty) {

                remaining.put(type, typeSet);
            }
        }

        return remaining;
    }

    /**
     * Determines if there are any assets which will still need to be supplemented
     * by the content pack dependencies of this <code>ContentPack</code> instance
     * after a set of packs has been applied.
     * 
     * @param packs <code>Collection&lt;ContentPack&gt;</code>: The subset of packs
     *              to check for.
     * @return <code>boolean</code>: Whether or not the set of asset dependencies
     *         remaining after the packs have been applied is empty.
     */
    public boolean satisfiesMissing(Collection<ContentPack> packs) {

        return getRemainingMissing(packs).isEmpty();
    }

    /**
     * <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     * A map of all asset configurations in this <code>ContentPack</code> instance.
     */
    private final ImmutableMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assets;

    /**
     * Retrieves the asset map of this <code>ContentPack</code> instance.
     * 
     * @return <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     *         The <code>assets</code> field of this <code>ContentPack</code>
     *         instance.
     */
    public ImmutableMap<AssetType, ImmutableMap<Identifier, AssetSchema>> getAssets() {

        return assets;
    }

    /**
     * Retrieves whether or not an asset is contained in the asset map of this
     * <code>ContentPack</code> instance.
     * 
     * @param type       <code>AssetType</code>: The type of asset to search for.
     * @param identifier <code>Identifier</code>: The identifier to the asset to
     *                   search for.
     * @return <code>boolean</code>: Whether or not the <code>assets</code> field of
     *         this <code>ContentPack</code> instance contains the asset.
     */
    public boolean containsAsset(AssetType type, Identifier identifier) {

        return assets.get(type).containsKey(identifier);
    }

    /**
     * Retrieves a value from the asset map of this <code>ContentPack</code>
     * instance.
     * 
     * @param type       <code>AssetType</code>: The type of asset to retrieve.
     * @param identifier <code>Identifier</code>: The identifier of the asset to
     *                   retrieve.
     * @return <code>AssetSchema</code>: The asset retrieved from the
     *         <code>assets</code> field of this <code>ContentPack</code> instance.
     */
    public AssetSchema getAsset(AssetType type, Identifier identifier) {

        return assets.get(type).get(identifier);
    }

    /**
     * <code>ResourceSet</code>: The set of resources of this
     * <code>ContentPack</code> instance.
     */
    private final ResourceSet resources;

    /**
     * Retrieves the set of resources of this <code>ContentPack</code> instance.
     * 
     * @return <code>ResourceSet</code>: The <code>resources</code> field of this
     *         <code>ContentPack</code> instance.
     */
    public ResourceSet getResources() {

        return resources;
    }

    private final AssetCatalogue catalogue;

    public AssetCatalogue getCatalogue() {

        return catalogue;
    }

    /**
     * Creates a new instance of the <code>ContentPack</code> class using the
     * directory to its root folder.
     * 
     * @param root         <code>Path</code>: The directory of the root folder of
     *                     this pack.
     * @param dependencies <code>Set&lt;Identified&gt;</code>: The pack dependencies
     *                     of this <code>ContentPack</code> instance.
     */
    public ContentPack(PackSchema schema, Set<Identifier> assetDependencies, Set<Identifier> resourceDependencies) {

        super(schema);

        HashMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assetMap = new HashMap<>();

        // The map of assets which must be satisfied by dependencies (i.e missing from
        // this pack).
        HashMap<AssetType, HashSet<Identifier>> missingAssetsMap = AssetType.createAssetMap(_ -> new HashSet<>());

        // Collects all of the assets from the dependencies.
        HashMap<AssetType, Set<Identifier>> dependencyAssets = AssetType
                .createAssetMap(type -> assetDependencies.stream().map(ContentPack::getPack) // Convert from an
                                                                                             // identifier to a pack.
                        .flatMap(dependency -> dependency.getAssets().get(type).keySet().stream()) // Flatten identifier
                                                                                                   // keys.
                        .collect(Collectors.toSet())); // Collect in a set.

        // The map of unvalidated assets (currently all assets).
        HashMap<AssetType, HashMap<Identifier, AssetSchema>> unvalidatedAssets = AssetType
                .createAssetMap(type -> new HashMap<>(schema.getAssets().get(type)));

        // The map of validated assets (currently no assets).
        HashMap<AssetType, HashMap<Identifier, AssetSchema>> validatedAssets = AssetType
                .createAssetMap(_ -> new HashMap<>());

        // Traverse all unvalidated asset types.
        for (Map.Entry<AssetType, HashMap<Identifier, AssetSchema>> typeEntry : unvalidatedAssets.entrySet()) {

            AssetType type = typeEntry.getKey();
            HashMap<Identifier, AssetSchema> typeMap = typeEntry.getValue();

            // Validate the next asset while there are unvalidated assets of a certain type.
            while (!typeMap.isEmpty()) {

                try {

                    // This will validate both the next asset and all of its dependencies.
                    validateAsset(typeMap.keySet().iterator().next(), type, unvalidatedAssets, validatedAssets,
                            dependencyAssets, missingAssetsMap);
                } catch (ReferenceWithoutDefinitionException _) {
                }
            }

            assetMap.put(type, new ImmutableMap<>(validatedAssets.get(type)));
        }

        assets = new ImmutableMap<>(assetMap);
        missingAssets = new ImmutableMap<>(
                missingAssetsMap.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(),
                        entry -> new ImmutableSet<>(entry.getValue()), (parent, _) -> parent, HashMap::new)));

        resources = schema.getResources();

        // TODO Process location and global event dependencies
        // TODO Process overlay and menu dependencies
        // TODO Process recipe dependencies
        catalogue = schema.getCatalogue();
    }

    /**
     * Validates an asset from this <code>ContentPack</code> instance and its
     * dependencies, also determining any external dependencies which are required.
     * 
     * @param identifier       <code>Identifier</code>: The identifier of the asset
     *                         to validate.
     * @param type             <code>AssetType</code>: The asset type of the asset
     *                         to validate.
     * @param unvalidated      <code>HashMap&lt;AssetType, HashMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     *                         The set of currently unvalidated assets in this
     *                         <code>ContentPack</code> instance.
     * @param validated        <code>HashMap&lt;AssetType, HashMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     *                         The set of currently validated assets in this
     *                         <code>ContentPack</code> instance.
     * @param dependencyAssets <code>HashMap&lt;AssetType, Set&lt;Identifier&gt;&gt;</code>:
     *                         The set of all assets collectively contained within
     *                         the dependencies of this <code>ContentPack</code>
     *                         instance.
     * @param missingAssets    <code>HashMap&lt;AssetType, HashSet&lt;Identifier&gt;&gt;</code>:
     *                         The set of assets which must be satisfied by the
     *                         content pack dependencies of this
     *                         <code>ContentPack</code> instance.
     * @throws ReferenceWithoutDefinitionException Thrown if the asset referenced
     *                                             another missing asset.
     */
    // TODO: Current hierarchical, modify to allow circular dependencies
    private void validateAsset(Identifier identifier, AssetType type,
            HashMap<AssetType, HashMap<Identifier, AssetSchema>> unvalidated,
            HashMap<AssetType, HashMap<Identifier, AssetSchema>> validated,
            HashMap<AssetType, Set<Identifier>> dependencyAssets, HashMap<AssetType, HashSet<Identifier>> missingAssets)
            throws ReferenceWithoutDefinitionException {

        // Retrieve the asset.
        AssetSchema asset = unvalidated.get(type).remove(identifier);

        // Iterate through each asset dependency.
        for (AssetPresets dependencyPresets : asset.getAssetDependencies()) {

            Identifier dependency = dependencyPresets.getIdentifier();
            AssetType dependencyType = dependencyPresets.getType();

            if (validated.get(dependencyType).containsKey(dependency)) { // If the dependency has already been
                                                                         // validated, the match is found.
                continue;
            }

            if (unvalidated.get(dependencyType).containsKey(dependency)) { // Validate the dependency if it is
                                                                           // unvalidated.
                try {

                    validateAsset(dependency, dependencyType, unvalidated, validated, dependencyAssets, missingAssets);
                    continue;
                } catch (ReferenceWithoutDefinitionException _) {
                }
            }

            if (dependencyAssets.get(dependencyType).contains(dependency)) { // If necessary, check if
                                                                             // the pack
                // dependencies contains the
                // dependency. If so, add it as a
                // required dependency.

                missingAssets.get(type).add(dependency);
                continue;
            }

            // If a match was not found, throw an exception.

            // Raise the exception with the definition code of the sentence-case asset type
            // (e.g 'Animation Controller', 'Element', 'Render Material').
            throw new ReferenceWithoutDefinitionException(dependencyPresets.getIdentifierEntry(),
                    dependencyType.toSentenceString());
        }

        validated.get(asset.getType()).put(asset.getIdentifier(), asset);
    }
}
