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

package com.transcendruins.assets.catalogue.locations;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.ImageIcon;

import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.catalogue.AssetCatalogue.PinIcon;
import com.transcendruins.assets.interfaces.map.LocationRender;
import com.transcendruins.assets.layouts.LayoutContext;
import com.transcendruins.assets.layouts.LayoutInstance;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.rendering.renderbuffer.RenderBuffer;
import com.transcendruins.resources.styles.Style.IconSize;
import com.transcendruins.utilities.PropertyHolder;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.utilities.selection.WeightedRoll;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.Player;
import com.transcendruins.world.PlayerSpawn;
import com.transcendruins.world.World;

public final class GlobalLocationInstance extends PropertyHolder {

    /**
     * <code>World</code>: The world to use during generation.
     */
    private final World world;

    /**
     * Retrieves the world of this <code>GlobalLocationInstance</code> instance.
     * 
     * @return <code>World</code>: The <code>world</code> field of this
     *         <code>GlobalLocaitonInstance</code> instance.
     */
    public final World getWorld() {

        return world;
    }

    /**
     * <code>long</code>: The random ID of this <code>GlobalLocationInstance</code>
     * instance.
     */
    private final long randomId;

    /**
     * <code>DeterministicRandom</code>: The random number generator of this
     * <code>GlobalLocationInstance</code> instance.
     */
    private final DeterministicRandom random;

    /**
     * <code>TRScript</code>: The name of this <code>GlobalLocationInstance</code>
     * instance.
     */
    private final TRScript name;

    /**
     * <code>TRScript</code>: Retrieves the name of this
     * <code>GlobalLocationInstance</code> instance.
     * 
     * @return <code>TRScript</code>: The <code>name</code> field of this
     *         <code>GlobalLocationInstance</code> instance.
     */
    public final TRScript getName() {

        return name;
    }

    /**
     * <code>TRScript</code>: The description of this
     * <code>GlobalLocationInstance</code> instance.
     */
    private final TRScript description;

    /**
     * Retrieves the description of this <code>GlobalLocationInstance</code>
     * instance.
     * 
     * @return <code>TRScript</code>: The <code>description</code> field of this
     *         <code>GlobalLocationInstance</code> instance.
     */
    public final TRScript getDescription() {

        return description;
    }

    /**
     * <code>ImageIcon</code>: The icon of this <code>GlobalLocationInstance</code>
     * instance.
     */
    private final ImageIcon icon;

    /**
     * Retrieves the icon of this <code>GlobalLocationInstance</code> instance.
     * 
     * @return <code>ImageIcon</code>: The <code>icon</code> field of this
     *         <code>GlobalLocationInstance</code> instance.
     */
    public final ImageIcon getIcon() {

        return icon;
    }

    /**
     * <code>ImageIcon</code>: The pin of this <code>GlobalLocationInstance</code>
     * instance.
     */
    private final ImageIcon pin;

    private final IconSize pinSize;

    /**
     * <code>double</code>: The icon height of this
     * <code>GlobalLocationInstance</code> instance.
     */
    private final double iconHeight;

    /**
     * Retrieves the icon height of this <code>GlobalLocationInstance</code>
     * instance.
     * 
     * @return <code>double</code>: The <code>iconHeight</code> field of this
     *         <code>GlobalLocationInstance</code> instance.
     */
    public final double getIconHeight() {

        return iconHeight;
    }

    /**
     * <code>Point2D</code>: The coordinates of this
     * <code>GlobalLocationInstance</code> instance.
     */
    private final Point2D coordinates;

    /**
     * Retrieves the coordinates of this <code>GlobalLocationInstance</code>
     * instance.
     *
     * @return <code>Point2D</code>: The <code>coordinates</code> field of this
     *         <code>GlobalLocationInstance</code> instance.
     */
    public final Point2D getCoordinates() {

        return coordinates;
    }

    /**
     * <code>ImmutableMap&lt;String, WeightedRoll&lt;AssetPresets&gt;&gt;</code>:
     * The templates for the areas of this <code>GlobalLocationInstance</code>
     * instance. These are areas which may or have may not been instantiated yet,
     * depending on whether or not the <code>generate()</code> method has been
     * called since the previous application of attributes.
     */
    private final ImmutableMap<String, WeightedRoll<AssetPresets>> areaTemplates;

