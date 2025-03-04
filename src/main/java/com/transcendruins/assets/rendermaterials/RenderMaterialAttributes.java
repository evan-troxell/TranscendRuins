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

import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>RenderMaterialAttributes</code>: A class which represents the
 * attributes of a <code>RenderMaterialSchema</code> instance.
 */
public final class RenderMaterialAttributes extends AssetAttributes {

    /**
     * <code>Boolean</code>: Whether or not backface culling should be applied to
     * the model.
     */
    private final Boolean backfaceCulling;

    /**
     * Retrieves whether or not this <code>RenderMaterialAttributes</code>
     * instance implements backface culling.
     * 
     * @return <code>Boolean</code>: The <code>backfaceCulling</code> field of this
     *         <code>RenderMaterialAttributes</code> instance.
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
     * Retrieves whether or not this <code>RenderMaterialAttributes</code>
     * instance implements the fresnel effect.
     * 
     * @return <code>Boolean</code>: The <code>fresnelEffect</code> field of this
     *         <code>RenderMaterialAttributes</code> instance.
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
     * <code>RenderMaterialAttributes</code> instance.
     * 
     * @return <code>boolean</code>: The <code>faceDimmingFactor</code> field of
     *         this <code>RenderMaterialAttributes</code> instance.
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
     * Retrieves whether or not this <code>RenderMaterialAttributes</code>
     * instance implements anti aliasing.
     * 
     * @return <code>Boolean</code>: The <code>antiAliasing</code> field of this
     *         <code>RenderMaterialAttributes</code> instance.
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
     * Retrieves whether or not this <code>RenderMaterialAttributes</code>
     * instance implements opaqueness.
     * 
     * @return <code>Boolean</code>: The <code>opaque</code> field of this
     *         <code>RenderMaterialAttributes</code> instance.
     */
    public Boolean getOpaque() {

        return opaque;
    }

    /**
     * Compiles this <code>RenderMaterialAttributes</code> instance into a
     * completed instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created
     *               this <code>RenderMaterialAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to
     *               compile this <code>RenderMaterialAttributes</code>
     *               instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>RenderMaterialAttributes</code> instance is the
     *               base attribute set of a
     *               <code>RenderMaterialAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>RenderMaterialAttributes</code>
     *                         instance.
     */
    public RenderMaterialAttributes(AssetSchema schema, TracedDictionary json, boolean isBase)
            throws LoggedException {

        super(schema, json, isBase);

        backfaceCulling = json.getAsBoolean("backfaceCulling", true, null).getValue();

        fresnelEffect = json.getAsBoolean("fresnelEffect", true, null).getValue();

        faceDimmingFactor = json.getAsDouble("faceDimmingFactor", true, null, num -> 0.0 <= num && num <= 1.0)
                .getValue();

        antiAliasing = json.getAsBoolean("antiAliasing", true, null).getValue();

        opaque = json.getAsBoolean("opaque", true, null).getValue();
    }
}
