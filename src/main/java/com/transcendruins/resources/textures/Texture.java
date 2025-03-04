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

import javax.swing.ImageIcon;

import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.ReferenceWithoutDefinitionException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class Texture {

    public static final Color BLANK = new Color(0);

    private final WeightedRoll<TracedPath> entries;

    public Texture(TracedDictionary json, String key, TracedPath root, List<TracedPath> texturePaths)
            throws LoggedException {

        entries = json.get(key, List.of(

                json.arrayCase(entry -> {

                    TracedArray textureJson = entry.getValue();
                    if (textureJson.isEmpty()) {

                        throw new CollectionSizeException(entry, textureJson);
                    }

                    ArrayList<WeightedRoll.Entry<TracedPath>> jsonEntries = new ArrayList<>();

                    for (int i : textureJson.getIndices()) {

                        TracedEntry<String> pathEntry = textureJson.getAsString(i, false, null);
                        TracedPath path = root.extend(pathEntry.getValue());

                        if (!texturePaths.contains(path)) {

                            throw new ReferenceWithoutDefinitionException(pathEntry, "Path");
                        }

                        jsonEntries.add(new WeightedRoll.Entry<>(path));
                    }

                    return new WeightedRoll<>(jsonEntries);
                }),

                json.dictCase(entry -> {

                    TracedDictionary textureJson = entry.getValue();

                    if (textureJson.getKeys().isEmpty()) {

                        throw new MissingPropertyException(entry);
                    }

                    ArrayList<WeightedRoll.Entry<TracedPath>> jsonEntries = new ArrayList<>();

                    for (String pathString : textureJson.getKeys()) {

                        TracedPath path = root.extend(pathString);
                        TracedEntry<Double> weightEntry = textureJson.getAsDouble(pathString, false, null,
                                num -> num > 0);

                        if (!texturePaths.contains(path)) {

                            throw new ReferenceWithoutDefinitionException(path, weightEntry, "Path");
                        }

                        jsonEntries.add(new WeightedRoll.Entry<>(path, weightEntry.getValue()));
                    }

                    return new WeightedRoll<>(jsonEntries);
                }),

                json.stringCase(entry -> {
                    TracedPath path = root.extend(entry.getValue());

                    if (!texturePaths.contains(path)) {

                        throw new ReferenceWithoutDefinitionException(entry, "Path");
                    }

                    return new WeightedRoll<>(path);
                })));
    }

    public ImageIcon getTexture(double random) {

        TracedPath path = entries.get(random);

        return path != null ? path.retrieveImage() : null;
    }
}
