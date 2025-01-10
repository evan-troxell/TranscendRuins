package com.transcendruins.packcompiling.assetschemas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.transcendruins.packcompiling.Pack;
import com.transcendruins.packcompiling.PackCompiler;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.fileexceptions.FileFormatException;
import com.transcendruins.utilities.exceptions.fileexceptions.MissingFileException;
import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.IdentifierFormatException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingAttributeSetException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingIdentifierException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.metadata.Metadata;

/**
 * <code>AssetSchema</code>: A class representing any asset schema type, including but not limited to: structures, elements, entities, items, and more.
 */
public abstract class AssetSchema {

    /**
     * <code>AssetType</code>: The type of this code>AssetSchema</code> instance, represented by a <code>AssetType</code> enum.
     */
    public final AssetType type;

    /**
     * <code>TracedDictionary</code>: The JSON information of this <code>AssetSchema</code> instance.
     */
    public final TracedDictionary json;

    /**
     * <code>TracedDictionary</code>: The JSON information of the schema of this <code>AssetSchema</code> instance.
     */
    public final TracedDictionary schemaJson;

    /**
     * <code>Metadata</code>: The metadata of this <code>AssetSchema</code> instance.
     */
    public final Metadata metadata;

    /**
     * <code>ArrayList&lt;AssetSchema&gt;</code>: A collection of assets which this <code> AssetSchema</code> instances is parent to. These are <i>not</i> the dependencies of this <code>AssetSchema</code> instance, but instead the other <code>AssetSchema</code> instances which are dependent on this one in order to validate.
     */
    private final ArrayList<AssetSchema> parentTo = new ArrayList<>();

    /**
     * <code>boolean</code>: Whether or not this <code>AssetSchema</code> instance has been processed.
     */
    private boolean processed = false;

    /**
     * <code>boolean</code>: Whether or not the validation on this <code>AssetSchema</code> instance has failed.
     */
    private boolean validationFailed = false;

    /**
     * <code>AssetSchemaAttributes</code>: The base attribute set of this <code>AssetSchema</code> instance.
     */
    private final AssetSchemaAttributes attributeSet;

    /**
     * <code>HashMap&lt;String, AssetSchemaAttributes&gt;</code>: The attribute sets of this <code>AssetSchema</code> instance.
     */
    private final HashMap<String, AssetSchemaAttributes> attributeSets = new HashMap<>();

    /**
     * <code>HashMap&lt;AssetType, Collection&lt;TracedEntry&lt;Identifier&gt;&gt;&gt;</code>: The collection of element dependencies in this <code>AssetSchema</code> instance.
     */
    private final HashMap<AssetType, Collection<TracedEntry<Identifier>>> assetDependencies = new HashMap<>();

    /**
     * Creates a new instance of the <code>AssetSchema</code> class.
     * @param path <code>TracedPath</code>: The path to this <code>AssetSchema</code> instance.
     * @param type <code>AssetType</code>: The type of this code>AssetSchema</code> instance, represented by a <code>AssetType</code> enum.
     * @throws LoggedException Thrown if an exception is raised while creating this <code>AssetSchema</code> instance.
     */
    public AssetSchema(TracedPath path, AssetType type) throws MissingPropertyException, PropertyTypeException, IdentifierFormatException, FileFormatException, MissingFileException, ArrayLengthException, LoggedException {

        this.type = type;
        json = JSONOperator.retrieveJSON(path);
        metadata = json.getAsMetadata("metadata", false).getValue();

        TracedEntry<TracedDictionary> schemaEntry = json.getAsDictionary("attributes", false);
        schemaJson = schemaEntry.getValue();
        attributeSet = getAttribute(schemaJson, true);

        TracedEntry<TracedDictionary> attributeSetsEntry = json.getAsDictionary("attributeSets", true);

        if (attributeSetsEntry.containsValue()) {

            TracedDictionary attributeSetsJson = attributeSetsEntry.getValue();
            for (String attributeSetKey : attributeSetsJson.getKeys()) {

                TracedEntry<TracedDictionary> attributeSetEntry = attributeSetsJson.getAsDictionary(attributeSetKey, false);
                TracedDictionary attributeSetJson = attributeSetEntry.getValue();
                attributeSets.put(attributeSetKey, getAttribute(attributeSetJson, true));
            }
        }
    }

    /**
     * Builds a attribute set of this <code>AssetSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the attribute set.
     * @param isBase <code>boolean</code>: Whether or not the attributes being built are
     * @return <code>AssetSchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the attribute set.
     */
    private AssetSchemaAttributes getAttribute(TracedDictionary jsonSchema, boolean isBase) throws LoggedException{

        return (isBase) ? buildBaseAttributeSet(jsonSchema) : buildAttributeSet(jsonSchema);
    }

    /**
     * Builds the base attribute set of this <code>AssetSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the attribute set.
     * @return <code>AssetSchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the attribute set.
     */
    public abstract AssetSchemaAttributes buildBaseAttributeSet(TracedDictionary jsonSchema) throws LoggedException;

