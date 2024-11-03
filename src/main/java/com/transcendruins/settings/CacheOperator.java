package com.transcendruins.settings;

import java.io.IOException;
import java.util.HashMap;

import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.world.World;

public final class CacheOperator {

    /**
     * <code>CacheOperator.CacheElement</code>: An enum class representing the various kinds of cache elements.
     */
    public enum CacheElement {

        /**
         * <code>CacheOperator.CacheElement</code>: An enum constant representing stored terrain information.
         */
        TERRAIN,

        /**
         * <code>CacheOperator.CacheElement</code>: An enum constant representing stored entity information.
         */
        ENTITIES
    }

    /**
     * <code>TracedPath</code>: The filepath of the cache directory.
     */
    private static final TracedPath CACHE_DIRECTORY = buildCacheDirectory();

    /**
     * <code>TracedPath</code>: The filepath of the worlds directory.
     */
    private static final TracedPath WORLDS_DIRECTORY = buildWorldsDirectory();

    /**
     * Generates the cache directory.
     * @return <code>TracedPath</code>: The path to the generated cache directory
     */
    private static TracedPath buildCacheDirectory() {

        TracedPath cacheRoot = TracedPath.HOME_DIRECTORY.extend("transcendRuinsCache");

        try {

            cacheRoot.createFile(true);
        } catch (IOException e) {}

        return cacheRoot;
    }

    /**
     * Generates the worlds directory.
     * @return <code>TracedPath</code>: The path to the generated worlds directory
     */
    private static TracedPath buildWorldsDirectory() {

        TracedPath worldRoot = CACHE_DIRECTORY.extend("worlds");

        try {

            worldRoot.createFile(true);
        } catch (IOException e) {}

        return worldRoot;
    }

    /**
     * Saves a single world to the cache directory.
     * @param world <code>World</code>: The world to save.
     * @throws IOException Thrown if an error is raised while attempting to save the world.
     */
    public static void saveWorld(World world) throws IOException {

        //UUID uuid = world.getUUID();

        String worldName = "world_";// + uuid.toString();
        TracedPath worldPath = WORLDS_DIRECTORY.extend(worldName);

        buildElementDirectories(world, worldPath);
    }

    /**
     * Saves the elements of a world to a cache.
     * @param world <code>World</code>: The world from which to build the element map.
     * @param worldPath <code>TracedPath</code>: The root from which to create element directories.
     * @throws IOException Thrown if an error is raised while attempting to create the element directories.
     */
    private static void buildElementDirectories(World world, TracedPath worldPath) throws IOException {

        HashMap<CacheElement, TracedPath> elementDirectories = new HashMap<>();

        elementDirectories.put(CacheElement.TERRAIN, worldPath.extend("terrain"));
        elementDirectories.put(CacheElement.ENTITIES, worldPath.extend("entities"));

        for (TracedPath cacheElementDirectory : elementDirectories.values()) {

            cacheElementDirectory.createFile(true);
        }
    }

    /**
     * Retrieves the cache directory.
     * @return <code>TracedPath</code>: The <code>CACHE_DIRECTORY</code> property.
     */
    public static TracedPath getCacheDirectory() {

        return CACHE_DIRECTORY;
    }

    /**
     * Determines whether or not the cache directory exists.
     * @return <code>boolean</code>: If a directory exists at the <code>CACHE_DIRECTORY</code> property.
     */
    public static boolean cacheExists() {

        return CACHE_DIRECTORY.exists() && CACHE_DIRECTORY.isFile(true);
    }

    /**
     * Prevents the <code>CacheOperator</code> class from being instantiated.
     */
    private CacheOperator() {}
}
