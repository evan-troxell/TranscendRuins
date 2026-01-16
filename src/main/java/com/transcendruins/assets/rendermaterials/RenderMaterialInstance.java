/* Copyright 2026 Evan Troxell
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

import java.awt.image.BufferedImage;
import java.util.function.Function;

import com.jme3.math.ColorRGBA;
import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.rendering.renderbuffer.MaterialData;
import com.transcendruins.world.World;

/**
 * <code>RenderMaterialInstance</code>: A class representing a generated render
 * material instance.
 */
public final class RenderMaterialInstance extends AssetInstance {

    private boolean lit;

    private boolean diffuses;
    private ColorRGBA diffuse;

    private boolean hasAmbient;
    private ColorRGBA ambient;

    private int shininess;

    private boolean hasSpecularMap;

    private ColorRGBA specular;
    private boolean hasSpecular;

    private String specularMapPath;
    private BufferedImage specularMap;

    private boolean transparent;

    private boolean backfaceCulling;

    private int materialBitMask;

    private int createMaterialBitMask() {

        int mask = 0;
        boolean[] bits = { lit, diffuses, hasAmbient, hasSpecular, hasSpecularMap, transparent, backfaceCulling };

        for (int i = 0; i < bits.length; i++) {

            if (bits[i]) {

                mask |= 1 << i;
            }
        }

        return mask;
    }

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

        lit = calculateAttribute(attributes.getLit(), lit, attributes, false);
        setProperty("lit", lit);

        diffuse = calculateAttribute(attributes.getDiffuse(), diffuse, attributes, null);
        diffuses = diffuse != null;
        setProperty("diffuses", diffuses);

        ambient = calculateAttribute(attributes.getAmbient(), ambient, attributes, null);
        hasAmbient = ambient != null;
        setProperty("hasAmbient", hasAmbient);

        shininess = calculateAttribute(attributes.getShininess(), shininess, attributes, 16);
        setProperty("shininess", shininess);

        specular = calculateAttribute(attributes.getSpecular(), specular, attributes, null);
        hasSpecular = specular != null;
        setProperty("hasSpecular", hasSpecular);

        specularMapPath = calculateAttribute(attributes.getSpecularMap(), specularMapPath, attributes, null);
        setProperty("specularMap", specularMapPath);

        hasSpecularMap = specularMapPath != null;
        setProperty("hasSpecularMap", hasSpecularMap);

        if (hasSpecularMap) {

            specularMap = getInstanceTextureAsBufferedImage(specularMapPath, BufferedImage.TYPE_INT_ARGB);
        } else {

            specularMap = null;
        }

        transparent = calculateAttribute(attributes.getTransparent(), transparent, attributes, false);
        setProperty("transparent", transparent);

        backfaceCulling = calculateAttribute(attributes.getBackfaceCulling(), backfaceCulling, attributes, true);
        setProperty("backfaceCulling", backfaceCulling);

        materialBitMask = createMaterialBitMask();
        setProperty("materialBitMask", materialBitMask);
    }

    @Override
    protected void onUpdate(double time) {
    }

    public final MaterialData createMaterialData(BufferedImage texture, int textureWidth, int textureHeight) {

        return new MaterialData(texture, textureWidth, textureHeight, materialBitMask, diffuse, ambient, shininess,
                specular, specularMap);
    }

    @Override
    public final RenderMaterialInstance clone(Function<AssetPresets, ? extends AssetContext> contextualize,
            World world) {

        RenderMaterialInstance asset = (RenderMaterialInstance) super.clone(contextualize, world);

        asset.lit = lit;
        asset.diffuses = diffuses;
        asset.diffuse = diffuse;
        asset.hasAmbient = hasAmbient;
        asset.ambient = ambient;
        asset.shininess = shininess;
        asset.hasSpecularMap = hasSpecularMap;
        asset.specular = specular;
        asset.hasSpecular = hasSpecular;
        asset.specularMapPath = specularMapPath;
        asset.specularMap = specularMap;
        asset.transparent = transparent;
        asset.backfaceCulling = backfaceCulling;
        asset.materialBitMask = materialBitMask;

        return asset;
    }
}
