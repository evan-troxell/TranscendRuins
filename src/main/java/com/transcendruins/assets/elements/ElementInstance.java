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

package com.transcendruins.assets.elements;

import java.util.HashMap;

import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.assets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.assets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;

/**
 * <code>ElementInstance</code>: A class representing a generated element
 * instance.
 */
public final class ElementInstance extends PrimaryAssetInstance {

    // TODO: Implement element position/size
    @Override
    public final Vector getPosition() {

        return Vector.IDENTITY_VECTOR;
    }

    @Override
    public final Quaternion getRotation() {

        return Quaternion.IDENTITY_QUATERNION;
    }

    @Override
    public int getTileWidth() {

        return 0;
    }

    @Override
    public int getTileLength() {

        return 0;
    }

    /**
     * Creates a new instance of the <code>ElementInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>ElementInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public ElementInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        ElementContext context = (ElementContext) assetContext;
    }

    @Override
    public final void applyPrimaryAssetAttributes(PrimaryAssetAttributes attributeSet) {

        ElementAttributes attributes = (ElementAttributes) attributeSet;
    }

    @Override
    protected final void onPrimaryAssetUpdate(double time) {
    }

    @Override
    protected final HashMap<Triangle, Triangle> getPolygons(BoneActorSet boneActors, ModelInstance model,
            Vector position, Quaternion rotation) {

        return model.getPolygons(boneActors, position, rotation);
    }
}