    /**
     * <code>HashMap&lt;String, AreaGrid&gt;</code>: The areas of this
     * <code>GlobalLocationInstance</code> instance.
     */
    private final HashMap<String, AreaGrid> areas = new HashMap<>();

    /**
     * Retrieves the keys of the areas of this <code>GlobalLocationInstance</code>
     * instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The keys of the
     *         <code>areas</code> field of this <code>GlobalLocationInstance</code>
     *         instance.
     */
    public final ImmutableList<String> getAreas() {

        return new ImmutableList<>(areas.keySet());
    }

    /**
     * Retrieves an area from this <code>GlobalLocationInstance</code> instance.
     * 
     * @param area <code>String</code>: The key of the area to retrieve.
     * @return <code>AreaGrid</code>: The location retrieved from the
     *         <code>areas</code> field of this <code>GlobalLocationInstance</code>
     *         instance.
     */
    public final AreaGrid getArea(String area) {

        ZonedDateTime now = ZonedDateTime.now();

        if (regenerateConditional(now)) {

            generated = true;
            active = false;
        }

        return areas.get(area);
    }

    /**
     * Retrieves the name of the area a specific player is in from this
     * <code>GlobalLocationInstance</code> instance.
     * 
     * @param player <code>Player</code>: The id of the player whose area name to
     *               retrieve.
     * @return <code>String</code>: The location name retrieved from the
     *         <code>players</code> field of this
     *         <code>GlobalLocationInstance</code> instance.
     */
    public final String getAreaName(Player player) {

        if (!players.containsKey(player)) {

            return null;
        }

        return players.get(player).area();
    }

    /**
     * Retrieves the area a specific player is in from this
     * <code>GlobalLocationInstance</code> instance.
     * 
     * @param player <code>Player</code>: The id of the player whose area to
     *               retrieve.
     * @return <code>AreaGrid</code>: The location retrieved from the
     *         <code>areas</code> field of this <code>GlobalLocationInstance</code>
     *         instance.
     */
    public final AreaGrid getArea(Player player) {

        if (!players.containsKey(player)) {

            return null;
        }

        ZonedDateTime now = ZonedDateTime.now();

        if (regenerateConditional(now)) {

            generated = true;
            active = false;
        }

        return areas.get(players.get(player).area());
    }

    /**
     * <code>String</code>: The key of the primary location of this
     * <code>GlobalLocationInstance</code> instance.
     */
    private final String primary;

    /**
     * Retrieves the key of the primary location of this
     * <code>GlobalLocationInstance</code> instance.
     * 
     * @return <code>String</code>: The <code>primary</code> field of this
     *         <code>GlobalLocationInstance</code> instance.
     */
    public final String getPrimary() {

        return primary;
    }

    /**
     * <code>ZonedDateTime</code>: The timestamp at which this
     * <code>GlobalLocationInstance</code> was created.
     */
    private final ZonedDateTime locationCreatedTimestamp;

    /**
     * <code>LocationReset</code>: The reset behavior of this
     * <code>GlobalLocationInstance</code> instance.
     */
    private final LocationReset reset;

    /**
     * <code>LocationDuration</code>: The duration of this
     * <code>GlobalLocationInstance</code> instance.
     */
    private final LocationDuration duration;

    /**
     * <code>LocationTriggerType</code>: The trigger type of this
     * <code>GlobalLocationInstance</code> instance.
     */
    private final LocationTriggerType triggerType;

    /**
     * Retrieves the trigger type of this <code>GlobalLocationInstance</code>
     * instance.
     * 
     * @return <code>LocationTriggerType</code>: The <code>triggerType</code> field
     *         of this <code>GlobalLocationInstance</code> instance.
     */
    public final LocationTriggerType getTriggerType() {

        return triggerType;
    }

    /**
     * <code>ZonedDateTime</code>: The timestamp of when this
     * <code>GlobalLocationInstance</code> instance was last reset.
     */
    private ZonedDateTime locationResetTimestamp;

    /**
     * <code>ZonedDateTime</code>: The timestamp of when the last player exited this
     * <code>GlobalLocationInstance</code> instance.
     */
    private ZonedDateTime prevEntranceTimestamp;

