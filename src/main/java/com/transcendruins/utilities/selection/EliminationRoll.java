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

package com.transcendruins.utilities.selection;

import java.util.ArrayList;
import java.util.Iterator;

import com.transcendruins.utilities.random.DeterministicRandom;

/**
 * <code>EliminationRoll&lt;K&gt;</code>: A class representing a set of entries
 * selected based off of a weighted softmax function (ignoring the exponential
 * component). When a selection is made, the selected entry is remove from the
 * pool of available entries for subsequent selections.
 */
public final class EliminationRoll<K> implements Iterable<K> {

    /**
     * <code>WeightedRoll&lt;K&gt;</code>: The underlying weighted roll used for
     * selections.
     */
    private final WeightedRoll<K> roll;

    private final DeterministicRandom random;

    /**
     * Creates a new instance of the <code>WeightedRoll</code> class with a single
     * entry.
     * 
     * @param entry  <code>K</code>: The entry to add to this
     *               <code>WeightedRoll</code> instance.
     * @param random <code>DeterministicRandom</code>: The random number generator
     *               to use for selections.
     */
    public EliminationRoll(WeightedRoll<K> roll, DeterministicRandom random) {

        this.roll = roll;
        this.random = random;
    }

    @Override
    public final Iterator<K> iterator() {

        return new Iterator<>() {

            private WeightedRoll<K> currentRoll = roll;

            @Override
            public final boolean hasNext() {

                return currentRoll != null;
            }

            @Override
            public final K next() {

                if (currentRoll == null) {

                    return null;
                }

                K value = currentRoll.get(random.next());

                ArrayList<K> entries = new ArrayList<>(currentRoll.getEntries());
                ArrayList<Double> weights = new ArrayList<>(currentRoll.getWeights());

                int index = entries.indexOf(value);
                if (index == -1) {

                    return value;
                }

                entries.remove(index);
                if (entries.isEmpty()) {

                    currentRoll = null;
                    return value;
                }

                double weight = weights.remove(index);
                for (int i = index; i < weights.size(); i++) {

                    weights.set(i, weights.get(i) - weight);
                }

                currentRoll = new WeightedRoll<>(entries, weights);
                return value;
            }
        };
    }
}
