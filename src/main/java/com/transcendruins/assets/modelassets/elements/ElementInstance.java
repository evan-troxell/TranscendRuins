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

package com.transcendruins.assets.modelassets.elements;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.geometry.Quaternion;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.AreaTile;
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
        queueAreaUpdate();
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

        queueAreaUpdate();
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
    protected final Rectangle getInternalTileBounds() {

        return new Rectangle(position.x, position.y, rotatedTileWidth, rotatedTileLength);
    }

    private ImmutableSet<AreaTile> tiles = new ImmutableSet<>();

    @Override
    public final void updateArea(AreaGrid area) {

        AreaTile[] areaTiles = getArea(area);
        setTiles(Arrays.stream(areaTiles).toList());
    }

    public final AreaTile[] getArea(AreaGrid area) {

        Rectangle tileBounds = getTileBounds();
        return area.getArea(tileBounds.x, tileBounds.y, tileBounds.width, tileBounds.height);
    }

    public final void setTiles(Collection<AreaTile> newTiles) {

        removeAreaUpdate();

        // Find the tiles that should remain the same.
        HashSet<AreaTile> intersect = new HashSet<>(tiles);
        intersect.retainAll(newTiles);

        HashSet<AreaTile> remove = new HashSet<>(tiles);
        remove.removeAll(intersect);
        // Update the tiles that should be removed from.
        if (!remove.isEmpty()) {

            for (AreaTile tile : remove) {

                tile.removeElement(this);
            }
        }

        HashSet<AreaTile> add = new HashSet<>(newTiles);
        add.removeAll(intersect);

        // Update the tiles that should be added to.
        if (!add.isEmpty()) {

            for (AreaTile tile : add) {

                tile.addElement(this);
            }
        }

        tiles = new ImmutableSet<>(newTiles);
    }

    public final void clearTiles() {

        if (tiles.isEmpty()) {

            return;
        }

        for (AreaTile tile : tiles) {

            tile.removeElement(this);
        }

        tiles = new ImmutableSet<>();
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

        }, attributes, new Dimension(1, 1));

        updateTileRotation();
    }

    @Override
    protected final void onPrimaryAssetUpdate(double time) {
    }
}
