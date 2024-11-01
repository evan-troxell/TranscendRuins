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
     * <code>int</code>: An enum constant representing the cardinal direction <code>East</code>.
     */
    public static final int EAST = 0;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction <code>North</code>.
     */
    public static final int NORTH = 1;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction <code>West</code>.
     */
    public static final int WEST = 2;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction <code>South</code>.
     */
    public static final int SOUTH = 3;

    /**
     * <code>World</code>: The current world of the program.
     */
    private static World world;

    /**
     * <code>EnvironmentState</code>: The environment state of this <code>World</code> instance.
     */
    private final EnvironmentState environment;

    /**
     * Creates a new instance of the <code>World</code> class.
     * @param packs <code>ArrayList&lt;Pack&gt;</code>: The packs used to create this <code>World</code> instance.
     */
    private World(ArrayList<Pack> packs) {

        environment = new EnvironmentState(packs);
    }

    /**
     * Creates a new instance of the <code>World</code> class and assigns it to the <code>world</code> property.
     * @param packs <code>ArrayList&lt;Pack&gt;</code>: The packs used to create the new <code>World</code> instance.
     * @return <code>World</code>: The generated world.
     */
    public static World buildWorld(ArrayList<Pack> packs) {

        world = new World(packs);
        return world;
    }

    /**
     * Retrieves the current environment state of this <code>World</code> instance.
     * @return <code>EnvironmentState</code>: The <code>environment</code> property of this <code>World</code> instance.
     */
    public EnvironmentState getEnvironment() {

        return environment;
    }

    /**
     * Retrieves the current world of the program.
     * @return <code>World</code>: The <code>world</code> property.
     */
    public static World getWorld() {

        return world;
    }
}
