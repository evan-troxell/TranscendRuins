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
 * <code>SoundSet</code>: A class representing a set of compiled sounds.
 */
public final class SoundSet {

    /**
     * <code>TracedPath</code>: The path to the placeholder sound.
     */
    public static final TracedPath MISSING_SOUND = TracedPath.DATA_DIRECTORY.extend("missingSound.wav");

    /**
     * <code>TracedPath</code>: The path which leads to this <code>SoundSet</code>
     * instance.
     */
    private final TracedPath path;

    /**
     * Retrieves the path of this <code>SoundSet</code> instance.
     * 
     * @return <code>TracedPath</code>: The <code>path</code> field of this
     *         <code>SoundSet</code> instance.
     */
    public TracedPath getPath() {

        return path;
    }

    /**
     * <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;String, Sound&gt;&gt;</code>:
     * The map of sounds of this <code>SoundSet</code> instance.
     */
    private final ImmutableMap<AssetType, ImmutableMap<String, Sound>> sounds;

    /**
     * Retrieves the sounds of this <code>SoundSet</code> instance.
     * 
     * @return <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;String, Sound&gt;&gt;</code>:
     *         The <code>sounds</code> field of this <code>SoundSet</code>
     *         instance.
     */
    public ImmutableMap<AssetType, ImmutableMap<String, Sound>> getSounds() {

        return sounds;
    }

    /**
     * Creates a new instance of the <code>SoundSet</code> class.
     * 
     * @param path <code>TracedPath</code>: The path which leads to this
     *             <code>SoundSet</code> instance.
     */
    public SoundSet(TracedPath path) {

        this.path = path;

        ArrayList<TracedPath> soundPaths = path.compileFiles(TracedPath.SOUND, true);

        sounds = new ImmutableMap<>(AssetType.createAssetMap(type -> compileSet(type, soundPaths)));
    }

    /**
     * Compiles a JSON sound set of this <code>SoundSet</code> instance.
     * 
     * @param fileName   <code>String</code>: The file to compile at.
     * @param soundPaths <code>ArrayList&lt;TracedPath&gt;</code>: The sound
     *                   paths to compile using.
     * @return <code>ImmutableMap&lt;String, Sound&gt;</code>: The resulting
     *         sound set.
     * @throws LoggedException Thrown if an error occurs while compiling the sound
     *                         set.
     */
    private ImmutableMap<String, Sound> compileSet(AssetType type, ArrayList<TracedPath> soundPaths) {

        String fileName = type + "Sounds.json";

        TracedPath extendedPath = path.extend(fileName);
        if (!extendedPath.exists()) {

            return new ImmutableMap<>();
        }

        try {

            TracedDictionary json = JSONOperator.retrieveJSON(extendedPath);
            return new ImmutableMap<>(json.getKeys().stream()
                    .map(sound -> {
                        try {

                            return new AbstractMap.SimpleEntry<>(sound,
                                    new Sound(json, sound, path, soundPaths));
                        } catch (LoggedException e) {

                            e.print();
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

            e.print();
            return new ImmutableMap<>();
        }
    }
}
