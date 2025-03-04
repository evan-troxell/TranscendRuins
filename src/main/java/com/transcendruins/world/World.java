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

package com.transcendruins.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.transcendruins.contentmodules.packs.Pack;
import com.transcendruins.contentmodules.resources.Resource;

/**
 * <code>World</code>: A class representing a loaded world environment.
 */
public final class World {

    /**
     * <code>int</code>: The length and width of a unit tile.
     */
    public static final int UNIT_TILE = 20;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>East</code>.
     */
    public static final int NORTH = 0;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>North</code>.
     */
    public static final int EAST = 90;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>West</code>.
     */
    public static final int SOUTH = 180;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>South</code>.
     */
    public static final int WEST = 270;

    /**
     * <code>World</code>: The current world of the program.
     */
    private static World world;

    /**
     * Retrieves the current world of the program.
     * 
     * @return <code>World</code>: The <code>world</code> field
     */
    public static World getWorld() {

        return world;
    }

    /**
     * <code>UUID</code>: The unique identifier of this <code>World</code> instance.
     */
    private final UUID uuid;

    /**
     * Retrieves the UUID of this <code>World</code> instance.
     * 
     * @return <code>UUID</code>: The <code>uuid</code> field of this
     *         <code>World</code> instance.
     */
    public UUID getUuid() {

        return uuid;
    }

    /**
     * <code>EnvironmentState</code>: The environment state of this
     * <code>World</code> instance.
     */
    private final EnvironmentState environment;

    /**
     * Retrieves the current environment state of this <code>World</code> instance.
     * 
     * @return <code>EnvironmentState</code>: The <code>environment</code> field of
     *         this <code>World</code> instance.
     */
    public EnvironmentState getEnvironment() {

        return environment;
    }

    /**
     * <code>Random</code>: The random number generator (RNG) of this
     * <code>World</code> instance.
     */
    private final Random random = new Random();

    /**
     * Retreives the next random double from the RNG of this <code>World</code>
     * instance.
     * 
     * @return <code>double</code>: The next double value of the <code>random</code>
     *         field of this <code>World</code> instance.
     */
    public double nextRandom() {

        return random.nextDouble();
    }

    /**
     * Shuffles the contents of a collection.
     * 
     * @param <K>    The collection type to shuffle.
     * @param values <code>List&lt;K&gt;</code>: The values to shuffle.
     * @return <code>List&lt;K&gt;</code>: The shuffled list.
     */
    public <K> List<K> shuffle(List<K> values) {

        Collections.shuffle(values, random);

        return values;
    }

    /**
     * <code>long</code>: The time of creation of this <code>World</code> instance.
     */
    private long timeOfCreation;

    /**
     * Retrieve the current time in millis since the time of creation of this
     * <code>World</code> instance.
     * 
     * @return <code>long</code>: The <code>timeOfCreation</code> field subtracted
     *         from the current time in milliseconds.
     */
    public long getRuntimeMillis() {

        return initialized ? System.currentTimeMillis() - timeOfCreation : 0;
    }

    /**
     * Retrieve the current time in seconds since the time of creation of this
     * <code>World</code> instance.
     * 
     * @return <code>long</code>: The current runtime milliseconds divided by
     *         1000.0.
     */
    public double getRuntimeSeconds() {

        return getRuntimeMillis() / 1000.0;
    }

    /**
     * Creates a new instance of the <code>World</code> class.
     * 
     * @param packs     <code>List&lt;Pack&gt;</code>: The packs used to create
     *                  this <code>World</code> instance.
     * @param resources <code>List&lt;Resource&gt;</code>: The resources
     *                  used to create this <code>World</code> instance.
     */
    private World(List<Pack> packs, List<Resource> resources) {

        environment = new EnvironmentState(packs, resources);
        uuid = UUID.randomUUID();
    }

    private boolean initialized = false;

    public void start() {

        timeOfCreation = System.currentTimeMillis();
        initialized = true;
    }

    public void end() {

        timeOfCreation = -1;
        initialized = false;
    }

    /**
     * Creates a new instance of the <code>World</code> class and assigns it to the
     * <code>world</code> field
     * 
     * @param packs     <code>List&lt;Pack&gt;</code>: The packs used to create
     *                  the new <code>World</code> instance.
     * @param resources <code>List&lt;Resource&gt;</code>: The resources
     *                  used to create the new <code>World</code> instance.
     * @return <code>World</code>: The generated world.
     */
    public static World buildWorld(List<Pack> packs, List<Resource> resources) {

        world = new World(packs, resources);
        return world;
    }
}
