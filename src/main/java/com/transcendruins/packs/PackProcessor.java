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

package com.transcendruins.packs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.transcendruins.packs.content.ContentPack;
import com.transcendruins.packs.content.PackDependency;
import com.transcendruins.packs.content.PackDependency.DependencyType;
import com.transcendruins.packs.content.PackSchema;
import com.transcendruins.packs.resources.ResourcePack;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions.DependencyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.identifierexceptions.DuplicateIdentifierException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>PackProcessor</code>: A class which processes individual packs in a
 * directory into complete <code>ContentPack</code> instances.
 */
public final class PackProcessor {

    public static final TracedPath INTERNAL_PACKS_DIRECTORY = TracedPath.INTERNAL_DIRECTORY.extend("packs");

    public static final TracedPath LIBRARY_PACKS_DIRECTORY = TracedPath.LIBRARY_DIRECTORY.extend("packs");

    /**
     * <code>HashMap&lt;Identifier, PackSchema&gt;</code>: The set of all content
     * pack identifiers which have been entered into the processor.
     */
    private final HashMap<Identifier, PackSchema> processed = new HashMap<>();

    /**
     * <code>HashMap&lt;Identifier, PackSchema&gt;</code>: A map of all currently
     * validated packs to their identifiers.
     */
    private final HashMap<Identifier, PackSchema> validated = new HashMap<>();

    /**
     * <code>HashMap&lt;Identifier, PackSchema&gt;</code>: A map of all currently
     * unvalidated packs to their identifiers.
     */
    private final HashMap<Identifier, PackSchema> unvalidated = new HashMap<>();

    /**
     * The static <code>PackProcessor</code> instance which is used to compile and
     * validate all packs.
     */
    private static final PackProcessor PACK_PROCESSOR = new PackProcessor();

    /**
     * Retrieves the pack processor, for use in processing and compiling pack
     * directories into completed packs.
     * 
     * @return <code>PackProcessor</code>: The <code>PACK_PROCESSOR</code> field.
     */
    public static PackProcessor getProcessor() {

        return PACK_PROCESSOR;
    }

    /**
     * Creates a new instance of the <code>PackProcessor</code> class and
     * automatically compiles all vanilla versions and user-inputted packs.
     */
    private PackProcessor() {

        process(INTERNAL_PACKS_DIRECTORY);
        process(LIBRARY_PACKS_DIRECTORY);
    }

    /**
     * Compiles the resource and content packs in a root directory.
     * 
     * @param root <code>TracedPath</code>: The root directory to process.
     */
    public synchronized void process(TracedPath root) {

        TracedPath resourcePath = root.extend("resources");
        List<TracedPath> resourcePaths = resourcePath.listDirectories(TracedPath.RESOURCE_PACK);

        for (TracedPath resource : resourcePaths) {

            addResource(resource);
        }

        TracedPath contentPath = root.extend("content");
        List<TracedPath> contentPaths = contentPath.listDirectories(TracedPath.CONTENT_PACK);

        if (!contentPaths.isEmpty()) {

            for (TracedPath content : contentPaths) {

                addContent(content);
            }

            validate();
            compile();
        }
    }

    /**
     * Compiles a path into an unvalidated resource pack.
     * 
     * @param path <code>TracedPath</code>: The path to compile.
     */
    public synchronized void addResource(TracedPath path) {

        try {

            ResourcePack.addResource(new ResourcePack(path));

            // If the resource could not be processed for any reason, log the error.
        } catch (LoggedException _) {
        }
    }

    /**
     * Compiles a path into an unvalidated content pack.
     * 
     * @param path <code>TracedPath</code>: The path to compile.
     */
    public synchronized void addContent(TracedPath path) {

        try {

            PackSchema pack = new PackSchema(path);
            Identifier identifier = pack.getIdentifier();

            if (processed.containsKey(identifier)) {

                throw new DuplicateIdentifierException(pack.getIdentifierEntry());
            }

            processed.put(identifier, pack);
            unvalidated.put(identifier, pack);

            // If the pack could not be processed for any reason, log the error.
        } catch (LoggedException _) {
        }
    }

    /**
     * Validates all content packs in the <code>unvalidated</code> field of this
     * <code>PackProcessor</code> instance.
     */
    public synchronized void validate() {

        while (!unvalidated.isEmpty()) {

            try {

                validateContent(unvalidated.keySet().iterator().next());

            } catch (DependencyException _) {
            }
        }
    }

