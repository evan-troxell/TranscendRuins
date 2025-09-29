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

package com.transcendruins.assets.locations;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import static com.transcendruins.assets.AssetType.LAYOUT;
import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.assets.layouts.LayoutContext;
import com.transcendruins.assets.layouts.LayoutInstance;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.world.AreaGrid;

public final class LocationInstance extends AssetInstance {

    /**
     * <code>String</code>: The name of this <code>LocationInstance</code> instance.
     */
    private String name;

    /**
     * Retrieves the name of this <code>LocationInstance</code> instance.
     * 
     * @return <code>String</code>: The <code>name</code> field of this
     *         <code>LocationInstance</code> instance.
     */
    public String getName() {

        return name;
    }

    /**
     * <code>String</code>: The description of this <code>LocationInstance</code>
     * instance.
     */
    private String description;

    /**
     * Retrieves the description of this <code>LocationInstance</code> instance.
     * 
     * @return <code>String</code>: The <code>description</code> field of this
     *         <code>LocationInstance</code> instance.
     */
    public final String getDescription() {

        return description;
    }

    /**
     * <code>String</code>: The path to the icon of this
     * <code>LocationInstance</code> instance.
     */
    private String iconPath;

    /**
     * <code>ImageIcon</code>: The icon of this <code>LocationInstance</code>
     * instance.
     */
    private ImageIcon icon;

    /**
     * Retrieves the icon of this <code>LocationInstance</code> instance.
     * 
     * @return <code>ImageIcon</code>: The <code>icon</code> field of this
     *         <code>LocationInstance</code> instance.
     */
    public final ImageIcon getIcon() {

        return icon;
    }

    /**
     * <code>ImmutableMap&lt;String, WeightedRoll&lt;AssetPresets&gt;&gt;</code>:
     * The templates for the areas of this <code>LocationInstance</code> instance.
     * These are areas which may or have may not been instantiated yet, depending on
     * whether or not the <code>generate()</code> method has been called since the
     * previous application of attributes.
     */
    private ImmutableMap<String, WeightedRoll<AssetPresets>> areaTemplates;

    /**
     * <code>String</code>: The key of the primary area template for this
     * <code>LocationInstance</code> instance. This refers to an area which may or
     * have may not been instantiated yet, depending on whether or not the
     * <code>generate()</code> method has been called since the previous application
     * of attributes.
     */
    private String primaryTemplate;

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
     * <code>String</code>: The key of the primary location of this
     * <code>LocationInstance</code> instance.
     */
    private String primary;

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

    private String currentArea;

    public final boolean setCurrentArea(String currentArea) {

        if (!areas.containsKey(currentArea)) {

            return false;
        }

        this.currentArea = currentArea;
        return true;
    }