    /**
     * <code>HashMap&lt;Player, PlayerSpawn&gt;</code>: The set of players and their
     * spawn locations in this <code>GlobalLocationInstance</code> instance.
     */
    private final HashMap<Player, PlayerSpawn> players = new HashMap<>();

    /**
     * Retrieves whether or not this <code>GlobalLocationInstance</code> instance
     * has no players in it.
     * 
     * @return <code>boolean</code>: Whether or not the <code>players</code> field
     *         of this code>GlobalLocationInstance</code> instance is empty.
     */
    public final boolean isEmpty() {

        return players.isEmpty();
    }

    /**
     * <code>double</code>: The total duration of time spent inside the location
     * since the last reset, in minutes.
     */
    private double resetOffsetCounter = 0.0;

    /**
     * <code>boolean</code>: Whether or not this code>GlobalLocationInstance</code>
     * instance is generated. A <i>generated</i> location is one whose areas have
     * been generated but has not been activated, which will occur in the next
     * occupancy.
     */
    private boolean generated;

    /**
     * <code>boolean</code>: Whether or not this code>GlobalLocationInstance</code>
     * instance is activated. An <i>active</i> location is one which has had players
     * since being generated and is, if applicable, awaiting a reset timer.
     */
    private boolean active;

    /**
     * Determines if regeneration reset logic should be applied when a player enters
     * this <code>GlobalLocationInstance</code> instance based on if the location
     * has already been generated, is empty, and was already activated and is
     * awaiting a reset. If the location meets regeneration conditions, all areas
     * will be regenerated.
     * 
     * @param now <code>ZonedDateTime</code>: The current moment in time.
     * 
     * @return <code>boolean</code>: Whether or not regeneration logic should be
     *         applied.
     */
    private boolean regenerateConditional(ZonedDateTime now) {

        // If the location was already regenerated, return true.
        if (generated) {

            return true;
        }

        // If the location is not empty, it cannot be regenerated.
        if (!isEmpty()) {

            return false;
        }

        // If the location is active and has not reached the reset duration, do not
        // regenerate.
        if (active && (reset.getDuration() == -1 || getMinutesUntilReset(now) > 0)) {

            return false;
        }

        generate();
        return true;
    }

    /**
     * Adds a player to this <code>GlobalLocationInstance</code> instance.
     * 
     * @param player <code>Player</code>: The player to add.
     * @param spawn  <code>PlayerSpawn</code>: The spawm location to add at.
     * 
     * @return <code>boolean</code>: Whether or not the player was successfully
     *         added.
     */
    public final boolean add(Player player, PlayerSpawn spawn) {

        // If there are no players in the location, check if it needs to be generated.
        if (isEmpty()) {

            ZonedDateTime now = ZonedDateTime.now();

            if (regenerateConditional(now)) {

                active = true;

                locationResetTimestamp = now;
                resetOffsetCounter = 0;
            }

            prevEntranceTimestamp = now;
            generated = false;
        }

        // If the area cannot be assigned, use the primary area.
        if (spawn == null) {

            Point spawnPoint = getArea(primary).getSpawnPoint(player.getEntity(), player.getRandom());
            if (spawnPoint == null) {

                return false;
            }
            spawn = new PlayerSpawn(primary, spawnPoint.x, spawnPoint.y);
        }

        players.put(player, spawn);

        return true;
    }

    /**
     * Removes a player from this <code>GlobalLocationInstance</code> instance.
     * 
     * @param player <code>Player</code>: The player to remove.
     */
    public final void remove(Player player) {

        // If there is not a player in the location, the entrance timestamp will be
        // null.
        if (prevEntranceTimestamp == null) {

            return;
        }

        players.remove(player);
        if (players.isEmpty()) {

            ZonedDateTime now = ZonedDateTime.now();
            resetOffsetCounter += minutesBetween(prevEntranceTimestamp, now);

            prevEntranceTimestamp = null;
        }

        exit(player);
    }

