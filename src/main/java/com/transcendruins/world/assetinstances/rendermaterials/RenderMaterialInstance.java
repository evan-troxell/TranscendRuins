package com.transcendruins.world.assetinstances.rendermaterials;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaModules;
import com.transcendruins.packcompiling.assetschemas.rendermaterials.RenderMaterialSchema;
import com.transcendruins.packcompiling.assetschemas.rendermaterials.RenderMaterialSchemaModules;
import com.transcendruins.world.assetinstances.AssetInstance;

/**
 * <code>RenderMaterialInstance</code>: A class representing a generated render material instance.
 */
public final class RenderMaterialInstance extends AssetInstance {

    /**
     * <code>boolean</code>: Whether or not backface culling should be applied to this <code>RenderMaterialInstance</code> instance.
     */
    private boolean backfaceCulling;

    /**
     * <code>boolean</code>: Whether or not the fresnel effect (opaqueness increase as the viewing plane becomes perpendicular to the polygon being viewed) should be applied to this <code>RenderMaterialInstance</code> instance.
     */
    private boolean fresnelEffect;

    /**
     * <code>boolean</code>: Whether or not the face dimming effect (light becomes dimmer as the viewing plane becomes orthogonal to the polygon being viewed) should be applied to this <code>RenderMaterialInstance</code> instance.
     */
    private boolean faceDimming;

    /**
     * <code>double</code>: The factor which should be applied to the face dimming effect.
     */
    private double faceDimmingFactor;

    /**
     * <code>boolean</code>: Whether or not anti aliasing (pixel alpha adjustments as the polygon border intersects more of the pixel) should be applied to this <code>RenderMaterialInstance</code> instance.
     */
    private boolean antiAliasing;

    /**
     * <code>boolean</code>: Whether or alpha cancelling (total opaqueness) should be applied to this <code>RenderMaterialInstance</code> instance.
     */
    private boolean opaque;

    /**
     * Creates a new instance of the <code>RenderMaterialInstance</code> class.
     * @param schema <code>RenderMaterialSchema</code>: The schema used to generate this <code>RenderMaterialInstance</code> instance.
     */
    public RenderMaterialInstance(RenderMaterialSchema schema) {

        super(schema);
    }

    /**
     * Applies a module set to this <code>RenderMaterialInstance</code> instance.
     * @param moduleSet <code>AssetSchemaModules</code>: The module set to apply.
     */
    @Override
    protected void applyModuleSet(AssetSchemaModules moduleSet) {

        RenderMaterialSchemaModules modules = (RenderMaterialSchemaModules) moduleSet;

        if (modules.getBackfaceCulling() != null) {

            backfaceCulling = modules.getBackfaceCulling();
        }

        if (modules.getFresnelEffect() != null) {

            fresnelEffect = modules.getFresnelEffect();
        }

        if (modules.getFaceDimmingFactor() != null) {

            faceDimmingFactor = modules.getFaceDimmingFactor();
        }
        faceDimming = faceDimmingFactor != 0.0;

        if (modules.getAntiAliasing() != null) {

            antiAliasing = modules.getAntiAliasing();
        }

        if (modules.getOpaque() != null) {

            opaque = modules.getOpaque();
        }
    }

    /**
     * Retrieves the <code>backfaceCulling</code> field of this <code>RenderMaterialSchemaModules</code> instance.
     * @return <code>boolean</code>: Whether or not backface culling should be implemented.
     */
    public boolean backfaceCulling() {

        return backfaceCulling;
    }

    /**
     * Retrieves the <code>fresnelEffect</code> field of this <code>RenderMaterialSchemaModules</code> instance.
     * @return <code>boolean</code>: Whether or not the Fresnel effect should be implemented.
     */
    public boolean fresnelEffect() {

        return fresnelEffect;
    }

    /**
     * Retrieves the <code>faceDimming</code> field of this <code>RenderMaterialSchemaModules</code> instance.
     * @return <code>boolean</code>: Whether or not face dimming should be implemented.
     */
    public boolean faceDimming() {

        return faceDimming;
    }

    /**
     * Retrieves the <code>faceDimmingFactor</code> field of this <code>RenderMaterialSchemaModules</code> instance.
     * @return <code>double</code>: The retrieved face dimming factor.
     */
    public double faceDimmingFactor() {

        return faceDimmingFactor;
    }

    /**
     * Retrieves the <code>antiAliasing</code> field of this <code>RenderMaterialSchemaModules</code> instance.
     * @return <code>boolean</code>: Whether or not anti aliasing should be implemented.
     */
    public boolean antiAliasing() {

        return antiAliasing;
    }

    /**
     * Retrieves the <code>opaque</code> field of this <code>RenderMaterialSchemaModules</code> instance.
     * @return <code>boolean</code>: Whether or not alpha cancelling should be implemented.
     */
    public boolean opaque() {

        return opaque;
    }
}
