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

import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashSet;

import com.transcendruins.assets.layouts.shape.GenerationShapeInstance;
import com.transcendruins.assets.modelassets.attack.AttackInstance;
import com.transcendruins.assets.modelassets.elements.ElementInstance;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionInstance;
import com.transcendruins.geometry.Vector;
import com.transcendruins.rendering.renderBuffer.RenderBuffer;
import com.transcendruins.world.calls.AttackCall;
import com.transcendruins.world.calls.InteractionCall;

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

    private final HashSet<ElementInstance> elements = new HashSet<>();

    public final void addElement(ElementInstance element) {

        if (elements.contains(element)) {

            return;
        }

        elements.add(element);
        element.updateArea(this);
    }

    public final void removeElement(ElementInstance element) {

        if (!elements.contains(element)) {

            return;
        }

        elements.remove(element);
        element.clearTiles();
    }

    private final HashSet<EntityInstance> entities = new HashSet<>();

    public final void addEntity(EntityInstance entity) {

        if (entities.contains(entity)) {

            return;
        }

        entities.add(entity);
        entity.updateArea(this);
    }

    public final void removeEntity(EntityInstance entity) {

        if (!entities.contains(entity)) {

            return;
        }

        entities.remove(entity);
    }

    public final void updateEntity(EntityInstance entitiy) {

    }

    /**
     * Creates a new, blank instance of the <code>AreaGrid</code> class.
     * 
     * @param dimensions <code>Dimension</code>: The dimensions of the new
     *                   <code>AreaGrid</code> instance.
     */
    public AreaGrid(Dimension dimensions) {

        this.width = dimensions.width;
        this.length = dimensions.height;

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

    public final InteractionCall getNearestInteraction(Player player) {

        HashSet<PrimaryAssetInstance> assets = new HashSet<>();

        EntityInstance playerEntity = player.getEntity();
        Vector position = playerEntity.getPosition();
        double x = position.getX();
        double y = position.getY();

        double range = player.getInteractionRange();
        if (range < 0) {

            return null;
        }

        double sqr_r = range * range;

        // Handle tiles (elements).
        for (int i = Math.max(0, (int) Math.ceil(x - range)); i <= Math.min(width - 1,
                (int) Math.floor(x + range)); i++) {

            double x_offset = i - range;

            double y_range = Math.sqrt(sqr_r - x_offset * x_offset);

            for (int j = Math.max(0, (int) Math.ceil(y - y_range)); j <= Math.min(length - 1,
                    (int) Math.floor(y + y_range)); j++) {

                AreaTile tile = getTile(i, j);
                assets.addAll(tile.getElements());
            }
        }

        // Handle quadtrees (entities).
        // TODO: implement entity quadtree separation.
        assets.addAll(entities);

        assets.remove(playerEntity);

        double distance_sqr = Double.POSITIVE_INFINITY;
        InteractionCall interaction = null;

        for (PrimaryAssetInstance assetOption : assets) {

            for (AssetInteractionInstance interactionOption : assetOption.getInteraction()) {

                Vector displacement = interactionOption.getPosition(assetOption.getRotation(),
                        assetOption.getPosition().subtract(position));
                double newDist_sqr = displacement.dot(displacement);

                if (newDist_sqr <= sqr_r && newDist_sqr < distance_sqr) {

                    distance_sqr = newDist_sqr;
                    interaction = new InteractionCall(interactionOption, player, assetOption);
                }
            }
        }

        return interaction;
    }

    public final AttackCall getNearestTarget(Player player) {

        EntityInstance playerEntity = player.getEntity();
        Vector position = playerEntity.getPosition();

        AttackInstance attackData = playerEntity.getAttack();
        double range = attackData.getRange();
        if (range < 0) {

            return null;
        }

        double sqr_r = range * range;

        HashSet<EntityInstance> assets = new HashSet<>();

        assets.addAll(entities);

        assets.remove(playerEntity);

        double distance_sqr = Double.POSITIVE_INFINITY;
        AttackCall attack = null;

        for (EntityInstance assetOption : assets) {

            Vector displacement = assetOption.getPosition().subtract(position);
            double newDist_sqr = displacement.dot(displacement);

            if (newDist_sqr <= sqr_r && newDist_sqr < distance_sqr) {

                distance_sqr = newDist_sqr;
                attack = new AttackCall(attackData, playerEntity, assetOption);
            }
        }

        return attack;
    }

    /**
     * Retrieves the polygons contained within this <code>AreaGrid</code> instance.
     * 
     * @return <code>RenderBuffer</code>: The polygons in the elements, entities,
     *         items, and other rendered assets of this <code>AreaGrid</code>
     *         instance.
     */
    public final RenderBuffer getPolygons() {

        return new RenderBuffer();
    }
}
