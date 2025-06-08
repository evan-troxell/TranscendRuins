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
import java.util.HashMap;
import java.util.List;

import com.transcendruins.assets.global.events.GlobalEventSchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.fileexceptions.FileException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>GlobalSchema</code>: A class representing the global map schema of a
 * <code>ContentPack</code> instance.
 */
public final class GlobalSchema {

    /**
     * <code>ImmutableMap&lt;String, GlobalLocation&gt;</code>: The global locations
     * of this <code>GlobalSchema</code> instance.
     */
    private final ImmutableMap<String, GlobalLocation> locations;

    /**
     * Retrieves the global locations of this <code>GlobalSchema</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, GlobalLocation&gt;</code>: The
     *         <code>locations</code> field of this <code>GlobalSchema</code>
     *         instance.
     */
    public ImmutableMap<String, GlobalLocation> getLocations() {

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

    /**
     * Creates a new instance of the <code>GlobalSchema</code> class.
     * 
     * @param path <code>TracedPath</code>: The filepath to the JSON information of
     *             this <code>GlobalSchema</code> instance.
     */
    public GlobalSchema(TracedPath path) {

        HashMap<String, GlobalLocation> locationsMap = new HashMap<>();
        HashMap<String, ImmutableList<GlobalEventSchema>> eventsMap = new HashMap<>();

        // If the path exists, attempt to construct the global locations and events.
        if (path.exists()) {

            try {
                TracedDictionary json = JSONOperator.retrieveJSON(path);

                // Attempt to process the locations entry.
                try {

                    TracedEntry<TracedDictionary> locationsEntry = json.getAsDict("locations", true);
                    if (locationsEntry.containsValue()) {

                        TracedDictionary locationsJson = locationsEntry.getValue();
                        for (String locationKey : locationsJson) {

                            // In the case that any location is invalid, the rest of the locations should
                            // work properly.
                            try {

                                GlobalLocation location = new GlobalLocation(locationsJson, locationKey);

                                locationsMap.put(locationKey, location);
                            } catch (LoggedException _) {
                            }
                        }
                    }
                } catch (LoggedException _) {
                }

                // Attempt to process the events entry.
                try {

                    TracedEntry<TracedDictionary> eventsEntry = json.getAsDict("events", true);
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
            } catch (FileException _) {
            }
        }

        locations = new ImmutableMap<>(locationsMap);
        events = new ImmutableMap<>(eventsMap);
    }
}
