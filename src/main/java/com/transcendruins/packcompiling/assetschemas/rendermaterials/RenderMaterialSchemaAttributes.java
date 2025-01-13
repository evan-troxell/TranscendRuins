package com.transcendruins.packcompiling.assetschemas.rendermaterials;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>RenderMaterialSchemaAttributes</code>: A class which represents the
 * attributes of a <code>RenderMaterialSchema</code> instance.
 */
public final class RenderMaterialSchemaAttributes extends AssetSchemaAttributes {

    /**
     * <code>Boolean</code>: Whether or not backface culling should be applied to
     * the model.
     */
    private final Boolean backfaceCulling;

    /**
     * Retrieves whether or not this <code>RenderMaterialSchemaAttributes</code>
     * instance implements backface culling.
     * 
     * @return <code>Boolean</code>: The <code>backfaceCulling</code> field of this
     *         <code>RenderMaterialSchemaAttributes</code> instance.
     */
    public Boolean getBackfaceCulling() {

        return backfaceCulling;
    }

    /**
     * <code>Boolean</code>: Whether or not the fresnel effect (opaqueness increase
     * as the viewing plane becomes perpendicular to the polygon being viewed)
     * should be applied to the model.
     */
    private final Boolean fresnelEffect;

    /**
     * Retrieves whether or not this <code>RenderMaterialSchemaAttributes</code>
     * instance implements the fresnel effect.
     * 
     * @return <code>Boolean</code>: The <code>fresnelEffect</code> field of this
     *         <code>RenderMaterialSchemaAttributes</code> instance.
     */
    public Boolean getFresnelEffect() {

        return fresnelEffect;
    }

    /**
     * <code>Double</code>: The factor which should be applied to the face dimming
     * effect.
     */
    private final Double faceDimmingFactor;

    /**
     * Retrieves the face dimming factor of this
     * <code>RenderMaterialSchemaAttributes</code> instance.
     * 
     * @return <code>boolean</code>: The <code>faceDimmingFactor</code> field of
     *         this <code>RenderMaterialSchemaAttributes</code> instance.
     */
    public Double getFaceDimmingFactor() {

        return faceDimmingFactor;
    }

    /**
     * <code>Boolean</code>: Whether or not anti aliasing (pixel alpha adjustments
     * as the polygon border intersects more of the pixel) should be applied to the
     * model.
     */
    private final Boolean antiAliasing;

    /**
     * Retrieves whether or not this <code>RenderMaterialSchemaAttributes</code>
     * instance implements anti aliasing.
     * 
     * @return <code>Boolean</code>: The <code>antiAliasing</code> field of this
     *         <code>RenderMaterialSchemaAttributes</code> instance.
     */
    public Boolean getAntiAliasing() {

        return antiAliasing;
    }

    /**
     * <code>Boolean</code>: Whether or alpha cancelling (total opaqueness) should
     * be applied to the model.
     */
    private final Boolean opaque;

    /**
     * Retrieves whether or not this <code>RenderMaterialSchemaAttributes</code>
     * instance implements opaqueness.
     * 
     * @return <code>Boolean</code>: The <code>opaque</code> field of this
     *         <code>RenderMaterialSchemaAttributes</code> instance.
     */
    public Boolean getOpaque() {

        return opaque;
    }

    /**
     * Compiles this <code>RenderMaterialSchemaAttributes</code> instance into a
     * completed instance.
     * 
     * @param schema     <code>RenderMaterialSchema</code>: The schema which created
     *                   this <code>RenderMaterialSchemaAttributes</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to
     *                   compile this <code>RenderMaterialSchemaAttributes</code>
     *                   instance.
     * @param isBase     <code>boolean</code>: Whether or not this
     *                   <code>RenderMaterialSchemaAttributes</code> instance is the
     *                   base attribute set of a
     *                   <code>RenderMaterialSchemaAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>RenderMaterialSchemaAttributes</code>
     *                         instance.
     */
    public RenderMaterialSchemaAttributes(RenderMaterialSchema schema, TracedDictionary schemaJson, boolean isBase)
            throws LoggedException {

        super(schema, schemaJson, isBase);

        backfaceCulling = schemaJson.getAsBoolean("backfaceCulling", true, isBase ? true : null).getValue();

        fresnelEffect = schemaJson.getAsBoolean("fresnelEffect", true, isBase ? false : null).getValue();

        faceDimmingFactor = schemaJson.getAsDouble("faceDimmingFactor", true, isBase ? 0.0 : null, 0.0, 1.0).getValue();

        antiAliasing = schemaJson.getAsBoolean("antiAliasing", true, isBase ? true : null).getValue();

        opaque = schemaJson.getAsBoolean("opaque", true, isBase ? true : null).getValue();

        finalizeData();
    }

    @Override
    public void finalizeData() {

    }
}
