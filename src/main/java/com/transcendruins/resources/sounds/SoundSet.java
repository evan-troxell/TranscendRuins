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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.InternalPath;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>SoundSet</code>: A class representing a set of compiled sounds.
 */
public final class SoundSet {

    /**
     * <code>InternalPath</code>: The path to the placeholder sound.
     */
    public static final InternalPath MISSING_SOUND = TracedPath.INTERNAL_DATA_DIRECTORY.extend("missingSound.wav");

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
     * <code>ImmutableMap&lt;String, TracedPath&gt;</code>: The paths of this
     * <code>SoundSet</code> instance.
     */
    private final ImmutableMap<String, TracedPath> paths;

    /**
     * Retrieves the paths of this <code>SoundSet</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, TracedPath&gt;</code>: The
     *         <code>paths</code> field of this <code>SoundSet</code> instance.
     */
    public ImmutableMap<String, TracedPath> getPaths() {

        return paths;
    }

    /**
     * <code>ImmutableMap&lt;String, Sound&gt;</code>: The map of sounds of this
     * <code>SoundSet</code> instance.
     */
    private final ImmutableMap<String, Sound> sounds;

    /**
     * Retrieves the sounds of this <code>SoundSet</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, Sound&gt;</code>: The
     *         <code>sounds</code> field of this <code>SoundSet</code> instance.
     */
    public ImmutableMap<String, Sound> getSounds() {

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

        List<TracedPath> soundPaths = path.listRecursiveFiles(TracedPath.AUDIO);
        paths = new ImmutableMap<>(soundPaths.stream().collect(
                Collectors.toMap(p -> p.toString(path), p -> p, (_, replacement) -> replacement, HashMap::new)));

        TracedPath jsonPath = path.extend("sound.json");
        if (jsonPath.exists()) {

            HashMap<String, Sound> soundsMap;
            try {

                TracedDictionary json = JSONOperator.retrieveJSON(jsonPath);

                soundsMap = json.stream().map(sound -> {
                    try {

                        return new AbstractMap.SimpleEntry<>(sound, new Sound(json, sound));
                    } catch (LoggedException _) {

                        return null;
                    }
                }).filter(Objects::nonNull) // Remove null entries.
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existing, _) -> existing,
                                HashMap::new));

            } catch (LoggedException e) {

                soundsMap = new HashMap<>();
            }

            sounds = new ImmutableMap<>(soundsMap);
        } else {

            sounds = new ImmutableMap<>();
        }
    }
}
