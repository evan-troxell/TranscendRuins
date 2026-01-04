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
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.catalogue.events.GlobalEventSchema;
import com.transcendruins.assets.catalogue.locations.GlobalLocationSchema;
import com.transcendruins.assets.interfaces.map.TerrainRender;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.resources.styles.Style.IconSize;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.fileexceptions.FileException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.ReferenceWithoutDefinitionException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>AssetCatalogue</code>: A class representing the predefined asset
 * catalogue information of a <code>ContentPack</code> instance.
 */
public final class AssetCatalogue {

    public final record PinIcon(String icon, IconSize size) {

        public static PinIcon createPinIcon(TracedCollection collection, Object key) throws LoggedException {

            TracedEntry<TracedDictionary> pinEntry = collection.getAsDict(key, false);
            TracedDictionary pinJson = pinEntry.getValue();

            TracedEntry<String> iconEntry = pinJson.getAsString("icon", false, null);
            String icon = iconEntry.getValue();

            IconSize size = IconSize.createSize(pinJson, "size");

            return new PinIcon(icon, size);
        }
    }

    /**
     * <code>ImmutableMap&lt;String, PinIcon&gt;</code>: The pin icon catalogue of
     * this <code>AssetCatalogue</code> instance.
     */
    private final ImmutableMap<String, PinIcon> pinIcons;

    /**
     * Retrieves the pin icon catalogue of this <code>AssetCatalogue</code>
     * instance.
     * 
     * @return <code>ImmutableMap&lt;String, PinIcon&gt;</code>: The
     *         <code>pinIcons</code> field of this <code>AssetCatalogue</code>
     *         instance.
     */
    public final ImmutableMap<String, PinIcon> getPinIcons() {

        return pinIcons;
    }

    /**
     * <code>String</code>: The default location of this <code>AssetCatalogue</code>
     * instance.
     */
    private final String defaultLocation;

    /**
     * Retrieves the default location of this <code>AssetCatalogue</code> instance.
     * 
     * @return <code>String</code>: The <code>defaultLocation</code> field of this
     *         <code>AssetCatalogue</code> instance.
     */
    public final String getDefaultLocation() {

        return defaultLocation;
    }

    /**
     * <code>ImmutableMap&lt;String, GlobalLocationSchema&gt;</code>: The global
     * location catalogue of this <code>AssetCatalogue</code> instance.
     */
    private final ImmutableMap<String, GlobalLocationSchema> locations;

    /**
     * Retrieves the global location catalogue of this <code>AssetCatalogue</code>
     * instance.
     * 
     * @return <code>ImmutableMap&lt;String, GlobalLocationSchema&gt;</code>: The
     *         <code>locations</code> field of this <code>AssetCatalogue</code>
     *         instance.
     */
    public final ImmutableMap<String, GlobalLocationSchema> getLocations() {

        return locations;
    }

    /**
     * <code>ImmutableList&lt;TerrainRender&gt;</code>: The terrain catalogue of
     * this <code>AssetCatalogue</code> instance.
     */
    private final ImmutableList<TerrainRender> terrain;

    /**
     * Retrieves the terrain catalogue of this <code>AssetCatalogue</code> instance.
     * 
     * @return <code>ImmutableList&lt;TerrainRender&gt;</code>: The
     *         <code>terrain</code> field of this <code>AssetCatalogue</code>
     *         instance.
     */
    public final ImmutableList<TerrainRender> getTerrain() {

        return terrain;
    }

    /**
     * <code>ImmutableMap&lt;String, ImmutableList&lt;GlobalEventSchema&gt;&gt;</code>:
     * The global event catalogue of this <code>AssetCatalogue</code> instance.
     */
    private final ImmutableMap<String, ImmutableList<GlobalEventSchema>> events;

    /**
     * Retrieves the global event catalogue of this <code>AssetCatalogue</code>
     * instance.
     * 
     * @return <code>ImmutableMap&lt;String, ImmutableList&lt;GlobalEventSchema&gt;&gt;</code>:
     *         The <code>events</code> field of this <code>AssetCatalogue</code>
     *         instance.
     */
    public final ImmutableMap<String, ImmutableList<GlobalEventSchema>> getEvents() {

        return events;
    }

    /**
     * <code>ImmutableMap&lt;String, AssetPreset&gt;</code>: The screen overlay
     * catalogue of this <code>AssetCatalogue</code> instance.
     */
    private final ImmutableMap<String, AssetPresets> overlays;

    /**
     * Retrieves the screen overlay catalogue of this <code>AssetCatalogue</code>
     * instance.
     * 
     * @return <code>ImmutableMap&lt;String, AssetPreset&gt;</code>: The
     *         <code>overlays</code> field of this <code>AssetCatalogue</code>
     *         instance.
     */
    public final ImmutableMap<String, AssetPresets> getOverlays() {

        return overlays;
    }

