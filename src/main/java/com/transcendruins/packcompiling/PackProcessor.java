package com.transcendruins.packcompiling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.transcendruins.utilities.Sorter;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.DuplicateIdentifierException;
import com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions.InvalidDependencyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions.MissingDependencyException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.metadata.Metadata;

/**
 * <code>PackProcessor</code>: A class which processes individual packs in a
 * directory into complete <code>Pack</code> instances.
 */
public final class PackProcessor {

    /**
     * <code>TracedPath</code>: The filepath of the directory containing all vanilla
     * pack versions.
     */
    public static final TracedPath VANILLA_DIRECTORY = TracedPath.INTERNAL_DIRECTORY.extend("vanilla");

    /**
     * <code>Sorter&lt;Identifier&gt;</code>: A sorter which takes an input of any
     * <code>Collection&lt;Pack&gt;</code> composed of validated packs and outputs
     * an <code>ArrayList&lt;Pack&gt;</code> of packs, sorted by the
     * <code>totalDependencyCount</code> fields of the sorted packs from lowest to
     * highest.
     */
    public static final Sorter<Pack> DEPENDENCY_COUNT_SORTER = new Sorter<Pack>() {

        @Override
        public Pack sortSelector(Pack newEntry, Pack oldEntry) {

            return (newEntry.getTotalDependenciesCount() < oldEntry.getTotalDependenciesCount()) ? newEntry : oldEntry;
        }
    };

    /**
     * A map of all currently validated packs to their identifiers.
     */
    private final HashMap<Identifier, Pack> packsValidated = new HashMap<>();

    /**
     * A map of all currently unvalidated packs to their identifiers.
     */
    private final HashMap<Identifier, Pack> packsUnvalidated = new HashMap<>();

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
     * automatically compiles all vanilla versions.
     */
    private PackProcessor() {

        addRoot(VANILLA_DIRECTORY, true);

        validate();
        compile();
    }

    /**
     * Compiles all subdirectories in a single root directory into individual
     * unvalidated packs.
     * 
     * @param root      <code>TracedPath</code>: The root directory in which all
     *                  packs should be processed.
     * @param isVanilla <code>boolean</code>: Whether or not the packs in the root
     *                  directory are vanilla packs.
     */
    public void addRoot(TracedPath root, boolean isVanilla) {

        ArrayList<TracedPath> packPaths = root.compileDirectoryArray(".trpack");

        for (TracedPath path : packPaths) {

            add(path, isVanilla);
        }

    }

    /**
     * Compiles a path into an unvalidated pack.
     * 
     * @param path      <code>TracedPath</code>: The path to compile.
     * @param isVanilla <code>boolean</code>: Whether or not the path being
     *                  processed is a vanilla pack.
     */
    public void add(TracedPath path, boolean isVanilla) {

        try {

            Pack newPack = new Pack(path, isVanilla);
            Identifier identifier = newPack.getMetadata().getIdentifier();

            if (getValidatedPack(identifier) != null || packsUnvalidated.containsKey(identifier)) {

                throw new DuplicateIdentifierException(newPack.getMetadata().getIdentifierEntry());
            }
            packsUnvalidated.put(identifier, newPack);

            // If the pack could not be processed for any reason, log the error.
        } catch (LoggedException e) {

            e.logException();
        }
    }

    /**
     * Validates all packs in the <code>packsUnvalidated</code> field of this
     * <code>PackProcessor</code> instance.
     */
    public void validate() {

        while (!packsUnvalidated.isEmpty()) {

            Identifier packIdentifier = (Identifier) packsUnvalidated.keySet().iterator().next();

            try {

                checkPackDependencyValidity(packIdentifier);

            } catch (MissingDependencyException | InvalidDependencyException e) {

                e.logException();
            }
        }
    }

    /**
     * Finalizes all validated <code>Pack</code> instances in the
     * <code>packsValidated</code> field of this <code>PackProcessor</code>
     * instance.
     */
    public void compile() {

        ArrayList<Pack> sortedPacks = DEPENDENCY_COUNT_SORTER.sort(packsValidated.values());

        for (Pack pack : sortedPacks) {

            PackCompiler compiler = new PackCompiler(pack);

            pack.setCompiler(compiler);
            pack.compile();
            Pack.PACKS.put(pack.getMetadata().getIdentifier(), pack);
        }

        packsValidated.clear();
    }

