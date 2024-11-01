package com.transcendruins.packcompiling.assetschemas;

import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
* <code>ModelAssetSchemaComponents</code>: A class which represents the components of an <code>AssetSchema</code> instance which has the capability of being rendered using the standard <code>RenderInstance</code> method.
*/
public abstract class ModelAssetSchemaComponents extends AssetSchemaComponents {

    /**
     * <code>Identifier</code>: The model of this <code>ModelAssetSchemaComponents</code> instance.
     */
    private final Identifier modelIdentifier;

    /**
     * <code>Vector</code>: The rotation offset of the model of this <code>ModelAssetSchemaComponents</code> instance.
     */
    private final Vector rotationOffset;

    /**
     * <code>Identifier</code>: The render material of this <code>ModelAssetSchemaComponents</code> instance.
     */
    private final Identifier renderMaterialIdentifier;

    /**
     * <code>Identifier</code>: The animation controller of this <code>ModelAssetSchemaComponents</code> instance.
     */
    private final Identifier animationControllerIdentifier;

    /**
     * Compiles this <code>ModelAssetSchemaComponents</code> instance into a completed instance.
     * @param schema <code>AssetSchema</code>: The schema which created this <code>ModelAssetSchemaComponents</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>ModelAssetSchemaComponents</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>ModelAssetSchemaComponents</code> instance is the base component set of an <code>AssetSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while creating this <code>ModelAssetSchemaComponents</code> instance.
     */
    public ModelAssetSchemaComponents(AssetSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);

        TracedEntry<TracedDictionary> modelEntry = schemaJson.getAsDictionary("model", !isBase);
        TracedDictionary modelJson = modelEntry.getValue();

        if (modelJson != null) {

            TracedEntry<Identifier> modelIdentifierEntry = modelJson.getAsIdentifier("identifier", !isBase);
            modelIdentifier = modelIdentifierEntry.getValue();
            if (modelIdentifier != null) {

                addElementDependency(AssetType.MODEL, modelIdentifierEntry);
            }

            TracedEntry<Vector> modelRotationOffset = modelJson.getAsVector("rotationOffset", true, Vector.DIMENSION_3D);
            rotationOffset = modelRotationOffset.getValue();
        } else {

            modelIdentifier = null;
            rotationOffset = null;
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
     * Retrieves the model identifier of this <code>ModelAssetSchemaComponents</code> instance.
     * @return <code>Identifier</code>: The <code>modelIdentifier</code> property of this <code>ModelAssetSchemaComponents</code> instance.
     */
    public final Identifier getModelIdentifier() {

        return modelIdentifier;
    }

    /**
     * Retrieves the rotation offset of this <code>ModelAssetSchemaComponents</code> instance.
     * @return <code>Vector</code>: The <code>rotationOffset</code> property of this <code>ModelAssetSchemaComponents</code> instance.
     */
    public final Vector getRotationOffset() {

        return rotationOffset;
    }

    /**
     * Retrieves the render material identifier of this <code>ModelAssetSchemaComponents</code> instance.
     * @return <code>Identifier</code>: The <code>renderMaterialIdentifier</code> property of this <code>ModelAssetSchemaComponents</code> instance.
     */
    public final Identifier getRenderMaterialIdentifier() {

        return renderMaterialIdentifier;
    }

    /**
     * Retrieves the animation controller identifier of this <code>ModelAssetSchemaComponents</code> instance.
     * @return <code>Identifier</code>: The <code>animationControllerIdentifier</code> property of this <code>ModelAssetSchemaComponents</code> instance.
     */
    public final Identifier getAnimationControllerIdentifier() {

        return animationControllerIdentifier;
    }
}