    /**
     * <code>ImmutableMap&lt;String, AssetPreset&gt;</code>: The global map overlay
     * catalogue of this <code>AssetCatalogue</code> instance.
     */
    private final ImmutableMap<String, AssetPresets> globalOverlays;

    /**
     * Retrieves the global map overlay catalogue of this
     * <code>AssetCatalogue</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, AssetPreset&gt;</code>: The
     *         <code>globalOverlays</code> field of this <code>AssetCatalogue</code>
     *         instance.
     */
    public final ImmutableMap<String, AssetPresets> getGlobalOverlays() {

        return globalOverlays;
    }

    /**
     * <code>ImmutableMpa&lt;String, AssetPreset&gt;</code>: The UI menu catalogue
     * of this <code>AssetCatalogue</code> instance.
     */
    private final ImmutableMap<String, AssetPresets> menus;

    /**
     * Retrieves the UI menu catalogue of this <code>AssetCatalogue</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, AssetPreset&gt;</code>: The
     *         <code>menus</code> field of this <code>AssetCatalogue</code>
     *         instance.
     */
    public final ImmutableMap<String, AssetPresets> getMenus() {

        return menus;
    }

    /**
     * <code>ImmutableMap&lt;String, RecipeSet&gt;</code>: The recipe catalogue of
     * this <code>AssetCatalogue</code> instance.
     */
    private final ImmutableMap<String, RecipeSet> recipes;

    /**
     * Retrieves the recipe catalogue of this <code>AssetCatalogue</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, RecipeSet&gt;</code>: The
     *         <code>recipes</code> field of this <code>AssetCatalogue</code>
     *         instance.
     */
    public final ImmutableMap<String, RecipeSet> getRecipes() {

        return recipes;
    }

