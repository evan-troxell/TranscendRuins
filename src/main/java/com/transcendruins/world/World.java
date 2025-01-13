package com.transcendruins.world;

import java.util.ArrayList;

import com.transcendruins.packcompiling.Pack;

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
    public static final int EAST = 0;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>North</code>.
     */
    public static final int NORTH = 1;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>West</code>.
     */
    public static final int WEST = 2;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>South</code>.
     */
    public static final int SOUTH = 3;

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
     * <code>long</code>: The time of creation of this <code>World</code> instance.
     */
    private final long timeOfCreation;

    /**
     * Creates a new instance of the <code>World</code> class.
     * 
     * @param packs <code>ArrayList&lt;Pack&gt;</code>: The packs used to create
     *              this <code>World</code> instance.
     */
    private World(ArrayList<Pack> packs) {

        timeOfCreation = System.currentTimeMillis();
        environment = new EnvironmentState(packs);
    }

    /**
     * Creates a new instance of the <code>World</code> class and assigns it to the
     * <code>world</code> field
     * 
     * @param packs <code>ArrayList&lt;Pack&gt;</code>: The packs used to create the
     *              new <code>World</code> instance.
     * @return <code>World</code>: The generated world.
     */
    public static World buildWorld(ArrayList<Pack> packs) {

        world = new World(packs);
        return world;
    }

    /**
     * Retrieve the current time in millis since the time of creation of this
     * <code>World</code> instance.
     * 
     * @return <code>long</code>: The <code>timeOfCreation</code> field subtracted
     *         from the current time in milliseconds.
     */
    public long getRuntimeMillis() {

        return System.currentTimeMillis() - timeOfCreation;
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
}
