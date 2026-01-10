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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.random.DeterministicRandom;

/**
 * <code>WeightedRoll&lt;K&gt;</code>: A class representing a set of entries
 * selected based off of a weighted softmax function (ignoring the exponential
 * component).
 */
public final class WeightedRoll<K> {

    /**
     * <code>ImmutableList&lt;K&gt;</code>: The entries of this
     * <code>WeightedRoll</code> instance.
     */
    private final ImmutableList<K> entries;

    /**
     * Retrieves the entries of this <code>WeightedRoll</code> instance.
     * 
     * @return <code>ImmutableList&lt;Double&gt;</code>: The <code>entries</code>
     *         field of this <code>WeightedRoll</code> instance.
     */
    public final ImmutableList<K> getEntries() {

        return entries;
    }

    /**
     * <code>ImmutableList&lt;Double&gt;</code>: The cumulative weights of the
     * entries in this <code>WeightedRoll</code> instance. Each entry's weight
     * position is greater than the last, such that picking any random number
     * between 0.0 (inclusive) and the sum of weights (exclusive) will return the
     * entry at the index of the first component whose weight sum is greater than or
     * equal to the random number.
     */
    private final ImmutableList<Double> weights;

    /**
     * Retrieves the cumulative weights of this <code>WeightedRoll</code> instance.
     * 
     * @return <code>ImmutableList&lt;Double&gt;</code>: The <code>weights</code>
     *         field of this <code>WeightedRoll</code> instance.
     */
    public final ImmutableList<Double> getWeights() {

        return weights;
    }

    /**
     * <code>double</code>: The sum of the weights of this <code>WeightedRoll</code>
     * instance.
     */
    private final double weightSum;

    /**
     * Creates a new instance of the <code>WeightedRoll</code> class with a single
     * entry.
     * 
     * @param entry <code>K</code>: The entry to add to this
     *              <code>WeightedRoll</code> instance.
     */
    public WeightedRoll(K entry) {

        weightSum = 1;

        entries = new ImmutableList<>(entry);
        weights = new ImmutableList<>(weightSum);
    }

    /**
     * Creates a new instance of the <code>WeightedRoll</code> class from a stream
     * of available entries with a function to map each entry to its weight.
     * 
     * @param entries   <code>Stream&lt;K&gt;</code>: The stream of entries to add
     *                  to this <code>WeightedRoll</code> instance.
     * @param getWeight <code>Function&lt;K, Double&gt;</code>: The function which
     *                  maps each entry to its weight.
     */
    public WeightedRoll(Stream<K> entries, Function<K, Double> getWeight) {

        this.entries = new ImmutableList<>(entries.toList());
        double[] sum = new double[] { 0.0 };
        weights = new ImmutableList<>(this.entries.stream().map(entry -> {

            return (sum[0] += getWeight.apply(entry));
        }).toList());

        weightSum = sum[0];
    }

    /**
     * Creates a new instance of the <code>WeightedRoll</code> class from a list of
     * entries.
     * 
     * @param entries <code>List&lt;Entry&lt;K&gt;&gt;</code>: The list of entries
     *                to add to this <code>WeightedRoll</code> instance.
     */
    private WeightedRoll(List<Entry<K>> entries) {

        ArrayList<K> entryList = new ArrayList<>();
        ArrayList<Double> weightList = new ArrayList<>();

        double sum = 0.0;

        for (Entry<K> entry : entries) {

            sum += entry.weight;

            entryList.add(entry.val);
            weightList.add(sum);
        }

        this.entries = new ImmutableList<>(entryList);
        weights = new ImmutableList<>(weightList);

        weightSum = sum;
    }

