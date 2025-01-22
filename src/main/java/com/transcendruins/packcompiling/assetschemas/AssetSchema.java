package com.transcendruins.packcompiling.assetschemas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.transcendruins.packcompiling.Pack;
import com.transcendruins.packcompiling.PackCompiler;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingAttributeSetException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingIdentifierException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.metadata.Metadata;
import com.transcendruins.world.assetinstances.AssetPresets;

/**
 * <code>AssetSchema</code>: A class representing any asset schema type,
 * including but not limited to: structures, elements, entities, items, and
 * more.
 */
public abstract class AssetSchema {

    public static final int UNPROCESSED = -1;

    public static final int INVALIDATED = 0;

    public static final int VALIDATED = 1;

    /**
     * <code>AssetType</code>: The type of this <code>AssetSchema</code> instance.
     */
    private final AssetType type;

    /**
     * Retrieves the type of this <code>AssetSchema</code> instance.
     * 
     * @return <code>AssetType</code>: The <code>type</code> field of this
     *         <code>AssetSchema</code> instance.
     */
    public AssetType getType() {

        return type;
    }

    /**
     * <code>Metadata</code>: The metadata of this <code>AssetSchema</code>
     * instance.
     */
    private final Metadata metadata;

    /**
     * Retrieves the metadata of this <code>AssetSchema</code> instance.
     * 
     * @return <code>Metadata</code>: The <code>metadata</code> field of this
     *         <code>AssetSchema</code> instance.
     */
    public Metadata getMetadata() {

        return metadata;
    }

    /**
     * <code>ArrayList&lt;AssetSchema&gt;</code>: A collection of assets which this
     * <code> AssetSchema</code> instances is parent to. These are <i>not</i> the
     * dependencies of this <code>AssetSchema</code> instance, but instead the other
     * <code>AssetSchema</code> instances which are dependent on this one in order
     * to validate.
     */
    private final ArrayList<AssetSchema> parentTo = new ArrayList<>();

    /**
     * <code>int</code>: The validation state of this <code>AssetSchema</code>
     * instance.
     */
    private int validationState = UNPROCESSED;

    /**
     * <code>AssetSchemaAttributes</code>: The base attribute set of this
     * <code>AssetSchema</code> instance.
     */
    private final AssetSchemaAttributes attributeSet;

    /**
     * Retrieves the base attribute set of this <code>AssetSchema</code> instance.
     * 
     * @return <code>AssetSchemaAttributes</code>: The <code>attributeSet</code>
     *         field of this <code>AssetSchema</code> instance.
     */
    public AssetSchemaAttributes getAttributeSet() {

        return attributeSet;
    }

    /**
     * <code>HashMap&lt;String, AssetSchemaAttributes&gt;</code>: The attribute sets
     * of this <code>AssetSchema</code> instance.
     */
    private final HashMap<String, AssetSchemaAttributes> attributeSets = new HashMap<>();

    /**
     * Retrieves an attribute set from this <code>AssetSchema</code> instance.
     * 
     * @param entry <code>String</code>: The attribute set to retrieve.
     * @return <code>AssetSchemaAttributes</code>: The retrieved attribute set of
     *         this <code>AssetSchema</code> instance.
     */
    public AssetSchemaAttributes getAttributeSet(String entry) {

        return attributeSets.get(entry);
    }

    /**
     * <code>HashSet&lt;AssetPresets&gt;</code>: The collection of element
     * dependencies in this <code>AssetSchema</code> instance.
     */
    private final HashSet<AssetPresets> assetDependencies = new HashSet<>();

    /**
     * Creates a new instance of the <code>AssetSchema</code> class.
     * 
     * @param path <code>TracedPath</code>: The path to this
     *             <code>AssetSchema</code> instance.
     * @param type <code>AssetType</code>: The type of this code>AssetSchema</code>
     *             instance, represented by a <code>AssetType</code> enum.
     * @throws LoggedException Thrown if an exception is raised while creating this
     *                         <code>AssetSchema</code> instance.
     */
    public AssetSchema(TracedPath path, AssetType type) throws LoggedException {

        this.type = type;

        TracedDictionary json = JSONOperator.retrieveJSON(path);
        metadata = json.getAsMetadata("metadata", false).getValue();

        TracedEntry<TracedDictionary> schemaEntry = json.getAsDictionary("attributes", false);
        attributeSet = getAttributes(schemaEntry.getValue(), true);

        TracedEntry<TracedDictionary> attributeSetsEntry = json.getAsDictionary("attributeSets", true);
        if (attributeSetsEntry.containsValue()) {

            TracedDictionary attributeSetsJson = attributeSetsEntry.getValue();
            for (String attributeSetKey : attributeSetsJson.getKeys()) {

                TracedEntry<TracedDictionary> attributeSetEntry = attributeSetsJson.getAsDictionary(attributeSetKey,
                        false);
                TracedDictionary attributeSetJson = attributeSetEntry.getValue();
                attributeSets.put(attributeSetKey, getAttributes(attributeSetJson, true));
            }
        }

        TracedEntry<TracedDictionary> eventsEntry = json.getAsDictionary("events", true);
        if (eventsEntry.containsValue()) {

            // TODO Implement events system
            // Should include methods for conditions (using TRScript system),
            // adding/removing attribute sets, etc.
        }
    }

