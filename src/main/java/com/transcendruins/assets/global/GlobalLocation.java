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

package com.transcendruins.assets.global;

import java.util.ArrayList;
import java.util.List;

import static com.transcendruins.assets.AssetType.LOCATION;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>GlobalLocation</code>: A class representing a location on the global
 * map.
 */
public final class GlobalLocation {

    /**
     * <code>WeightedRoll&lt;AssetPresets&gt;</code>: The possible location
     * variations of this <code>GlobalLocation</code> instance.
     */
    private final WeightedRoll<AssetPresets> locations;

    /**
     * Retrieves a location from this <code>GlobalLocation</code> instance based on
     * a random number.
     * 
     * @param random <code>double</code>: A random number between 0.0 and 1.0 used
     *               to select a location.
     * @return <code>AssetPresets</code>: A random location from this
     *         <code>GlobalLocation</code> instance.
     */
    public AssetPresets getLocation(double random) {

        return locations.get(random);
    }

    /**
     * <code>ImmutableList&lt;AssetPresets&gt;</code>: The locations which this
     * <code>GlobalLocation</code> instance depends on.
     */
    private final ImmutableList<AssetPresets> locationDependencies;

    /**
     * Retrieves the locations which this <code>GlobalLocation</code> instance
     * depends on.
     * 
     * @return <code>ImmutableList&lt;AssetPresets&gt;</code>: The
     *         <code>locationDependencies</code> field of this
     *         <code>GlobalLocation</code> instance.
     */
    public ImmutableList<AssetPresets> getLocationDependencies() {

        return locationDependencies;
    }

    /**
     * Creates a new instance of the <code>GlobalLocation</code> class.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to search for.
     * @throws LoggedException Thrown if an error occurs while parsing the
     *                         collection.
     */
    public GlobalLocation(TracedCollection collection, Object key) throws LoggedException {

        ArrayList<AssetPresets> locationDependenciesList = new ArrayList<>();

        locations = collection.get(key, List.of(collection.arrayCase(entry -> {

            TracedArray locationArray = entry.getValue();
            ArrayList<WeightedRoll.Entry<AssetPresets>> presets = new ArrayList<>();

            // If the location is an array, iterate through each entry.
            for (int i : locationArray) {

                TracedEntry<TracedDictionary> locationEntry = locationArray.getAsDict(i, false);
                TracedDictionary locationJson = locationEntry.getValue();

                TracedEntry<AssetPresets> presetEntry = locationJson.getAsPresets("location", false, LOCATION);
                AssetPresets location = presetEntry.getValue();
                locationDependenciesList.add(location);

                TracedEntry<Double> chanceEntry = locationJson.getAsDouble("chance", true, 100.0, num -> num > 0.0);
                double chance = chanceEntry.getValue();

                presets.add(new WeightedRoll.Entry<>(location, chance));
            }

            return new WeightedRoll<>(entry, locationArray, presets);
        }), collection.presetsCase(entry -> {

            AssetPresets location = entry.getValue();
            return new WeightedRoll<>(location);
        }, LOCATION)));

        locationDependencies = new ImmutableList<>(locationDependenciesList);
    }
}
