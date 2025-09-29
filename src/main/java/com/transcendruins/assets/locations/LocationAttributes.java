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

import java.awt.geom.Point2D;
import java.util.HashMap;

import static com.transcendruins.assets.AssetType.LAYOUT;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.KeyNameException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.ReferenceWithoutDefinitionException;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>LocationAttributes</code>: A class which represents the attributes of a
 * <code>LocationSchema</code> instance.
 */
public final class LocationAttributes extends AssetAttributes {

    /**
     * <code>String</code>: The name of this <code>LocationAttributes</code>
     * instance.
     */
    private final String name;

    /**
     * <code>String</code>: The description of this <code>LocationAttributes</code>
     * instance.
     * 
     * @return <code>String</code>: The <code>name</code> field of this
     *         <code>LocationAttributes</code> instance.
     */
    public final String getName() {

        return name;
    }

    /**
     * <code>String</code>: The description of this <code>LocationAttributes</code>
     * instance.
     */
    private final String description;

    /**
     * Retrieves the description of this <code>LocationAttributes</code> instance.
     * 
     * @return <code>String</code>: The <code>description</code> field of this
     *         <code>LocationAttributes</code> instance.
     */
    public final String getDescription() {

        return description;
    }

    /**
     * <code>String</code>: The icon of this <code>LocationAttributes</code>
     * instance.
     */
    private final String icon;

    /**
     * Retrieves the icon of this <code>LocationAttributes</code> instance.
     * 
     * @return <code>String</code>: The <code>icon</code> field of this
     *         <code>LocationAttributes</code> instance.
     */
    public final String getIcon() {

        return icon;
    }

    /**
     * <code>ImmutableMap&lt;String, WeightedRoll&lt;AssetPresets&gt;&gt;</code>:
     * The areas contained within this <code>LocationAttributes</code> instance.
     * Each key-value pair represents an area layout combined with its associated
     * key.
     */
    private final ImmutableMap<String, WeightedRoll<AssetPresets>> areas;

    /**
     * Retrieves the areas contained within this <code>LocationAttributes</code>
     * instance.
     * 
     * @return <code>ImmutableMap&lt;String, WeightedRoll&lt;AssetPresets&gt;&gt;</code>:
     *         The <code>areas</code> field of this <code>LocationAttributes</code>
     *         instance.
     */
    public final ImmutableMap<String, WeightedRoll<AssetPresets>> getAreas() {

        return areas;
    }

    /**
     * <code>String</code>: The primary area of this <code>LocationAttributes</code>
     * instance. This is the area that will be used as the default area for this
     * <code>LocationAttributes</code> instance.
     */
    private final String primary;

    /**
     * Retrieves the primary area of this <code>LocationAttributes</code> instance.
     * 
     * @return <code>String</code>: The <code>primary</code> field of this
     *         <code>LocationAttributes</code> instance.
     */
    public final String getPrimary() {

        return primary;
    }

    /**
     * <code>WeightedRoll&lt;Point2D&gt;</code>: The potential coordinates of this
     * <code>LocationAttributes</code> instance.
     */
    private final WeightedRoll<Point2D> coordinates;

    /**
     * Retrieves the potential coordinates of this <code>LocationAttributes</code>
     * instance.
     *
     * @return <code>WeightedRoll&lt;Point2D&gt;</code>: The
     *         <code>coordinates</code> field of this
     *         <code>LocationAttributes</code> instance.
     */
    public final WeightedRoll<Point2D> getCoordinates() {

        return coordinates;
    }

    private final LocationReset reset;

    public final LocationReset getReset() {

        return reset;
    }

    /**
     * Compiles this <code>LocationAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>LocationAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>LocationAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>LocationAttributes</code> instance is the base attribute
     *               set of a <code>LocationAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>LocationAttributes</code> instance.
     */
    public LocationAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        TracedEntry<String> nameEntry = json.getAsString("name", !isBase, null);
        name = nameEntry.getValue();

        TracedEntry<String> descriptionEntry = json.getAsString("description", !isBase, null);
        description = descriptionEntry.getValue();

        TracedEntry<String> iconEntry = json.getAsString("icon", !isBase, null);
        icon = iconEntry.getValue();

        TracedEntry<TracedDictionary> areasEntry = json.getAsDict("areas", !isBase);
        if (areasEntry.containsValue()) {

            TracedDictionary areasJson = areasEntry.getValue();
            HashMap<String, WeightedRoll<AssetPresets>> areasDict = new HashMap<>();

            for (String areaKey : areasJson) {

                // Spaces represent escape sequences for anononomous locations, so they
                // should not be contained in the area key.
                if (areaKey.contains(" ")) {

                    throw new KeyNameException(areasJson, areaKey);
                }

                WeightedRoll<AssetPresets> areaOptions = areasJson.getAsRoll(areaKey, false, null, "layout",
                        areasJson.presetsCase(entry -> {

                            AssetPresets area = entry.getValue();
                            addAssetDependency(area);

                            return area;
                        }, LAYOUT));

                areasDict.put(areaKey, areaOptions);
            }

            areas = new ImmutableMap<>(areasDict);
        } else {

            areas = null;
        }

        TracedEntry<String> primaryEntry = json.getAsString("primary", !isBase, null);
        primary = primaryEntry.getValue();

        // If the areas are defined in these attributes, then they should be required to
        // contain the primary.
        if (areas != null && !areas.containsKey(primary)) {

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

    }

    /**
     * Processes the (X, Y) coordinates from a dictionary into a point.
     * 
     * @param json <code>TracedDictionary</code>: The dictionary to process.
     * @return <code>Point2D</code>: The resulting point with double values.
     */
    public Point2D parsePoint(TracedDictionary json) throws LoggedException {

        TracedEntry<Double> xEntry = json.getAsDouble("x", false, null);
        double x = xEntry.getValue();

        TracedEntry<Double> yEntry = json.getAsDouble("y", false, null);
        double y = yEntry.getValue();

        return new Point2D.Double(x, y);
    }
}