    /**
     * Recursively iterates through the dependencies of a pack and determines if
     * they are satisfied by other, previously validated packs in the program.
     * 
     * @return <code>long</code>: The number of dependencies contained in the pack.
     * @param packIdentifier <code>Identifier</code>: The identifier of the pack
     *                       whose validity must be determined.
     * @throws MissingDependencyException Thrown if a dependency is missing from
     *                                    this <code>PackProcess</code> instance or
     *                                    is otherwise unable to be recognized.
     * @throws InvalidDependencyException Thrown if any dependency of the input pack
     *                                    is invalid.
     */
    private long checkPackDependencyValidity(Identifier packIdentifier)
            throws MissingDependencyException, InvalidDependencyException {

        // Check whether or not the pack has already been validated - the pack will be
        // in 'packs' or 'packsValidated' if it has been.
        Pack pack = getValidatedPack(packIdentifier);
        if (pack != null) {

            return pack.getTotalDependenciesCount();
        }

        // Retrieves the pack.
        pack = packsUnvalidated.get(packIdentifier);
        packsUnvalidated.remove(packIdentifier);

        // Validate all dependencies.
        for (TracedEntry<Metadata> dependencyMetadataEntry : pack.getDependencies().values()) {

            Metadata dependencyMetadata = dependencyMetadataEntry.getValue();
            Identifier dependencyIdentifier = dependencyMetadata.getIdentifier();
            String dependencyIdentifierString = dependencyIdentifier.getFull();

            // Whether or not the dependency has been validated.
            boolean validated = false;

            // A set of pre-validated packs which satisfy this dependency.
            HashSet<Identifier> validIdentifiers = dependencyMetadata
                    .retrieveCompatibleIdentifiers(Pack.PACKS.keySet());
            validIdentifiers.addAll(dependencyMetadata.retrieveCompatibleIdentifiers(packsValidated.keySet()));

            // If there are any packs that satisfy this dependency, validate this
            // dependency.
            if (!validIdentifiers.isEmpty()) {

                validated = true;
            }

            // A set of compatible packs which have yet to be validated.
            HashSet<Identifier> compatiblePacks = dependencyMetadata
                    .retrieveCompatibleIdentifiers(packsUnvalidated.keySet());

            // Iterates through all compatible packs and validates them.
            for (Identifier compatiblePackIdentifier : compatiblePacks) {

                // Attempts to recursively check the pack dependency validity of the compatible
                // pack.
                try {

                    // Check the pack dependency validity of the compatible pack, and assign the
                    // allowed dependencies accordingly.
                    checkPackDependencyValidity(compatiblePackIdentifier);

                    // If the pack is valid, add it to the validIdentifiers list and set 'validated'
                    // to true.
                    validIdentifiers.add(compatiblePackIdentifier);
                    validated = true;

                    // If any exception is thrown, log it and move on to the next compatible pack.
                } catch (MissingDependencyException | InvalidDependencyException e) {

                    e.logException();
                }
            }

            // If the dependency has not been validated, throw an exception indicating such.
            if (!validated) {

                throw new MissingDependencyException(pack.getDependenciesEntry(), dependencyMetadataEntry);
            }

            // Adds the validated dependencies to the dependencies map.
            if (!pack.mappedDependencies.containsKey(dependencyIdentifierString)) {

                pack.mappedDependencies.put(dependencyIdentifierString, new HashMap<>());
            }
            pack.mappedDependencies.get(dependencyIdentifierString).put(dependencyMetadataEntry, validIdentifiers);

            // Iterate through the validated identifiers and append their dependencies onto
            // the dependencies of the 'pack' variable.
            for (Identifier validatedIdentifier : validIdentifiers) {

                Pack validatedPack = getValidatedPack(validatedIdentifier);

                // Copies the mapped dependencies of the validated pack onto the new pack.
                combineMappedMetadatas(validatedPack.mappedDependencies, pack.mappedDependencies);
            }

            String packIdentifierString = pack.getMetadata().getIdentifier().getFull();

            // A pack should never have itself or another version of itself as a dependency
            // of itself. If the pack contains its own identifier as a key in its
            // 'mappedDependencies' field, throw an exception stating as such.
            if (pack.mappedDependencies.containsKey(packIdentifierString)) {

                throw InvalidDependencyException.overlapsPackIdentifier(pack.getDependenciesEntry(),
                        pack.mappedDependencies.get(packIdentifierString).keySet().iterator().next());
            }
        }

        // Filters the dependencies of the pack
        filterDependencies(pack);
        packsValidated.put(pack.getMetadata().getIdentifier(), pack);

        return pack.getTotalDependenciesCount();
    }

