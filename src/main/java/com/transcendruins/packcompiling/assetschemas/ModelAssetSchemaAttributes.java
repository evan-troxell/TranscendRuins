package com.transcendruins.packcompiling.assetschemas;

import org.json.simple.JSONObject;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>ModelAssetSchemaAttributes</code>: A class which represents the
 * attributes of an <code>AssetSchema</code> instance which has the capability
 * of being rendered using the standard <code>RenderInstance</code> method.
 */
public abstract class ModelAssetSchemaAttributes extends AssetSchemaAttributes {

    /**
     * <code>Identifier</code>: The model of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Identifier modelIdentifier;

    /**
     * Retrieves the model identifier of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     * 
     * @return <code>Identifier</code>: The <code>modelIdentifier</code> field of
     *         this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Identifier getModelIdentifier() {

        return modelIdentifier;
    }

    /**
     * <code>Double</code>: The angle of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Double angle;

    /**
     * Retrieves the rotational offset of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     * 
     * @return <code>double</code>: The <code>angle</code> field of this
     *         <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Double getAngle() {

        return angle;
    }

    /**
     * <code>Double</code>: The heading of the axis of rotation of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Double axisHeading;

    /**
     * Retrieves the heading of the axis of rotation of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     * 
     * @return <code>double</code>: The <code>axisHeading</code> field of this
     *         <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Double getAxisHeading() {

        return axisHeading;
    }

    /**
     * <code>Double</code>: The pitch of the axis of rotation of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Double axisPitch;

    /**
     * Retrieves the pitch of the axis of rotation of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     * 
     * @return <code>double</code>: The <code>axisPitch</code> field of this
     *         <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Double getAxisPitch() {

        return axisPitch;
    }

    /**
     * <code>Identifier</code>: The render material of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Identifier renderMaterialIdentifier;

    /**
     * Retrieves the render material identifier of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     * 
     * @return <code>Identifier</code>: The <code>renderMaterialIdentifier</code>
     *         field of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Identifier getRenderMaterialIdentifier() {

        return renderMaterialIdentifier;
    }

    /**
     * <code>Identifier</code>: The animation controller of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final Identifier animationControllerIdentifier;

    /**
     * Retrieves the animation controller identifier of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     * 
     * @return <code>Identifier</code>: The
     *         <code>animationControllerIdentifier</code> field of this
     *         <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final Identifier getAnimationControllerIdentifier() {

        return animationControllerIdentifier;
    }

    /**
     * Compiles this <code>ModelAssetSchemaAttributes</code> instance into a
     * completed instance.
     * 
     * @param schema     <code>AssetSchema</code>: The schema which created this
     *                   <code>ModelAssetSchemaAttributes</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to
     *                   compile this <code>ModelAssetSchemaAttributes</code>
     *                   instance.
     * @param isBase     <code>boolean</code>: Whether or not this
     *                   <code>ModelAssetSchemaAttributes</code> instance is the
     *                   base attribute set of an <code>AssetSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while creating this
     *                         <code>ModelAssetSchemaAttributes</code> instance.
     */
    public ModelAssetSchemaAttributes(AssetSchema schema, TracedDictionary schemaJson, boolean isBase)
            throws LoggedException {

        super(schema, schemaJson, isBase);

        TracedEntry<?> modelEntry = schemaJson.get("model", !isBase, null, String.class, JSONObject.class);

        if (modelEntry.getValue() instanceof TracedDictionary modelJson) {

            TracedEntry<Identifier> modelIdentifierEntry = modelJson.getAsIdentifier("identifier", !isBase);
            modelIdentifier = modelIdentifierEntry.getValue();
            if (modelIdentifierEntry.containsValue()) {

                addElementDependency(AssetType.MODEL, modelIdentifierEntry);
            }

            TracedEntry<TracedDictionary> rotationEntry = modelJson.getAsDictionary("rotation", true);

            if (rotationEntry.containsValue()) {
                TracedDictionary rotationJson = rotationEntry.getValue();

                TracedEntry<Double> angleEntry = rotationJson.getAsDouble("angle", true, 0d);
                angle = Math.toRadians(angleEntry.getValue());

                TracedEntry<Double> axisHeadingEntry = rotationJson.getAsDouble("axisHeading", true, 0d);
                axisHeading = Math.toRadians(axisHeadingEntry.getValue());

                TracedEntry<Double> axisPitchEntry = rotationJson.getAsDouble("axisPitch", true, 0d);
                axisPitch = Math.toRadians(axisPitchEntry.getValue());

            } else {

                angle = 0.0;
                axisHeading = 0.0;
                axisPitch = 0.0;
            }
        } else if (modelEntry.getValue() instanceof String) {

            TracedEntry<Identifier> modelIdentifierEntry = schemaJson.getAsIdentifier("model", !isBase);
            modelIdentifier = modelIdentifierEntry.getValue();
            angle = 0.0;
            axisHeading = 0.0;
            axisPitch = 0.0;
        } else {

            modelIdentifier = null;
            angle = null;
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
}
