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

package com.transcendruins.assets.catalogue.events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>GlobalEventSchema</code>: A class representing the parsed JSON
 * information of a global map event.
 */
public final class GlobalEventSchema {

    /**
     * <code>String</code>: The name of this <code>GlobalEventSchema</code>
     * instance.
     */
    private final String name;

    /**
     * <code>String</code>: The description of this <code>GlobalEventSchema</code>
     * instance.
     */
    private final String description;

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The locations which this
     * <code>GlobalEventSchema</code> instance should activate.
     */
    private final ImmutableList<String> activateLocations;

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The locations which this
     * <code>GlobalEventSchema</code> instance should deactivate.
     */
    private final ImmutableList<String> deactivateLocations;

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The global events which this
     * <code>GlobalEventSchema</code> instance should call.
     */
    private final ImmutableList<String> globalEvents;

    /**
     * <code>ImmutableList&lt;LocalEvent&gt;</code>: The local events which this
     * <code>GlobalEventSchema</code> instance should call.
     */
    private final ImmutableList<LocalEvent> localEvents;

    /**
     * <code>double</code>: The duration, in minutes, of this
     * <code>GlobalEventSchema</code> instance. A value of -1.0 indicates an
     * unlimited duration.
     */
    private final double duration;

    /**
     * <code>double</code>: The cooldown, in minutes, of this
     * <code>GlobalEventSchema</code> instance.
     */
    private final double cooldown;

    /**
     * <code>int</code>: The number of tasks required by this
     * <code>GlobalEventSchema</code> instance to be considered complete.
     */
    private final int tasksRequired;

    /**
     * <code>int</code>: The number of tasks which can simultaneously be active for
     * this <code>GlobalEventSchema</code> instance.
     */
    private final int tasksActive;

    /**
     * <code>double</code>: The maximum duration, in minutes, of each task for this
     * <code>GlobalEventSchema</code> instance. A value of -1.0 indicates an
     * unlimited duration.
     */
    private final double taskDuration;

    /**
     * <code>double</code>: The cooldown, in minutes, of each task for this
     * <code>GlobalEventSchema</code> instance.
     */
    private final double taskCooldown;

    /**
     * <code>WeightedRoll&lt;GlobalEventTask&gt;</code>: The tasks which are
     * available to be performed by this <code>GlobalEventSchema</code> instance.
     */
    private final WeightedRoll<GlobalEventTask> tasks;

    /**
     * <code>ImmutableSet&lt;String&gt;</code>: The global locations which this
     * <code>GlobalEventSchema</code> instance depends on.
     */
    private final ImmutableSet<String> locationDependencies;

    /**
     * <code>ImmutableSet&lt;String&gt;</code>: The global events which this
     * <code>GlobalEventSchema</code> instance depends on.
     */
    private final ImmutableSet<String> eventDependencies;

