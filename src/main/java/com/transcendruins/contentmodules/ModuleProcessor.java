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

package com.transcendruins.contentmodules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.transcendruins.contentmodules.packs.Pack;
import com.transcendruins.contentmodules.packs.PackDependency;
import com.transcendruins.contentmodules.packs.PackSchema;
import com.transcendruins.contentmodules.resources.Resource;
import com.transcendruins.save.CacheOperator;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions.DependencyException;
import static com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions.DependencyException.unresolvedDependency;
import static com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions.DependencyException.versionHierarchyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.identifierexceptions.DuplicateIdentifierException;
import com.transcendruins.utilities.files.TracedPath;
import static com.transcendruins.utilities.files.TracedPath.CUSTOM_DIRECTORY;
import static com.transcendruins.utilities.files.TracedPath.INTERNAL_DIRECTORY;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>ModuleProcessor</code>: A class which processes individual packs in a
 * directory into complete <code>Pack</code> instances.
 */
public final class ModuleProcessor {

    /**
     * <code>TracedPath</code>: The filepath of the directory containing all
     * user-inputted modules.
     */
    public static final TracedPath CACHE_MODULES = CacheOperator.CACHE_DIRECTORY.extend("modules");

    /**
     * <code>HashMap&lt;Identifier, PackSchema&gt;</code>: The set of all pack
     * identifiers which
     * have been entered into the processor.
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
     * The static <code>ModuleProcessor</code> instance which is used to compile and
     * validate all packs.
     */
    private static final ModuleProcessor PACK_PROCESSOR = new ModuleProcessor();

    /**
     * Retrieves the pack processor, for use in processing and compiling pack
     * directories into completed packs.
     * 
     * @return <code>ModuleProcessor</code>: The <code>PACK_PROCESSOR</code> field.
     */
    public static ModuleProcessor getProcessor() {

        return PACK_PROCESSOR;
    }

    /**
     * Creates a new instance of the <code>ModuleProcessor</code> class and
     * automatically compiles all vanilla versions and user-inputted modules.
     */
    private ModuleProcessor() {

        process(INTERNAL_DIRECTORY);
        process(CUSTOM_DIRECTORY);
        process(CACHE_MODULES);
    }

    /**
     * Compiles the resources and packs in a root directory.
     * 
     * @param root <code>TracedPath</code>: The root directory to process.
     */
    public synchronized void process(TracedPath root) {

        TracedPath resourcePath = root.extend("resources");
        if (resourcePath.exists()) {

            addResources(resourcePath);
        }

        TracedPath packPath = root.extend("packs");
        if (packPath.exists()) {

            addPacks(packPath);
            validate();
            compile();
        }
    }

    /**
     * Compiles all subdirectories in a single root directory into individual
     * resources.
     * 
     * @param root <code>TracedPath</code>: The root directory in which all
     *             resources should be processed.
     */
    public synchronized void addResources(TracedPath root) {

        ArrayList<TracedPath> resourcePaths = root.compileDirectories(TracedPath.RESOURCE);
        resourcePaths.forEach(path -> addResource(path));
    }

    /**
     * Compiles a path into an unvalidated pack.
     * 
     * @param path <code>TracedPath</code>: The path to compile.
     */
    public synchronized void addResource(TracedPath path) {

        try {

            Resource.addResource(new Resource(path));

            // If the resource could not be processed for any reason, log the error.
        } catch (LoggedException e) {

            e.print();
        }
    }

    /**
     * Compiles all subdirectories in a single root directory into individual
     * unvalidated packs.
     * 
     * @param root <code>TracedPath</code>: The root directory in which all
     *             packs should be processed.
     */
    public synchronized void addPacks(TracedPath root) {

        ArrayList<TracedPath> packPaths = root.compileDirectories(TracedPath.PACK);
        packPaths.forEach(path -> addPack(path));
    }

    /**
     * Compiles a path into an unvalidated pack.
     * 
     * @param path <code>TracedPath</code>: The path to compile.
     */
    public synchronized void addPack(TracedPath path) {

        try {

            PackSchema pack = new PackSchema(path);
            Identifier identifier = pack.getIdentifier();

            if (processed.containsKey(identifier)) {

                throw new DuplicateIdentifierException(pack.getIdentifierEntry());
            }

            processed.put(identifier, pack);
            unvalidated.put(identifier, pack);

            // If the pack could not be processed for any reason, log the error.
        } catch (LoggedException e) {

            e.print();
        }
    }

    /**
     * Validates all packs in the <code>unvalidated</code> field of this
     * <code>ModuleProcessor</code> instance.
     */
    public synchronized void validate() {

        while (!unvalidated.isEmpty()) {

            try {

                validatePack(unvalidated.keySet().iterator().next());

            } catch (DependencyException e) {

                e.print();
            }
        }
    }

