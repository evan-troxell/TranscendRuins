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

package com.transcendruins.resources.sounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.sound.StoredSound;

/**
 * <code>Sound</code>: A class representing a single sound entry.
 */
public final class Sound {

    /**
     * <code>WeightedRoll&lt;String&gt;</code>: The filepath entries of this
     * <code>Sound</code> instance.
     */
    private final WeightedRoll<String> entries;

    /**
     * Creates a new instance of the <code>Sound</code> class
     * 
     * @param json <code>TracedDictionary</code>: The JSON to parse from.
     * @param key  <code>Object</code>: The key to search for.
     * @throws LoggedException Thrown if an error occurs while parsing the
     *                         collection.
     */
    public Sound(TracedDictionary json, String key) throws LoggedException {

        entries = json.get(key, List.of(

                json.arrayCase(entry -> {

                    TracedArray soundJson = entry.getValue();

                    ArrayList<WeightedRoll.Entry<String>> jsonEntries = new ArrayList<>();

                    for (int i : soundJson) {

                        TracedEntry<String> pathEntry = soundJson.getAsString(i, false, null);
                        String path = pathEntry.getValue();

                        jsonEntries.add(new WeightedRoll.Entry<>(path));
                    }

                    return new WeightedRoll<>(entry, soundJson, jsonEntries);
                }),

                json.dictCase(entry -> {

                    TracedDictionary soundJson = entry.getValue();

                    ArrayList<WeightedRoll.Entry<String>> jsonEntries = new ArrayList<>();

                    for (String pathString : soundJson) {

                        String path = pathString;
                        TracedEntry<Double> weightEntry = soundJson.getAsDouble(pathString, false, null,
                                num -> num > 0);

                        jsonEntries.add(new WeightedRoll.Entry<>(path, weightEntry.getValue()));
                    }

                    return new WeightedRoll<>(entry, soundJson, jsonEntries);
                }),

                json.stringCase(entry -> {

                    String path = entry.getValue();

                    return new WeightedRoll<>(path);
                })));

    }

    /**
     * Retrieves a sound from the available paths of this <code>Sound</code>
     * instance.
     * 
     * @param random <code>double</code>: The random ID key to use, in the range of
     *               <code>[0.0, 1.0]</code>.
     * @param paths  <code>Map&lt;String, TracedPath&gt;</code>: The supplied paths.
     * @return <code>StoredSound</code>: The retrieved sound.
     */
    public StoredSound getSound(double random, Map<String, TracedPath> paths) {

        String path = entries.get(random);

        return paths.containsKey(path) ? paths.get(path).retrieveAudio() : null;
    }
}
