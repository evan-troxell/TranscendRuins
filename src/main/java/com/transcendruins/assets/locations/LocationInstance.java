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
import java.util.Random;
import java.util.stream.Collectors;

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
    public String getDescription() {

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
    public ImageIcon getIcon() {

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
    public ImmutableList<String> getAreas() {

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
    public AreaGrid getArea(String area) {

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
    public String getPrimary() {

        return primary;
    }

    private String currentArea;

    public boolean setCurrentArea(String currentArea) {

        if (!areas.containsKey(currentArea)) {

            return false;
        }

        this.currentArea = currentArea;
        return true;
    }

    public String getCurrentArea() {

        return currentArea;
    }

    /**
     * <code>LocationReset</code>: The reset settings of this
     * <code>LocationInstance</code> instance.
     */
    private LocationReset reset;

    /**
     * <code>ZonedDateTime</code>: The timestamp of when the player last exited this
     * <code>LocationInstance</code> instance.
     */
    private ZonedDateTime prevExitTimestamp = ZonedDateTime.now();

    /**
     * <code>double</code>: The total duration of time since the last reset, in
     * minutes. This does not include the most recent period of time since the
     * player left the location.
     */
    private double resetDurationCount = 0.0;

    /**
     * <code>LocationEvent</code>: The event settings of this
     * <code>LocationInstance</code> instance.
     */
    private LocationEvent event;

    private ZonedDateTime locationCreatedTimestamp;

    private boolean active;

    private void setActive(boolean active) {

        this.active = active;
        setProperty("active", active);
    }

    public boolean isActive() {

        return active;
    }

    public void activate() {

        if (active || event == null) {

            return;
        }
        setActive(true);

        locationCreatedTimestamp = ZonedDateTime.now();
    }

    public void deactivate() {

        if (!active || event == null) {

            return;
        }

        setActive(false);
    }

    /**
     * <code>boolean</code>: Whether or not the player is currently occupying this
     * <code>LocationInstance</code> instance.
     */
    private boolean occupied;

    /**
     * Retrieves whether or not the player is currently occupying this
     * <code>LocationInstance</code> instance.
     * 
     * @return <code>boolean</code>: The <code>occupied</code> field of this
     *         <code>LocationInstance</code> instance.
     */
    public boolean getOccupied() {

        return occupied;
    }

    /**
     * Enters the player into this <code>LocationInstance</code> instance.
     */
    public void enter() {

        if (occupied) {

            return;
        }

        // If this location is not already active or the reset countdown has conluded,
        // reset this location.
        if (!resetCountdownActive() || !active) {

            generate();
        }

        // Adjust the reset duration counter.
        ZonedDateTime now = ZonedDateTime.now();
        resetDurationCount += minutesBetween(prevExitTimestamp, now);

        occupied = true;
    }

    /**
     * Exits the player from this <code>LocationInstance</code> instance.
     */
    public void exit() {

        if (!occupied) {

            return;
        }

        // Adjust the previous timestamp so the countdown only starts counting down from
        // when the player exited the location.
        prevExitTimestamp = ZonedDateTime.now();

        occupied = false;
    }

    /**
     * Determines whether or not this <code>LocationInstance</code> instance has a
     * reset mechanism.
     * 
     * @return <code>boolean</code>: If this <code>LocationInstance</code> has a
     *         reset mechanism.
     */
    public boolean hasReset() {

        return reset != null && reset.getDuration() != -1;
    }

    /**
     * Determines the length in minutes until the reset countdown will end.
     * 
     * @return <code>double</code>: The duration in minutes.
     */
    public double getMinutesUntilReset() {

        ZonedDateTime now = ZonedDateTime.now();

        double minutes = resetDurationCount + minutesBetween(prevExitTimestamp, now);

        return reset.getDuration() - minutes;
    }

    /**
     * Determines whether or not this <code>LocationInstance</code> instance is
     * within the reset cooldown. A value of <code>true</code> means the location
     * does not need to be reset.
     * 
     * @return <code>boolean</code>: Whether or not this
     *         <code>LocationInstance</code> instance is within the reset cooldown.
     */
    public boolean resetCountdownActive() {

        if (!hasReset()) {

            return true;
        }

        return getMinutesUntilReset() > 0;

    }

    /**
     * Outputs the reset counter as a <code>String</code>, or <code>null</code> if
     * the location does not have a reset countdown, if the countdown has finished,
     * or if the player is in the location.
     * 
     * @return <code>String</code>: The minutes formatted as H:MM:SS or MM:SS.
     */
    public String getResetCounter() {

        // If there is no reset countdown or the player is in the location, do not print
        // the counter.
        if (!hasReset() || !reset.getDisplayCountdownTimer() || occupied) {

            return null;
        }

        double remaining = getMinutesUntilReset();
        return formatMinutes(remaining);
    }

    /**
     * Determines whether or not this <code>LocationInstance</code> instance has an
     * event-triggered end mechanism.
     * 
     * @return <code>boolean</code>: If this <code>LocationInstance</code> has an
     *         end mechanism.
     */
    public boolean hasEnd() {

        return event != null && event.getDuration() != -1;
    }

    /**
     * Determines the length in minutes until the location will end.
     * 
     * @return <code>double</code>: The duration in minutes.
     */
    public double getMinutesUntilEnd() {

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime start = event.getStartTimestamp();
        if (start == null) {

            start = locationCreatedTimestamp;
        }

        double minutes = minutesBetween(start, now);

        return event.getDuration() - minutes;
    }

    /**
     * Determines whether or not this <code>LocationInstance</code> instance is
     * within the end countdown. A value of <code>true</code> means the location
     * does not need to be ended.
     * 
     * @return <code>boolean</code>: Whether or not this
     *         <code>LocationInstance</code> instance is within the end cooldown.
     */
    public boolean endCountdownActive() {

        if (!hasEnd()) {

            return true;
        }

        return getMinutesUntilEnd() > 0;

    }

    /**
     * Outputs the end counter as a <code>String</code>, or <code>null</code> if the
     * location does not have a end countdown or if the countdown has finished.
     * 
     * @return <code>String</code>: The minutes formatted as H:MM:SS or MM:SS.
     */
    public String getEndCounter() {

        if (!hasEnd() || !event.getDisplayCountdownTimer()) {

            return null;
        }

        double remaining = getMinutesUntilEnd();
        return formatMinutes(remaining);
    }

    /**
     * <code>Random</code>: The random number generator used to generate locations.
     * This is seeded with the asset key to ensure each asset will generate areas in
     * a deterministic manner.
     */
    private final Random locationGenerator;

    public LocationInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        LocationContext context = (LocationContext) assetContext;

        locationGenerator = createAssetRandomizer();

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

            icon = getTexture(val);

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

        reset = calculateAttribute(attributes.getReset(), reset);

        event = calculateAttribute(attributes.getEvent(), event);
    }

    /**
     * Generates the contents of this <code>LocationInstance</code> instance. This
     * automatically resets each area in this <code>LocationInstance</code> instance
     * in a deterministic pattern.
     */
    public void generate() {

        areas.clear();

        for (Map.Entry<String, WeightedRoll<AssetPresets>> areaEntry : areaTemplates.entrySet()) {

            String areaName = areaEntry.getKey();
            AssetPresets areaPresets = areaEntry.getValue().get(locationGenerator.nextDouble(1));

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
     * provided asset presets. This area will be named using the area ID and will be
     * assigned a number based on the existing areas in this location.
     * 
     * @param areaPresets <code>AssetPresets</code>: The presets to use for the new
     *                    area.
     * @return <code>String</code>: The name of the new area.
     */
    public String addArea(AssetPresets areaPresets) {

        String areaId = areaPresets.getIdentifier().toString();

        List<Integer> matches = getAreas().stream()
                // Filter out areas that don't start with the area ID.
                .filter(area -> area.startsWith(areaId))
                // Retrieve the area number from the name.
                .map(area -> Integer.valueOf(area.split(" ")[1]))
                // Sort the area numbers into a list.
                .sorted().collect(Collectors.toList());

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
    public String addArea(String areaName, AssetPresets areaPresets) {

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
    protected void onUpdate(double time) {

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
