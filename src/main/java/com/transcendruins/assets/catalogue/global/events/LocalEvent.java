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

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>LocalEvent</code>: A class representing a local event, which is an
 * event relative to a global location.
 */
public final class LocalEvent {

    /**
     * <code>String</code>: The global location of this local event.
     */
    private final String location;

    /**
     * Retrieves the global location of this local event.
     *
     * @return <code>String</code>: The <code>location</code> field of this
     *         <code>LocalEvent</code> instance.
     */
    public String getLocation() {

        return location;
    }

    /**
     * <code>String</code>: The event key of this local event.
     */
    private final String event;

    /**
     * Retrieves the event key of this local event.
     *
     * @return <code>String</code>: The <code>event</code> field of this
     *         <code>LocalEvent</code> instance.
     */
    public String getEvent() {

        return event;
    }

    /**
     * Creates a new instance of the <code>LocalEvent</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON information of this
     *             <code>LocalEvent</code> instance.
     * @throws LoggedException If there is an error processing the JSON information.
     */
    public LocalEvent(TracedDictionary json) throws LoggedException {

        TracedEntry<String> locationEntry = json.getAsString("location", false, null);
        location = locationEntry.getValue();

        TracedEntry<String> eventEntry = json.getAsString("event", false, null);
        event = eventEntry.getValue();
    }
}
