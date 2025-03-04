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

package com.transcendruins.assets.modelassets.inventory;

import java.util.HashMap;

import com.transcendruins.assets.Attributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.KeyNameException;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class InventorySchema extends Attributes {

    /**
     * <code>String</code>: The regular expression used to ensure named slots do not
     * match an index pattern.
     */
    private static final String INDEX_PATTERN = "[+]?\\d+";

    public static final InventorySchema DEFAULT = new InventorySchema(true);

    private final Integer gridSlots;

    public Integer getGridSlots() {

        return gridSlots;
    }

    private final InventorySlotSchema grid;

    public InventorySlotSchema getGrid() {

        return grid;
    }

    private final ImmutableMap<String, InventorySlotSchema> named;

    public ImmutableMap<String, InventorySlotSchema> getNamed() {

        return named;
    }

    private InventorySchema(boolean isBase) {

        super(isBase);

        gridSlots = null;
        grid = null;

        named = null;
    }

    public InventorySchema(TracedDictionary json, boolean isBase)
            throws LoggedException {

        super(isBase);

        TracedEntry<TracedDictionary> gridEntry = json.getAsDict("grid", true);

        if (gridEntry.containsValue()) {

            TracedDictionary gridJson = gridEntry.getValue();

            TracedEntry<Integer> gridSlotsEntry = gridJson.getAsInteger("slots", true, null);

            gridSlots = gridSlotsEntry.containsValue() ? gridSlotsEntry.getValue() : null;
            grid = new InventorySlotSchema(gridJson, isBase);
        } else {

            gridSlots = null;
            grid = null;
        }

        TracedEntry<TracedDictionary> namedEntry = json.getAsDict("named", true);

        if (namedEntry.containsValue()) {

            HashMap<String, InventorySlotSchema> namedMap = new HashMap<>();

            TracedDictionary namedJson = namedEntry.getValue();

            for (String slotName : namedJson.getKeys()) {

                TracedEntry<TracedDictionary> namedSlotEntry = namedJson.getAsDict(slotName, false);
                TracedDictionary namedSlotJson = namedSlotEntry.getValue();

                // If the slot name matches an index pattern, raise an exceptiopn to ensure it
                // does not overlap with a grid slot name.
                if (slotName.matches(INDEX_PATTERN)) {

                    throw new KeyNameException(slotName, namedSlotEntry);
                }

                namedMap.put(slotName, new InventorySlotSchema(namedSlotJson, isBase));
            }

            named = new ImmutableMap<>(namedMap);
        } else {

            named = null;
        }
    }
}
