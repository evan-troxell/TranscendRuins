package com.transcendruins.packcompiling;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.ImageIcon;

import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchema;
import com.transcendruins.packcompiling.assetschemas.elements.ElementSchema;
import com.transcendruins.packcompiling.assetschemas.entities.EntitySchema;
import com.transcendruins.packcompiling.assetschemas.items.ItemSchema;
import com.transcendruins.packcompiling.assetschemas.models.ModelSchema;
import com.transcendruins.packcompiling.assetschemas.rendermaterials.RenderMaterialSchema;
import com.transcendruins.packcompiling.assetschemas.structures.StructureSchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.DuplicateIdentifierException;
import com.transcendruins.utilities.exceptions.propertyexceptions.IdentifierFormatException;
import com.transcendruins.utilities.exceptions.propertyexceptions.StringLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.dependencyexceptions.InvalidDependencyException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.metadata.Metadata;

/**
 * <code>Pack</code>: A class representing the parsed JSON information of a pack.
 */
public final class Pack {

    /**
     * <code>HashMap&lt;Identifier, Pack&gt;</code>: The set of all packs stored within the program.
     */
    public static final HashMap<Identifier, Pack> PACKS = new HashMap<>();

    /**
     * <code>TracedPath</code>: The directory of the root folder of this <code>Pack</code> instance.
     */
    private final TracedPath root;

    /**
     * <code>HashMap&lt;String, HashMap&lt;TracedEntry&lt;Metadata&gt;, Collection&lt;Identifier&gt;&gt;&gt;</code>: A map of each dependency identifier in this <code>Pack</code> instance to a map of the dependency metadatas which reference the dependency identifier to a collection of valid pack(s) which satisfies the dependency.
     */
    public final HashMap<String, HashMap<TracedEntry<Metadata>, Collection<Identifier>>> mappedDependencies = new HashMap<>();

    /**
     * <code>HashMap&lt;String, Collection&lt;Identifier&gt;&gt;</code>: A map of each dependency identifier in this <code>Pack</code> instance to a list of all pack identifiers which satisfy the dependency. Note that the list of pack identifiers includes <i>all</i> dependencies and subdependencies of this <code>Pack</code> instance.
     * */
    public final HashMap<String, Collection<Identifier>> filteredDependencies = new HashMap<>();

    /**
     * <code>long</code>: The total number of dependencies (including dependencies of those dependencies and so on) in this <code>Pack</code> instance.
     */
    private long totalDependenciesCount = 0;

    /**
     * <code>TracedEntry&lt;Metadata&gt;</code>: The metadata of this <code>Pack</code> instance.
     */
    public final TracedEntry<Metadata> metadataEntry;

    /**
     * <code>TracedDictionary</code>: The JSON information of the metadata of this <code>Pack</code> instance.
     */
    public final TracedDictionary metadataJson;

    /**
     * <code>String</code>: The name of this pack.
     */
    public final String name;

    /**
     * <code>String</code>: The description of this pack.
     */
    public final String description;

    /**
     * <code>String[]</code>: The authors of this pack.
     */
    public final String[] authors;

    /**
     * <code>ImageIcon</code>: The display icon of this pack.
     */
    public final ImageIcon displayIcon;

    /**
     * <code>TracedArray&lt;TracedArray&gt;</code>: The entry of the dependencies of this pack.
     */
    public final TracedEntry<TracedArray> dependenciesEntry;

    /**
     * <code>HashMap&lt;Identifier, TracedEntry&lt;Metadata&gt;&gt;</code>: The dependencies of this pack.
     */
    public final HashMap<Identifier, TracedEntry<Metadata>> dependencies = new HashMap<>();

    /**
     * <code>HashMap&lt;AssetType, HashMap&lt;Identifier, AssetSchema&gt;&gt;</code>: A map of all asset configurations in this <code>Pack</code> instance.
     */
    private final HashMap<AssetType, HashMap<Identifier, AssetSchema>> assetMap = defaultAssetMap();

    /**
     * <code>PackCompiler</code>: The compiler used to compile this <code>Pack</code> instance.
     */
    private PackCompiler compiler;

    /**
     * <code>ArrayList&lt;AssetSchema&gt;</code>: All assets which have been declared to be invalid.
     */
    private final ArrayList<AssetSchema> invalidAssets = new ArrayList<>();

