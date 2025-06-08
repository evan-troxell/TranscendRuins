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

package com.transcendruins.resources.textures;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class Texture {

    public static final Color BLANK = new Color(0);

    private final WeightedRoll<String> entries;

    public Texture(TracedDictionary json, String key) throws LoggedException {

        entries = json.get(key, List.of(

                json.arrayCase(entry -> {

                    TracedArray textureJson = entry.getValue();

                    ArrayList<WeightedRoll.Entry<String>> jsonEntries = new ArrayList<>();

                    for (int i : textureJson) {

                        TracedEntry<String> pathEntry = textureJson.getAsString(i, false, null);
                        String path = pathEntry.getValue();

                        jsonEntries.add(new WeightedRoll.Entry<>(path));
                    }

                    return new WeightedRoll<>(entry, textureJson, jsonEntries);
                }),

                json.dictCase(entry -> {

                    TracedDictionary textureJson = entry.getValue();

                    ArrayList<WeightedRoll.Entry<String>> jsonEntries = new ArrayList<>();

                    for (String pathString : textureJson) {

                        String path = pathString;
                        TracedEntry<Double> weightEntry = textureJson.getAsDouble(pathString, false, null,
                                num -> num > 0);

                        jsonEntries.add(new WeightedRoll.Entry<>(path, weightEntry.getValue()));
                    }

                    return new WeightedRoll<>(entry, textureJson, jsonEntries);
                }),

                json.stringCase(entry -> {

                    String path = entry.getValue();
                    return new WeightedRoll<>(path);
                })));
    }

    public ImageIcon getTexture(double random, Map<String, TracedPath> paths) {

        String path = entries.get(random);

        return paths.containsKey(path) ? paths.get(path).retrieveImage() : null;
    }
}
