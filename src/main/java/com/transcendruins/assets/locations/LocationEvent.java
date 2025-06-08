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

import java.time.ZonedDateTime;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>LocationEvent</code>: A class representing the event settings of a
 * global location.
 */
public final class LocationEvent {

    /**
     * <code>InteractionType</code>: An enum class representing possible inactive
     * mode types of a global location.
     */
    public enum InactiveModeType {

        /**
         * <code>LocationEvent.InactiveModeType</code>: An inactive mode type
         * representing a location which is hidden while inactive.
         */
        HIDDEN,

        /**
         * <code>LocationEvent.InactiveModeType</code>: An inactive mode type
         * representing a location which is visible but locked while inactive.
         */
        LOCKED;

        /**
         * Parses a value from a <code>TracedCollection</code> instance into a
         * <code>LocationEvent.InactiveModeType</code> enum.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to search for.
         * @return <code>LocationEvent.InactiveModeType</code>: The parsed inactive mode
         *         type.
         * @throws LoggedException Thrown if the inactive mode type could not be parsed.
         */
        public static InactiveModeType parseInactiveModeType(TracedCollection collection, Object key)
                throws LoggedException {

            TracedEntry<String> entry = collection.getAsString(key, true, "hidden");

            return switch (entry.getValue()) {

            case "hidden" -> HIDDEN;

            case "locked" -> LOCKED;

            default -> throw new UnexpectedValueException(entry);
            };
        }
    }

    /**
     * <code>double</code>: The duration of this <code>LocationEvent</code>
     * instance, in minutes. This value is guaranteed to be greater than or equal to
     * 0.0, except for values of -1 which represent no countdown.
     */
    private final double duration;

    /**
     * Retrieves the duration of this <code>LocationEvent</code> instance, in
     * minutes.
     *
     * @return <code>double</code>: The <code>duration</code> field of this
     *         <code>LocationEvent</code> instance.
     */
    public double getDuration() {

        return duration;
    }

    /**
     * <code>ZonedDateTime</code>: The timestamp at which the duration of this
     * <code>LocationEvent</code> instance started.
     */
    private final ZonedDateTime startTimestamp;

    /**
     * Retrieves the timestamp at which the duration of this
     * <code>LocationEvent</code> instance started.
     *
     * @return <code>ZonedDateTime</code>: The <code>startTimestamp</code> field of
     *         this <code>LocationEvent</code> instance.
     */
    public ZonedDateTime getStartTimestamp() {

        return startTimestamp;
    }

    /**
     * <code>boolean</code>: Whether or not this <code>LocationEvent</code> instance
     * should display the countdown timer.
     */
    private final boolean displayCountdownTimer;

    /**
     * Retrieves whether or not this <code>LocationEvent</code> instance should
     * display the countdown timer.
     * 
     * @return <code>boolean</code>: The <code>displayCountdownTimer</code> field of
     *         this <code>LocationEvent</code> instance.
     */
    public boolean getDisplayCountdownTimer() {

        return displayCountdownTimer;
    }

    /**
     * <code>boolean</code>: Whether or not this <code>LocationEvent</code> instance
     * should retain location data after the location deactivates.
     */
    private final boolean retainLocationData;

    /**
     * Retrieves whether or not this <code>LocationEvent</code> instance should
     * retain location data after the location deactivates.
     *
     * @return <code>boolean</code>: The <code>retainLocationData</code> field of
     *         this <code>LocationEvent</code> instance.
     */
    public boolean getRetainLocationData() {

        return retainLocationData;
    }

    /**
     * <code>boolean</code>: Whether or not this <code>LocationEvent</code> instance
     * should deactivate once the player exits the location.
     */
    private final boolean endOnExit;

    /**
     * Retrieves whether or not this <code>LocationEvent</code> instance should
     * deactivate once the player exits the location.
     *
     * @return <code>boolean</code>: The <code>endOnExit</code> field of this
     *         <code>LocationEvent</code> instance.
     */
    public boolean getEndOnExit() {

        return endOnExit;
    }

    /**
     * <code>LocationEvent.InactiveModeType</code>: The inactive mode of this
     * <code>LocationEvent</code> instance.
     */
    private final LocationEvent.InactiveModeType inactiveMode;

    /**
     * Retrieves the inactive mode of this <code>LocationEvent</code> instance.
     *
     * @return <code>LocationEvent.InactiveModeType</code>: The
     *         <code>inactiveMode</code> field of this <code>LocationEvent</code>
     *         instance.
     */
    public LocationEvent.InactiveModeType getInactiveMode() {

        return inactiveMode;
    }

    /**
     * Creates a new instance of the <code>LocationEvent</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON to parse into this
     *             <code>LocationEvent</code> instance.
     * @throws LoggedException Thrown if there are any issues or missing fields when
     *                         parsing the JSON.
     */
    public LocationEvent(TracedDictionary json) throws LoggedException {

        TracedEntry<Double> durationEntry = json.getAsDouble("duration", true, -1.0, num -> num >= 0 || num == -1);
        duration = durationEntry.getValue();

        TracedEntry<ZonedDateTime> startTimestampEntry = json.getAsTimestamp("startTimestamp", true);
        startTimestamp = startTimestampEntry.getValue();

        TracedEntry<Boolean> displayCountdownTimerEntry = json.getAsBoolean("displayCountdownTimer", true, true);
        displayCountdownTimer = displayCountdownTimerEntry.getValue();

        TracedEntry<Boolean> retainLocationDataEntry = json.getAsBoolean("retainLocationData", true, false);
        retainLocationData = retainLocationDataEntry.getValue();

        TracedEntry<Boolean> endOnExitEntry = json.getAsBoolean("endOnExit", true, false);
        endOnExit = endOnExitEntry.getValue();

        inactiveMode = InactiveModeType.parseInactiveModeType(json, "inactiveMode");
    }
}
