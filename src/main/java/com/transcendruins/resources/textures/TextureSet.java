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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.transcendruins.assets.AssetType;
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
     * <code>TracedPath</code>: The path to the placeholder texture.
     */
    public static final TracedPath MISSING_TEXTURE = TracedPath.INTERNAL_DIRECTORY.extend("missingTexture.png");

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
     * <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;String, Texture&gt;&gt;</code>:
     * The map of textures of this <code>TextureSet</code> instance.
     */
    private final ImmutableMap<AssetType, ImmutableMap<String, Texture>> textures;

    /**
     * Retrieves the textures of this <code>TextureSet</code> instance.
     * 
     * @return <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;String, Texture&gt;&gt;</code>:
     *         The <code>textures</code> field of this <code>TextureSet</code>
     *         instance.
     */
    public ImmutableMap<AssetType, ImmutableMap<String, Texture>> getTextures() {

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

        ArrayList<TracedPath> texturePaths = path.compileFiles(TracedPath.IMAGE, true);

        textures = new ImmutableMap<>(AssetType.buildAssetMap(type -> compileSet(type, texturePaths)));
    }

    /**
     * Compiles a JSON texture set of this <code>TextureSet</code> instance.
     * 
     * @param fileName     <code>String</code>: The file to compile at.
     * @param texturePaths <code>ArrayList&lt;TracedPath&gt;</code>: The texture
     *                     paths to compile using.
     * @return <code>ImmutableMap&lt;String, Texture&gt;</code>: The resulting
     *         texture set.
     * @throws LoggedException Thrown if an error occurs while compiling the texture
     *                         set.
     */
    private ImmutableMap<String, Texture> compileSet(AssetType type, ArrayList<TracedPath> texturePaths) {

        String fileName = type + "Textures.json";

        TracedPath extendedPath = path.extend(fileName);
        if (!extendedPath.exists()) {

            return new ImmutableMap<>();
        }

        try {

            TracedDictionary json = JSONOperator.retrieveJSON(extendedPath);
            return new ImmutableMap<>(json.getKeys().stream()
                    .map(texture -> {
                        try {

                            return new AbstractMap.SimpleEntry<>(texture,
                                    new Texture(json, texture, path, texturePaths));
                        } catch (LoggedException e) {

                            return null;
                        }
                    })
                    .filter(Objects::nonNull) // Remove null entries.
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (existing, _) -> existing,
                            HashMap::new)));
        } catch (LoggedException e) {

            return new ImmutableMap<>();
        }
    }
}