    /**
     * Enters a player from the global map into this
     * <code>GlobalLocationInstance</code> instance.
     * 
     * @param player <code>Player</code>: The player to enter.
     * @return <code>boolean</code>: Whether or not the player was successfully
     *         entered.
     */
    public final boolean enter(Player player) {

        PlayerSpawn spawn = players.get(player);
        if (spawn == null) {

            return false;
        }

        AreaGrid area = getArea(spawn.area());
        if (area == null) {

            return false;
        }

        EntityInstance playerEntity = player.getEntity();

        int spawnX = spawn.x();
        int spawnZ = spawn.z();

        playerEntity.setPosition(spawnX, spawnZ);
        area.addEntity(playerEntity, null);

        return true;
    }

    /**
     * Exits a player from this <code>GlobalLocationInstance</code> instance into
     * the global map.
     * 
     * @param player <code>Player</code>: The player to exit.
     */
    public final void exit(Player player) {

        AreaGrid area = getArea(player);
        if (area == null) {

            return;
        }

        EntityInstance playerEntity = player.getEntity();
        area.removeEntity(playerEntity);
    }

    /**
     * Retrieves the polygons of a player from this
     * <code>GlobalLocationInstance</code> instance.
     * 
     * @param player <code>Player</code>: The player whose area's polygons should be
     *               retrieved.
     * @return <code>RenderBuffer</code>: The retrieved polygons, packaged into a
     *         buffer.
     */
    public final RenderBuffer getPolygons(Player player) {

        if (!players.containsKey(player)) {

            return new RenderBuffer();
        }

        return getArea(player).getPolygons();
    }

    /**
     * Determines the length in minutes until the reset countdown will end.
     * 
     * @param now <code>ZonedDateTime</code>: The current moment in time.
     * @return <code>double</code>: The duration in minutes.
     */
    public final double getMinutesUntilReset(ZonedDateTime now) {

        double minutes = minutesBetween(locationResetTimestamp, now);
        return reset.getDuration() - minutes + resetOffsetCounter;
    }

    /**
     * Outputs the reset counter as a <code>String</code>, or <code>null</code> if
     * the location does not have a reset countdown, if the countdown has finished,
     * or if the player is in the location.
     * 
     * @param now <code>ZonedDateTime</code>: The current moment in time.
     * @return <code>String</code>: The minutes formatted as H:MM:SS or MM:SS, or
     *         <code>null</code> if the counter should not be displayed.
     */
    public final String getResetCounter(ZonedDateTime now) {

        if (!reset.getDisplayCountdownTimer() || reset.getDuration() == -1 || locationResetTimestamp == null
                || locationResetTimestamp == null || resetOffsetCounter == 0) {

            return null;
        }

        if (!players.isEmpty()) {

            double minutes = reset.getDuration() + resetOffsetCounter + minutesBetween(prevEntranceTimestamp, now)
                    - minutesBetween(locationResetTimestamp, now);

            return formatMinutes(minutes);
        }

        double remaining = getMinutesUntilReset(now);
        if (remaining < 0) {

            return null;
        }

        return formatMinutes(remaining);
    }

    /**
     * Determines the length in minutes until the location will end.
     * 
     * @param now <code>ZonedDateTime</code>: The current moment in time.
     * @return <code>double</code>: The duration in minutes.
     */
    public final double getMinutesUntilEnd(ZonedDateTime now) {

        ZonedDateTime start = duration.getStartTimestamp();
        if (start == null) {

            start = locationCreatedTimestamp;
        }

        double minutes = minutesBetween(start, now);

        return duration.getDuration() - minutes;
    }

    /**
     * Outputs the end counter as a <code>String</code>, or <code>null</code> if the
     * location does not have a end countdown or if the countdown has finished.
     * 
     * @param now <code>ZonedDateTime</code>: The current moment in time.
     * @return <code>String</code>: The minutes formatted as H:MM:SS or MM:SS.
     */
    public final String getEndCounter(ZonedDateTime now) {

        if (!duration.getDisplayCountdownTimer() || duration.getDuration() == -1) {

            return null;
        }

        double remaining = getMinutesUntilEnd(now);
        return formatMinutes(remaining);
    }