    /**
     * Creates a new instance of the <code>GlobalEventSchema</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON information of this
     *             <code>GlobalEventSchema</code> instance.
     * @throws LoggedException Thrown if the JSON information is invalid or does not
     *                         conform to the expected schema.
     */
    public GlobalEventSchema(TracedDictionary json) throws LoggedException {

        HashSet<String> locationDependenciesList = new HashSet<>();
        HashSet<String> eventDependenciesList = new HashSet<>();

        TracedEntry<String> nameEntry = json.getAsString("name", false, null);
        name = nameEntry.getValue();

        TracedEntry<String> descriptionEntry = json.getAsString("description", false, null);
        description = descriptionEntry.getValue();

        ArrayList<String> activateLocationsList = new ArrayList<>();
        ArrayList<String> deactivateLocationsList = new ArrayList<>();

        TracedEntry<TracedDictionary> locationsEntry = json.getAsDict("locations", true);
        if (locationsEntry.containsValue()) {

            TracedDictionary locationsJson = locationsEntry.getValue();

            // Process the location(s) which should be activated.
            locationsJson.compute("activate", List.of(

                    locationsJson.stringCase(entry -> {
                        String activateLocation = entry.getValue();

                        activateLocationsList.add(activateLocation);
                        locationDependenciesList.add(activateLocation);
                        return null;
                    }),

                    locationsJson.arrayCase(entry -> {
                        TracedArray activateJson = entry.getValue();
                        for (int i : activateJson) {

                            TracedEntry<String> activateLocationEntry = activateJson.getAsString(i, false, null);
                            String activateLocation = activateLocationEntry.getValue();

                            activateLocationsList.add(activateLocation);
                            locationDependenciesList.add(activateLocation);
                        }

                        return null;
                    }), locationsJson.nullCase(_ -> null)));

            // Process the location(s) which should be deactivated.
            locationsJson.compute("deactivate", List.of(

                    locationsJson.stringCase(entry -> {
                        String deactivateLocation = entry.getValue();

                        deactivateLocationsList.add(deactivateLocation);
                        locationDependenciesList.add(deactivateLocation);
                        return null;
                    }),

                    locationsJson.arrayCase(entry -> {
                        TracedArray deactivateJson = entry.getValue();
                        for (int i : deactivateJson) {

                            TracedEntry<String> deactivateLocationEntry = deactivateJson.getAsString(i, false, null);
                            String deactivateLocation = deactivateLocationEntry.getValue();

                            deactivateLocationsList.add(deactivateLocation);
                            locationDependenciesList.add(deactivateLocation);
                        }

                        return null;
                    }), locationsJson.nullCase(_ -> null)));
        }

        activateLocations = new ImmutableList<>(activateLocationsList);
        deactivateLocations = new ImmutableList<>(deactivateLocationsList);

        TracedEntry<TracedDictionary> eventsEntry = json.getAsDict("events", true);
        if (eventsEntry.containsValue()) {

            TracedDictionary eventsJson = eventsEntry.getValue();

            // Process the global event(s) which should be called.
            globalEvents = eventsJson.get("global", List.of(

                    eventsJson.stringCase(entry -> {

                        String globalEvent = entry.getValue();

                        eventDependenciesList.add(globalEvent);
                        return new ImmutableList<>(globalEvent);
                    }),

                    eventsJson.arrayCase(entry -> {

                        TracedArray globalJson = entry.getValue();

                        ArrayList<String> globalEventsList = new ArrayList<>();
                        for (int i : globalJson) {

                            TracedEntry<String> globalEventEntry = globalJson.getAsString(i, false, null);
                            String globalEvent = globalEventEntry.getValue();

                            globalEventsList.add(globalEvent);
                            eventDependenciesList.add(globalEvent);
                        }

                        return new ImmutableList<>(globalEventsList);
                    }), eventsJson.nullCase(_ -> new ImmutableList<>())));

            // Process the local event(s) which should be called.
            localEvents = eventsJson.get("local", List.of(eventsJson.dictCase(entry -> {

                TracedDictionary localJson = entry.getValue();
                LocalEvent local = new LocalEvent(localJson);

                locationDependenciesList.add(local.getLocation());
                return new ImmutableList<>(local);
            }), eventsJson.arrayCase(entry -> {

                TracedArray localJson = entry.getValue();

                ArrayList<LocalEvent> localEventsList = new ArrayList<>();
                for (int i : localJson) {

                    TracedEntry<TracedDictionary> localEntry = localJson.getAsDict(i, false);
                    TracedDictionary localJsonValue = localEntry.getValue();

                    LocalEvent local = new LocalEvent(localJsonValue);

                    localEventsList.add(local);
                    locationDependenciesList.add(local.getLocation());
                }

                return new ImmutableList<>(localEventsList);
            }), eventsJson.nullCase(_ -> new ImmutableList<>())));
        } else {

            // If there are no events, set the global and local events to empty lists.
            globalEvents = new ImmutableList<>();
            localEvents = new ImmutableList<>();
        }

        // The duration should be positive unless it is -1.0, which represents an
        // unlimited duration.
        TracedEntry<Double> durationEntry = json.getAsDouble("duration", true, -1.0, num -> num >= 0.0 || num == -1.0);
        duration = durationEntry.getValue();

        // The cooldown should be greater than or equal to 0.0.
        TracedEntry<Double> cooldownEntry = json.getAsDouble("cooldown", true, 0.0, num -> num >= 0.0);
        cooldown = cooldownEntry.getValue();

        TracedEntry<TracedDictionary> tasksEntry = json.getAsDict("tasks", true);
        if (tasksEntry.containsValue()) {

            TracedDictionary tasksJson = tasksEntry.getValue();

            // There should be a positive number of required tasks.
            TracedEntry<Integer> requiredEntry = tasksJson.getAsInteger("required", false, null, num -> num > 0);
            tasksRequired = requiredEntry.getValue();

            // There should be a positive number of active tasks which should not exceeed
            // the required tasks.
            TracedEntry<Integer> activeEntry = tasksJson.getAsInteger("active", false, null,
                    num -> 0 < num && num <= tasksRequired);
            tasksActive = activeEntry.getValue();

            // The task duration should be positive unless it is -1.0, which represents an
            // unlimited duration.
            TracedEntry<Double> taskDurationEntry = tasksJson.getAsDouble("duration", true, -1.0,
                    num -> num >= 0.0 || num == -1.0);
            taskDuration = taskDurationEntry.getValue();

            // The task cooldown should be greater than or equal to 0.0.
            TracedEntry<Double> taskCooldownEntry = tasksJson.getAsDouble("cooldown", true, 0.0, num -> num >= 0.0);
            taskCooldown = taskCooldownEntry.getValue();

            // Process the tasks as a weighted roll.
            tasks = tasksJson.getAsRoll("tasks", false, null, entry -> {

                GlobalEventTask task = new GlobalEventTask(entry.getValue());
                if (task.hasLocation()) {

                    locationDependenciesList.addAll(task.getLocation());
                }

                return task;
            });
        } else {

            tasksRequired = 0;
            tasksActive = 0;
            taskDuration = -1.0;
            taskCooldown = 0.0;
            tasks = null;
        }

        locationDependencies = new ImmutableSet<>(locationDependenciesList);
        eventDependencies = new ImmutableSet<>(eventDependenciesList);

        // TODO: add start condition and max number of times event can be called

    }
}