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
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.transcendruins.assets.layouts.placement.GenerationPlacement;
import com.transcendruins.assets.layouts.placement.GenerationShapeInstance;
import com.transcendruins.assets.layouts.placement.PlacementArea;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.assets.modelassets.attack.AttackInstance;
import com.transcendruins.assets.modelassets.elements.ElementInstance;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionInstance;
import com.transcendruins.geometry.Vector;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.rendering.renderBuffer.RenderBuffer;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.world.calls.AttackCall;
import com.transcendruins.world.calls.InteractionCall;

/**
 * <code>AreaGrid</code>: A class representing a grid of tiles which a location
 * area is entirely or partially composed of.
 */
public final class AreaGrid implements PlacementArea, RenderInstance {

    private final Rectangle bounds;

    /**
     * Retrieves the width, in tiles, of this <code>AreaGrid</code> instance.
     * 
     * @return <code>int</code>: The <code>width</code> field of this
     *         <code>AreaGrid</code> instance.
     */
    @Override
    public final int getWidth() {

        return bounds.width;
    }

    /**
     * Retrieves the length, in tiles, of this <code>AreaGrid</code> instance.
     * 
     * @return <code>int</code>: The <code>length</code> field of this
     *         <code>AreaGrid</code> instance.
     */
    @Override
    public final int getLength() {

        return bounds.height;
    }

    private final GenerationPlacement spawn;

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

        return 0 <= x && x < bounds.width && 0 <= z && z < bounds.height;
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

        int i = x + z * bounds.width;