    /**
     * Creates a new instance of the <code>GlobalLocationInstance</code> class.
     * 
     * @param schema <code>GlobalLocationSchema</code>: The schema to apply.
     * @param world  <code>World</code>: The world to use during generation.
     */
    public GlobalLocationInstance(GlobalLocationSchema schema, World world) {

        this.world = world;
        setParent(world);

        randomId = world.getRandom().next();
        random = new DeterministicRandom(randomId);

        name = schema.getName();
        description = schema.getDescription();
        icon = world.getTexture(schema.getIcon(), randomId);

        PinIcon pinIcon = world.getPinIcon(schema.getPin());
        if (pinIcon != null) {

            String pinPath = pinIcon.icon();

            pin = world.getTexture(pinPath, randomId);
            pinSize = pinIcon.size();
        } else {

            pin = null;
            pinSize = null;
        }
        iconHeight = schema.getIconHeight();

        coordinates = schema.getCoordinates().get(randomId);

        reset = schema.getReset();
        duration = schema.getDuration();
        triggerType = schema.getTriggerType();

        areaTemplates = schema.getAreas();
        primary = schema.getPrimary();

        locationCreatedTimestamp = ZonedDateTime.now();
    }

    /**
     * Generates the contents of this <code>GlobalLocationInstance</code> instance.
     * This automatically resets each area in this
     * <code>GlobalLocationInstance</code> instance in a deterministic pattern.
     */
    public final void generate() {

        areas.clear();

        for (Map.Entry<String, WeightedRoll<AssetPresets>> areaEntry : areaTemplates.entrySet()) {

            String areaName = areaEntry.getKey();
            WeightedRoll<AssetPresets> presetsRoll = areaEntry.getValue();
            AssetPresets areaPresets = presetsRoll.get(random.next());

            LayoutContext areaContext = new LayoutContext(areaPresets, this);
            LayoutInstance areaLayout = areaContext.instantiate();

            AreaGrid area = areaLayout.generate();
            areas.put(areaName, area);
        }
    }

    /**
     * Determine whether or not this code>GlobalLocationInstance</code> instance has
     * expired.
     * 
     * @param now <code>ZonedDateTime</code>: The current moment in time.
     * 
     * @return <code>boolean</code>: Whether or not the location has expired.
     */
    public final boolean expired(ZonedDateTime now) {

        // The location cannot expire while occupied.
        if (!isEmpty()) {

            return false;
        }

        // If the location automatically ends when the last player leaves, expire.
        if (resetOffsetCounter > 0 && duration.getEndOnExit()) {

            return true;
        }

        return duration.getDuration() != -1 && getMinutesUntilEnd(now) <= 0;
    }

    /**
     * Updates all areas in this <code>GlobalLocationInstance</code> instance.
     * 
     * @param time <code>double</code>: The current time in seconds.
     */
    public final void update(double time) {

        if (generated || active) {

            areas.values().stream().filter(Objects::nonNull).forEach(area -> area.update(time));
        }
    }

    /**
     * Formats the time (in minutes) between 2 date times.
     * 
     * @param start <code>ZonedDateTime</code>: The start time of the duration.
     * @param end   <code>ZonedDateTime</code>: The end time of the duration.
     * @return <code>double</code>: The absolute value of the duration in minutes.
     */
    private double minutesBetween(ZonedDateTime start, ZonedDateTime end) {

        return Duration.between(start, end).abs().toMillis() / 60000.0;
    }

    /**
     * Formats a length of time (in minutes) to a string.
     * 
     * @param minutes <code>double</code>: The length in minutes to format.
     * @return <code>String</code>: The resulting format as H:MM:SS or MM:SS, or
     *         <code>null</code> if the length is negative.
     */
    private String formatMinutes(double minutes) {

        if (minutes < 0.0) {

            return null;
        }

        int hoursTemp = (int) (minutes / 60);
        minutes %= 60.0;

        int minutesTemp = (int) minutes;
        minutes %= 1.0;

        int secondsTemp = (int) (minutes * 60);

        if (hoursTemp > 0) {

            return "%d:%02d:%02d".formatted(hoursTemp, minutesTemp, secondsTemp);
        } else {

            return "%02d:%02d".formatted(minutesTemp, secondsTemp);
        }
    }

    /**
     * Retrieves the global map rendering information of this
     * <code>GlobalLocationInstance</code> instance.
     * 
     * @return <code>LocationRender</code>: The location icons and text to display
     *         on the global map overlay.
     */
    public final LocationRender getRender() {

        return new LocationRender(name, description, icon, pin, pinSize, coordinates.getX(), coordinates.getY(),
                iconHeight);
    }
}
