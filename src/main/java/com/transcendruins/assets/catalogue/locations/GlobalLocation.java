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

import static com.transcendruins.assets.AssetType.LOCATION;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class GlobalLocation {

    private final AssetPresets location;

    public final AssetPresets AssetPresets() {

        return location;
    }

    private final LocationTrigger trigger;

    public final LocationTrigger getTrigger() {

        return trigger;
    }

    public GlobalLocation(TracedCollection json) throws LoggedException {

        TracedEntry<AssetPresets> presetsEntry = json.getAsPresets("location", false, LOCATION);
        location = presetsEntry.getValue();

        TracedEntry<TracedDictionary> triggerEntry = json.getAsDict("trigger", true);
        if (triggerEntry.containsValue()) {

            TracedDictionary triggerJson = triggerEntry.getValue();
            trigger = new LocationTrigger(triggerJson);
        } else {

            trigger = null;
        }
    }
}
