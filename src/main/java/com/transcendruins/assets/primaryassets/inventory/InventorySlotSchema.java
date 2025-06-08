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

package com.transcendruins.assets.primaryassets.inventory;

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.assets.Attributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class InventorySlotSchema extends Attributes {

    public static final InventorySlotSchema DEFAULT = new InventorySlotSchema(true);

    private final ImmutableList<String> acceptedTypes;

    public ImmutableList<String> getAcceptedTypes() {

        return acceptedTypes;
    }

    public final String modelSocket;

    public String getModelSocket() {

        return modelSocket;
    }

    private InventorySlotSchema(boolean isBase) {

        super(isBase);

        acceptedTypes = null;
        modelSocket = null;
    }

    public InventorySlotSchema(TracedDictionary json, boolean isBase) throws LoggedException {

        super(isBase);

        if (json.containsKey("acceptedTypes")) {

            ArrayList<String> acceptedTypesList = new ArrayList<>();

            json.get("acceptedTypes", List.of(

                    json.arrayCase(entry -> {

                        TracedArray acceptedTypesJson = entry.getValue();

                        for (int i : acceptedTypesJson) {

                            TracedEntry<String> acceptedTypeEntry = acceptedTypesJson.getAsString(i, false, null);
                            String acceptedType = acceptedTypeEntry.getValue();

                            acceptedTypesList.add(acceptedType);
                        }

                        return null;
                    }),

                    json.stringCase(entry -> {

                        String acceptedTypesJson = entry.getValue();

                        acceptedTypesList.add(acceptedTypesJson);
                        return null;
                    })));

            acceptedTypes = new ImmutableList<>(acceptedTypesList);
        } else {

            acceptedTypes = null;
        }

        TracedEntry<String> modelSocketEntry = json.getAsString("modelSocket", true, null);
        modelSocket = modelSocketEntry.getValue();
    }
}