    /**
     * Recursively iterates through the dependencies of an unvalidated pack and
     * determines if they are satisfied by other previously validated content pack
     * in the program.
     * 
     * @param identifier <code>Identifier</code>: The identifier of the pack to
     *                   validate.
     * @throws DependencyException Thrown if an exception related to the
     *                             dependencies of the pack pointed to by the
     *                             <code>identifier</code> parameter is raised.
     */
    private void validateContent(Identifier identifier) throws DependencyException {

        // Retrieves the pack and removes it from unvalidated.
        PackSchema pack = unvalidated.remove(identifier);
        ImmutableMap<DependencyType, ImmutableMap<Identifier, PackDependency>> dependencies = pack.getDependencies();

        // Iterate through each dependency in the pack.
        for (Map.Entry<Identifier, PackDependency> dependencyEntry : dependencies.get(DependencyType.ASSET)
                .entrySet()) {

            Identifier dependencyIdentifier = dependencyEntry.getKey();
            PackDependency dependency = dependencyEntry.getValue();

            boolean matchFound = false;

            // If the dependency is compatible with any existing packs, its match has
            // already been found.
            if (!dependency.getMatches(ContentPack.getPacks(dependencyIdentifier)).isEmpty()) {

                matchFound = true;
            }

            // If the dependency is compatible with any validated packs, its match has
            // already been found.
            if (!dependency.getMatches(validated.keySet()).isEmpty()) {

                matchFound = true;
            }

            // Retrieve the compatible unvalidated packs.
            HashSet<Identifier> compatible = dependency.getMatches(unvalidated.keySet());

            // Iterate through each compatible pack and test for validity.
            for (Identifier candidate : compatible) {

                try {

                    validateContent(candidate);
                    matchFound = true;
                } catch (DependencyException _) {
                }
            }

            if (!matchFound) {

                throw DependencyException.unresolvedDependency(dependency);
            }

            for (Identifier candidate : dependency.getMatches(processed.keySet())) {

                validateVersionHierarchy(identifier, dependency, processed.get(candidate));
            }
        }

        // Iterate through each dependency in the pack.
        for (Map.Entry<Identifier, PackDependency> dependencyEntry : dependencies.get(DependencyType.RESOURCE)
                .entrySet()) {

            Identifier dependencyIdentifier = dependencyEntry.getKey();
            PackDependency dependency = dependencyEntry.getValue();

            if (dependency.getMatches(ResourcePack.getResources(dependencyIdentifier)).isEmpty()) {

                throw DependencyException.unresolvedDependency(dependency);
            }
        }

        validated.put(identifier, pack);
    }

    /**
     * Ensures that a pack only contains dependencies which are lower than that of a
     * specified identifier. This works by propogating the identifyer through each
     * additional layer of packs and checking all compatible dependencies (same
     * namespace:id combination) to ensure every dependency is lower than the pack.
     * The call case of this function should be used to determine if a pack's
     * identifier is greater than every other pack node in its dependency tree.
     * 
     * @param identifier <code>Identifier</code>: The identifier of the pack whose
     *                   subdependencies to check through.
     * @param root       <code>PackDependency</code>: The dependency which this
     *                   validation originated from. This should be a dependency of
     *                   the pack which the <code>identifier</code> parameter points
     *                   to.
     * @param pack       <code>PackSchema</code>: The subdependency to check.
     * @throws DependencyException
     */
    private void validateVersionHierarchy(Identifier identifier, PackDependency root, PackSchema pack)
            throws DependencyException {

        for (Map.Entry<Identifier, PackDependency> dependencyEntry : pack.getDependencies().get(DependencyType.ASSET)
                .entrySet()) {

            PackDependency dependency = dependencyEntry.getValue();

            // If the dependency matches but is not lower, raise an error.
            if (dependency.compatible(identifier) && !dependency.isLessThan(identifier.getVersion())) {

                throw DependencyException.versionHierarchyException(identifier, root, dependency);
            }

            // Check all packs which match the dependency.
            for (Identifier overlap : dependency.getMatches(processed.keySet())) {

                validateVersionHierarchy(identifier, root, processed.get(overlap));
            }
        }
    }

    /**
     * Compiles all content packs in the <code>validated</code> field of this
     * <code>PackProcessor</code> instance.
     */
    public synchronized void compile() {

        if (!unvalidated.isEmpty()) {

            validate();
        }

        while (!validated.isEmpty()) {

            compilePack(validated.keySet().iterator().next());
        }
    }

    /**
     * Recursively iterates through the dependencies of a validated pack and
     * compiles them, generating a complete list of dependencies to create the final
     * <code>ContentPack</code> from.
     * 
     * @param identifier <code>Identifier</code>: The identifier of the pack to
     *                   compile.
     */
    private void compilePack(Identifier identifier) {

        // Retrieves the pack and removes it from validated.
        PackSchema pack = validated.remove(identifier);

        ImmutableMap<DependencyType, ImmutableMap<Identifier, PackDependency>> dependencies = pack.getDependencies();

        HashSet<Identifier> assetDependencies = new HashSet<>();
        for (Map.Entry<Identifier, PackDependency> dependencyEntry : dependencies.get(DependencyType.ASSET)
                .entrySet()) {

            Identifier dependencyIdentifier = dependencyEntry.getKey();
            PackDependency dependency = dependencyEntry.getValue();

            for (Identifier candidate : dependency.getMatches(validated.keySet())) {

                compilePack(candidate);
            }

            assetDependencies.addAll(dependency.getMatches(ContentPack.getPacks(dependencyIdentifier)));
        }

        HashSet<Identifier> resourceDependencies = new HashSet<>();
        for (Map.Entry<Identifier, PackDependency> dependencyEntry : dependencies.get(DependencyType.RESOURCE)
                .entrySet()) {

            Identifier dependencyIdentifier = dependencyEntry.getKey();
            PackDependency dependency = dependencyEntry.getValue();

            resourceDependencies.addAll(dependency.getMatches(ResourcePack.getResources(dependencyIdentifier)));
        }

        ContentPack.addPack(new ContentPack(pack, assetDependencies, resourceDependencies));
    }
}
