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
package com.transcendruins.assets;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>SelectionType</code>: An enum class representing methods of selection
 * during a generation process.
 */
public enum SelectionType {

    /**
     * <code>SelectionType</code>: A selection type referring to the addition of
     * each additional element in order.
     */
    SEQUENCE,

    /**
     * <code>SelectionType</code>: A selection type referring to the selection of
     * random elements.
     */
    SELECT;

    /**
     * Parses a value from a <code>TracedCollection</code> instance into a
     * <code>SelectionType</code> enum.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to search for.
     * @return <code>SelectionType</code>: The parsed selection type.
     * @throws LoggedException Thrown if the selection type could not be found or
     *                         parsed.
     */
    public static final SelectionType parseSelectionType(TracedCollection collection, Object key)
            throws LoggedException {

        TracedEntry<String> entry = collection.getAsString(key, false, null);

        return switch (entry.getValue()) {

        case "sequence", "sequential" -> SEQUENCE;

        case "select", "selection" -> SELECT;

        default -> throw new UnexpectedValueException(entry);
        };
    }
}
