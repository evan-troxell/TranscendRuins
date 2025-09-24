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

package com.transcendruins.assets.catalogue.global.events;

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.PropertyHolder;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>GlobalEventTask</code>: A class representing a task in a global event.
 */
public final class GlobalEventTask {

    /**
     * <code>String</code>: The name of this task.
     */
    private final String name;

    /**
     * Retrieves the name of this task.
     *
     * @return <code>String</code>: The <code>name</code> field of this
     *         <code>GlobalEventTask</code> instance.
     */
    public String getName() {

        return name;
    }

    /**
     * <code>String</code>: The description of this task.
     */
    private final String description;

    /**
     * Retrieves the description of this task.
     *
     * @return <code>String</code>: The <code>description</code> field of this
     *         <code>GlobalEventTask</code> instance.
     */
    public String getDescription() {

        return description;
    }

    /**
     * <code>boolean</code>: Whether or not this task has a location or locations
     * associated with it.
     */
    private final boolean hasLocation;

    /**
     * Retrieves whether or not this task has a location or locations associated
     * with it.
     *
     * @return <code>boolean</code>: The <code>hasLocation</code> field of this
     *         <code>GlobalEventTask</code> instance.
     */
    public boolean hasLocation() {

        return hasLocation;
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The location(s) of this task.
     */
    private final ImmutableList<String> location;

    /**
     * Retrieves the location(s) of this task.
     *
     * @return <code>ImmutableList&lt;String&gt;</code>: The <code>location</code>
     *         field of this <code>GlobalEventTask</code> instance.
     */
    public ImmutableList<String> getLocation() {

        return location;
    }

    /**
     * <code>boolean</code>: Whether or not this task has a counter.
     */
    private final boolean hasCounter;

    /**
     * Retrieves whether or not this task has a counter.
     *
     * @return <code>boolean</code>: The <code>hasCounter</code> field of this
     *         <code>GlobalEventTask</code> instance.
     */
    public boolean hasCounter() {

        return hasCounter;
    }

    /**
     * <code>TRScript</code>: The method to evaluate the counter value of this task.
     */
    private final TRScript counterValue;

    /**
     * Retrieves the method to evaluate the counter value of this task.
     *
     * @return <code>TRScript</code>: The <code>counterValue</code> field of this
     *         <code>GlobalEventTask</code> instance.
     */
    private final double counterTarget;

    /**
     * Determines the counter value of this task for a given asset.
     * 
     * @param asset <code>PropertyHolder</code>: The <code>PropertyHolder</code>
     *              asset to evaluate the counter for.
     * @return <code>String</code>: The counter value formatted as a string.
     */
    public String getCounter(PropertyHolder asset) {

        double value = counterValue.evaluateDouble(asset);
        String valueString = (value % 1 == 0.0) ? String.valueOf((int) value) : String.valueOf(value);

        String targetString = (counterTarget % 1 == 0.0) ? String.valueOf((int) counterTarget)
                : String.valueOf(counterTarget);

        return "%s/%s".formatted(valueString, targetString);

    }

    /**
     * <code>ImmutableList&lt;TRScript&gt;</code>: The conditions required to be met
     * for this task to pass.
     */
    private final ImmutableList<TRScript> conditions;

    /**
     * Determines whether or not this task passes for a given asset.
     * 
     * @param asset <code>PropertyHolder</code>: The asset to evaluate the
     *              conditions against.
     * @return <code>boolean</code>: Whether or not the conditions pass for the
     *         given asset.
     */
    public boolean passes(PropertyHolder asset) {

        for (TRScript condition : conditions) {

            if (!condition.evaluateBoolean(asset)) {

                return false;
            }
        }

        return true;
    }

    /**
     * Creates a new instance of the <code>GlobalEventTask</code> class.
     *
     * @param json <code>TracedDictionary</code>: The JSON information of this
     *             <code>GlobalEventTask</code> instance.
     * @throws LoggedException If there is an error processing the JSON information.
     */
    public GlobalEventTask(TracedDictionary json) throws LoggedException {

        TracedEntry<String> nameEntry = json.getAsString("name", false, null);
        name = nameEntry.getValue();

        TracedEntry<String> descriptionEntry = json.getAsString("description", false, null);
        description = descriptionEntry.getValue();

        location = json.get("location", List.of(

                // Process a single location.
                json.stringCase(entry -> {

                    return new ImmutableList<>(entry.getValue());
                }),

                // Process a list of locations.
                json.arrayCase(entry -> {

                    ArrayList<String> locationList = new ArrayList<>();

                    TracedArray locationArray = entry.getValue();
                    for (int i : locationArray) {

                        TracedEntry<String> locationEntry = locationArray.getAsString(i, false, null);
                        locationList.add(locationEntry.getValue());
                    }

                    return new ImmutableList<>(locationList);
                }),

                // Process no locations.
                json.nullCase(_ -> new ImmutableList<>())));
        hasLocation = !location.isEmpty();

        TracedEntry<TracedDictionary> counterEntry = json.getAsDict("counter", true);
        hasCounter = counterEntry.containsValue();
        if (hasCounter) {

            TracedDictionary counterJson = counterEntry.getValue();

            counterValue = new TRScript(counterJson, "value");

            TracedEntry<Double> counterTargetEntry = counterJson.getAsDouble("target", false, null);
            counterTarget = counterTargetEntry.getValue();
        } else {

            counterValue = null;
            counterTarget = 0.0;
        }

        conditions = json.get("conditions", List.of(json.arrayCase(entry -> {

            ArrayList<TRScript> conditionsList = new ArrayList<>();

            TracedArray conditionsJson = entry.getValue();
            for (int i : conditionsJson) {

                TracedEntry<TRScript> conditionEntry = conditionsJson.getAsScript(i, false);
                TRScript condition = conditionEntry.getValue();
                conditionsList.add(condition);
            }

            return new ImmutableList<>(conditionsList);
        }), json.nullCase(_ -> new ImmutableList<>()), json.scriptCase(entry -> {

            TRScript condition = entry.getValue();
            return new ImmutableList<>(condition);
        })));
    }
}
