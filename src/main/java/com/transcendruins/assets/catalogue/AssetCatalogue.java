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

package com.transcendruins.assets.catalogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.transcendruins.assets.AssetType;
import static com.transcendruins.assets.AssetType.LOCATION;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.catalogue.global.events.GlobalEventSchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.fileexceptions.FileException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>GlobalSchema</code>: A class representing the global map schema of a
 * <code>ContentPack</code> instance.
 */
public final class AssetCatalogue {

    /**
     * <code>ImmutableMap&lt;String, AssetPresets&gt;</code>: The global locations
     * of this <code>GlobalSchema</code> instance.
     */
    private final ImmutableMap<String, AssetPresets> locations;

    /**
     * Retrieves the global locations of this <code>GlobalSchema</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, AssetPresets&gt;</code>: The
     *         <code>locations</code> field of this <code>GlobalSchema</code>
     *         instance.
     */
    public ImmutableMap<String, AssetPresets> getLocations() {

        return locations;
    }

    /**
     * <code>ImmutableMap&lt;String, ImmutableList&lt;GlobalEventSchema&gt;&gt;</code>:
     * The global events of this <code>GlobalSchema</code> instance.
     */
    private final ImmutableMap<String, ImmutableList<GlobalEventSchema>> events;

    /**
     * Retrieves the global events of this <code>GlobalSchema</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, ImmutableList&lt;GlobalEventSchema&gt;&gt;</code>:
     *         The <code>events</code> field of this <code>GlobalSchema</code>
     *         instance.
     */
    public ImmutableMap<String, ImmutableList<GlobalEventSchema>> getEvents() {

        return events;
    }

    private final ImmutableMap<String, AssetPresets> menus;

    public ImmutableMap<String, AssetPresets> getMenus() {

        return menus;
    }

    private final ImmutableMap<String, ImmutableMap<String, AssetPresets>> recipes;

    public ImmutableMap<String, ImmutableMap<String, AssetPresets>> getRecipes() {

        return recipes;
    }

    /**
     * Creates a new instance of the <code>GlobalSchema</code> class.
     * 
     * @param path <code>TracedPath</code>: The filepath to the JSON information of
     *             this <code>GlobalSchema</code> instance.
     */
    public AssetCatalogue(TracedPath path) {

        ImmutableMap<String, AssetPresets> locationsMap = new ImmutableMap<>();
        ImmutableMap<String, ImmutableList<GlobalEventSchema>> eventsMap = new ImmutableMap<>();

        // Process the global map data.
        TracedPath globalPath = path.extend("global.json");
        if (globalPath.exists()) {

            try {

                TracedDictionary json = JSONOperator.retrieveJSON(globalPath);

                // Attempt to process the locations.
                locationsMap = createLocations(json, "locations");

                // Attempt to process the events.
                eventsMap = createEvents(json, "events");
            } catch (FileException _) {
            }
        }

        locations = locationsMap;
        events = eventsMap;

        ImmutableMap<String, AssetPresets> menusMap = new ImmutableMap<>();

        // Process the hud data.
        TracedPath hudPath = path.extend("hud.json");
        if (hudPath.exists()) {

            try {

                TracedDictionary json = JSONOperator.retrieveJSON(hudPath);

                // Attempt to process the menus.
                menusMap = createMenus(json);
            } catch (FileException _) {
            }
        }

        menus = menusMap;

        ImmutableMap<String, ImmutableMap<String, AssetPresets>> recipesMap = new ImmutableMap<>();

        // Process the crafting data.
        TracedPath craftingPath = path.extend("recipes.json");
        if (craftingPath.exists()) {

            try {

                TracedDictionary json = JSONOperator.retrieveJSON(craftingPath);

                // Attempt to process the recipes.
                recipesMap = createRecipes(json);

            } catch (FileException _) {
            }
        }

        recipes = recipesMap;
    }

