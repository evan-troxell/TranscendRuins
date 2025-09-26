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
package com.transcendruins.utilities.random;

import java.util.Random;

/**
 * <code>DeterministicRandom</code>: A class representing a random value which
 * is entirely deterministic and can be loaded from its current state.
 */
public final class DeterministicRandom {

    /**
     * <code>long</code>: The current state of this <code>DeterministicRandom</code>
     * instance.
     */
    private long state;

    /**
     * Retrieves the current state of this <code>DeterministicRandom</code>
     * instance.
     * 
     * @return <code>long</code>: The <code>state</code> field of this
     *         <code>DeterministicRandom</code> instance.
     */
    public final long getState() {

        return state;
    }

    /**
     * Creates a new instance of the <code>DeterministicRandom</code> class.
     * 
     * @param seed <code>long</code>: The initial state to use.
     */
    public DeterministicRandom(long seed) {

        state = seed;
    }

    /**
     * Retrieves the next value and updates the state of this
     * <code>DeterministicRandom</code> instance.
     * 
     * @return <code>long</code>: The produced value.
     */
    public final long next() {

        Random r = new Random(state);
        state = r.nextLong(); // update state
        return state;
    }

    /**
     * Converts a long value in the range of <code>[-2^63, 2^63)</code> to a double
     * in the range of <code>[0, 1)</code>.
     * 
     * @param random <code>long</code>: The long value to convert.
     * @return <code>double</code>: The produced value.
     */
    public static final double toDouble(long random) {

        long bits = random & ((1L << 53) - 1);
        return bits / (double) (1L << 53);
    }
}