    /**
     * Creates a new instance of the <code>Pack</code> class using the directory to its root folder.
     * @param root <code>Path</code>: The directory of the root folder of this pack.
     * @param vanillaNamespace <code>boolean</code>: Whether or not this <code>Pack</code> instance is required to use a vanilla namespace.
     * @throws LoggedException Thrown if any exception is raised while creating this <code>Pack</code> instance.
     */
    public Pack(TracedPath root, boolean vanillaNamespace) throws LoggedException {

        this.root = root;

        TracedPath manifestPath = root.extend("manifest.json");

        TracedDictionary manifestJson = JSONOperator.retrieveJSON(manifestPath);
        metadataEntry = manifestJson.getAsMetadata("metadata", false, false);
        metadataJson = getMetadata().json;

        boolean isVanillaNamespace = getMetadata().getIdentifier().getNameSpace().equals("transcendRuins");
        if (vanillaNamespace != isVanillaNamespace) {

            throw new IdentifierFormatException(metadataJson.getAsString("identifier", false, null));
        }

        TracedEntry<String> nameEntry = metadataJson.getAsString("name", false, null);
        name = nameEntry.getValue();
        if (name.isEmpty()) {

            throw new StringLengthException(nameEntry);
        }

        TracedEntry<String> descriptionEntry = metadataJson.getAsString("description", true, null);
        description = descriptionEntry.getValue() == null ? "[DESCRIPTION UNLISTED]" : descriptionEntry.getValue();

        if (metadataJson.containsKey("author")) {

            TracedEntry<String> authorEntry = metadataJson.getAsString("author",  false, null);
            String author = authorEntry.getValue();

            if (author.isEmpty()) {

                throw new StringLengthException(authorEntry);
            }
            authors = new String[] {author};

        } else if (metadataJson.containsKey("authors")) {

            TracedEntry<TracedArray> authorsEntry = metadataJson.getAsArray("authors", false);
            TracedArray authorsJson = authorsEntry.getValue();
            authors = new String[authorsJson.size()];

            if (authorsJson.isEmpty()) {

                throw new ArrayLengthException(authorsEntry);
            }
            for (int i : authorsJson.getIndices()) {

                TracedEntry<String> authorsIndexEntry = authorsJson.getAsString(i, false, null);
                String authorsIndex = authorsIndexEntry.getValue();

                if (authorsIndex.isEmpty()) {

                    throw new StringLengthException(authorsIndexEntry);
                }
                authors[i] = authorsIndex;
            }
        } else {

            authors = new String[] {"[AUTHOR UNKNOWN]"};
        }

        TracedPath displayIconPath = root.extend("displayIcon.png");
        if (displayIconPath.exists()) {

            displayIcon = displayIconPath.retrieveImage();
        } else {

            displayIcon = null;
        }

        dependenciesEntry = manifestJson.getAsArray("dependencies", true);

        if (dependenciesEntry.containsValue()) {
            
            TracedArray dependenciesJson = (TracedArray) dependenciesEntry.getValue();

            for (int i : dependenciesJson.getIndices()) {

                TracedEntry<Metadata> dependenciesIndexEntry = dependenciesJson.getAsMetadata(i, false, true);
                Metadata dependencyIndex = dependenciesIndexEntry.getValue();

                HashSet<TracedEntry<Metadata>> duplicateDependencies = dependencyIndex.retrieveOverslaps(dependencies.values(), true);

                // Ensure there are not multiple of the same dependency.
                if (!duplicateDependencies.isEmpty()) {

                    throw InvalidDependencyException.overlappingDependencies(dependenciesEntry, dependenciesIndexEntry, duplicateDependencies);
                }

                // Ensure the dependency does not have the same identifier as the pack.
                if (dependencyIndex.overlaps(getMetadata(), true)) {

                    throw InvalidDependencyException.overlapsPackIdentifier(dependenciesEntry, dependenciesIndexEntry);
                }

                dependencies.put(dependencyIndex.getIdentifier(), dependenciesIndexEntry);
            }
        }
    }