    public final String getCurrentArea() {

        return currentArea;
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
    private LocationReset reset;

    private ZonedDateTime locationResetTimestamp;

    /**
     * <code>ZonedDateTime</code>: The timestamp of when the last player exited this
     * <code>LocationInstance</code> instance.
     */
    private ZonedDateTime prevEntranceTimestamp;

    private final HashMap<Long, String> players = new HashMap<>();

    public final boolean isEmpty() {

        return players.isEmpty();
    }

    /**
     * <code>double</code>: The total duration of time spent inside the location
     * since the last reset, in minutes.
     */
    private double resetOffsetCounter = 0.0;

    public final void enter(long player, String area) {

        if (area == null) {

            area = primary;
        }

        if (isEmpty()) {

            prevEntranceTimestamp = ZonedDateTime.now();

            // If there are currently no players and the location should be reset,
            // regenerate.
            if (getMinutesUntilReset(prevEntranceTimestamp) <= 0) {

                generate();
            }
        }
        players.put(player, area);
    }

    public final void exit(long player) {

        // If there is not a player in the location, the entrance timestamp will be
        // null.
        if (prevEntranceTimestamp == null) {

            return;
        }

        players.remove(player);
        if (players.isEmpty()) {

            ZonedDateTime now = ZonedDateTime.now();
            double duration = minutesBetween(prevEntranceTimestamp, now);
            resetOffsetCounter += duration;

            prevEntranceTimestamp = null;
        }
    }

    private boolean active;

    private void setActive(boolean active) {

        this.active = active;
        setProperty("active", active);
    }

    public final boolean isActive() {

        return active;
    }

    public final void activate() {

        if (!active) {

            return;
        }

        setActive(true);
        locationResetTimestamp = ZonedDateTime.now();
    }

    public final void deactivate() {

        if (!active) {

            return;
        }

        setActive(false);
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

        if (!reset.getDisplayCountdownTimer()) {

            return null;
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
     * @param start           <code>ZonedDateTime</code>: The time of origin of this
     *                        <code>LocationInstance</code> instance. If this
     *                        location does not have a set origin time, its moment
     *                        of creation will be the time the location was created.
     * @param triggerDuration <code>double</code>: The total duration of time
     *                        between the creation of this
     *                        <code>LocationInstance</code> instance and its end.
     * @return <code>double</code>: The duration in minutes.
     */
    public final double getMinutesUntilEnd(ZonedDateTime start, double triggerDuration) {

        ZonedDateTime now = ZonedDateTime.now();
        if (start == null) {

            start = locationCreatedTimestamp;
        }

        double minutes = minutesBetween(start, now);

        return triggerDuration - minutes;
    }

    /**
     * Outputs the end counter as a <code>String</code>, or <code>null</code> if the
     * location does not have a end countdown or if the countdown has finished.
     * 
     * @param start           <code>ZonedDateTime</code>: The time of origin of this
     *                        <code>LocationInstance</code> instance. If this
     *                        location does not have a set origin time, its moment
     *                        of creation will be the time the location was created.
     * @param triggerDuration <code>double</code>: The total duration of time
     *                        between the creation of this
     *                        <code>LocationInstance</code> instance and its end.
     * @return <code>String</code>: The minutes formatted as H:MM:SS or MM:SS.
     */
    public final String getEndCounter(ZonedDateTime start, double triggerDuration) {

        double remaining = getMinutesUntilEnd(start, triggerDuration);
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
    public LocationInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        LocationContext context = (LocationContext) assetContext;

        locationCreatedTimestamp = ZonedDateTime.now();

        setActive(false);
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        LocationAttributes attributes = (LocationAttributes) attributeSet;

        name = calculateAttribute(attributes.getName(), name);
        setProperty("name", name);

        description = calculateAttribute(attributes.getDescription(), name);
        setProperty("description", description);

        iconPath = calculateAttribute(attributes.getIcon(), val -> {

            icon = getInstanceTexture(val);

            return val;
        }, iconPath);
        setProperty("icon", iconPath);

        areaTemplates = calculateAttribute(attributes.getAreas(), areaTemplates);

        // Under certain circumstances, the new primary may not be contained in the area
        // templates - the primary should only be set/replaced if it points to an area.
        String newPrimaryTemplate = attributes.getPrimary();
        if (areaTemplates.containsKey(newPrimaryTemplate)) {

            primaryTemplate = newPrimaryTemplate;
        }

        reset = attributes.getReset();
        setProperty("resetDuration", reset.getDuration());
        setProperty("displayCountdownTimer", reset.getDisplayCountdownTimer());
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
            AssetPresets areaPresets = presetsRoll.get(nextRandom());

            LayoutContext areaContext = new LayoutContext(areaPresets, getWorld(), this);
            LayoutInstance areaLayout = (LayoutInstance) LAYOUT.createAsset(areaContext);

            AreaGrid area = areaLayout.generate();
            areas.put(areaName, area);
        }

        setProperty("areas", getAreas());

        primary = primaryTemplate;
        setProperty("primary", primary);
    }

    /**
     * Adds a new area to this <code>LocationInstance</code> instance using the
     * provided asset presets. This area will be named using the area id and will be
     * assigned a number based on the existing areas in this location.
     * 
     * @param areaPresets <code>AssetPresets</code>: The presets to use for the new
     *                    area.
     * @return <code>String</code>: The name of the new area.
     */
    public final String addArea(AssetPresets areaPresets) {

        String areaId = areaPresets.getIdentifier().toString();

        List<Integer> matches = getAreas().stream()
                // Filter out areas that don't start with the area ID.
                .filter(area -> area.startsWith(areaId))
                // Retrieve the area number from the name.
                .map(area -> Integer.valueOf(area.split(" ")[1]))
                // Sort the area numbers into a list.
                .sorted().toList();

        int next = matches.isEmpty() ? 0 : matches.getLast() + 1;
        String areaName = "%s %d".formatted(areaId, next);

        return addArea(areaName, areaPresets);
    }

    /**
     * Adds a new area to this <code>LocationInstance</code> instance using the
     * provided asset presets and area name.
     * 
     * @param areaName    <code>String</code>: The name of the new area.
     * @param areaPresets <code>AssetPresets</code>: The presets to use for the new
     *                    area.
     * @return <code>String</code>: The name of the new area.
     */
    public final String addArea(String areaName, AssetPresets areaPresets) {

        if (areas.containsKey(areaName)) {

            return null;
        }

        LayoutContext areaContext = new LayoutContext(areaPresets, getWorld(), this);
        LayoutInstance areaLayout = (LayoutInstance) LAYOUT.createAsset(areaContext);

        AreaGrid area = areaLayout.generate();
        areas.put(areaName, area);
        setProperty("areas", getAreas());

        return areaName;
    }

    @Override
    protected final void onUpdate(double time) {

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

}
