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

package com.transcendruins.contentmodules.packs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.contentmodules.ContentModule;
import com.transcendruins.resources.ResourceSet;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.ReferenceWithoutDefinitionException;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>Pack</code>: A class representing the parsed JSON information of a
 * pack.
 */
public final class Pack extends ContentModule {

    /**
     * <code>HashMap&lt;Identifier, HashMap&lt;Identifier, Pack&gt;&gt;</code>: The
     * set of all packs stored within the program.
     */
    private static final HashMap<Identifier, HashMap<Identifier, Pack>> PACKS = new HashMap<>();

    /**
     * Adds a pack to the set of all packs.
     * 
     * @param pack <code>Pack</code>: The pack to add.
     */
    public static void addPack(Pack pack) {

        Identifier identifier = pack.getIdentifier();

        HashMap<Identifier, Pack> packs = PACKS.computeIfAbsent(identifier.toGeneric(), _ -> new HashMap<>());
        packs.putIfAbsent(identifier, pack);
    }

    /**
     * Retrieves the set of all packs of a generic ID.
     * 
     * @param id <code>Identifier</code>: The generic ID to check for.
     * @return <code>HashSet&lt;Identifier&gt;</code>: The pack identifiers which
     *         match the <code>id</code> parameter.
     */
    public static HashSet<Identifier> getPacks(Identifier id) {

        return new HashSet<>(PACKS.getOrDefault(id.toGeneric(), new HashMap<>()).keySet());
    }

    public static Pack getPack(Identifier id) {

        return PACKS.getOrDefault(id.toGeneric(), new HashMap<>()).get(id);
    }

    private final ImmutableMap<AssetType, ImmutableSet<Identifier>> missingAssets;

    public HashMap<AssetType, HashSet<Identifier>> getLeftoverMissing(Collection<Pack> packs) {

        HashMap<AssetType, HashSet<Identifier>> remaining = new HashMap<>();

        for (Map.Entry<AssetType, ImmutableSet<Identifier>> typeEntry : missingAssets.entrySet()) {

            AssetType type = typeEntry.getKey();
            HashSet<Identifier> typeSet = new HashSet<>(typeEntry.getValue());
            for (Pack pack : packs) {

                if (typeSet.isEmpty()) {

                    break;
                }

                typeSet.removeAll(pack.getAssets().get(type).keySet());
            }

            remaining.put(type, typeSet);
        }

        return remaining;
    }

    public boolean satisfiesMissing(Collection<Pack> packs) {

        for (Map.Entry<AssetType, ImmutableSet<Identifier>> typeEntry : missingAssets.entrySet()) {

            AssetType type = typeEntry.getKey();
            HashSet<Identifier> typeSet = new HashSet<>(typeEntry.getValue());
            for (Pack pack : packs) {

                if (typeSet.isEmpty()) {

                    return true;
                }

                typeSet.removeAll(pack.getAssets().get(type).keySet());
            }

            if (typeSet.isEmpty()) {

                return true;
            }
        }

        return true;
    }

    /**
     * <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     * A map of all asset configurations in this <code>Pack</code> instance.
     */
    private final ImmutableMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assets;

    /**
     * Retrieves the asset map of this <code>Pack</code> instance.
     * 
     * @return <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     *         The <code>assets</code> field of this <code>Pack</code> instance.
     */
    public ImmutableMap<AssetType, ImmutableMap<Identifier, AssetSchema>> getAssets() {

        return assets;
    }

    /**
     * Retrieves whether or not an asset is contained in the asset map of this
     * <code>Pack</code> instance.
     * 
     * @param type       <code>AssetType</code>: The type of asset to search for.
     * @param identifier <code>Identifier</code>: The identifier to the asset to
     *                   search for.
     * @return <code>boolean</code>: Whether or not the <code>assets</code> field
     *         of this <code>Pack</code> instance contains the asset.
     */
    public boolean containsAsset(AssetType type, Identifier identifier) {

        return assets.get(type).containsKey(identifier);
    }

    /**
     * Retrieves a value from the asset map of this <code>Pack</code> instance.
     * 
     * @param type       <code>AssetType</code>: The type of asset to retrieve.
     * @param identifier <code>Identifier</code>: The identifier of the asset to
     *                   retrieve.
     * @return <code>AssetSchema</code>: The asset retrieved from the
     *         <code>assets</code> field of this <code>Pack</code> instance.
     */
    public AssetSchema getAsset(AssetType type, Identifier identifier) {

        return assets.get(type).get(identifier);
    }