    /**
     * Sets the compiler used to assist with the compiling of this <code>Pack</code> instance.
     * @param compiler <code>PackCompiler</code>: The <code>PackCompiler</code> instance to assign to the <code>compiler</code> field of this <code>Pack</code> instance.
     */
    public void setCompiler(PackCompiler compiler) {

        this.compiler = compiler;
    }

    /**
     * Compiles this <code>Pack</code> instance into a completed instance.
     */
    public void compile() {

        // Build all animations in the pack.
        TracedPath animationcontrollersPath = root.extend("animationControllers");
        if (animationcontrollersPath.exists()) {

            HashMap<Identifier, AssetSchema> assets = assetMap.get(AssetType.ANIMATION_CONTROLLER);

            // Compiles a list of paths and iterates through them.
            ArrayList<TracedPath> assetPaths = animationcontrollersPath.compileFileArray(".json", true);
            for (TracedPath assetPath : assetPaths) {

                try {

                    AnimationSchema asset = new AnimationSchema(assetPath);
                    Identifier assetIdentifier = asset.metadata.getIdentifier();

                    // Ensure the asset has not already been processed.
                    if (assets.containsKey(assetIdentifier)) {

                        throw new DuplicateIdentifierException(asset.metadata.identifierEntry);
                    }
                    assets.put(assetIdentifier, asset);

                } catch (LoggedException e) {

                    e.logException();
                }
            }
        }

        // Build all animations in the pack.
        TracedPath animationsPath = root.extend("animations");
        if (animationsPath.exists()) {

            HashMap<Identifier, AssetSchema> assets = assetMap.get(AssetType.ANIMATION);

            // Compiles a list of paths and iterates through them.
            ArrayList<TracedPath> assetPaths = animationsPath.compileFileArray(".json", true);
            for (TracedPath assetPath : assetPaths) {

                try {

                    AnimationSchema asset = new AnimationSchema(assetPath);
                    Identifier assetIdentifier = asset.metadata.getIdentifier();

                    // Ensure the asset has not already been processed.
                    if (assets.containsKey(assetIdentifier)) {

                        throw new DuplicateIdentifierException(asset.metadata.identifierEntry);
                    }
                    assets.put(assetIdentifier, asset);

                } catch (LoggedException e) {

                    e.logException();
                }
            }
        }

        // Build all elements in the pack.
        TracedPath elementsPath = root.extend("elements");
        if (elementsPath.exists()) {

            HashMap<Identifier, AssetSchema> assets = assetMap.get(AssetType.ELEMENT);

            // Compiles a list of paths and iterates through them.
            ArrayList<TracedPath> assetPaths = elementsPath.compileFileArray(".json", true);
            for (TracedPath assetPath : assetPaths) {

                try {

                    ElementSchema asset = new ElementSchema(assetPath);
                    Identifier assetIdentifier = asset.metadata.getIdentifier();

                    // Ensure the asset has not already been processed.
                    if (assets.containsKey(assetIdentifier)) {

                        throw new DuplicateIdentifierException(asset.metadata.identifierEntry);
                    }
                    assets.put(assetIdentifier, asset);

                } catch (LoggedException e) {

                    e.logException();
                }
            }
        }

        // Build all entities in the pack.
        TracedPath entitiesPath = root.extend("entities");
        if (entitiesPath.exists()) {

            HashMap<Identifier, AssetSchema> assets = assetMap.get(AssetType.ENTITY);

            // Compiles a list of paths and iterates through them.
            ArrayList<TracedPath> assetPaths = entitiesPath.compileFileArray(".json", true);
            for (TracedPath assetPath : assetPaths) {

                try {

                    EntitySchema asset = new EntitySchema(assetPath);
                    Identifier assetIdentifier = asset.metadata.getIdentifier();

                    // Ensure the asset has not already been processed.
                    if (assets.containsKey(assetIdentifier)) {

                        throw new DuplicateIdentifierException(asset.metadata.identifierEntry);
                    }
                    assets.put(assetIdentifier, asset);

                } catch (LoggedException e) {

                    e.logException();
                }
            }
        }

        // Build all items in the pack.
        TracedPath itemsPath = root.extend("items");
        if (itemsPath.exists()) {

        HashMap<Identifier, AssetSchema> assets = assetMap.get(AssetType.ITEM);

        // Compiles a list of paths and iterates through them.
        ArrayList<TracedPath> assetPaths = itemsPath.compileFileArray(".json", true);
        for (TracedPath assetPath : assetPaths) {

                try {

                    ItemSchema asset = new ItemSchema(assetPath);
                    Identifier assetIdentifier = asset.metadata.getIdentifier();

                    // Ensure the asset has not already been processed.
                    if (assets.containsKey(assetIdentifier)) {

                        throw new DuplicateIdentifierException(asset.metadata.identifierEntry);
                    }
                    assets.put(assetIdentifier, asset);

                } catch (LoggedException e) {

                    e.logException();
                }
            }
        }

        // Build all models in the pack.
        TracedPath modelsPath = root.extend("models");
        if (modelsPath.exists()) {

            HashMap<Identifier, AssetSchema> assets = assetMap.get(AssetType.MODEL);

            // Compiles a list of paths and iterates through them.
            ArrayList<TracedPath> assetPaths = modelsPath.compileFileArray(".json", true);
            for (TracedPath assetPath : assetPaths) {

                try {

                    ModelSchema asset = new ModelSchema(assetPath);
                    Identifier assetIdentifier = asset.metadata.getIdentifier();

                    // Ensure the asset has not already been processed.
                    if (assets.containsKey(assetIdentifier)) {

                        throw new DuplicateIdentifierException(asset.metadata.identifierEntry);
                    }
                    assets.put(assetIdentifier, asset);

                } catch (LoggedException e) {

                    e.logException();
                }
            }
        }

        // Build all render materials in the pack.
        TracedPath renderMaterialsPath = root.extend("renderMaterials");
        if (renderMaterialsPath.exists()) {

            HashMap<Identifier, AssetSchema> assets = assetMap.get(AssetType.RENDER_MATERIAL);

            // Compiles a list of paths and iterates through them.
            ArrayList<TracedPath> assetPaths = renderMaterialsPath.compileFileArray(".json", true);
            for (TracedPath assetPath : assetPaths) {

                try {

                    RenderMaterialSchema asset = new RenderMaterialSchema(assetPath);
                    Identifier assetIdentifier = asset.metadata.getIdentifier();

                    // Ensure the asset has not already been processed.
                    if (assets.containsKey(assetIdentifier)) {

                        throw new DuplicateIdentifierException(asset.metadata.identifierEntry);
                    }
                    assets.put(assetIdentifier, asset);

                } catch (LoggedException e) {

                    e.logException();
                }
            }
        }

        // Build all structures in the pack.
        TracedPath structuresPath = root.extend("structures");
        if (structuresPath.exists()) {

            HashMap<Identifier, AssetSchema> assets = assetMap.get(AssetType.STRUCTURE);

            // Compiles a list of paths and iterates through them.
            ArrayList<TracedPath> assetPaths = structuresPath.compileFileArray(".json", true);
            for (TracedPath assetPath: assetPaths) {

                try {

                    StructureSchema asset = new StructureSchema(assetPath);
                    Identifier assetIdentifier = asset.metadata.getIdentifier();

                    // Ensure the asset has not already been processed.
                    if (assets.containsKey(assetIdentifier)) {

                        throw new DuplicateIdentifierException(asset.metadata.identifierEntry);
                    }
                    assets.put(assetIdentifier, asset);

                } catch (LoggedException e) {

                    e.logException();
                }
            }
        }

        // Validate all assets in the map.
        for (HashMap<Identifier, AssetSchema> packAssets: assetMap.values()) {

            for (AssetSchema asset : packAssets.values()) {

                // Attempt to validate the asset dependency.
                asset.validate(this);
            }
        }

        // Remove all invalid assets from the map and give all assets their dependencies.
        for (AssetSchema invalidAsset : invalidAssets) {

            assetMap.get(invalidAsset.type).remove(invalidAsset.metadata.getIdentifier());
        }
    }