    /**
     * Builds a attribute set of this <code>AssetSchema</code> instance.
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build the attribute set.
     * @return <code>AssetSchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the attribute set.
     */
    public abstract AssetSchemaAttributes buildAttributeSet(TracedDictionary jsonSchema) throws LoggedException;

    /**
     * Retrieves the base attribute set of this <code>AssetSchema</code> instance.
     * @return <code>AssetSchemaAttributes</code>: The <code>attributeSet</code> field of this <code>AssetSchema</code> instance.
     */
    public AssetSchemaAttributes getAttributeSet() {

        return attributeSet;
    }

    /**
     * Determines whether a attribute set is present in this <code>AssetSchema</code> instance.
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The attribute set to check for.
     * @throws MissingAttributeSetException Thrown to indicate a reference to a attribute group missing from this <code>AssetSchema</code> instance.
     */
    public void containsAttributeSet(TracedEntry<String> entry) throws MissingAttributeSetException {

        String attributeSetKey = entry.getValue();
        if (!attributeSets.containsKey(attributeSetKey)) {

            throw new MissingAttributeSetException(entry);
        }
    }

    /**
     * Retrieves a attribute set from this <code>AssetSchema</code> instance.
     * @param entry <code>String</code>: The attribute set to retrieve.
     * @return <code>AssetSchemaAttributes</code>: The retrieved attribute set of this <code>AssetSchema</code> instance.
     */
    public AssetSchemaAttributes getAttributeSet(String entry)  {

        return attributeSets.get(entry);
    }

    /**
     * Adds a asset dependency to the map of dependencies present in this <code>AssetSchema</code> instance.
     * @param elementType <code>AssetType</code>: The type of dependency to be added.
     * @param dependency <code>TracedEntry&lt;Identifier&gt;</code>: The identifier of the dependency to be added.
     */
    final void addElementDependency(AssetType elementType, TracedEntry<Identifier> dependency) {

        if (!assetDependencies.containsKey(elementType)) {

            assetDependencies.put(elementType, new ArrayList<>());
        }
        assetDependencies.get(elementType).add(dependency);
    }

    /**
     * Validates this <code>AssetSchema</code> instance, determining if all asset dependencies are satisfied.
     * @param pack <code>Pack</code>: The pack used to validate this <code>AssetSchema</code> instance.
     */
    public final void validate(Pack pack) {

        // If the asset is already processed, it does not need to be processed a second time.
        if (processed) {

            return;
        }
        processed = true;

        // Attempts to compile this pack.
        try {

            PackCompiler compiler = pack.getCompiler();

            for (Map.Entry<AssetType, Collection<TracedEntry<Identifier>>> assetCollection : assetDependencies.entrySet()) {

                AssetType assetType = assetCollection.getKey();
                for (TracedEntry<Identifier> assetEntry : assetCollection.getValue()) {

                    Identifier assetId = assetEntry.getValue();

                    // If a dependency already has an asset which satisfies this asset dependency, the dependency is satisfied
                    if (compiler.containsAsset(assetType, assetId)) {

                        continue;
                    }

                    AssetSchema asset = pack.getAsset(assetType, assetId);
                    boolean assetError = false;

                    // If the asset cannot be found, assert as such.
                    if (pack.containsAsset(assetType, assetId)) {

                        // Attempt to validate the asset dependency.
                        asset.validate(pack);

                        // This asset is a child of the asset being processed, so add this asset to the parentTo list of the other asset.
                        asset.parentTo.add(this);

                        // If the validation failed, assert as such.
                        if (asset.validationFailed()) {

                            assetError = true;
                        }
                    } else {

                        assetError = true;
                    }

                    // If the asset is invalid for any reason, throw an error stating the asset is missing.
                    if (assetError) {

                        throw new MissingIdentifierException(assetEntry);
                    }
                }
            }
        } catch (LoggedException e) {

            // If the pack could not be processed for any reason, log the error and then invalidate this asset and all child assets.
            e.logException();
            invalidate(pack);
        }
    }

    /**
     * Invalidates this <code>AssetSchema</code> instance and all other assets who are children of this asset.
     * @param pack <code>Pack</code>: The pack used to validate this <code>AssetSchema</code> instance.
     */
    public final void invalidate(Pack pack) {

        validationFailed = true;
        pack.addInvalidAsset(this);

        for (AssetSchema childAsset : parentTo) {

            if (!childAsset.validationFailed) {

                childAsset.invalidate(pack);
            }
        }
    }

    /**
     * Retrieves whether or not this <code>AssetSchema</code> instances has been processed.
     * @return <code>boolean</code>: The <code>processed</code> field of this <code>AssetSchema</code> instance.
     */
    public final boolean processed() {

        return processed;
    }

    /**
     * Retrieves whether or not the validation of this <code>AssetSchema</code> instances has failed.
     * @return <code>boolean</code>: The <code>validationFailed</code> field of this <code>AssetSchema</code> instance.
     */
    public final boolean validationFailed() {

        return validationFailed;
    }

    /**
     * Returns the string representation of this <code>AssetSchema</code> instance.
     * @return <code>String</code>: This <code>AssetSchema</code> instance in the following string representation: <br>"<code>namespace:identifier</code>"
     */
    @Override
    public String toString() {

        return metadata.toString();
    }
}
