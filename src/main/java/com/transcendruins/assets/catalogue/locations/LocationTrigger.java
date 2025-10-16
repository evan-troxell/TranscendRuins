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

import java.time.ZonedDateTime;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>LocationTrigger</code>: A class representing the event settings of a
 * global location.
 */
public final class LocationTrigger {

    /**
     * <code>LocationTrigger</code>: The default location trigger presets.
     */
    public static final LocationTrigger DEFAULT = new LocationTrigger();

    /**
     * Creates a new instance of the <code>LocationTrigger</code> class.
     */
    public LocationTrigger() {

        duration = -1.0;
        startTimestamp = null;
        displayCountdownTimer = false;
        endOnExit = false;
    }

    /**
     * <code>double</code>: The duration of this <code>LocationTrigger</code>
     * instance, in minutes. This value is guaranteed to be greater than or equal to
     * 0.0, except for values of -1 which represent no countdown.
     */
    private final double duration;

    /**
     * Retrieves the duration of this <code>LocationTrigger</code> instance, in
     * minutes.
     *
     * @return <code>double</code>: The <code>duration</code> field of this
     *         <code>LocationTrigger</code> instance.
     */
    public double getDuration() {

        return duration;
    }

    /**
     * <code>ZonedDateTime</code>: The timestamp at which the duration of this
     * <code>LocationTrigger</code> instance started.
     */
    private final ZonedDateTime startTimestamp;

    /**
     * Retrieves the timestamp at which the duration of this
     * <code>LocationTrigger</code> instance started.
     *
     * @return <code>ZonedDateTime</code>: The <code>startTimestamp</code> field of
     *         this <code>LocationTrigger</code> instance.
     */
    public ZonedDateTime getStartTimestamp() {

        return startTimestamp;
    }

    /**
     * <code>boolean</code>: Whether or not this <code>LocationTrigger</code>
     * instance should display the countdown timer.
     */
    private final boolean displayCountdownTimer;

    /**
     * Retrieves whether or not this <code>LocationTrigger</code> instance should
     * display the countdown timer.
     * 
     * @return <code>boolean</code>: The <code>displayCountdownTimer</code> field of
     *         this <code>LocationTrigger</code> instance.
     */
    public boolean getDisplayCountdownTimer() {

        return displayCountdownTimer;
    }

    /**
     * <code>boolean</code>: Whether or not this <code>LocationTrigger</code>
     * instance should deactivate once the player exits the location.
     */
    private final boolean endOnExit;

    /**
     * Retrieves whether or not this <code>LocationTrigger</code> instance should
     * deactivate once the player exits the location.
     *
     * @return <code>boolean</code>: The <code>endOnExit</code> field of this
     *         <code>LocationTrigger</code> instance.
     */
    public boolean getEndOnExit() {

        return endOnExit;
    }

    /**
     * Creates a new instance of the <code>LocationTrigger</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON to parse into this
     *             <code>LocationTrigger</code> instance.
     * @throws LoggedException Thrown if there are any issues or missing fields when
     *                         parsing the JSON.
     */
    public LocationTrigger(TracedDictionary json) throws LoggedException {

        TracedEntry<Double> durationEntry = json.getAsDouble("duration", true, -1.0, num -> num > 0 || num == -1);
        duration = durationEntry.getValue();

        TracedEntry<ZonedDateTime> startTimestampEntry = json.getAsTimestamp("startTimestamp", true);
        startTimestamp = startTimestampEntry.getValue();

        TracedEntry<Boolean> displayCountdownTimerEntry = json.getAsBoolean("displayCountdownTimer", true, true);
        displayCountdownTimer = displayCountdownTimerEntry.getValue() && duration > -1;

        TracedEntry<Boolean> endOnExitEntry = json.getAsBoolean("endOnExit", true, false);
        endOnExit = endOnExitEntry.getValue();

        // TODO: retain location data option
    }
}