    /**
     * Adds another invalid <code>AssetSchema</code> instance to the list of invalid assets of this <code>Pack</code> instance.
     * @param asset <Code>AssetSchema</code>: The invalid asset to add.
     */
    public void addInvalidAsset(AssetSchema asset) {

        invalidAssets.add(asset);
    }

    /**
     * Retrieves the asset map of this <code>Pack</code> instance.
     * @return <code>HashMap&lt;AssetType, HashMap&lt;Identifier, AssetSchema&gt;&gt;</code>: The <code>assetMap</code> field of this <code>Pack</code> instance.
     */
    public HashMap<AssetType, HashMap<Identifier, AssetSchema>> getAssetMap() {

        return assetMap;
    }

    /**
     * Retrieves a value from the asset map of this <code>Pack</code> instance.
     * @param type <code>AssetType</code>: The type of asset to retrieve.
     * @param identifier <code>Identifier</code>: The identifier of the asset to retrieve.
     * @return <code>AssetSchema</code>: The asset retrieved from the <code>assetMap</code> field of this <code>Pack</code> instance.
     */
    public AssetSchema getAsset(AssetType type, Identifier identifier) {

        return assetMap.get(type).get(identifier);
    }

    /**
     * Retrieves whether or not an asset is contained in the asset map of this <code>Pack</code> instance.
     * @param type <code>AssetType</code>: The type of asset to search for.
     * @param identifier <code>Identifier</code>: The identifier to the asset to search for.
     * @return <code>boolean</code>: Whether or not the <code>assetMap</code> field of this <code>Pack<>/code> instance contains the asset.
     */
    public boolean containsAsset(AssetType type, Identifier identifier) {

        return assetMap.get(type).containsKey(identifier);
    }

