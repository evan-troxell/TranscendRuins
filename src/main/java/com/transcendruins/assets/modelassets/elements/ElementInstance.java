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

package com.transcendruins.assets.modelassets.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.items.ItemInstance;
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

    private final Point position = new Point();

    @Override
    public final void setPosition(int x, int z) {

        position.x = x;
        position.y = z;

        queueAreaUpdate();
    }

    @Override
    public final void translate(int dx, int dz) {

        setPosition(position.x + dx, position.y + dz);
    }

    private int rotatedTileWidth;

    private int rotatedTileLength;

    private int heading;

    private void updateTileRotation() {

        if (heading == World.NORTH || heading == World.SOUTH) {

            rotatedTileWidth = tileLength;
            rotatedTileLength = tileWidth;
        } else {

            rotatedTileWidth = tileWidth;
            rotatedTileLength = tileLength;
        }

        queueAreaUpdate();
    }

    public final void rotate(int direction) {

        if (direction == World.EAST) {

            return;
        }

        heading += direction;
        heading %= 4;
        updateTileRotation();
    }

    @Override
    public final void rotate(int direction, int areaWidth, int areaLength) {

        if (direction == World.EAST) {

            return;
        }

        heading += direction;
        heading %= 4;
        updateTileRotation();

        int x = position.x;
        int z = position.y;

        switch (direction) {

        // 90 degrees.
        case World.NORTH -> {

            position.x = z;
            position.y = areaLength - x - rotatedTileLength;
        }

        // 180 degrees.
        case World.WEST -> {

            position.x = areaWidth - x - rotatedTileWidth;
            position.y = areaLength - z - rotatedTileLength;
        }

        // 270 degrees.
        case World.SOUTH -> {

            position.x = areaWidth - z - rotatedTileWidth;
            position.y = x;
        }
        }
    };

    @Override
    protected final Rectangle getInternalTileBounds() {

        return new Rectangle(position.x, position.y, rotatedTileWidth, rotatedTileLength);
    }

    @Override
    public final Rectangle getInternalTileBoundsTranslated(int dx, int dz) {

        return new Rectangle(position.x + dx, position.y + dz, rotatedTileWidth, rotatedTileLength);
    }

    @Override
    public final Rectangle getInternalTileBoundsAt(int tileX, int tileZ) {

        return new Rectangle(tileX, tileZ, rotatedTileWidth, rotatedTileLength);
    }

    private ImmutableSet<AreaTile> tiles = new ImmutableSet<>();

    @Override
    public final void updateArea(AreaGrid area) {

        AreaTile[] areaTiles = getArea(area);
        setTiles(Arrays.stream(areaTiles).toList());
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

    @Override
    public final void updateSlot(String name, ItemInstance item) {
    }

    private final Vector tileOffset;

    @Override
    public final Vector getPosition() {

        double x = (position.x + rotatedTileWidth / 2.0) * World.UNIT_TILE;
        double y = tileOffset.getY(); // TODO adjust for tile height
        double z = (position.y + rotatedTileLength / 2.0) * World.UNIT_TILE;

        if (tileOffset != Vector.IDENTITY_VECTOR) {

            switch (heading) {

            case World.EAST -> {

                x += tileOffset.getX();
                z += tileOffset.getZ();
            }

            case World.NORTH -> {

                x -= tileOffset.getZ();
                z += tileOffset.getX();
            }

            case World.WEST -> {

                x -= tileOffset.getX();
                z -= tileOffset.getZ();
            }

            case World.SOUTH -> {

                x += tileOffset.getZ();
                z -= tileOffset.getX();
            }
            }
        }

        return new Vector(x, y, z);
    }

    @Override
    public final Quaternion getRotation() {

        return Quaternion.fromEulerRotation(heading * Math.PI / 2.0, new Vector(0, 1.0, 0));
    }

    public final boolean addModelChild(ElementInstance modelChild, String attachment) {

        return super.addModelChild(modelChild, attachment);
    }

    public final boolean addModelChild(EntityInstance modelChild, String attachment) {

        return super.addModelChild(modelChild, attachment);
    }

    @Override
    public final boolean alive() {

        // TODO write alive code
        return true;
    }

    private Color mapColor;

    public final Color getMapColor() {

        return mapColor;
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

            updateTileRotation();

        }, attributes, new Dimension(1, 1));

        mapColor = calculateAttribute(attributes.getMapColor(), roll -> roll.get(getRandomId()), mapColor, attributes,
                null);
    }

    @Override
    protected final void onPrimaryAssetUpdate(double time) {
    }
}
