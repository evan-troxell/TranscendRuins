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

package com.transcendruins.assets.catalogue.locations;

import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import com.transcendruins.PropertyHolder;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.catalogue.AssetCatalogue.PinIcon;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.assets.interfaces.map.LocationRender;
import com.transcendruins.assets.layouts.LayoutContext;
import com.transcendruins.assets.layouts.LayoutInstance;
import com.transcendruins.resources.styles.Style.TextureSize;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.Player;
import com.transcendruins.world.World;

public final class GlobalLocationInstance extends PropertyHolder {

    private final World world;

    private final long randomId;

    private final DeterministicRandom random;

    private final String name;

    /**
     * <code>String</code>: The description of this
     * <code>GlobalLocationInstance</code> instance.
     * 
     * @return <code>String</code>: The <code>name</code> field of this
     *         <code>GlobalLocationInstance</code> instance.
     */
    public final String getName() {

        return name;
    }

    /**
     * <code>String</code>: The description of this
     * <code>GlobalLocationInstance</code> instance.
     */
    private final String description;

    /**
     * Retrieves the description of this <code>GlobalLocationInstance</code>
     * instance.
     * 
     * @return <code>String</code>: The <code>description</code> field of this
     *         <code>GlobalLocationInstance</code> instance.
     */
    public final String getDescription() {

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

    private final TextureSize pinSize;

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
     * The templates for the areas of this <code>LocationInstance</code> instance.
     * These are areas which may or have may not been instantiated yet, depending on
     * whether or not the <code>generate()</code> method has been called since the
     * previous application of attributes.
     */
    private final ImmutableMap<String, WeightedRoll<AssetPresets>> areaTemplates;

    /**
     * <code>HashMap&lt;String, AreaGrid&gt;</code>: The areas of this
     * <code>LocationInstance</code> instance.
     */
    private final HashMap<String, AreaGrid> areas = new HashMap<>();

    /**
     * Retrieves the keys of the areas of this <code>LocationInstance</code>
     * instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The keys of the
     *         <code>areas</code> field of this <code>LocationInstance</code>
     *         instance.
     */
    public final ImmutableList<String> getAreas() {

        return new ImmutableList<>(areas.keySet());
    }

    /**
     * Retrieves an area from this <code>LocationInstance</code> instance.
     * 
     * @param area <code>String</code>: The key of the area to retrieve.
     * @return <code>AreaGrid</code>: The location retrieved from the
     *         <code>areas</code> field of this <code>LocationInstance</code>
     *         instance.
     */
    public final AreaGrid getArea(String area) {

        return areas.get(area);
    }

    /**
     * Retrieves the area a specific player is in from this
     * <code>LocationInstance</code> instance.
     * 
     * @param player <code>Player</code>: The id of the player whose area to
     *               retrieve.
     * @return <code>AreaGrid</code>: The location retrieved from the
     *         <code>areas</code> field of this <code>LocationInstance</code>
     *         instance.
     */
    public final AreaGrid getArea(Player player) {

        if (!players.containsKey(player)) {

            return null;
        }

        return areas.get(players.get(player));
    }

    /**
     * <code>String</code>: The key of the primary location of this
     * <code>LocationInstance</code> instance.
     */
    private final String primary;

    /**
     * Retrieves the key of the primary location of this
     * <code>LocationInstance</code> instance.
     * 
     * @return <code>String</code>: The <code>primary</code> field of this
     *         <code>LocationInstance</code> instance.
     */
    public final String getPrimary() {

        return primary;
    }

    /**
     * <code>ZonedDateTime</code>: The timestamp at which this
     * <code>LocationInstance</code> was created.
     */
    private final ZonedDateTime locationCreatedTimestamp;

    /**
     * <code>LocationReset</code>: The reset behavior of this
     * <code>LocationInstance</code> instance.
     */
    private final LocationReset reset;

    private final LocationDuration duration;

    private final LocationTriggerType triggerType;

    public final LocationTriggerType getTriggerType() {

        return triggerType;
    }

    private ZonedDateTime locationResetTimestamp;

    /**
     * <code>ZonedDateTime</code>: The timestamp of when the last player exited this
     * <code>LocationInstance</code> instance.
     */
    private ZonedDateTime prevEntranceTimestamp;

    private final HashMap<Player, String> players = new HashMap<>();

    public final boolean isEmpty() {

        return players.isEmpty();
    }

    /**
     * <code>double</code>: The total duration of time spent inside the location
     * since the last reset, in minutes.
     */
    private double resetOffsetCounter = 0.0;

    private boolean active;

    public final void add(Player player, String area) {

        // If the area cannot be assigned, use the primary area.
        if (area == null || !areas.containsKey(area)) {

            area = primary;
        }

        // If there are no players in the location, check if it needs to be generated.
        if (isEmpty()) {

            ZonedDateTime now = ZonedDateTime.now();

            // If the location is active, the location should reset, and the active period
            // is over, regenerate.
            if (active) {

                active = reset.getDuration() == -1 || getMinutesUntilReset(now) > 0;
            }

            // If the location is not already active, generate.
            if (!active) {

                active = true;

                locationResetTimestamp = now;
                resetOffsetCounter = 0;

                generate();
            }

            prevEntranceTimestamp = now;
        }
        players.put(player, area);
    }

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
    }

    public final void enter(Player player) {

        AreaGrid area = getArea(player);
        // TODO update the area with the player entity
    }

    public final void exit(Player player) {

        AreaGrid area = getArea(player);
        // TODO update the area with the player entity
    }

    public final Set<PolyGroup> getPolygons(Player player) {

        if (!players.containsKey(player)) {

            return Set.of();
        }

        return areas.get(players.get(player)).getPolygons();
    }

    /**
     * Determines the length in minutes until the reset countdown will end.
     * 
     * @param now <code>ZonedDateTime</code>: The current timestamp.
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
     * @param now <code>ZonedDateTime</code>: The current timestamp.
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
     * @param now <code>ZonedDateTime</code>: The current timestamp.
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
     * @param now <code>ZonedDateTime</code>: The current timestamp.
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
     * Creates a new instance of the <code>LocationInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>LocationInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public GlobalLocationInstance(GlobalLocationSchema schema, World world) {

        this.world = world;
        randomId = world.nextRandom();
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
     * Generates the contents of this <code>LocationInstance</code> instance. This
     * automatically resets each area in this <code>LocationInstance</code> instance
     * in a deterministic pattern.
     */
    public final void generate() {

        areas.clear();

        for (Map.Entry<String, WeightedRoll<AssetPresets>> areaEntry : areaTemplates.entrySet()) {

            String areaName = areaEntry.getKey();
            WeightedRoll<AssetPresets> presetsRoll = areaEntry.getValue();
            AssetPresets areaPresets = presetsRoll.get(random.next());

            LayoutContext areaContext = new LayoutContext(areaPresets, world, this);
            LayoutInstance areaLayout = areaContext.instantiate();

            AreaGrid area = areaLayout.generate();
            areas.put(areaName, area);
        }
    }

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

    public final void update(double time) {

        if (active) {

            // TODO add area updates
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

    public final LocationRender getRender() {

        return new LocationRender(name, description, icon, pin, pinSize, coordinates.getX(), coordinates.getY(),
                iconHeight);
    }
}