    /**
     * Builds an attribute set of this <code>AssetSchema</code> instance.
     * 
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build
     *                   the attribute set.
     * @param isBase     <code>boolean</code>: Whether or not the attributes being
     *                   built are
     * @return <code>AssetSchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the
     *                         attribute set.
     */
    private AssetSchemaAttributes getAttributes(TracedDictionary jsonSchema, boolean isBase) throws LoggedException {

        return buildAttributeSet(jsonSchema, isBase);
    }

    /**
     * Builds an attribute set of this <code>AssetSchema</code> instance.
     * 
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to build
     *                   the attribute set.
     * @param isBase     <code>boolean</code>: Whether or not the attribute set is
     *                   the base attribute set of this <code>AssetSchema</code>
     *                   instance.
     * @return <code>AssetSchemaAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the
     *                         attribute set.
     */
    public abstract AssetSchemaAttributes buildAttributeSet(TracedDictionary jsonSchema, boolean isBase)
            throws LoggedException;

    /**
     * Determines whether an attribute set is present in this
     * <code>AssetSchema</code> instance.
     * 
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The attribute set to
     *              check for.
     * @throws MissingAttributeSetException Thrown to indicate a reference to a
     *                                      attribute group missing from this
     *                                      <code>AssetSchema</code> instance.
     */
    public void containsAttributeSet(TracedEntry<String> entry) throws MissingAttributeSetException {

        String attributeSetKey = entry.getValue();
        if (!attributeSets.containsKey(attributeSetKey)) {

            throw new MissingAttributeSetException(entry);
        }
    }

    /**
     * Adds a asset dependency to the map of dependencies present in this
     * <code>AssetSchema</code> instance.
     * 
     * @param dependency <code>AssetPresets</code>: The dependency presets to be
     *                   added.
     */
    protected final void addElementDependency(AssetPresets dependency) {

        assetDependencies.add(dependency);
    }

    /**
     * Validates this <code>AssetSchema</code> instance, determining if all asset
     * dependencies are satisfied.
     * 
     * @param pack <code>Pack</code>: The pack used to validate this
     *             <code>AssetSchema</code> instance.
     */
    public final void validate(Pack pack) {

        // If the asset is already processed, it does not need to be processed a second
        // time.
        if (validationState != UNPROCESSED) {

            return;
        }
        validationState = VALIDATED;

        // Attempts to compile this pack.
        try {

            PackCompiler compiler = pack.getCompiler();

            for (AssetPresets assetPreset : assetDependencies) {

                Identifier assetId = assetPreset.getIdentifier();
                AssetType assetType = assetPreset.getType();

                // If a dependency already has an asset which satisfies this asset dependency,
                // the dependency is satisfied.
                if (compiler.containsAsset(assetType, assetId)) {

                    continue;
                }

                AssetSchema asset = pack.getAsset(assetType, assetId);
                boolean assetError = false;

                // If the asset cannot be found, assert as such.
                if (pack.containsAsset(assetType, assetId)) {

                    // Attempt to validate the asset dependency.
                    asset.validate(pack);

                    // This asset is a child of the asset being processed, so add this asset to the
                    // parentTo list of the other asset.
                    asset.parentTo.add(this);

                    // If the validation failed, assert as such.
                    if (asset.getValidationState() == INVALIDATED) {

                        assetError = true;
                    }
                } else {

                    assetError = true;
                }

                // If the asset is invalid for any reason, throw an error stating the asset is
                // missing.
                if (assetError) {

                    throw new MissingIdentifierException(assetPreset.getIdentifierEntry());
                }

                for (TracedEntry<String> assetAttributeSet : assetPreset.getAttributeSets()) {

                    if (!asset.attributeSets.containsKey(assetAttributeSet.getValue())) {

                        throw new MissingAttributeSetException(assetAttributeSet);
                    }
                }
            }
        } catch (LoggedException e) {

            // If the pack could not be processed for any reason, log the error and then
            // invalidate this asset and all child assets.
            e.logException();
            invalidate(pack);
        }
    }

    /**
     * Invalidates this <code>AssetSchema</code> instance and all other assets who
     * are children of this asset.
     * 
     * @param pack <code>Pack</code>: The pack used to validate this
     *             <code>AssetSchema</code> instance.
     */
    public final void invalidate(Pack pack) {

        validationState = INVALIDATED;
        pack.addInvalidAsset(this);

        for (AssetSchema childAsset : parentTo) {

            // If the validation has NOT failed yet, fail it.
            // Otherwise, there is no reason to fail it a second time.
            if (childAsset.validationState != INVALIDATED) {

                childAsset.invalidate(pack);
            }
        }
    }

    /**
     * Retrieves the validation state of this <code>AssetSchema</code> instance.
     * 
     * @return <code>int</code>: The <code>validationState</code> field of this
     *         <code>AssetSchema</code> instance.
     */
    public final int getValidationState() {

        return validationState;
    }

    /**
     * Returns the string representation of this <code>AssetSchema</code> instance.
     * 
     * @return <code>String</code>: This <code>AssetSchema</code> instance in the
     *         following string representation: <br>
     *         "<code>namespace:identifier</code>"
     */
    @Override
    public String toString() {

        return metadata.toString();
    }
}