    /**
     * Recursively iterates through the dependencies of an unvalidated pack and
     * determines if they are satisfied by other previously validated packs in the
     * program.
     * 
     * @param identifier <code>Identifier</code>: The identifier of the pack to
     *                   validate.
     */
    private void validatePack(Identifier identifier) throws DependencyException {

        // Retrieves the pack and removes it from unvalidated.
        PackSchema pack = unvalidated.remove(identifier);
        ImmutableMap<String, ImmutableMap<Identifier, PackDependency>> dependencies = pack.getDependencies();

        // Iterate through each dependency in the pack.
        for (Map.Entry<Identifier, PackDependency> dependencyEntry : dependencies.get(PackDependency.ASSET)
                .entrySet()) {

            Identifier dependencyIdentifier = dependencyEntry.getKey();
            PackDependency dependency = dependencyEntry.getValue();

            boolean matchFound = false;

            // If the dependency is compatible with any existing packs, its match has
            // already been found.
            if (!dependency.getOverlaps(Pack.getPacks(dependencyIdentifier)).isEmpty()) {

                matchFound = true;
            }

            // If the dependency is compatible with any validated packs, its match has
            // already been found.
            if (!dependency.getOverlaps(validated.keySet()).isEmpty()) {

                matchFound = true;
            }

            // Retrieve the compatible unvalidated packs.
            HashSet<Identifier> compatible = dependency.getOverlaps(unvalidated.keySet());

            // Iterate through each compatible pack and test for validity.
            for (Identifier candidate : compatible) {

                try {

                    validatePack(candidate);
                    matchFound = true;
                } catch (LoggedException e) {

                    e.print();
                }
            }

            if (!matchFound) {

                throw unresolvedDependency(dependency);
            }
        }

        // Iterate through each dependency in the pack.
        for (Map.Entry<Identifier, PackDependency> dependencyEntry : dependencies.get(PackDependency.RESOURCE)
                .entrySet()) {

            Identifier dependencyIdentifier = dependencyEntry.getKey();
            PackDependency dependency = dependencyEntry.getValue();

            if (dependency.getOverlaps(Resource.getResources(dependencyIdentifier)).isEmpty()) {

                throw unresolvedDependency(dependency);
            }
        }

        for (Map.Entry<Identifier, PackDependency> dependencyEntry : dependencies.get(PackDependency.ASSET)
                .entrySet()) {

            PackDependency dependency = dependencyEntry.getValue();
            for (Identifier candidate : dependency.getOverlaps(processed.keySet())) {

                validateVersionHierarchy(identifier, dependency, processed.get(candidate));
            }
        }

        validated.put(identifier, pack);
    }

    /**
     * Ensures that a pack only contains dependencies which are lower than that of a
     * specified identifier.
     * 
     * @param identifier
     * @param root
     * @param pack
     * @throws DependencyException
     */
    private void validateVersionHierarchy(Identifier identifier, PackDependency root, PackSchema pack)
            throws DependencyException {

        for (Map.Entry<Identifier, PackDependency> dependencyEntry : pack.getDependencies().get(PackDependency.ASSET)
                .entrySet()) {

            PackDependency dependency = dependencyEntry.getValue();

            if (dependency.compatible(identifier) && dependency.lessThan(identifier.getVersion())) {

                throw versionHierarchyException(identifier, root, dependency);
            }

            for (Identifier overlap : dependency.getOverlaps(processed.keySet())) {

                validateVersionHierarchy(identifier, root, processed.get(overlap));
            }
        }
    }

    /**
     * Compiles all packs in the <code>validated</code> field of this
     * <code>ModuleProcessor</code> instance.
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
     * compiles them,
     * generating a complete list of dependencies to create the final
     * <code>Pack</code> from.
     * 
     * @param identifier <code>Identifier</code>: The identifier of the pack to
     *                   compile.
     */
    private void compilePack(Identifier identifier) {

        // Retrieves the pack and removes it from validated.
        PackSchema pack = validated.remove(identifier);

        HashSet<Identifier> resourceDependencies = new HashSet<>();
        HashSet<Identifier> assetDependencies = new HashSet<>();

        ImmutableMap<String, ImmutableMap<Identifier, PackDependency>> dependencies = pack.getDependencies();

        for (Map.Entry<Identifier, PackDependency> dependencyEntry : dependencies.get(PackDependency.ASSET)
                .entrySet()) {

            Identifier dependencyIdentifier = dependencyEntry.getKey();
            PackDependency dependency = dependencyEntry.getValue();

            for (Identifier candidate : dependency.getOverlaps(validated.keySet())) {

                compilePack(candidate);
            }

            assetDependencies.addAll(dependency.getOverlaps(Pack.getPacks(dependencyIdentifier)));
        }

        for (Map.Entry<Identifier, PackDependency> dependencyEntry : dependencies.get(PackDependency.RESOURCE)
                .entrySet()) {

            Identifier dependencyIdentifier = dependencyEntry.getKey();
            PackDependency dependency = dependencyEntry.getValue();

            resourceDependencies.addAll(dependency.getOverlaps(Resource.getResources(dependencyIdentifier)));
        }

        Pack.addPack(new Pack(pack, assetDependencies, resourceDependencies));
    }
}
