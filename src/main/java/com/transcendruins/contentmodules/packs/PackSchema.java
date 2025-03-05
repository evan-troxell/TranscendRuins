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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.contentmodules.ContentModule;
import com.transcendruins.resources.ResourceSet;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions.DependencyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.identifierexceptions.DuplicateIdentifierException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>PackSchema</code>: A class representing the parsed JSON information of
 * a
 * pack.
 */
public final class PackSchema extends ContentModule {

    /**
     * <code>ImmutableMap&lt;String, &lt;Identifier, PackDependency&gt;&gt;</code>:
     * The dependencies
     * of this pack.
     */
    private final ImmutableMap<String, ImmutableMap<Identifier, PackDependency>> dependencies;

    /**
     * Retrieves the dependencies of this <code>PackSchema</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, &lt;Identifier, PackDependency&gt;&gt;</code>:
     *         The <code>dependencies</code> field of this <code>PackSchema</code>
     *         instance.
     */
    public ImmutableMap<String, ImmutableMap<Identifier, PackDependency>> getDependencies() {

        return dependencies;
    }

    /**
     * <code>HashMap&lt;AssetType, HashMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     * A map of all asset configurations in this <code>PackSchema</code> instance.
     */
    private final ImmutableMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assets;

    /**
     * Retrieves the asset map of this <code>PackSchema</code> instance.
     * 
     * @return <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     *         The <code>assets</code> field of this <code>PackSchema</code>
     *         instance.
     */
    public ImmutableMap<AssetType, ImmutableMap<Identifier, AssetSchema>> getAssets() {

        return assets;
    }

    /**
     * Retrieves whether or not an asset is contained in the asset map of this
     * <code>PackSchema</code> instance.
     * 
     * @param type       <code>AssetType</code>: The type of asset to search for.
     * @param identifier <code>Identifier</code>: The identifier to the asset to
     *                   search for.
     * @return <code>boolean</code>: Whether or not the <code>assets</code> field
     *         of this <code>PackSchema</code> instance contains the asset.
     */
    public boolean containsAsset(AssetType type, Identifier identifier) {

        return assets.get(type).containsKey(identifier);
    }

    /**
     * Retrieves a value from the asset map of this <code>PackSchema</code>
     * instance.
     * 
     * @param type       <code>AssetType</code>: The type of asset to retrieve.
     * @param identifier <code>Identifier</code>: The identifier of the asset to
     *                   retrieve.
     * @return <code>AssetSchema</code>: The asset retrieved from the
     *         <code>assets</code> field of this <code>PackSchema</code> instance.
     */
    public AssetSchema getAsset(AssetType type, Identifier identifier) {

        return assets.get(type).get(identifier);
    }

    /**
     * <code>ResourceSet</code>: The set of resources of this
     * <code>PackSchema</code> instance.
     */
    private final ResourceSet resources;

    /**
     * Retrieves the set of resources of this <code>PackSchema</code> instance.
     * 
     * @return <code>Resourceset</code>: The <code>resources</code> field of this
     *         <code>PackSchema</code> instance.
     */
    public ResourceSet getResources() {

        return resources;
    }

    /**
     * Creates a new instance of the <code>PackSchema</code> class using the
     * directory to its root folder.
     * 
     * @param root <code>Path</code>: The directory of the root folder
     *             of this pack.
     * @throws LoggedException Thrown if any exception is raised while creating this
     *                         <code>PackSchema</code> instance.
     */
    public PackSchema(TracedPath root) throws LoggedException {

        super(root);

        Identifier packId = getIdentifier();

        TracedDictionary json = getJson();

        HashMap<Identifier, PackDependency> assetDependencies = new HashMap<>();
        HashMap<Identifier, PackDependency> resourceDependencies = new HashMap<>();

        TracedEntry<TracedArray> dependenciesEntry = json.getAsArray("dependencies", true);
        if (dependenciesEntry.containsValue()) {

            TracedArray dependenciesJson = (TracedArray) dependenciesEntry.getValue();

            for (int i : dependenciesJson.getIndices()) {

                TracedEntry<TracedDictionary> dependencyEntry = dependenciesJson.getAsDict(i, false);
                PackDependency dependency = new PackDependency(dependencyEntry);
                Identifier dependencyId = dependency.getIdentifier();

                if (dependency.getType().equals(PackDependency.RESOURCE)) {

                    // Check for any overlaps with resource dependencies.
                    if (!dependency.getCompatible(resourceDependencies.keySet()).isEmpty()) {

                        throw DependencyException.duplicateDependency(packId, dependency);
                    }
                    resourceDependencies.put(dependencyId, dependency);
                } else {

                    if (dependency.compatible(packId)
                            && !dependency.lessThan(packId.getVersion())) {

                        // Ensure the pack version is less than the dependency range if they share the
                        // same ID.
                        throw DependencyException.ambiguousDependency(packId, dependency);
                    }

                    // Check for any overlaps with asset dependencies.
                    if (!dependency.getCompatible(assetDependencies.keySet()).isEmpty()) {

                        throw DependencyException.duplicateDependency(packId, dependency);
                    }
                    assetDependencies.put(dependencyId, dependency);
                }

            }
        }

        dependencies = new ImmutableMap<>(Map.of(
                PackDependency.RESOURCE, new ImmutableMap<>(resourceDependencies),
                PackDependency.ASSET, new ImmutableMap<>(assetDependencies)));

        resources = new ResourceSet(root);

        HashMap<String, ArrayList<TracedPath>> paths = root.compileDirectories()
                .stream()
                .collect(Collectors.toMap(path -> path.getFileName(), path -> path.compileFiles(TracedPath.JSON, true),
                        (val, _) -> val, HashMap::new));

        assets = new ImmutableMap<>(AssetType.createAssetMap(type -> createSchemas(type, paths)));
    }

    private ImmutableMap<Identifier, AssetSchema> createSchemas(AssetType assetType,
            HashMap<String, ArrayList<TracedPath>> paths) {

        HashMap<Identifier, AssetSchema> schemaMap = paths
                .getOrDefault(assetType.toString(), new ArrayList<>())
                .stream().map(path -> {

                    try {

                        // Attempt to create the schema.
                        return assetType.createSchema(path);
                    } catch (LoggedException e) {

                        // If the schema could not be built, log the exception and return an empty
                        // value.
                        e.print();
                        return null;
                    }
                })
                .filter(Objects::nonNull) // Remove all null values.
                .collect(Collectors.toMap( // Map the schemas.
                        AssetSchema::getIdentifier, // Retrieve the identifier.
                        schema -> schema, // Keep the schema.
                        (parent, child) -> {

                            // If a duplicate is found, log a duplication exception and ignore the
                            // duplicate value.
                            try {

                                throw new DuplicateIdentifierException(child.getIdentifierEntry());
                            } catch (DuplicateIdentifierException e) {

                                e.print();
                            }

                            return parent;
                        },
                        HashMap::new));

        return new ImmutableMap<>(schemaMap);
    }
}
