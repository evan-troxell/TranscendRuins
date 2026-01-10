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

package com.transcendruins.resources.textures;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>TextureSet</code>: A class representing a set of compiled textures.
 */
public final class TextureSet {

    /**
     * <code>TracedPath</code>: The path which leads to this <code>TextureSet</code>
     * instance.
     */
    private final TracedPath path;

    /**
     * Retrieves the path of this <code>TextureSet</code> instance.
     * 
     * @return <code>TracedPath</code>: The <code>path</code> field of this
     *         <code>TextureSet</code> instance.
     */
    public TracedPath getPath() {

        return path;
    }

    /**
     * <code>ImmutableMap&lt;String, TracedPath&gt;</code>: The paths of this
     * <code>TextureSet</code> instance.
     */
    private final ImmutableMap<String, TracedPath> paths;

    /**
     * Retrieves the paths of this <code>TextureSet</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, TracedPath&gt;</code>: The
     *         <code>paths</code> field of this <code>TextureSet</code> instance.
     */
    public ImmutableMap<String, TracedPath> getPaths() {

        return paths;
    }

    /**
     * <code>ImmutableMap&lt;String, Texture&gt;</code>: The map of textures of this
     * <code>TextureSet</code> instance.
     */
    private final ImmutableMap<String, Texture> textures;

    /**
     * Retrieves the textures of this <code>TextureSet</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, Texture&gt;</code>: The
     *         <code>textures</code> field of this <code>TextureSet</code> instance.
     */
    public ImmutableMap<String, Texture> getTextures() {

        return textures;
    }

    /**
     * Creates a new instance of the <code>TextureSet</code> class.
     * 
     * @param path <code>TracedPath</code>: The path which leads to this
     *             <code>TextureSet</code> instance.
     */
    public TextureSet(TracedPath path) {

        this.path = path;

        List<TracedPath> texturePaths = path.listRecursiveFiles(TracedPath.IMAGE);
        paths = new ImmutableMap<>(texturePaths.stream().collect(
                Collectors.toMap(p -> p.toString(path), p -> p, (_, replacement) -> replacement, HashMap::new)));

        TracedPath jsonPath = path.extend("texture.json");
        if (jsonPath.exists()) {

            HashMap<String, Texture> texturesMap;
            try {

                TracedDictionary json = JSONOperator.retrieveJSON(jsonPath);

                texturesMap = json.stream().map(texture -> {
                    try {

                        return new AbstractMap.SimpleEntry<>(texture, new Texture(json, texture));
                    } catch (LoggedException _) {

                        return null;
                    }
                }).filter(Objects::nonNull) // Remove null entries.
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existing, _) -> existing,
                                HashMap::new));

            } catch (LoggedException _) {

                texturesMap = new HashMap<>();
            }

            textures = new ImmutableMap<>(texturesMap);
        } else {

            textures = new ImmutableMap<>();
        }
    }
}
