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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.entities.EntityInstance;
import com.transcendruins.assets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.assets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.World;

/**
 * <code>ElementInstance</code>: A class representing a generated element
 * instance.
 */
public final class ElementInstance extends PrimaryAssetInstance {

    private int tileWidth;

    private int tileLength;

    private Point position = new Point();

    public final void setTilePosition(int tileX, int tileZ) {

        position = new Point(tileX, tileZ);
        queueTileUpdate();
    }

    private int rotatedTileWidth;

    private int rotatedTileLength;

    private void updateTileRotation() {

        if (heading == 90 || heading == 270) {

            rotatedTileWidth = tileLength;
            rotatedTileLength = tileWidth;
        } else {

            rotatedTileWidth = tileWidth;
            rotatedTileLength = tileLength;
        }

        queueTileUpdate();
    }

    private int heading;

    public final void rotate(int degrees, AreaGrid area) {

        if (degrees == 0) {

            return;
        }

        this.heading += degrees;
        this.heading %= 360;
        updateTileRotation();

        int areaWidth = area.getWidth();
        int areaLength = area.getLength();

        position = switch (degrees) {

        case 90 -> new Point(areaLength - position.y - tileLength, position.x);

        case 180 -> new Point(areaWidth - position.x - tileWidth, areaLength - position.y - tileLength);

        case 270 -> new Point(position.y, areaWidth - position.x - tileWidth);

        default -> position;
        };
    };

    @Override
    public final Rectangle getTileBounds() {

        return new Rectangle(position.x, position.y, rotatedTileWidth, rotatedTileLength);
    }

    private final Vector tileOffset;

    @Override
    public final Vector getPosition() {

        double x = position.x * World.UNIT_TILE + rotatedTileWidth / 2.0;
        double y = position.y * World.UNIT_TILE + rotatedTileLength / 2.0;
        double z = tileOffset.getZ();

        if (tileOffset != Vector.IDENTITY_VECTOR) {

            switch (heading) {

            case 0 -> {

                x += tileOffset.getX();
                y += tileOffset.getY();
            }

            case 90 -> {

                x -= tileOffset.getY();
                y += tileOffset.getX();
            }

            case 180 -> {

                x -= tileOffset.getY();
                y -= tileOffset.getX();
            }

            case 270 -> {

                x += tileOffset.getY();
                y -= tileOffset.getX();
            }
            }
        }

        return new Vector(x, y, z);
    }

    @Override
    public final Quaternion getRotation() {

        return Quaternion.fromEulerRotation(Math.toRadians(heading), new Vector(0, 1.0, 0));
    }

    public final boolean addModelChild(ElementInstance modelChild, String attachment) {

        return super.addModelChild(modelChild, attachment);
    }

    public final boolean addModelChild(EntityInstance modelChild, String attachment) {

        return super.addModelChild(modelChild, attachment);
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

        tileOffset = context.getTileOffset();
    }

    @Override
    public final void applyPrimaryAssetAttributes(PrimaryAssetAttributes attributeSet) {

        ElementAttributes attributes = (ElementAttributes) attributeSet;

        computeAttribute(attributes.getTileDimensions(), tileDimensions -> {

            tileWidth = tileDimensions.width;
            tileLength = tileDimensions.height;

            queueTileUpdate();
        }, attributes, new Dimension(1, 1));

        updateTileRotation();
    }

    @Override
    protected final void onPrimaryAssetUpdate(double time) {
    }
}
