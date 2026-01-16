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

import com.jme3.math.Vector3f;
import com.transcendruins.assets.layouts.placement.GenerationPlacement;
import com.transcendruins.assets.layouts.placement.GenerationShapeInstance;
import com.transcendruins.assets.layouts.placement.PlacementArea;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.assets.modelassets.attack.AttackInstance;
import com.transcendruins.assets.modelassets.elements.ElementInstance;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionInstance;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.rendering.renderbuffer.RenderBuffer;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.world.calls.AttackCall;
import com.transcendruins.world.calls.InteractionCall;

/**
 * <code>AreaGrid</code>: A class representing a grid of tiles which a location
 * area is entirely or partially composed of.
 */
public final class AreaGrid implements PlacementArea, RenderInstance {

    /**
     * <code>Rectangle</code>: The rectangular bounds of this <code>AreaGrid</code>
     * instance, starting at the point <code>(0, 0)</code>.
     */
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

    /**
     * <code>GenerationPlacement</code>: The default spawn placement of this
     * <code>AreaGrid</code> instance.
     */
    private final GenerationPlacement spawn;

    /**
     * <code>AreaTile[]</code>: The grid of tiles in this <code>AreaGrid</code>
     * instance.
     */
    private final AreaTile[] tiles;

    /**
     * Retrieves a tile from this <code>AreaGrid</code> instance.
     * 
     * @param x <code>int</code>: The X coordinate of the tile as an integer from
     *          <code>0</code> to the width of this <code>AreaGrid</code> instance,
     *          exclusive.
     * @param z The Z coordinate as an integer from <code>0</code> to the length of
     *          this <code>AreaGrid</code> instance, exclusive.
     * @return <code>AreaTile</code>: The retrieved <code>AreaTile</code> instance,
     *         or <code>null</code> if the tile could not be retrieved.
     */
    public final AreaTile getTile(int x, int z) {

        if (!bounds.contains(x, z)) {

            return null;
        }

        int i = x + z * bounds.width;

        return tiles[i];
    }

    /**
     * Retrieves a region of tiles from this <code>AreaGrid</code> instance.
     * 
     * @param x      <code>int</code>: The X coordinate of the tile to start at.
     * @param z      <code>int</code>: The Z coordinate of the tile to start at.
     * @param width  <code>int</code>: The width of the region to retrieve.
     * @param length <code>int</code>: The length of the region to retrieve.
     * @return <code>AreaTile[]</code>: An array of length
     *         <code>width * length</code> of the retrieved tiles.
     */
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

    /**
     * <code>HashMap&lt;PrimaryAssetInstance, HashSet&lt;String&gt;&gt;</code>: The
     * set of all tagged assets mapped to their tags.
     */
    private final HashMap<PrimaryAssetInstance, HashSet<String>> tagged = new HashMap<>();

    /**
     * Retrieves the tags of an asset.
     * 
     * @param asset <code>PrimaryAssetInstance</code>: The asset whose tags to
     *              retrieve.
     * @return <code>HashSet&lt;String&gt;</code>: The set of tags previously
     *         attached to the asset. This will not reflect changes to the asset's
     *         tags.
     */
    public final HashSet<String> getTag(PrimaryAssetInstance asset) {

        return tagged.containsKey(asset) ? new HashSet<>(tagged.get(asset)) : null;
    }

    /**
     * <code>HashMap&lt;String, HashSet&lt;PrimaryAssetInstance&gt;&gt;</code>: The
     * map of all tags to the assets they match.
     */
    private final HashMap<String, HashSet<PrimaryAssetInstance>> matches = new HashMap<>();

    /**
     * Retrieves the assets matched by a tag.
     * 
     * @param tag <code>String</code>: The tag to match for.
     * @return <code>List&lt;PrimaryAssetInstance&gt;</code>: The retrieved assets.
     */
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

