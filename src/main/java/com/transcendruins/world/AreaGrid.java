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

package com.transcendruins.world;

import java.util.Arrays;
import java.util.Set;

import com.transcendruins.assets.layouts.shape.GenerationShapeInstance;
import com.transcendruins.graphics3d.PolyGroup;

/**
 * <code>AreaGrid</code>: A class representing a grid of tiles which a location
 * area is entirely or partially composed of.
 */
public final class AreaGrid {

    /**
     * <code>int</code>: The width, in tiles, of this <code>AreaGrid</code>
     * instance.
     */
    private final int width;

    /**
     * Retrieves the width, in tiles, of this <code>AreaGrid</code> instance.
     * 
     * @return <code>int</code>: The <code>width</code> field of this
     *         <code>AreaGrid</code> instance.
     */
    public final int getWidth() {

        return width;
    }

    /**
     * <code>int</code>: The length, in tiles, of this <code>AreaGrid</code>
     * instance.
     */
    private final int length;

    /**
     * Retrieves the length, in tiles, of this <code>AreaGrid</code> instance.
     * 
     * @return <code>int</code>: The <code>length</code> field of this
     *         <code>AreaGrid</code> instance.
     */
    public final int getLength() {

        return length;
    }

    /**
     * <code>AreaTile[]</code>: The grid of tiles in this <code>AreaGrid</code>
     * instance.
     */
    private final AreaTile[] tiles;

    /**
     * Determines whether or not a coordinate is contained within this
     * <code>AreaGrid</code> instance.
     * 
     * @param x <code>int</code>: The X component of the coordinate as an integer
     *          from <code>0</code> to the width of this <code>AreaGrid</code>
     *          instance, exclusive.
     * @param z <code>int</code>: The Z component of the coordinate as an integer
     *          from <code>0</code> to the length of this <code>AreaGrid</code>
     *          instance, exclusive.
     * @return <code>boolean</code>: If the coordinate is valid.
     */
    public final boolean inBounds(int x, int z) {

        return 0 <= x && x < width && 0 <= z && z < length;
    }

    /**
     * Retrieves a tile from this <code>AreaGrid</code> instance.
     * 
     * @param x <code>int</code>: The X component of the tile as an integer from
     *          <code>0</code> to the width of this <code>AreaGrid</code> instance,
     *          exclusive.
     * @param z The Z component of the coordinate as an integer from <code>0</code>
     *          to the length of this <code>AreaGrid</code> instance, exclusive.
     * @return <code>AreaTile</code>: The retrieved <code>AreaTile</code> instance,
     *         or <code>null</code> if the tile could not be retrieved.
     */
    public final AreaTile getTile(int x, int z) {

        if (!inBounds(x, z)) {

            return null;
        }

        int i = x + z * width;

        return tiles[i];
    }

    public final AreaTile[] getArea(int x, int z, int width, int length) {

        if (width <= 0 || length <= 0 || this.width <= x || this.length <= z) {

            return new AreaTile[0];
        }

        if (x < 0) {

            width += x;
            x = 0;
        }

        if (x + width > this.width) {

            width = this.width - x;
        }

        if (z < 0) {

            length += z;
            z = 0;
        }

        if (z + length > this.length) {

            length = this.length - z;
        }

        int area = width * length;
        AreaTile[] copy = new AreaTile[area];

        for (int zC = 0; zC < length; zC++) {

            int xC = x + (zC + z) * this.width;

            System.arraycopy(tiles, xC, copy, zC * width, width);
        }

        return copy;
    }

    /**
     * Creates a new, blank instance of the <code>AreaGrid</code> class.
     * 
     * @param width  <code>int</code>: The width of the new <code>AreaGrid</code>
     *               instance.
     * @param length <code>int</code>: The length of the new <code>AreaGrid</code>
     *               instance.
     */
    public AreaGrid(int width, int length) {

        this.width = width;
        this.length = length;

        tiles = new AreaTile[width * length];
        Arrays.setAll(tiles, _ -> new AreaTile());
    }

    public AreaGrid(int width, int length, GenerationShapeInstance shape) {

        this.width = width;
        this.length = length;

        tiles = new AreaTile[width * length];
        for (int x = 0; x < width; x++) {

            for (int z = 0; z < length; z++) {

                if (shape.isValidPlacement(x, z, width / 2, length / 2)) {
                    tiles[x + z * width] = new AreaTile();
                }
            }
        }
    }

    /**
     * Determines whether or not another area can be applied onto this
     * <code>AreaGrid</code> instance at a certain position. This requires the area
     * to be free from other obstructions.
     * 
     * @param startX <code>int</code>: The leftmost coordinate in the grid at which
     *               to check the new area at.
     * @param startZ <code>int</code>: The topmost coordinate in the grid at which
     *               to check the new area at.
     * @param area   <code>AreaGrid</code>: The area to compare to this
     *               <code>AreaGrid</code> instance.
     * @return <code>boolean</code>: If the new area can be placed at the input
     *         coordinate.
     */
    public final boolean canApply(int startX, int startZ, AreaGrid area) {

        if (area.getWidth() <= 0 || area.getLength() <= 0) {

            return false;
        }

        if (startX < 0 || width < startX + area.getWidth()) {

            return false;
        }

        if (startZ < 0 || length < startZ + area.getLength()) {

            return false;
        }

        for (int x = startX; x < startX + area.getWidth(); x++) {

            for (int z = startZ; z < startZ + area.getLength(); z++) {

                AreaTile tile = getTile(x, z);
                AreaTile newTile = area.getTile(x - startX, z - startZ);

                if (newTile == null) {

                    continue;
                }

                if (tile == null || !tile.canApply(newTile)) {

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Applies each of the tiles from another <code>AreaGrid</code> instance onto
     * this <code>AreaGrid</code> instance. If the area is not fully contained
     * within the coordinate grid of this <code>AreaGrid</code> instance, the
     * extruding portions will be cut off.
     * 
     * @param startX <code>int</code>: The leftmost coordinate in the grid to apply
     *               the new area at.
     * @param startZ <code>int</code>: The topmost coordinate in the grid to apply
     *               the new area at.
     * @param area   <code>AreaGrid</code>: The area to apply to this
     *               <code>AreaGrid</code> instance.
     */
    public final void apply(int startX, int startZ, AreaGrid area) {

        if (!canApply(startX, startZ, area)) {

            return;
        }

        for (int x = 0; x < area.getWidth(); x++) {

            for (int z = 0; z < area.getLength(); z++) {

                AreaTile tile = getTile(x + startX, z + startZ);
                AreaTile newTile = area.getTile(x, z);

                if (tile == null || newTile == null) {

                    continue;
                }

                tile.apply(newTile);
            }
        }
    }

    /**
     * Retrieves the polygons contained within this <code>AreaGrid</code> instance.
     * 
     * @return <code>Set&lt;PolyGroup&gt;</code>: The set of all polygons in the
     *         elements, entities, items, and other rendered assets of this
     *         <code>AreaGrid</code> instance.
     */
    public final Set<PolyGroup> getPolygons() {

        return Set.of();
    }
}