    /**
     * Combines the metadata-dependency list key-value pairs from a child map to a
     * parent map.
     * 
     * @param child  <code>HashMap&lt;String, HashMap&lt;TracedEntry&lt;Metadata&gt;, Collection&lt;Identifier&gt;&gt;&gt;</code>:
     *               The child map to assign from.
     * @param parent <code>HashMap&lt;String, HashMap&lt;TracedEntry&lt;Metadata&gt;, Collection&lt;Identifier&gt;&gt;&gt;</code>:
     *               The parent map to assign to.
     */
    private void combineMappedMetadatas(HashMap<String, HashMap<TracedEntry<Metadata>, Collection<Identifier>>> child,
            HashMap<String, HashMap<TracedEntry<Metadata>, Collection<Identifier>>> parent) {

        // Iterates through all identifiers in the child map and assigns its metadata
        // keys to the parent.
        for (String identifier : child.keySet()) {

            HashMap<TracedEntry<Metadata>, Collection<Identifier>> mappedDependencyList = child.get(identifier);

            if (!parent.containsKey(identifier)) {

                // If the parent does not already have a map to the identifier string, assign
                // the mapped dependency list to it.
                parent.put(identifier, mappedDependencyList);
            } else {

                // If the parent does already have a map to the identifier tring, assign all
                // key-value pairs to the map.
                parent.get(identifier).putAll(mappedDependencyList);
            }
        }
    }

    /**
     * Filters the <code>mappedDependencies</code> field of the <code>pack</code>
     * perameter into a map of compatible packs to their dependency identifier
     * string and reassigns it to the <code>filteredDependencies</code> field of the
     * pack.
     * 
     * @param pack <code>Pack</code>: The pack whose <code>mappedDependencies</code>
     *             field to filter.
     * @throws InvalidDependencyException Thrown if any dependency of the input pack
     *                                    is invalid.
     */
    private void filterDependencies(Pack pack) throws InvalidDependencyException {

        long dependencyCountIncrease = 0;

        // Iterate through each dependency identifier of the pack.
        for (String dependencyPackIdentifier : pack.mappedDependencies.keySet()) {

            // This is the map of all dependencies that reference the
            // dependencyPackIdentifier ID, to the list of pack identifiers which satisfy
            // it.
            HashMap<TracedEntry<Metadata>, Collection<Identifier>> metadatasLists = pack.mappedDependencies
                    .get(dependencyPackIdentifier);

            Iterator<Map.Entry<TracedEntry<Metadata>, Collection<Identifier>>> dependencyIterator = metadatasLists
                    .entrySet().iterator();

            // Each dependency will be compared to the dependencies of the first pack.
            Map.Entry<TracedEntry<Metadata>, Collection<Identifier>> initEntry = dependencyIterator.next();

            // Make a copy of the first set of packs so the original collection is not
            // modified.
            Collection<Identifier> condensedPackList = new ArrayList<>(initEntry.getValue());
            TracedEntry<Metadata> initMetadata = initEntry.getKey();

            // Retrieve the first pack of the initial dependency to compare later packs to.
            Pack modelPack = getValidatedPack(condensedPackList.iterator().next());
            Collection<TracedEntry<Metadata>> modelPackDependencies = modelPack.getDependencies().values();
            checkSubDependencies(pack, modelPackDependencies, initMetadata, condensedPackList);

            // Increase the total dependency count of the pack by the number of dependencies
            // of the model pack + 1.
            dependencyCountIncrease += modelPack.getTotalDependenciesCount() + 1;

            // Iterate through every following dependency in the pack to check for pack
            // overlaps.
            while (dependencyIterator.hasNext()) {

                Map.Entry<TracedEntry<Metadata>, Collection<Identifier>> entry = dependencyIterator.next();
                TracedEntry<Metadata> dependencyMetadata = entry.getKey();
                Collection<Identifier> dependencyPackList = entry.getValue();

                // Retain only the packs which overlap between the group collection and this
                // pack list.
                condensedPackList.retainAll(dependencyPackList);
                checkSubDependencies(pack, modelPackDependencies, dependencyMetadata, dependencyPackList);

                if (condensedPackList.isEmpty()) {

                    throw InvalidDependencyException.incompatibleDependencyVersions(pack.getDependenciesEntry(),
                            dependencyMetadata, initMetadata);
                }
            }

            Collection<Identifier> sortedList = Identifier.sortIdentifiersByHighestVersion(condensedPackList);
            pack.filteredDependencies.put(dependencyPackIdentifier, sortedList);
        }

        pack.setTotalDependenciesCount(pack.getTotalDependenciesCount() + dependencyCountIncrease);
    }