    /**
     * <code>ResourceSet</code>: The set of resources of this <code>Pack</code>
     * instance.
     */
    private final ResourceSet resources;

    /**
     * Retrieves the set of resources of this <code>Pack</code> instance.
     * 
     * @return <code>ResourceSet</code>: The <code>resources</code> field of this
     *         <code>Pack</code> instance.
     */
    public ResourceSet getResources() {

        return resources;
    }

    /**
     * Creates a new instance of the <code>Pack</code> class using the directory to
     * its root folder.
     * 
     * @param root         <code>Path</code>: The directory of the root folder
     *                     of this pack.
     * @param dependencies <code>Set&lt;Identified&gt;</code>: The pack dependencies
     *                     of this <code>Pack</code> instance.
     */
    public Pack(PackSchema schema, Set<Identifier> assetDependencies, Set<Identifier> resourceDependencies) {

        super(schema);

        this.resources = schema.getResources();

        HashMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assetMap = new HashMap<>();
        HashMap<AssetType, HashSet<Identifier>> missingAssetsMap = AssetType.createAssetMap(_ -> new HashSet<>());

        HashMap<AssetType, Set<Identifier>> dependencyAssets = AssetType
                .createAssetMap(type -> assetDependencies.stream()
                        .map(Pack::getPack) // Convert from an identifier to a pack.
                        .flatMap(dependency -> dependency.getAssets().get(type).keySet().stream()) // Flatten identifier
                                                                                                   // keys.
                        .collect(Collectors.toSet())); // Collect in a set.

        HashMap<AssetType, HashMap<Identifier, AssetSchema>> unvalidatedAssets = AssetType
                .createAssetMap(type -> new HashMap<>(schema.getAssets().get(type)));

        HashMap<AssetType, HashMap<Identifier, AssetSchema>> validatedAssets = AssetType
                .createAssetMap(_ -> new HashMap<>());

        for (Map.Entry<AssetType, HashMap<Identifier, AssetSchema>> typeEntry : unvalidatedAssets.entrySet()) {

            AssetType type = typeEntry.getKey();
            HashMap<Identifier, AssetSchema> typeMap = typeEntry.getValue();

            while (!typeMap.isEmpty()) {

                try {

                    validateAsset(typeMap.keySet().iterator().next(), type, unvalidatedAssets, validatedAssets,
                            dependencyAssets, missingAssetsMap);
                } catch (ReferenceWithoutDefinitionException e) {

                    e.print();
                }
            }

            assetMap.put(type, new ImmutableMap<>(validatedAssets.get(type)));
        }

        assets = new ImmutableMap<>(assetMap);
        missingAssets = new ImmutableMap<>(missingAssetsMap.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> new ImmutableSet<>(entry.getValue()),
                (parent, _) -> parent,
                HashMap::new)));

    }

    private void validateAsset(Identifier identifier, AssetType type,
            HashMap<AssetType, HashMap<Identifier, AssetSchema>> unvalidated,
            HashMap<AssetType, HashMap<Identifier, AssetSchema>> validated,
            HashMap<AssetType, Set<Identifier>> dependencyAssets,
            HashMap<AssetType, HashSet<Identifier>> missingAssets) throws ReferenceWithoutDefinitionException {

        AssetSchema asset = unvalidated.get(type).remove(identifier);
        for (AssetPresets dependencyPresets : asset.getAssetDependencies()) {

            Identifier dependency = dependencyPresets.getIdentifier();
            AssetType dependencyType = dependencyPresets.getType();

            boolean matchFound = false;

            if (validated.get(dependencyType).containsKey(dependency)) { // If the dependency has already been
                                                                         // validated, the match is found.

                matchFound = true;

            } else if (unvalidated.get(dependencyType).containsKey(dependency) // Validate the dependency if it is
                                                                               // unvalidated.
            ) {

                try {

                    validateAsset(dependency, dependencyType, unvalidated, validated, dependencyAssets,
                            missingAssets);

                    matchFound = true;
                } catch (ReferenceWithoutDefinitionException e) {

                    e.print();
                }
            }

            if (!matchFound && dependencyAssets.get(dependencyType).contains(dependency)) { // If necessary, check if
                                                                                            // the pack
                // dependencies contains the
                // dependency. If so, add it as a
                // required dependency.

                matchFound = true;
                missingAssets.get(type).add(dependency);
            }

            if (!matchFound) { // If a match was not found, throw an exception.

                throw new ReferenceWithoutDefinitionException(dependencyPresets.getIdentifierEntry(), "Identifier");
            }
        }

        validated.get(asset.getType()).put(asset.getIdentifier(), asset);
    }
}
