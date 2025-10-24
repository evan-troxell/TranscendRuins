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

    public final ImmutableList<String> getAcceptedTypes() {

        return acceptedTypes;
    }

    public final String modelAttachment;

    public final String getModelAttachment() {

        return modelAttachment;
    }

    private InventorySlotSchema(boolean isBase) {

        super(isBase);

        acceptedTypes = null;
        modelAttachment = null;
    }

    public InventorySlotSchema(TracedDictionary json, boolean isBase) throws LoggedException {

        super(isBase);

        acceptedTypes = json.get("acceptedTypes", List.of(

                json.arrayCase(entry -> {

                    ArrayList<String> acceptedTypesList = new ArrayList<>();

                    TracedArray acceptedTypesJson = entry.getValue();
                    for (int i : acceptedTypesJson) {

                        TracedEntry<String> acceptedTypeEntry = acceptedTypesJson.getAsString(i, false, null);
                        String acceptedType = acceptedTypeEntry.getValue();

                        acceptedTypesList.add(acceptedType);
                    }

                    return new ImmutableList<>(acceptedTypesList);
                }),

                json.stringCase(entry -> {

                    String acceptedType = entry.getValue();
                    return new ImmutableList<>(acceptedType);
                }), json.nullCase(_ -> null)));

        TracedEntry<String> modelAttachmentEntry = json.getAsString("modelAttachment", true, null);
        modelAttachment = modelAttachmentEntry.getValue();
    }
}