    /**
     * Updates an asset with an additional set of tags.
     * 
     * @param asset <code>PrimaryAssetInstance</code>: The asset to update.
     * @param tag   <code>Set&lt;String&gt;</code>: The set of tags to update.
     */
    private void updateTag(PrimaryAssetInstance asset, Set<String> tag) {

        if (tag == null || tag.isEmpty()) {

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

    /**
     * Removes all tags from an asset.
     * 
     * @param asset <code>PrimaryAssetInstance</code>: The asset whose tags to
     *              remove.
     */
    private void removeTag(PrimaryAssetInstance asset) {

        HashSet<String> tags = tagged.remove(asset);
        if (tags == null) {

            return;
        }

        for (String tag : tags) {

            matches.get(tag).remove(asset);
        }
    }

    /**
     * <code>ArrayList&lt;ElementInstance&gt;</code>: The set of all elements in
     * this <code>AreaGrid</code> instance.
     */
    private final ArrayList<ElementInstance> elements = new ArrayList<>();

    /**
     * Retrieves the set of all elements in this <code>AreaGrid</code> instance.
     * 
     * @return <code>ArrayList&lt;ElementInstance&gt;</code>: The retrieved
     *         elements.
     */
    public final ArrayList<ElementInstance> getElements() {

        return new ArrayList<>(elements);
    }

    /**
     * Retrieves the set of all elements in this <code>AreaGrid</code> instance with
     * a specific identifier.
     * 
     * @param identifier <code>Identifier</code>: The identifier to retrieve.
     * @return <code>ArrayList&lt;ElementInstance&gt;</code>: The retrieved
     *         elements.
     */
    public final List<ElementInstance> getElements(Identifier identifier) {

        return elements.stream().filter(element -> element.getIdentifier() == identifier).toList();
    }

    /**
     * <code>ArrayList&lt;EntityInstance&gt;</code>: The set of all entities in this
     * <code>AreaGrid</code> instance.
     */
    private final ArrayList<EntityInstance> entities = new ArrayList<>();

    /**
     * Retrieves the set of all entities in this <code>AreaGrid</code> instance.
     * 
     * @return <code>ArrayList&lt;EntityInstance&gt;</code>: The retrieved entities.
     */
    public final ArrayList<EntityInstance> getEntities() {

        return new ArrayList<>(entities);
    }

    /**
     * Retrieves the set of all entities in this <code>AreaGrid</code> instance with
     * a specific identifier.
     * 
     * @param identifier <code>Identifier</code>: The identifier to retrieve.
     * @return <code>ArrayList&lt;EntityInstance&gt;</code>: The retrieved entities.
     */
    public final List<EntityInstance> getEntities(Identifier identifier) {

        return entities.stream().filter(element -> element.getIdentifier() == identifier).toList();
    }

    /**
     * Retrieves a stream of all assets in this <code>AreaGrid</code> instance.
     * 
     * @return <code>Stream&lt;PrimaryAssetInstance&gt;</code>: The concatenated
     *         <code>elements</code> and <code>entities</code> streams.
     */
    public final Stream<PrimaryAssetInstance> getAssets() {

        return Stream.concat(elements.stream(), entities.stream());
    }

    /**
     * Creates a new instance of the <code>AreaGrid</code> class with a single
     * asset.
     * 
     * @param asset <code>PrimaryAssetInstance</code>: The asset to add.
     * @param tag   <code>Set&lt;String&gt;</code>: The tags to attach to the asset.
     * @param spawn <code>GenerationPlacement</code>: The spawn position of the new
     *              <code>AreaGrid</code> instance.
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

    /**
     * Rotates the contents of this <code>AreaGrid</code> instance by 90°, 180°, or
     * 270°.
     * 
     * @param direction <code>int</code>: The cardinal direction to rotate
     *                  counterclockwise in, based on the unit circle.
     *                  <code>EAST</code> corresponds to 0°, <code>NORTH</code>
     *                  corresponds to 90°, <code>WEST</code> corresponds to 180°,
     *                  and <code>SOUTH</code> corresponds to 270°.
     */
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

    /**
     * Appends another area onto this <code>AreaGrid</code> instance at a specific
     * point.
     * 
     * @param area <code>AreaGrid</code>: The area to add.
     * @param x    <code>int</code>: The X coordinate to add at.
     * @param z    <code>int</code>: The Z coordinate to add at.
     */
    public final void addArea(AreaGrid area, int x, int z) {

        for (ElementInstance element : area.elements) {

            element.translate(x, z);
            addElement(element, area.getTag(element));
        }

        for (EntityInstance entity : area.entities) {

            entity.translate(x, z);
            addEntity(entity, area.getTag(entity));
        }

        // TODO project terrain texture, height map, etc.
    }

    /**
     * Determines if another area can be added onto this <code>AreaGrid</code>
     * instance at a specific point.
     * 
     * @param area <code>AreaGrid</code>: The area to check.
     * @param x    <code>int</code>: The X coordinate to check.
     * @param z    <code>int</code>: The Z coordinate to check.
     * @return <code>boolean</code>: Whether or not the area can be added.
     */
    public final boolean canAddAt(AreaGrid area, int x, int z) {

        if (!bounds.contains(area.bounds)) {

            return false;
        }

        return getAssets().allMatch(asset -> canAdd(asset.getTileBoundsTranslated(x, z)));
    }

    /**
     * Appends an element onto this <code>AreaGrid</code> instance.
     * 
     * @param element <code>ElementInstance</code>: The element to add.
     * @param tags    <code>Set&lt;String&gt;</code>: The tags to attach to the
     *                element
     */
    public final void addElement(ElementInstance element, Set<String> tag) {

        updateTag(element, tag);

        if (elements.contains(element)) {

            return;
        }

        elements.add(element);
        element.updateArea(this);
    }

    /**
     * Removes an element from this <code>AreaGrid</code> instance.
     * 
     * @param element <code>ElementInstance</code>: The element to remove.
     */
    public final void removeElement(ElementInstance element) {

        if (!elements.contains(element)) {

            return;
        }

        elements.remove(element);
        element.clearTiles();

        removeTag(element);
    }

    /**
     * Appends an entity onto this <code>AreaGrid</code> instance.
     * 
     * @param entity <code>EntityInstance</code>: The entity to add.
     * @param tags   <code>Set&lt;String&gt;</code>: The tags to attach to the
     *               entity
     */
    public final void addEntity(EntityInstance entity, Set<String> tag) {

        updateTag(entity, tag);

        if (entities.contains(entity)) {

            return;
        }

        entities.add(entity);
        entity.updateArea(this);
    }

    /**
     * Removes an entity from this <code>AreaGrid</code> instance.
     * 
     * @param entity <code>EntityInstance</code>: The entity to remove.
     */
    public final void removeEntity(EntityInstance entity) {

        if (!entities.contains(entity)) {

            return;
        }

        entities.remove(entity);

        removeTag(entity);
    }

    public final void updateEntity(EntityInstance entity) {

        // TODO update entity bounds
    }

    /**
     * Determines if an asset can be added to this <code>AreaGrid</code> instance at
     * a specific position.
     * 
     * @param asset <code>PrimaryAssetInstance</code>: The asset to check.
     * @param x     <code>int</code>: The X coordinate to check.
     * @param x     <code>int</code>: The Z coordinate to check.
     * @return <code>boolean</code>: Whether or not the asset can be added.
     */
    public final boolean canAddAt(PrimaryAssetInstance asset, int x, int z) {

        Rectangle assetBounds = asset.getTileBoundsAt(x, z);
        return canAdd(assetBounds);
    }

    /**
     * Determines if a rectangular region in this <code>AreaGrid</code> instance can
     * be added to.
     * 
     * @param assetBounds <code>Rectangle</code>: The region to check.
     * @return <code>boolean</code>: Whether or not the region can be added.
     */
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
                return false;
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
                        return false;
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

    /**
     * Calculates the spawn position of an asset using the default spawn location of
     * this <code>AreaGrid</code> instance.
     * 
     * @param asset  <code>PrimaryAssetInstance</code>: The asset whose spawn
     *               location to calculate.
     * @param random <code>DeterministicRandom</code>: The random number generator
     *               to calculate with.
     * 
     * @preturn <code>Point</code>: The calculated spawn position.
     */
    public final Point getSpawnPoint(PrimaryAssetInstance asset, DeterministicRandom random) {

        Rectangle assetBounds = asset.getTileBoundsAt(0, 0);
        GenerationShapeInstance spawnShape = spawn.generateShape(this, assetBounds.width, assetBounds.height, random);

        return spawnShape.getPoint(p -> canAddAt(asset, p.x, p.y), random);
    }

    /**
     * Calculates the spawn position of an asset around another asset.
     * 
     * @param target <code>PrimaryAssetInstance</code>: The asset whose position to
     *               spawn around.
     * @param range  <code>double</code>: The range around the target asset to
     *               spawn.
     * @param asset  <code>PrimaryAssetInstance</code>: The asset whose spawn
     *               location to calculate.
     * @param random <code>DeterministicRandom</code>: The random number generator
     *               to calculate with.
     * 
     * @preturn <code>Point</code>: The calculated spawn position.
     */
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

    /**
     * Retrieves the nearest interaction target of a player.
     * 
     * @param player <code>Player</code>: The player whose interaction to check.
     * @param time   <code>long</code>: The current timestamp in milliseconds.
     * 
     * @return <code>InteractionCall</code>: The interaction, player, and target
     *         asset bundled together.
     */
    public final InteractionCall getNearestInteraction(Player player, long time) {

        HashSet<PrimaryAssetInstance> assets = new HashSet<>();

        EntityInstance playerEntity = player.getEntity();
        Vector3f position = playerEntity.getPosition();
        double x = position.getX() / World.UNIT_TILE;
        double y = position.getY() / World.UNIT_TILE;

        double range = player.getInteractionDetectionRange();
        if (range < 0) {

            return null;
        }

        double sqr_r = range * range;

        // Handle tiles (elements).
        for (int i = Math.max(0, (int) Math.ceil(x - range)); i <= Math.floor(x + range) && i < bounds.width; i++) {

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

        sqr_r *= World.UNIT_TILE * World.UNIT_TILE;

        for (PrimaryAssetInstance assetOption : assets) {

            for (AssetInteractionInstance interactionOption : assetOption.getInteraction()) {

                if (!interactionOption.passes(assetOption)) {

                    continue;
                }

                Vector3f displacement = interactionOption.getPosition(assetOption.getRotation(),
                        assetOption.getPosition().subtract(position));
                double newDist_sqr = displacement.dot(displacement);

                if (newDist_sqr <= sqr_r && newDist_sqr < distance_sqr) {

                    distance_sqr = newDist_sqr;
                    interaction = new InteractionCall(interactionOption, assetOption);
                }
            }
        }

        return interaction;
    }

    /**
     * Retrieves the nearest attack target of an entity.
     * 
     * @param entity <code>EntityInstance</code>: The entity whose attack to check.
     * 
     * @return <code>AttackCall</code>: The attacm, entity, and target bundled
     *         together.
     */
    public final AttackCall getNearestTarget(EntityInstance entity) {

        double range = entity.getDetectionRange();

        AttackInstance attackData = entity.getAttack();
        if (attackData == null) {

            return null;
        }

        Vector3f position = entity.getPosition();

        double sqr_r = range * range * World.UNIT_TILE * World.UNIT_TILE;

        HashSet<EntityInstance> assets = new HashSet<>();

        // Handle quadtrees (entities).
        // TODO: implement entity quadtree separation.
        assets.addAll(entities);

        assets.remove(entity);

        double distance_sqr = Double.POSITIVE_INFINITY;
        AttackCall attack = null;

        for (EntityInstance assetOption : assets) {

            Vector3f displacement = assetOption.getPosition().subtract(position);
            double newDist_sqr = displacement.dot(displacement);

            if (newDist_sqr <= sqr_r && newDist_sqr < distance_sqr) {

                distance_sqr = newDist_sqr;
                attack = new AttackCall(attackData, assetOption, entity.getMainhandItem());
            }
        }

        return attack;
    }

    /**
     * Updates all assets in this <code>AreaGrid</code> instance.
     * 
     * @param time <code>double</code>: The current time in seconds.
     */
    public final void update(double time) {

        elements.forEach(element -> element.update(time));
        entities.forEach(entity -> {

            entity.update(time);

            // If the entity does not have an attack locked, check for a new option.
            if (!entity.getAttackLocked()) {

                entity.setAttack(getNearestTarget(entity));
            }
        });
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
