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

package com.transcendruins.assets.primaryassets;

import com.transcendruins.App;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>InteractionType</code>: An enum class representing possible
 * interactions with a <code>PrimaryAssetInstance</code> instance.
 */
public enum InteractionType {

    /**
     * <code>InteractionType</code>: An interaction type representing no
     * interaction.
     */
    NONE,

    /**
     * <code>InteractionType</code>: An interaction type representing an interaction
     * with an inventory.
     */
    INVENTORY,

    /**
     * <code>InteractionType</code>: An interaction type representing an interaction
     * with a passageway.
     */
    PASSAGEWAY;

    /**
     * <code>String</code>: The camel-case version of this
     * <code>InteractionType</code> instance.
     */
    private final String name = App.toCamelCase(name());

    /**
     * Parses a value from a <code>TracedCollection</code> instance into a
     * <code>InteractionType</code> enum.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to search for.
     * @return <code>InteractionType</code>: The parsed interaction type.
     * @throws LoggedException Thrown if the interaction type could not be found or
     *                         parsed.
     */
    public static InteractionType parseInteractionType(TracedCollection collection, Object key) throws LoggedException {

        TracedEntry<String> entry = collection.getAsString(key, false, null);

        return switch (entry.getValue()) {

        case "none" -> NONE;

        case "inventory" -> INVENTORY;

        case "passageway" -> PASSAGEWAY;

        default -> throw new UnexpectedValueException(entry);
        };
    }

    @Override
    public String toString() {

        return name;
    }
}
