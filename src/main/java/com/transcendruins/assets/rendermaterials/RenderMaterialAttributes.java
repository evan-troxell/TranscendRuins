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

import com.jme3.math.ColorRGBA;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>RenderMaterialAttributes</code>: A class which represents the
 * attributes of a <code>RenderMaterialSchema</code> instance.
 */
public final class RenderMaterialAttributes extends AssetAttributes {

    private final Boolean lit;

    public final Boolean getLit() {

        return lit;
    }

    private final ColorRGBA diffuse;

    public final ColorRGBA getDiffuse() {

        return diffuse;
    }

    private final ColorRGBA ambient;

    public final ColorRGBA getAmbient() {

        return ambient;
    }

    private final Integer shininess;

    public final Integer getShininess() {

        return shininess;
    }

    private final ColorRGBA specular;

    public final ColorRGBA getSpecular() {

        return specular;
    }

    private final String specularMap;

    public final String getSpecularMap() {

        return specularMap;
    }

    private final Boolean transparent;

    public final Boolean getTransparent() {

        return transparent;
    }

    private final Boolean hasBackfaceCulling;

    public final Boolean getHasBackfaceCulling() {

        return hasBackfaceCulling;
    }

    /**
     * Compiles this <code>RenderMaterialAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>RenderMaterialAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>RenderMaterialAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>RenderMaterialAttributes</code> instance is the base
     *               attribute set of a <code>RenderMaterialAttributes</code>
     *               instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>RenderMaterialAttributes</code> instance.
     */
    public RenderMaterialAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        TracedEntry<Boolean> litEntry = json.getAsBoolean("lit", true, null);
        lit = litEntry.getValue();

        TracedEntry<ColorRGBA> diffuseEntry = json.getAsColorRGBA("diffuse", true, null);
        diffuse = diffuseEntry.getValue();

        TracedEntry<ColorRGBA> ambientEntry = json.getAsColorRGBA("ambient", true, null);
        ambient = ambientEntry.getValue();

        TracedEntry<Integer> shininessEntry = json.getAsByte("shininess", true, null);
        shininess = shininessEntry.getValue();

        TracedEntry<ColorRGBA> specularEntry = json.getAsColorRGBA("specular", true, null);
        specular = specularEntry.getValue();

        TracedEntry<String> specularMapEntry = json.getAsString("specularMap", true, null);
        specularMap = specularMapEntry.getValue();

        TracedEntry<Boolean> transparentEntry = json.getAsBoolean("transparent", true, null);
        transparent = transparentEntry.getValue();

        TracedEntry<Boolean> hasBackfaceCullingEntry = json.getAsBoolean("hasBackfaceCulling", true, null);
        hasBackfaceCulling = hasBackfaceCullingEntry.getValue();
    }
}
