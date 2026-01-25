/* Copyright 2026 Evan Troxell
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

package com.transcendruins.assets.modelassets.primaryassets.inventory;

import java.util.ArrayList;

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

    public final Boolean lockAdd;

    public final Boolean getLockAdd() {

        return lockAdd;
    }

    public final Boolean lockRemove;

    public final Boolean getLockRemove() {

        return lockRemove;
    }

    private InventorySlotSchema(boolean isBase) {

        super(isBase);

        acceptedTypes = null;
        modelAttachment = null;

        lockAdd = null;
        lockRemove = null;
    }

    public InventorySlotSchema(TracedDictionary json, boolean isBase) throws LoggedException {

        super(isBase);

        TracedEntry<TracedArray> acceptedTypesEntry = json.getAsArray("acceptedTypes", true);
        if (acceptedTypesEntry.containsValue()) {

            ArrayList<String> acceptedTypesList = new ArrayList<>();

            TracedArray acceptedTypesJson = acceptedTypesEntry.getValue();
            for (int i : acceptedTypesJson) {

                TracedEntry<String> acceptedTypeEntry = acceptedTypesJson.getAsString(i, false, null);
                String acceptedType = acceptedTypeEntry.getValue();

                acceptedTypesList.add(acceptedType);
            }

            acceptedTypes = new ImmutableList<>(acceptedTypesList);
        } else {

            acceptedTypes = null;
        }

        TracedEntry<String> modelAttachmentEntry = json.getAsString("modelAttachment", true, null);
        modelAttachment = modelAttachmentEntry.getValue();

        TracedEntry<Boolean> lockAddEntry = json.getAsBoolean("lockAdd", true, null);
        lockAdd = lockAddEntry.getValue();

        TracedEntry<Boolean> lockRemoveEntry = json.getAsBoolean("lockRemove", true, null);
        lockRemove = lockRemoveEntry.getValue();
    }
}
