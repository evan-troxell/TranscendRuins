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
import java.util.Map;

import javax.swing.ImageIcon;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.selection.WeightedRoll;

/**
 * <code>Texture</code>: A class representing a single texture entry.
 */
public final class Texture {

    /**
     * <code>Color</code>: A blank color.
     */
    public static final Color BLANK = new Color(0);

    /**
     * <code>WeightedRoll&lt;String&gt;</code>: The filepath entries of this
     * <code>Texture</code> instance.
     */
    private final WeightedRoll<String> entries;

    /**
     * Creates a new instance of the <code>Texture</code> class
     * 
     * @param json <code>TracedDictionary</code>: The JSON to parse from.
     * @param key  <code>Object</code>: The key to search for.
     * @throws LoggedException Thrown if an error occurs while parsing the
     *                         collection.
     */
    public Texture(TracedDictionary json, String key) throws LoggedException {

        String regExp = "^/+";

        entries = json.getAsStringRoll(key, false, null, entry -> entry.getValue().replaceFirst(regExp, ""),
                (_, stringKey) -> stringKey.replaceFirst(regExp, ""));
    }

    /**
     * Retrieves a texture from the available paths of this <code>Texture</code>
     * instance.
     * 
     * @param random <code>long</code>: The random id key to use.
     * @param paths  <code>Map&lt;String, TracedPath&gt;</code>: The supplied paths.
     * @return <code>StoredTexture</code>: The retrieved texture.
     */
    public final ImageIcon getTexture(long random, Map<String, TracedPath> paths) {

        String path = entries.get(random);

        return paths.containsKey(path) ? paths.get(path).retrieveImage() : null;
    }
}