        return tiles[i];
    }

    public final AreaTile[] getArea(int x, int z, int width, int length) {

        if (width <= 0 || length <= 0 || bounds.width <= x || bounds.height <= z) {

            return new AreaTile[0];
        }

        if (x < 0) {

            width += x;
            x = 0;
        }

        if (x + width > bounds.width) {

            width = bounds.width - x;
        }

        if (z < 0) {

            length += z;
            z = 0;
        }

        if (z + length > bounds.height) {

            length = bounds.height - z;
        }

        int area = width * length;
        AreaTile[] copy = new AreaTile[area];

        for (int zC = 0; zC < length; zC++) {

            int xC = x + (zC + z) * bounds.width;

            System.arraycopy(tiles, xC, copy, zC * width, width);
        }

        return copy;
    }

    private final HashMap<PrimaryAssetInstance, HashSet<String>> tagged = new HashMap<>();

    public final HashSet<String> getTag(PrimaryAssetInstance element) {

        return new HashSet<>(tagged.get(element));
    }

    private final HashMap<String, HashSet<PrimaryAssetInstance>> matches = new HashMap<>();

    public final List<PrimaryAssetInstance> getAssets(String tag) {

        if (!matches.containsKey(tag)) {

            return List.of();
        }

        return new ArrayList<>(matches.get(tag));
    }

    @Override
    public final List<Rectangle> getMatches(String tag) {

        HashSet<PrimaryAssetInstance> match = matches.get(tag);
        if (match == null) {

            return List.of();
        }

        return match.stream().map(PrimaryAssetInstance::getTileBounds).toList();
    }

    private void updateTag(PrimaryAssetInstance asset, Set<String> tag) {

        if (tag == null) {

            return;
        }

        HashSet<String> tags = tagged.computeIfAbsent(asset, _ -> new HashSet<>());

        tag = new HashSet<>(tag);
        tag.removeAll(tags);
        tags.addAll(tag);

        for (String t : tag) {

            matches.computeIfAbsent(t, _ -> new HashSet<>()).add(asset);
        }
    }

    private void removeTag(PrimaryAssetInstance asset) {

        HashSet<String> tags = tagged.remove(asset);
        if (tags == null) {

            return;
        }

        for (String tag : tags) {

            matches.get(tag).remove(asset);
        }
    }

    private final HashSet<ElementInstance> elements = new HashSet<>();

    public final List<ElementInstance> getElements(Identifier identifier) {

        return elements.stream().filter(element -> element.getIdentifier() == identifier).toList();
    }

    private final HashSet<EntityInstance> entities = new HashSet<>();

    public final List<EntityInstance> getEntities(Identifier identifier) {

        return entities.stream().filter(element -> element.getIdentifier() == identifier).toList();
    }

    public final Stream<PrimaryAssetInstance> getAssets() {

        return Stream.concat(elements.stream(), entities.stream());
    }

    /**
     * Creates a new, blank instance of the <code>AreaGrid</code> class.
     * 
     * @param dimensions <code>Dimension</code>: The dimensions of the new
     *                   <code>AreaGrid</code> instance.
     * @param spawn      <code>GenerationPlacement</code>: The spawn point of the
     *                   new <code>AreaGrid</code> instance.
     */
    public AreaGrid(PrimaryAssetInstance asset, Set<String> tag, GenerationPlacement spawn) {

        this(asset.getTileBounds().getSize(), spawn);

        switch (asset) {

        case ElementInstance element -> addElement(element, tag);

        case EntityInstance entity -> addEntity(entity, tag);

        default -> {
        }
        }
    }

    /**
     * Creates a new, blank instance of the <code>AreaGrid</code> class.
     * 
     * @param dimensions <code>Dimension</code>: The dimensions of the new
     *                   <code>AreaGrid</code> instance.
     * @param spawn      <code>GenerationPlacement</code>: The spawn point of the
     *                   new <code>AreaGrid</code> instance.
     */
    public AreaGrid(Dimension dimensions, GenerationPlacement spawn) {

        bounds = new Rectangle(new Point(), dimensions);

        this.spawn = (spawn == null) ? GenerationPlacement.DEFAULT : spawn;

        tiles = new AreaTile[bounds.width * bounds.height];
        Arrays.setAll(tiles, _ -> new AreaTile());
    }

    public final void rotate(int direction) {

        if (direction == World.EAST) {

            return;
        }

        if (direction == World.NORTH || direction == World.SOUTH) {

            int prevWidth = bounds.width;
            bounds.width = bounds.height;
            bounds.height = prevWidth;
        }

        getAssets().forEach(asset -> asset.rotate(direction, bounds.width, bounds.height));

        // TODO Rotate terrain texture, height map, etc.
    }

    public final void addArea(AreaGrid area, int tileX, int tileZ) {

        for (ElementInstance element : area.elements) {

            element.translate(tileX, tileZ);
            addElement(element, area.getTag(element));
        }

        for (EntityInstance entity : area.entities) {

            entity.translate(tileX, tileZ);
            addEntity(entity, area.getTag(entity));
        }

        // TODO project terrain texture, height map, etc.
    }

    public final boolean canAddAt(AreaGrid area, int tileX, int tileZ) {

        if (!bounds.contains(area.bounds)) {

            return false;
        }

        return getAssets().allMatch(asset -> canAdd(asset.getTileBoundsTranslated(tileX, tileZ)));
    }

    public final void addElement(ElementInstance element, Set<String> tag) {

        updateTag(element, tag);

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

        removeTag(element);
    }

    public final void addEntity(EntityInstance entity, Set<String> tag) {

        updateTag(entity, tag);

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

        removeTag(entity);
    }

    public final void updateEntity(EntityInstance entity) {

    }

    public final boolean canAddAt(PrimaryAssetInstance asset, int tileX, int tileZ) {

        Rectangle assetBounds = asset.getTileBoundsAt(tileX, tileZ);
        return canAdd(assetBounds);
    }

    private boolean canAdd(Rectangle assetBounds) {

        if (!bounds.contains(assetBounds)) {

            return false;
        }

        // If there are fewer elements than tiles to check, iterate through the tiles.
        if (elements.size() < assetBounds.width * assetBounds.height) {

            for (ElementInstance intersectElement : elements) {

                if (!assetBounds.intersects(intersectElement.getTileBounds())) {

                    continue;
                }

                // TODO Operate on overlapped elements
            }
        } else {

            HashSet<ElementInstance> overlappedElements = new HashSet<>();

            for (int x = 0; x < assetBounds.width; x++) {

                for (int z = 0; z < assetBounds.height; z++) {

                    AreaTile tile = getTile(assetBounds.x + x, assetBounds.y + z);

                    for (ElementInstance intersectElement : tile.getElements()) {

                        if (overlappedElements.contains(intersectElement)) {

                            continue;
                        }
                        overlappedElements.add(intersectElement);

                        // TODO Operate on overlapped elements
                    }
                }
            }
        }

        for (EntityInstance intersectEntity : entities) {

            if (!assetBounds.intersects(intersectEntity.getTileBounds())) {

                continue;
            }

            // TODO Operate on overlapped entities
        }

        return true;
    }

    public final Point getSpawnPoint(PrimaryAssetInstance asset, DeterministicRandom random) {

        Rectangle assetBounds = asset.getTileBoundsAt(0, 0);
        GenerationShapeInstance spawnShape = spawn.generateShape(this, assetBounds.width, assetBounds.height, random);
        return spawnShape.getPoint(p -> canAddAt(asset, p.x, p.y), random);
    }

    public final Point getSpawnPoint(PrimaryAssetInstance target, double range, PrimaryAssetInstance asset,
            DeterministicRandom random) {

        ArrayList<Point> options = new ArrayList<>();

        int r = (int) Math.floor(range);
        Rectangle tileBounds = target.getTileBounds();

        for (int x = Math.max(tileBounds.x - r, 0); x < tileBounds.x + tileBounds.width + r && x < bounds.width; x++) {

            for (int y = Math.max(tileBounds.y - r, 0); y < tileBounds.y + tileBounds.height + r
                    && y < bounds.height; y++) {

                // Check for tile bounds and entity/element collisions.
                if (!canAddAt(asset, x, y)) {

                    continue;
                }

                options.add(new Point(x, y));
            }
        }

        if (options.isEmpty()) {

            return null;
        }

        return random.next(options);
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
        for (int i = Math.max(0, (int) Math.ceil(x - range)); i <= Math.floor(x + range) && x < bounds.width; i++) {

            double x_offset = i - range;

            double y_range = Math.sqrt(sqr_r - x_offset * x_offset);

            for (int j = Math.max(0, (int) Math.ceil(y - y_range)); j <= Math.floor(y + y_range)
                    && j < bounds.height; j++) {

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

        // Handle quadtrees (entities).
        // TODO: implement entity quadtree separation.
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
    @Override
    public final RenderBuffer getPolygons() {

        RenderBuffer buffer = new RenderBuffer();
        buffer.append(
                getAssets().filter(asset -> !asset.hasModelParent()).map(ModelAssetInstance::getPolygons).toList());

        return buffer;
    }
}
