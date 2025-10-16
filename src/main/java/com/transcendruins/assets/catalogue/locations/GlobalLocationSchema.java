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
import java.util.HashMap;

import static com.transcendruins.assets.AssetType.LAYOUT;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.ReferenceWithoutDefinitionException;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class GlobalLocationSchema {

    private final String name;

    /**
     * <code>String</code>: The description of this
     * <code>GlobalLocationSchema</code> instance.
     * 
     * @return <code>String</code>: The <code>name</code> field of this
     *         <code>GlobalLocationSchema</code> instance.
     */
    public final String getName() {

        return name;
    }

    /**
     * <code>String</code>: The description of this
     * <code>GlobalLocationSchema</code> instance.
     */
    private final String description;

    /**
     * Retrieves the description of this <code>GlobalLocationSchema</code> instance.
     * 
     * @return <code>String</code>: The <code>description</code> field of this
     *         <code>GlobalLocationSchema</code> instance.
     */
    public final String getDescription() {

        return description;
    }

    /**
     * <code>String</code>: The icon of this <code>GlobalLocationSchema</code>
     * instance.
     */
    private final String icon;

    /**
     * Retrieves the icon of this <code>GlobalLocationSchema</code> instance.
     * 
     * @return <code>String</code>: The <code>icon</code> field of this
     *         <code>GlobalLocationSchema</code> instance.
     */
    public final String getIcon() {

        return icon;
    }

    /**
     * <code>ImmutableMap&lt;String, WeightedRoll&lt;AssetPresets&gt;&gt;</code>:
     * The areas contained within this <code>GlobalLocationSchema</code> instance.
     * Each key-value pair represents an area layout combined with its associated
     * key.
     */
    private final ImmutableMap<String, WeightedRoll<AssetPresets>> areas;

    /**
     * Retrieves the areas contained within this <code>GlobalLocationSchema</code>
     * instance.
     * 
     * @return <code>ImmutableMap&lt;String, WeightedRoll&lt;AssetPresets&gt;&gt;</code>:
     *         The <code>areas</code> field of this
     *         <code>GlobalLocationSchema</code> instance.
     */
    public final ImmutableMap<String, WeightedRoll<AssetPresets>> getAreas() {

        return areas;
    }

    /**
     * <code>String</code>: The primary area of this
     * <code>GlobalLocationSchema</code> instance. This is the area that will be
     * used as the default area for this <code>GlobalLocationSchema</code> instance.
     */
    private final String primary;

    /**
     * Retrieves the primary area of this <code>GlobalLocationSchema</code>
     * instance.
     * 
     * @return <code>String</code>: The <code>primary</code> field of this
     *         <code>GlobalLocationSchema</code> instance.
     */
    public final String getPrimary() {

        return primary;
    }

    /**
     * <code>WeightedRoll&lt;Point2D&gt;</code>: The potential coordinates of this
     * <code>GlobalLocationSchema</code> instance.
     */
    private final WeightedRoll<Point2D> coordinates;

    /**
     * Retrieves the potential coordinates of this <code>GlobalLocationSchema</code>
     * instance.
     *
     * @return <code>WeightedRoll&lt;Point2D&gt;</code>: The
     *         <code>coordinates</code> field of this
     *         <code>GlobalLocationSchema</code> instance.
     */
    public final WeightedRoll<Point2D> getCoordinates() {

        return coordinates;
    }

    private final LocationReset reset;

    public final LocationReset getReset() {

        return reset;
    }

    private final LocationTrigger trigger;

    public final LocationTrigger getTrigger() {

        return trigger;
    }

    public GlobalLocationSchema(TracedCollection json) throws LoggedException {

        TracedEntry<String> nameEntry = json.getAsString("name", false, null);
        name = nameEntry.getValue();

        TracedEntry<String> descriptionEntry = json.getAsString("description", false, null);
        description = descriptionEntry.getValue();

        TracedEntry<String> iconEntry = json.getAsString("icon", false, null);
        icon = iconEntry.getValue();

        TracedEntry<TracedDictionary> areasEntry = json.getAsDict("areas", false);

        TracedDictionary areasJson = areasEntry.getValue();
        HashMap<String, WeightedRoll<AssetPresets>> areasDict = new HashMap<>();

        for (String areaKey : areasJson) {

            WeightedRoll<AssetPresets> areaOptions = areasJson.getAsRoll(areaKey, false, null, "layout",
                    areasJson.presetsCase(entry -> {

                        AssetPresets area = entry.getValue();

                        return area;
                    }, LAYOUT));

            areasDict.put(areaKey, areaOptions);
        }

        areas = new ImmutableMap<>(areasDict);

        TracedEntry<String> primaryEntry = json.getAsString("primary", true, "primary");
        primary = primaryEntry.getValue();

        // If the areas are defined in these attributes, then they should be required to
        // contain the primary.
        if (!areas.containsKey(primary)) {

            throw new ReferenceWithoutDefinitionException(primaryEntry, "Area");
        }

        coordinates = json.getAsRoll("coordinates", false, null, entry -> {

            TracedDictionary coordinateJson = entry.getValue();
            return parsePoint(coordinateJson);
        });

        TracedEntry<TracedDictionary> resetEntry = json.getAsDict("reset", true);
        if (resetEntry.containsValue()) {

            TracedDictionary resetJson = resetEntry.getValue();
            reset = new LocationReset(resetJson);
        } else {

            reset = LocationReset.DEFAULT;
        }

        TracedEntry<TracedDictionary> triggerEntry = json.getAsDict("trigger", true);
        if (triggerEntry.containsValue()) {

            TracedDictionary triggerJson = triggerEntry.getValue();
            trigger = new LocationTrigger(triggerJson);
        } else {

            trigger = LocationTrigger.DEFAULT;
        }
    }

    /**
     * Processes the (X, Y) coordinates from a dictionary into a point.
     * 
     * @param json <code>TracedDictionary</code>: The dictionary to process.
     * @return <code>Point2D</code>: The resulting point with double values.
     */
    private Point2D parsePoint(TracedDictionary json) throws LoggedException {

        TracedEntry<Double> xEntry = json.getAsDouble("x", false, null);
        double x = xEntry.getValue();

        TracedEntry<Double> yEntry = json.getAsDouble("y", false, null);
        double y = yEntry.getValue();

        return new Point2D.Double(x, y);
    }
}