    /**
     * Checks the subdependencies of a pack's dependencies and ensures that none
     * diverge.
     * 
     * @param pack                  <code>Pack</code>: The pack whose dependencies
     *                              are checked.
     * @param modelPackDependencies <code>Colleciton&lt;TracedEntry&lt;Metadata&gt;&gt;</code>:
     *                              THe dependencies of the model pack to check
     *                              against.
     * @param entry                 <code>TracedEntry&lt;Metadata&gt;</code>: The
     *                              metadata entry to check.
     * @param entryList             <code>Collection&lt;Identifier&gt;</code>: The
     *                              list of packs which satisfy the dependency being
     *                              checked.
     * @throws InvalidDependencyException Thrown if any dependency of the input pack
     *                                    is invalid.
     */
    private void checkSubDependencies(Pack pack, Collection<TracedEntry<Metadata>> modelPackDependencies,
            TracedEntry<Metadata> entry, Collection<Identifier> entryList) throws InvalidDependencyException {

        // The dependencies of the pack being processed, which is in turn a dependency
        // of the original pack being processed.
        Pack modelDependencyCheck = getValidatedPack(entryList.iterator().next());
        Collection<TracedEntry<Metadata>> modelDependencyCheckDependencies = modelDependencyCheck.getDependencies()
                .values();

        // Retrieves all packs in the 'newDependencies' variables. Checks each pack to
        // ensure they all have identical (save for versions) dependencies.
        for (Identifier dependencyCheckId : entryList) {

            Pack dependencyCheck = getValidatedPack(dependencyCheckId);
            Collection<TracedEntry<Metadata>> dependencyCheckDependencies = dependencyCheck.getDependencies().values();
            Collection<TracedEntry<Metadata>> checkModelDependencies = new ArrayList<>(modelPackDependencies);

            // Check to ensure there are no unexpected dependencies in the pack.
            for (TracedEntry<Metadata> dependencyCheckDependency : dependencyCheckDependencies) {

                Metadata dependencyCheckDependencyMetadata = dependencyCheckDependency.getValue();

                // Checks the dependency against other packs within itself.
                HashSet<TracedEntry<Metadata>> modelDependencyChecks = dependencyCheckDependencyMetadata
                        .retrieveOverslaps(modelDependencyCheckDependencies, true);
                if (modelDependencyChecks.size() != 1) {

                    throw InvalidDependencyException.divergingDependencies(pack.getDependenciesEntry(), entry);
                }

                // Checks the dependency against the expected packs from other dependencies.
                HashSet<TracedEntry<Metadata>> modelChecks = dependencyCheckDependencyMetadata
                        .retrieveOverslaps(checkModelDependencies, true);
                if (modelChecks.size() != 1) {

                    throw InvalidDependencyException.divergingDependencies(pack.getDependenciesEntry(), entry,
                            dependencyCheckDependency);
                }
                checkModelDependencies.removeAll(modelChecks);
            }

            // Check to ensure there are no missing dependencies in the pack.
            if (!checkModelDependencies.isEmpty()) {

                throw InvalidDependencyException.missingDependencies(pack.getDependenciesEntry(), entry,
                        checkModelDependencies);
            }
        }
    }

    /**
     * Retrieves a validated pack from either the <code>packs</code> field of the
     * <code>Pack</code> class, or the <code>packsValidated</code> field of this
     * <code>PackProcessor</code> instance.
     * 
     * @param identifier <code>Identifier</code>: The pack identifier to retrieve.
     * @return <code>Pack</code>: The retrieved <code>Pack</code> instance.
     */
    private Pack getValidatedPack(Identifier identifier) {

        return (Pack.PACKS.containsKey(identifier)) ? Pack.PACKS.get(identifier) : packsValidated.get(identifier);
    }
}