    /**
     * Retrieves the root of this <code>Pack</code> instance.
     * @return <code>TracedPath</code>: The <code>root</code> field of this <code>Pack</code> instance.
     */
    public TracedPath getRoot() {

        return root;
    }

    /**
     * Retrieves the compiler used to compiler this <code>Pack</code> instance.
     * @return <code>PackCompiler</code>: The <code>compiler</code> field of this <code>Pack</code> instance.
     */
    public PackCompiler getCompiler() {

        return compiler;
    }

    /**
     * Sets the total dependencies count of this <code>Pack</code> instance.
     * @param totalDependenciesCount <code>long</code>: The value to assign to the <code>totalDependenciesCount</code> field of this <code>Pack</code> instance.
     */
    public void setTotalDependenciesCount(long totalDependenciesCount) {

        this.totalDependenciesCount = totalDependenciesCount;
    }

    /**
     * Retrieves the total dependencies count of this <code>Pack</code> instance.
     * @return <code>long</code>: The <code>totalDependenciesCount</code> field of this <code>Pack</code> instance.
     */
    public long getTotalDependenciesCount() {

        return totalDependenciesCount;
    }

    /**
     * Retrieves the <code>Metadata</code> of this <code>Pack</code> instance.
     * @return <code>Metadata</code>: The value of the <code>metadataEntry</code> field of this <code>Pack</code> instance.
     */
    public Metadata getMetadata() {

        return metadataEntry.getValue();
    }

    /**
     * Returns the string representation of this <code>Metadata</code> instance.
     * @return <code>String</code>: This <code>Pack</code> instance in the following string representation: <br>"<code>namespace:identifier [a, b, c]</code>"
     */
    @Override
    public String toString() {

        return getMetadata().toString();
    }

    /**
     * Creates a default asset map containing all the types of <code>AssetSchema</code> subclasses.
     * @return <code>HashMap&lt;AssetType, HashMap&lt;Identifier, AssetSchema&gt;&gt;</code>: The resulting map instance.
     */
    public static HashMap<AssetType, HashMap<Identifier, AssetSchema>> defaultAssetMap() {

        HashMap<AssetType, HashMap<Identifier, AssetSchema>> newVal = new HashMap<>();

        newVal.put(AssetType.ANIMATION_CONTROLLER, new HashMap<>());
        newVal.put(AssetType.ANIMATION, new HashMap<>());
        newVal.put(AssetType.ELEMENT, new HashMap<>());
        newVal.put(AssetType.ENTITY, new HashMap<>());
        newVal.put(AssetType.ITEM, new HashMap<>());
        newVal.put(AssetType.MODEL, new HashMap<>());
        newVal.put(AssetType.RENDER_MATERIAL, new HashMap<>());
        newVal.put(AssetType.STRUCTURE, new HashMap<>());

        return newVal;
    }
}