    private ImmutableMap<String, AssetPresets> createLocations(TracedCollection collection, Object key) {

        HashMap<String, AssetPresets> locationsMap = new HashMap<>();

        try {

            TracedEntry<TracedDictionary> locationsEntry = collection.getAsDict(key, true);
            if (locationsEntry.containsValue()) {

                TracedDictionary locationsJson = locationsEntry.getValue();
                for (String locationKey : locationsJson) {

                    // In the case that any location is invalid, the rest of the locations should
                    // work properly.
                    try {

                        TracedEntry<AssetPresets> locationEntry = locationsJson.getAsPresets(locationKey, false,
                                LOCATION);
                        AssetPresets location = locationEntry.getValue();

                        locationsMap.put(locationKey, location);
                    } catch (LoggedException _) {
                    }
                }
            }
        } catch (LoggedException _) {
        }

        return new ImmutableMap<>(locationsMap);
    }

    private ImmutableMap<String, ImmutableList<GlobalEventSchema>> createEvents(TracedCollection collection,
            Object key) {

        HashMap<String, ImmutableList<GlobalEventSchema>> eventsMap = new HashMap<>();

        try {

            TracedEntry<TracedDictionary> eventsEntry = collection.getAsDict(key, true);
            if (eventsEntry.containsValue()) {

                TracedDictionary eventsJson = eventsEntry.getValue();
                for (String eventKey : eventsJson) {

                    // In the case than any event is invalid, the rest of the events should work
                    // properly.
                    try {

                        ArrayList<GlobalEventSchema> eventList = new ArrayList<>();

                        eventsJson.compute(eventKey, List.of(eventsJson.arrayCase(entry -> {

                            TracedArray eventArray = entry.getValue();
                            for (int i : eventArray) {

                                TracedEntry<TracedDictionary> eventEntry = eventArray.getAsDict(i, false);
                                TracedDictionary eventJson = eventEntry.getValue();

                                GlobalEventSchema event = new GlobalEventSchema(eventJson);
                                eventList.add(event);
                            }

                            return null;
                        }),

                                eventsJson.dictCase(entry -> {

                                    TracedDictionary eventJson = entry.getValue();
                                    eventList.add(new GlobalEventSchema(eventJson));

                                    return null;
                                })));

                        eventsMap.put(eventKey, new ImmutableList<>(eventList));

                    } catch (LoggedException _) {
                    }
                }
            }

        } catch (LoggedException _) {
        }

        return new ImmutableMap<>(eventsMap);
    }

    private ImmutableMap<String, AssetPresets> createMenus(TracedDictionary json) {

        HashMap<String, AssetPresets> menusMap = new HashMap<>();

        for (String menu : json) {

            try {

                TracedEntry<AssetPresets> presetsEntry = json.getAsPresets(menu, false, AssetType.INTERFACE);
                AssetPresets presets = presetsEntry.getValue();

                menusMap.put(menu, presets);
            } catch (LoggedException _) {
            }
        }

        return new ImmutableMap<>(menusMap);
    }

    private ImmutableMap<String, ImmutableMap<String, AssetPresets>> createRecipes(TracedDictionary json) {

        HashMap<String, ImmutableMap<String, AssetPresets>> recipesMap = new HashMap<>();

        // Validate each crafting category type.
        for (String category : json) {

            try {

                TracedEntry<TracedDictionary> categoryEntry = json.getAsDict(category, false);
                TracedDictionary categoryJson = categoryEntry.getValue();

                HashMap<String, AssetPresets> categoryMap = new HashMap<>();

                for (String recipe : categoryJson) {

                    try {

                        // Validate each recipe.
                        TracedEntry<AssetPresets> presetsEntry = categoryJson.getAsPresets(recipe, false,
                                AssetType.RECIPE);
                        AssetPresets presets = presetsEntry.getValue();

                        categoryMap.put(recipe, presets);
                    } catch (LoggedException _) {
                    }
                }

                // Update the dependencies.
                recipesMap.put(category, new ImmutableMap<>(categoryMap));
            } catch (LoggedException _) {
            }
        }

        return new ImmutableMap<>(recipesMap);
    }
}
