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

package com.transcendruins.save;

import java.io.IOException;
import java.util.UUID;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.world.World;

/**
 * <code>CacheOperator</code>: A set of operations for accessing and
 * manipulating the data cache.
 */
public final class CacheOperator {

    /**
     * <code>CacheOperator.CacheElement</code>: An enum class representing the
     * various kinds of cache elements.
     */
    public enum CacheElement {

        /**
         * <code>CacheOperator.CacheElement</code>: An enum constant representing stored
         * terrain information.
         */
        TERRAIN,

        /**
         * <code>CacheOperator.CacheElement</code>: An enum constant representing stored
         * entity information.
         */
        ENTITIES
    }

    /**
     * <code>TracedPath</code>: The filepath to the cache directory.
     */
    public static final TracedPath CACHE_DIRECTORY;

    static {

        CACHE_DIRECTORY = TracedPath.HOME_DIRECTORY.extend("transcendRuinsCache");

        try {

            if (!CACHE_DIRECTORY.isFile(true)) {

                CACHE_DIRECTORY.createFile(true);
            }
        } catch (IOException e) {

            LoggedException.write("Cache directory could not be found.");
        }
    }

    /**
     * <code>TracedPath</code>: The filepath to the worlds directory.
     */
    public static final TracedPath WORLDS_DIRECTORY;

    static {

        WORLDS_DIRECTORY = CACHE_DIRECTORY.extend("worlds");

        try {

            if (!WORLDS_DIRECTORY.isFile(true)) {

                WORLDS_DIRECTORY.createFile(true);
            }
        } catch (IOException e) {

            LoggedException.write("Worlds directory could not be found.");
        }
    }

    /**
     * Saves a single world to the cache directory.
     * 
     * @param world <code>World</code>: The world to save.
     * @throws IOException Thrown if an error is raised while attempting to save the
     *                     world.
     */
    public static void saveWorld(World world) throws IOException {

        UUID uuid = world.getUuid();
        TracedPath worldPath = WORLDS_DIRECTORY.extend(uuid.toString());
    }

    /**
     * Prevents the <code>CacheOperator</code> class from being instantiated.
     */
    private CacheOperator() {
    }
}
