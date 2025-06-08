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

package com.transcendruins.resources.languages;

import java.util.HashMap;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class Language {

    private final ImmutableMap<String, String> mappings;

    public ImmutableMap<String, String> getMappings() {

        return mappings;
    }

    public Language(TracedPath path) throws LoggedException {

        HashMap<String, String> map = new HashMap<>();
        TracedDictionary json = JSONOperator.retrieveJSON(path);

        for (String key : json) {

            try {

                TracedEntry<String> mappingEntry = json.getAsString(key, false, null);
                map.put(key, mappingEntry.getValue());
            } catch (LoggedException _) {
            }
        }

        mappings = new ImmutableMap<>(map);
    }
}
