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

package com.transcendruins.assets.modelassets.entities;

import java.util.HashMap;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.extra.BoneActorSet;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.assets.models.ModelAttributes;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.graphics3d.Position3D;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;

/**
 * <code>EntityInstance</code>: A class representing a generated entity
 * instance.
 */
public final class EntityInstance extends ModelAssetInstance {

    private ModelAssetInstance parent;
    private ModelAttributes.Bone socket;

    public void setAttachment(ModelAssetInstance parent, String socket) {

        setParent(parent);

        this.parent = parent;
        this.socket = parent.getModel().getBone(socket);
    }

    public ModelAttributes.Bone getSocket() {

        return socket;
    }

    private final Position3D position;

    /**
     * Creates a new instance of the <code>EntityInstance</code> class.
     * 
     * @param presets <code>EntityPresets</code>: The presets used to
     *                generate this <code>EntityInstance</code> instance.
     */
    public EntityInstance(EntityContext context) {

        super(context);

        this.position = new Position3D(context.getPosition(), context.getHeading(), context.getPitch(), true);
        context.getPosition();
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        super.applyAttributes(attributeSet);
        EntityAttributes attributes = (EntityAttributes) attributeSet;
    }

    @Override
    protected void onModelAssetUpdate(double time) {
    }

    @Override
    public Vector getPosition() {

        return position.getPosition();
    }

    @Override
    public Quaternion getRotation() {

        return position.getRotation();
    }

    @Override
    protected HashMap<Triangle, Triangle> getPolygons(BoneActorSet boneActors, ModelInstance model, Vector position,
            Quaternion rotation) {

        return hasParent()
                ? model.getPolygons(boneActors, parent.getBoneActors(), getSocket(), position, rotation)
                : model.getPolygons(boneActors, position, rotation);
    }
}
