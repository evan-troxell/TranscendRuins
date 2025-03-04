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

package com.transcendruins.assets.extra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.transcendruins.utilities.immutable.ImmutableList;

public final class WeightedRoll<K> {

    private final ImmutableList<K> entries;

    private final ImmutableList<Double> weights;

    private double weightSum = 0;

    public WeightedRoll(K entry) {

        weightSum += 1;

        entries = new ImmutableList<>(entry);
        weights = new ImmutableList<>(weightSum);
    }

    public WeightedRoll(Stream<K> entries, Function<K, Double> getWeight) {

        this.entries = new ImmutableList<>(entries.collect(Collectors.toList()));
        weights = new ImmutableList<>(entries.map(getWeight).map(weight -> {

            weightSum += weight;
            return weightSum;
        }).collect(Collectors.toList()));
    }

    public WeightedRoll(List<Entry<K>> entries) {

        ArrayList<K> entryList = new ArrayList<>();
        ArrayList<Double> weightList = new ArrayList<>();

        for (Entry<K> entry : entries) {

            weightSum += entry.weight;

            entryList.add(entry.val);
            weightList.add(weightSum);
        }

        this.entries = new ImmutableList<>(entryList);
        weights = new ImmutableList<>(weightList);
    }

    public K get(double random) {

        if (random < 0) {

            random = 0;
        } else if (random >= 1) {

            random = Math.nextDown(1.0);
        }

        random *= weightSum;

        int index = Collections.binarySearch(weights, random);
        if (index < 0) {

            index = -index - 1;
        }

        return (index < entries.size()) ? entries.get(index) : null;
    }

    public static final class Entry<K> {

        private final K val;

        private final double weight;

        public Entry(K val, double weight) {

            this.val = val;
            this.weight = weight;
        }

        public Entry(K val) {

            this(val, 1);
        }
    }
}
