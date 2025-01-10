package com.transcendruins.packcompiling.assetschemas;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
* <code>ModelAssetSchemaAttributes</code>: A class which represents the attributes of an <code>AssetSchema</code> instance which has the capability of being rendered using the standard <code>RenderInstance</code> method.
*/
public abstract class ModelAssetSchemaAttributes extends AssetSchemaAttributes {

    /**
     * <code>Identifier</code>: The model of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Identifier modelIdentifier;

    /**
     * <code>Double</code>: The rotational offset of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Double rotationOffset;

    /**
     * <code>Double</code>: The heading of the axis of rotation of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Double axisHeading;

    /**
     * <code>Double</code>: The pitch of the axis of rotation of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Double axisPitch;

    /**
     * <code>Identifier</code>: The render material of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Identifier renderMaterialIdentifier;

    /**
     * <code>Identifier</code>: The animation controller of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Identifier animationControllerIdentifier;

    /**
     * Compiles this <code>ModelAssetSchemaAttributes</code> instance into a completed instance.
     * @param schema <code>AssetSchema</code>: The schema which created this <code>ModelAssetSchemaAttributes</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>ModelAssetSchemaAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>ModelAssetSchemaAttributes</code> instance is the base attribute set of an <code>AssetSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while creating this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public ModelAssetSchemaAttributes(AssetSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);

        TracedEntry<TracedDictionary> modelEntry = schemaJson.getAsDictionary("model", !isBase);

        if (modelEntry.containsValue()) {

            TracedDictionary modelJson = modelEntry.getValue();
            TracedEntry<Identifier> modelIdentifierEntry = modelJson.getAsIdentifier("identifier", !isBase);
            modelIdentifier = modelIdentifierEntry.getValue();
            if (modelIdentifierEntry.containsValue()) {

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
            rotationOffset = null;
            axisHeading = null;
            axisPitch = null;
        }

        TracedEntry<Identifier> renderMaterialEntry = schemaJson.getAsIdentifier("renderMaterial", !isBase);
        renderMaterialIdentifier = renderMaterialEntry.getValue();
        if (renderMaterialEntry.containsValue()) {

            addElementDependency(AssetType.RENDER_MATERIAL, renderMaterialEntry);
        }

        TracedEntry<Identifier> animationControllerEntry = schemaJson.getAsIdentifier("animationController", true);
        animationControllerIdentifier = animationControllerEntry.getValue();
        if (animationControllerEntry.containsValue()) {

            addElementDependency(AssetType.ANIMATION_CONTROLLER, animationControllerEntry);
        }
    }

    /**
     * Retrieves the model identifier of this <code>ModelAssetSchemaAttributes</code> instance.
     * @return <code>Identifier</code>: The <code>modelIdentifier</code> field of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Identifier getModelIdentifier() {

        return modelIdentifier;
    }

    /**
     * Retrieves the rotational offset of this <code>ModelAssetSchemaAttributes</code> instance.
     * @return <code>double</code>: The <code>rotationOffset</code> field of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Double getRotationOffset() {

        return rotationOffset;
    }

    /**
     * Retrieves the heading of the axis of rotation of this <code>ModelAssetSchemaAttributes</code> instance.
     * @return <code>double</code>: The <code>axisHeading</code> field of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Double getAxisHeading() {

        return axisHeading;
    }

    /**
     * Retrieves the pitch of the axis of rotation of this <code>ModelAssetSchemaAttributes</code> instance.
     * @return <code>double</code>: The <code>axisPitch</code> field of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Double getAxisPitch() {

        return axisPitch;
    }

    /**
     * Retrieves the render material identifier of this <code>ModelAssetSchemaAttributes</code> instance.
     * @return <code>Identifier</code>: The <code>renderMaterialIdentifier</code> field of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Identifier getRenderMaterialIdentifier() {

        return renderMaterialIdentifier;
    }

    /**
     * Retrieves the animation controller identifier of this <code>ModelAssetSchemaAttributes</code> instance.
     * @return <code>Identifier</code>: The <code>animationControllerIdentifier</code> field of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Identifier getAnimationControllerIdentifier() {

        return animationControllerIdentifier;
    }
}