    /**
     * Creates a new instance of the <code>WeightedRoll</code> class from a list of
     * entries which originated from a JSON array.
     * 
     * @param entry   <code>TracedEntry&lt;TracedArray&gt;</code>: The entry which
     *                the JSON array originated from.
     * @param array   <code>TracedArray</code>: The JSON array containing the
     *                entries.
     * @param entries <code>List&lt;Entry&lt;K&gt;&gt;</code>: The list of entries
     *                to add to this <code>WeightedRoll</code> instance.
     * @throws CollectionSizeException Thrown if the JSON array is empty.
     */
    public WeightedRoll(TracedEntry<TracedArray> entry, TracedArray array, List<Entry<K>> entries)
            throws CollectionSizeException {

        this(entries);

        // There will (presumably) be no required processing of elements if the array is
        // empty, so this step can be contained inside the constructor.
        if (array.isEmpty()) {

            throw new CollectionSizeException(entry, array);
        }
    }

    /**
     * Creates a new instance of the <code>WeightedRoll</code> class from a list of
     * entries which originated from a JSON dictionary.
     * 
     * @param entry   <code>TracedEntry&lt;TracedDictionary&gt;</code>: The entry
     *                which the JSON dictionary originated from.
     * @param array   <code>TracedDictionary</code>: The JSON dictionary containing
     *                the entries.
     * @param entries <code>List&lt;Entry&lt;K&gt;&gt;</code>: The list of entries
     *                to add to this <code>WeightedRoll</code> instance.
     * @throws CollectionSizeException Thrown if the JSON dictionary is empty.
     */
    public WeightedRoll(TracedEntry<TracedDictionary> entry, TracedDictionary dict, List<Entry<K>> entries)
            throws CollectionSizeException {

        this(entries);

        // There will (presumably) be no required processing of elements if the
        // dictionary is empty, so this step can be contained inside the constructor.
        if (dict.isEmpty()) {

            throw new CollectionSizeException(entry, dict);
        }
    }

    /**
     * Creates a new instance of the <code>WeightedRoll</code> class from a list of
     * entries and a list of weights.
     * 
     * @param entries <code>List&lt;K&gt;</code>: The entries to add to this
     *                <code>WeightedRoll</code> instance.
     * @param weights <code>List&lt;Double&gt;</code>: The weights to add to this
     *                <code>WeightedRoll</code> instance.
     */
    public WeightedRoll(List<K> entries, List<Double> weights) {

        this.entries = new ImmutableList<>(entries);
        this.weights = new ImmutableList<>(weights);

        weightSum = weights.getLast();
    }

    /**
     * Retrieves an entry from this <code>WeightedRoll</code> instance based on the
     * provided random number and the entries weights. The random number should be
     * between 0.0 (inclusive) and 1.0 (exclusive).
     * 
     * @param random <code>long</code>: The random number to base the retrieved
     *               entry off of.
     * @return <code>K</code>: The retrieved entry.
     */
    public final K get(long random) {

        double range = DeterministicRandom.toDouble(random) * weightSum;

        int index = Collections.binarySearch(weights, range);
        if (index < 0) {

            index = -index - 1;
        }

        return (index < entries.size()) ? entries.get(index) : null;
    }

    /**
     * <code>Entry&lt;K&gt;</code>: A class representing an entry in a
     * <code>WeightedRoll</code> instance.
     */
    public static final class Entry<K> {

        /**
         * <code>K</code>: The value of this <code>Entry</code> instance.
         */
        private final K val;

        /**
         * <code>double</code>: The weight of this <code>Entry</code> instance as a
         * positive number.
         */
        private final double weight;

        /**
         * Creates a new instance of the <code>Entry</code> class.
         * 
         * @param val    <code>K</code>: The value of this <code>Entry</code> instance.
         * @param weight <code>double</code>: The weight of this <code>Entry</code>
         *               instance as a positive number.
         */
        public Entry(K val, double weight) {

            this.val = val;
            this.weight = weight;
        }

        /**
         * Creates a new instance of the <code>Entry</code> class with a weight of 1.
         * 
         * @param val <code>K</code>: The value of this <code>Entry</code> instance.
         */
        public Entry(K val) {

            this(val, 1);
        }
    }
}
