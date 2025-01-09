package com.transcendruins.packcompiling.assetschemas;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
* <code>ModelAssetSchemaModules</code>: A class which represents the modules of an <code>AssetSchema</code> instance which has the capability of being rendered using the standard <code>RenderInstance</code> method.
*/
public abstract class ModelAssetSchemaModules extends AssetSchemaModules {

    /**
     * <code>Identifier</code>: The model of this <code>ModelAssetSchemaModules</code> instance.
     */
    private final Identifier modelIdentifier;

    /**
     * <code>double</code>: The rotational offset of this <code>ModelAssetSchemaModules</code> instance.
     */
    private final double rotationOffset;

    /**
     * <code>double</code>: The heading of the axis of rotation of this <code>ModelAssetSchemaModules</code> instance.
     */
    private final double axisHeading;

    /**
     * <code>double</code>: The pitch of the axis of rotation of this <code>ModelAssetSchemaModules</code> instance.
     */
    private final double axisPitch;

    /**
     * <code>Identifier</code>: The render material of this <code>ModelAssetSchemaModules</code> instance.
     */
    private final Identifier renderMaterialIdentifier;

    /**
     * <code>Identifier</code>: The animation controller of this <code>ModelAssetSchemaModules</code> instance.
     */
    private final Identifier animationControllerIdentifier;

    /**
     * Compiles this <code>ModelAssetSchemaModules</code> instance into a completed instance.
     * @param schema <code>AssetSchema</code>: The schema which created this <code>ModelAssetSchemaModules</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>ModelAssetSchemaModules</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>ModelAssetSchemaModules</code> instance is the base module set of an <code>AssetSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while creating this <code>ModelAssetSchemaModules</code> instance.
     */
    public ModelAssetSchemaModules(AssetSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);

        TracedEntry<TracedDictionary> modelEntry = schemaJson.getAsDictionary("model", !isBase);
        TracedDictionary modelJson = modelEntry.getValue();

        if (modelJson != null) {

            TracedEntry<Identifier> modelIdentifierEntry = modelJson.getAsIdentifier("identifier", !isBase);
            modelIdentifier = modelIdentifierEntry.getValue();
            if (modelIdentifier != null) {

                addElementDependency(AssetType.MODEL, modelIdentifierEntry);
            }

            TracedEntry<Double> modelRotationOffset = modelJson.getAsDouble("rotationOffset", true, 0d);
            rotationOffset = Math.toRadians(modelRotationOffset.getValue());

            TracedEntry<Double> modelAxisHeading = modelJson.getAsDouble("axisHeading", true, 0d);
            axisHeading = Math.toRadians(modelAxisHeading.getValue());

            TracedEntry<Double> modelAxisPitch = modelJson.getAsDouble("axisPitch", true, 0d);
            axisPitch = Math.toRadians(modelAxisPitch.getValue());
        } else {

            modelIdentifier = null;
            rotationOffset = 0;
            axisHeading = 0;
            axisPitch = 0;
        }

        TracedEntry<Identifier> renderMaterialEntry = schemaJson.getAsIdentifier("renderMaterial", !isBase);
        renderMaterialIdentifier = renderMaterialEntry.getValue();
        if (renderMaterialIdentifier != null) {

            addElementDependency(AssetType.RENDER_MATERIAL, renderMaterialEntry);
        }

        TracedEntry<Identifier> animationControllerEntry = schemaJson.getAsIdentifier("animationController", true);
        animationControllerIdentifier = animationControllerEntry.getValue();
        if (animationControllerIdentifier != null) {

            addElementDependency(AssetType.ANIMATION_CONTROLLER, animationControllerEntry);
        }
    }

    /**
     * Retrieves the model identifier of this <code>ModelAssetSchemaModules</code> instance.
     * @return <code>Identifier</code>: The <code>modelIdentifier</code> field of this <code>ModelAssetSchemaModules</code> instance.
     */
    public final Identifier getModelIdentifier() {

        return modelIdentifier;
    }

    /**
     * Retrieves the rotational offset of this <code>ModelAssetSchemaModules</code> instance.
     * @return <code>double</code>: The <code>rotationOffset</code> field of this <code>ModelAssetSchemaModules</code> instance.
     */
    public final double getRotationOffset() {

        return rotationOffset;
    }

    /**
     * Retrieves the heading of the axis of rotation of this <code>ModelAssetSchemaModules</code> instance.
     * @return <code>double</code>: The <code>axisHeading</code> field of this <code>ModelAssetSchemaModules</code> instance.
     */
    public final double getAxisHeading() {

        return axisHeading;
    }

    /**
     * Retrieves the pitch of the axis of rotation of this <code>ModelAssetSchemaModules</code> instance.
     * @return <code>double</code>: The <code>axisPitch</code> field of this <code>ModelAssetSchemaModules</code> instance.
     */
    public final double getAxisPitch() {

        return axisPitch;
    }

    /**
     * Retrieves the render material identifier of this <code>ModelAssetSchemaModules</code> instance.
     * @return <code>Identifier</code>: The <code>renderMaterialIdentifier</code> field of this <code>ModelAssetSchemaModules</code> instance.
     */
    public final Identifier getRenderMaterialIdentifier() {

        return renderMaterialIdentifier;
    }

    /**
     * Retrieves the animation controller identifier of this <code>ModelAssetSchemaModules</code> instance.
     * @return <code>Identifier</code>: The <code>animationControllerIdentifier</code> field of this <code>ModelAssetSchemaModules</code> instance.
     */
    public final Identifier getAnimationControllerIdentifier() {

        return animationControllerIdentifier;
    }
}
