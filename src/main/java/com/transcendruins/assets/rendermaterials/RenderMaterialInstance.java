/* Copyright 2025 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins.assets.rendermaterials;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;

/**
 * <code>RenderMaterialInstance</code>: A class representing a generated render
 * material instance.
 */
public final class RenderMaterialInstance extends AssetInstance {

    /**
     * <code>boolean</code>: Whether or not backface culling should be applied to
     * this <code>RenderMaterialInstance</code> instance.
     */
    private boolean backfaceCulling;

    /**
     * <code>boolean</code>: Whether or not the fresnel effect (opaqueness increase
     * as the viewing plane becomes perpendicular to the polygon being viewed)
     * should be applied to this <code>RenderMaterialInstance</code> instance.
     */
    private boolean fresnelEffect;

    /**
     * <code>boolean</code>: Whether or not the face dimming effect (light becomes
     * dimmer as the viewing plane becomes orthogonal to the polygon being viewed)
     * should be applied to this <code>RenderMaterialInstance</code> instance.
     */
    private boolean faceDimming;

    /**
     * <code>double</code>: The factor which should be applied to the face dimming
     * effect.
     */
    private double faceDimmingFactor;

    /**
     * <code>boolean</code>: Whether or not anti aliasing (pixel alpha adjustments
     * as the polygon border intersects more of the pixel) should be applied to this
     * <code>RenderMaterialInstance</code> instance.
     */
    private boolean antiAliasing;

    /**
     * <code>boolean</code>: Whether or alpha cancelling (total opaqueness) should
     * be applied to this <code>RenderMaterialInstance</code> instance.
     */
    private boolean opaque;

    /**
     * Creates a new instance of the <code>RenderMaterialInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>RenderMaterialInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public RenderMaterialInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        RenderMaterialContext context = (RenderMaterialContext) assetContext;
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        RenderMaterialAttributes attributes = (RenderMaterialAttributes) attributeSet;

        // Updates the backfaceCulling field.
        backfaceCulling = calculateAttribute(attributes.getBackfaceCulling(), backfaceCulling, attributeSet, true);
        setProperty("backfaceCulling", backfaceCulling);

        // Updates the fresnelEffect field.
        fresnelEffect = calculateAttribute(attributes.getFresnelEffect(), fresnelEffect, attributeSet, false);
        setProperty("fresnelEffect", fresnelEffect);

        // Updates the faceDimmingFactor field.
        faceDimmingFactor = calculateAttribute(attributes.getFaceDimmingFactor(), faceDimmingFactor, attributeSet, 0.0);
        setProperty("faceDimmingFactor", faceDimmingFactor);

        faceDimming = faceDimmingFactor != 0.0;
        setProperty("faceDimming", faceDimming);

        // Updates the antiAliasing field.
        antiAliasing = calculateAttribute(attributes.getAntiAliasing(), antiAliasing, attributeSet, true);
        setProperty("antiAliasing", antiAliasing);

        // Updates the opaque field.
        opaque = calculateAttribute(attributes.getOpaque(), opaque, attributeSet, true);
        setProperty("opaque", opaque);
    }

    @Override
    protected void onUpdate(double time) {
    }

    /**
     * Retrieves the <code>backfaceCulling</code> field of this
     * <code>RenderMaterialAttributes</code> instance.
     * 
     * @return <code>boolean</code>: Whether or not backface culling should be
     *         implemented.
     */
    public boolean backfaceCulling() {

        return backfaceCulling;
    }

    /**
     * Retrieves the <code>fresnelEffect</code> field of this
     * <code>RenderMaterialAttributes</code> instance.
     * 
     * @return <code>boolean</code>: Whether or not the Fresnel effect should be
     *         implemented.
     */
    public boolean fresnelEffect() {

        return fresnelEffect;
    }

    /**
     * Retrieves the <code>faceDimming</code> field of this
     * <code>RenderMaterialAttributes</code> instance.
     * 
     * @return <code>boolean</code>: Whether or not face dimming should be
     *         implemented.
     */
    public boolean faceDimming() {

        return faceDimming;
    }

    /**
     * Retrieves the <code>faceDimmingFactor</code> field of this
     * <code>RenderMaterialAttributes</code> instance.
     * 
     * @return <code>double</code>: The retrieved face dimming factor.
     */
    public double faceDimmingFactor() {

        return faceDimmingFactor;
    }

    /**
     * Retrieves the <code>antiAliasing</code> field of this
     * <code>RenderMaterialAttributes</code> instance.
     * 
     * @return <code>boolean</code>: Whether or not anti aliasing should be
     *         implemented.
     */
    public boolean antiAliasing() {

        return antiAliasing;
    }

    /**
     * Retrieves the <code>opaque</code> field of this
     * <code>RenderMaterialAttributes</code> instance.
     * 
     * @return <code>boolean</code>: Whether or not alpha cancelling should be
     *         implemented.
     */
    public boolean opaque() {

        return opaque;
    }
}
