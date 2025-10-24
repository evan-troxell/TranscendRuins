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

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.assets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Vector;

/**
 * <code>EntityInstance</code>: A class representing a generated entity
 * instance.
 */
public final class EntityInstance extends PrimaryAssetInstance {

    private Vector position = Vector.IDENTITY_VECTOR;

    public final void setTilePosition(int tileX, int tileZ) {

        position = new Vector(tileX, tileZ);
        queueTileUpdate();
    }

    private double heading;

    public final void rotate(double degrees) {

        if (degrees == 0) {

            return;
        }

        heading += degrees;
        heading %= 360;
        queueTileUpdate();
    }

    @Override
    public final Rectangle getTileBounds() {

        double C = Math.cos(Math.toRadians(heading));
        double S = Math.sin(Math.toRadians(heading));

        Vector hitbox = getHitbox();

        double halfWidth = hitbox.getX() / 2.0;
        double halfLength = hitbox.getZ() / 2.0;

        Point2D.Double pos = new Point2D.Double(position.getX(), position.getZ());

        List<Point2D.Double> v = List.of(new Point2D.Double(halfWidth, halfLength),
                new Point2D.Double(-halfWidth, halfLength), new Point2D.Double(-halfWidth, -halfLength),
                new Point2D.Double(halfWidth, -halfLength));

        // Initiate the bounds at the entity's position.
        double minX = pos.getX();
        double minY = pos.getY();
        double maxX = minX;
        double maxY = minY;

        for (Point2D.Double p : v) {

            double newX = C * p.x - S * p.y + pos.x;
            double newY = C * p.y + S * p.x + pos.y;

            if (newX < minX) {

                minX = newX;
            } else if (newX > maxX) {

                maxX = newX;
            }

            if (newY < minY) {

                minY = newY;
            } else if (newY > maxY) {

                maxY = newY;
            }
        }

        return new Rectangle((int) Math.floor(minX), (int) Math.floor(minY), (int) Math.floor(maxX - minX),
                (int) Math.floor(maxY - minY));
    }

    @Override
    public final Vector getPosition() {

        return position;
    }

    @Override
    public final Quaternion getRotation() {

        return Quaternion.fromEulerRotation(Math.toRadians(heading), new Vector(0, 1.0, 0));
    }

    public final boolean addModelChild(EntityInstance modelChild, String attachment) {

        return super.addModelChild(modelChild, attachment);
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

        if (attributes.getHitbox() != null) {

            queueTileUpdate();
        }
    }

    @Override
    protected void onPrimaryAssetUpdate(double time) {
    }
}