    /**
     * Creates a new instance of the <code>AssetCatalogue</code> class.
     * 
     * @param path <code>TracedPath</code>: The filepath to the JSON information of
     *             this <code>AssetCatalogue</code> instance.
     */
    public AssetCatalogue(TracedPath path) {

        ImmutableMap<String, PinIcon> pinIconsMap = new ImmutableMap<>();

        String defaultLocationString = null;
        ImmutableMap<String, GlobalLocationSchema> locationsMap = new ImmutableMap<>();
        ImmutableList<TerrainRender> terrainList = new ImmutableList<>();
        ImmutableMap<String, ImmutableList<GlobalEventSchema>> eventsMap = new ImmutableMap<>();

        // Process the global map data.
        TracedPath globalPath = path.extend("global.json");
        if (globalPath.exists()) {

            try {

                TracedDictionary json = JSONOperator.retrieveJSON(globalPath);

                try {

                    TracedEntry<TracedDictionary> pinIconsEntry = json.getAsDict("pinIcons", true);
                    if (pinIconsEntry.containsValue()) {

                        HashMap<String, PinIcon> pinIconsTemp = new HashMap<>();

                        TracedDictionary pinIconsJson = pinIconsEntry.getValue();
                        for (String pinIconKey : pinIconsJson) {

                            try {

                                PinIcon pinIcon = PinIcon.createPinIcon(pinIconsJson, pinIconKey);
                                pinIconsTemp.put(pinIconKey, pinIcon);
                            } catch (LoggedException _) {
                            }
                        }

                        pinIconsMap = new ImmutableMap<>(pinIconsTemp);
                    }
                } catch (LoggedException _) {
                }

                // Attempt to process the locations.
                locationsMap = createLocations(json, "locations");

                // Attempt to process the terrain.
                try {

                    TracedEntry<TracedDictionary> terrainEntry = json.getAsDict("terrain", true);
                    if (terrainEntry.containsValue()) {

                        ArrayList<TerrainRender> terrainTemp = new ArrayList<>();

                        TracedDictionary terrainJson = terrainEntry.getValue();
                        for (String terrainSectionKey : terrainJson) {

                            try {

                                TracedEntry<TracedDictionary> terrainRenderEntry = terrainJson
                                        .getAsDict(terrainSectionKey, false);
                                TracedDictionary terrainRenderJson = terrainRenderEntry.getValue();

                                TracedEntry<TRScript> iconEntry = terrainRenderJson.getAsScript("icon", false);
                                TRScript icon = iconEntry.getValue();

                                TracedEntry<Double> xEntry = terrainRenderJson.getAsDouble("x", false, null);
                                double x = xEntry.getValue();

                                TracedEntry<Double> yEntry = terrainRenderJson.getAsDouble("y", false, null);
                                double y = yEntry.getValue();

                                TracedEntry<Double> heightEntry = terrainRenderJson.getAsDouble("height", true, 0.0,
                                        num -> 0 <= num && num < 1);
                                double height = heightEntry.getValue();

                                TerrainRender terrainRender = new TerrainRender(icon, x, y, height);
                                terrainTemp.add(terrainRender);
                            } catch (LoggedException _) {
                            }
                        }

                        terrainList = new ImmutableList<>(terrainTemp);
                    }
                } catch (LoggedException _) {
                }

                // Attempt to process the events.
                eventsMap = createEvents(json, "events");

                try {

                    TracedEntry<String> defaultLocationEntry = json.getAsString("defaultLocation", true, null);
                    if (defaultLocationEntry.containsValue()) {

                        defaultLocationString = defaultLocationEntry.getValue();

                        if (!locationsMap.containsKey(defaultLocationString)) {

                            throw new ReferenceWithoutDefinitionException(defaultLocationEntry, "Location");
                        }
                    }
                } catch (LoggedException _) {
                }
            } catch (FileException _) {
            }
        }

        pinIcons = pinIconsMap;

        defaultLocation = defaultLocationString;
        locations = locationsMap;
        terrain = terrainList;
        events = eventsMap;

        ImmutableMap<String, AssetPresets> overlaysMap = new ImmutableMap<>();
        ImmutableMap<String, AssetPresets> globalOverlaysMap = new ImmutableMap<>();
        ImmutableMap<String, AssetPresets> menusMap = new ImmutableMap<>();

        // Process the hud data.
        TracedPath hudPath = path.extend("hud.json");
        if (hudPath.exists()) {

            try {

                TracedDictionary json = JSONOperator.retrieveJSON(hudPath);

                // Attempt to process the overlays.
                try {

                    TracedEntry<TracedDictionary> overlaysEntry = json.getAsDict("overlays", true);
                    if (overlaysEntry.containsValue()) {

                        TracedDictionary overlaysJson = overlaysEntry.getValue();
                        overlaysMap = createMenus(overlaysJson);
                    }
                } catch (PropertyException e) {
                }

                // Attempt to process the global map overlays.
                try {

                    TracedEntry<TracedDictionary> globalOverlaysEntry = json.getAsDict("globalOverlays", true);
                    if (globalOverlaysEntry.containsValue()) {

                        TracedDictionary globalOverlaysJson = globalOverlaysEntry.getValue();
                        globalOverlaysMap = createMenus(globalOverlaysJson);
                    }
                } catch (PropertyException e) {
                }

                // Attempt to process the menus.
                try {

                    TracedEntry<TracedDictionary> menusEntry = json.getAsDict("menus", true);
                    if (menusEntry.containsValue()) {

                        TracedDictionary menusJson = menusEntry.getValue();
                        menusMap = createMenus(menusJson);
                    }
                } catch (PropertyException e) {
                }
            } catch (FileException _) {
            }
        }

        overlays = overlaysMap;
        globalOverlays = globalOverlaysMap;
        menus = menusMap;

        ImmutableMap<String, RecipeSet> recipesMap = new ImmutableMap<>();

        // Process the crafting data.
        TracedPath craftingPath = path.extend("recipe.json");
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

    private ImmutableMap<String, GlobalLocationSchema> createLocations(TracedCollection collection, Object key) {

        HashMap<String, GlobalLocationSchema> locationsMap = new HashMap<>();

        try {

            TracedEntry<TracedDictionary> locationsEntry = collection.getAsDict(key, true);
            if (locationsEntry.containsValue()) {

                TracedDictionary locationsJson = locationsEntry.getValue();
                for (String locationKey : locationsJson) {

                    // In the case that any location is invalid, the rest of the locations should
                    // work properly.
                    try {

                        TracedEntry<TracedDictionary> locationEntry = locationsJson.getAsDict(locationKey, false);
                        TracedDictionary locationJson = locationEntry.getValue();

                        GlobalLocationSchema location = new GlobalLocationSchema(locationJson);
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

                        List<GlobalEventSchema> eventList = eventsJson.get(eventKey,
                                List.of(eventsJson.arrayCase(entry -> {

                                    ArrayList<GlobalEventSchema> newEventList = new ArrayList<>();
                                    TracedArray eventArray = entry.getValue();
                                    for (int i : eventArray) {

                                        TracedEntry<TracedDictionary> eventEntry = eventArray.getAsDict(i, false);
                                        TracedDictionary eventJson = eventEntry.getValue();

                                        GlobalEventSchema event = new GlobalEventSchema(eventJson);
                                        newEventList.add(event);
                                    }

                                    return newEventList;
                                }),

                                        eventsJson.dictCase(entry -> {

                                            TracedDictionary eventJson = entry.getValue();
                                            return List.of(new GlobalEventSchema(eventJson));
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

    private ImmutableMap<String, RecipeSet> createRecipes(TracedDictionary json) {

        HashMap<String, RecipeSet> recipesMap = new HashMap<>();

        // Validate each crafting category type.
        for (String category : json) {

            try {

                TracedEntry<TracedDictionary> categoryEntry = json.getAsDict(category, false);
                TracedDictionary categoryJson = categoryEntry.getValue();

                RecipeSet recipeSet = new RecipeSet(categoryJson);
                recipesMap.put(category, recipeSet);
            } catch (LoggedException _) {
            }
        }

        return new ImmutableMap<>(recipesMap);
    }
}
