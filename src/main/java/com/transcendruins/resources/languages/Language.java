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

package com.transcendruins.resources.languages;

import java.util.HashMap;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Language</code>: A class representing the entries within a single
 * language file.
 */
public final class Language {

    /**
     * <code>ImmutableMap&lt;String, String&gt;</code>: The text mappings of this
     * <code>Language</code> instance.
     */
    private final ImmutableMap<String, String> mappings;

    /**
     * Retrieves the text mappings of this <code>Language</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, String&gt;</code>: The
     *         <code>mappings</code> field of this <code>Language</code> instance.
     */
    public ImmutableMap<String, String> getMappings() {

        return mappings;
    }

    /**
     * Creates a new instance of the <code>Language</code> class.
     * 
     * @param path <code>TracedPath</code>: The filepath to this
     *             <code>Language</code> instance.
     * @throws LoggedException Thrown if an exception is raised while parsing the
     *                         JSON information.
     */
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
