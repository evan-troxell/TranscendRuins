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

package com.transcendruins.assets.entities;

import java.util.HashMap;

import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.models.ModelAttributes;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.assets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.assets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;

/**
 * <code>EntityInstance</code>: A class representing a generated entity
 * instance.
 */
public final class EntityInstance extends PrimaryAssetInstance {

    // TODO: Implement entitys position/size
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

    private ModelAttributes.Bone socket;

    private PrimaryAssetInstance parent;

    public void setAttachment(PrimaryAssetInstance parent, String socket) {

        setParent(parent);

        this.parent = parent;
        this.socket = parent.getModel().getBone(socket);
    }

    public ModelAttributes.Bone getSocket() {

        return socket;
    }

    /**
     * Creates a new instance of the <code>EntityInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>EntityInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public EntityInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        EntityContext context = (EntityContext) assetContext;
    }

    @Override
    public void applyPrimaryAssetAttributes(PrimaryAssetAttributes attributeSet) {

        EntityAttributes attributes = (EntityAttributes) attributeSet;
    }

    @Override
    protected void onPrimaryAssetUpdate(double time) {
    }

    @Override
    protected HashMap<Triangle, Triangle> getPolygons(BoneActorSet boneActors, ModelInstance model, Vector position,
            Quaternion rotation) {

        // TODO: implement parent behavior (hasParent() ? model.getPolygons(boneActors,
        // parent.getBoneActors(), getSocket(), position, rotation))
        return model.getPolygons(boneActors, position, rotation);
    }
}
