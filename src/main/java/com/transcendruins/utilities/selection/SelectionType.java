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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.random.DeterministicRandom;

/**
 * <code>SelectionType</code>: An enum class representing methods of selection
 * during a generation process.
 */
public enum SelectionType {

    /**
     * <code>SelectionType</code>: A selection type referring to the sequential
     * selection of elements in order.
     */
    SEQUENCE {

        @Override
        protected final <K> Map<K, int[]> generateContent(Map<K, Double> values, Function<K, int[]> onCall,
                DeterministicRandom random) {

            return values.entrySet().stream()
                    .filter(entry -> 100.0 * DeterministicRandom.toDouble(random.next()) < entry.getValue())
                    .map(Map.Entry::getKey).collect(Collectors.toMap(val -> val, onCall, (a, b) -> a, HashMap::new));
        }

    },

    /**
     * <code>SelectionType</code>: A selection type referring to the selection of
     * random elements.
     */
    SELECT {

        @Override
        protected final <K> Map<K, int[]> generateContent(Map<K, Double> values, Function<K, int[]> onCall,
                DeterministicRandom random) {

            WeightedRoll<K> roll = new WeightedRoll<>(values.keySet().stream(), val -> values.get(val));
            K selected = roll.get(random.next());

            return Map.of(selected, onCall.apply(selected));
        }

    };

    public static <K> Function<K, int[]> apply(Function<K, Boolean> onCall, Function<K, Integer> countFunction) {

        return (val) -> {

            int count = countFunction.apply(val);
            int failures = 0;
            for (int i = 0; i < count; i++) {

                if (!onCall.apply(val)) {

                    failures++;
                }
            }

            return new int[] { count, failures };
        };
    }

    public final <K> void generate(Collection<K> values, int rolls, Function<K, int[]> onCall,
            Function<K, Double> chanceFunction, Function<K, Integer> limitFunction, DeterministicRandom random,
            double maxFailRate, int minSampleSize) {

        LinkedHashMap<K, Integer> available = values.stream().collect(
                Collectors.toMap(val -> val, val -> limitFunction.apply(val), (a, b) -> a, LinkedHashMap::new));

        HashMap<K, int[]> failCountMap = new HashMap<>();

        for (int i = 0; i < rolls; i++) {

            if (available.isEmpty()) {

                return;
            }

            LinkedHashMap<K, Double> chanceMap = available.keySet().stream().collect(
                    Collectors.toMap(val -> val, val -> chanceFunction.apply(val), (a, b) -> a, LinkedHashMap::new));

            generateContent(chanceMap, onCall, random).entrySet().forEach(entry -> {

                K val = entry.getKey();

                int newCount = available.compute(val, (_, count) -> count - 1);
                if (newCount == 0) {

                    available.remove(val);
                    failCountMap.remove(val);
                    return;
                }

                int[] pass = entry.getValue();

                int[] source = failCountMap.computeIfAbsent(val, _ -> new int[2]);
                int count = (source[0] += pass[0]);
                int fail = (source[1] += pass[1]);

                if (count < minSampleSize || count <= 0) {

                    return;
                }

                if (maxFailRate < (double) fail / count) {

                    available.remove(val);
                    failCountMap.remove(val);
                }
            });
        }
    }

    protected abstract <K> Map<K, int[]> generateContent(Map<K, Double> values, Function<K, int[]> onCall,
            DeterministicRandom random);

    /**
     * Parses a value from a <code>TracedCollection</code> instance into a
     * <code>SelectionType</code> enum.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to search for.
     * @return <code>SelectionType</code>: The parsed selection type.
     * @throws LoggedException Thrown if the selection type could not be found or
     *                         parsed.
     */
    public static final SelectionType createSelectionType(TracedCollection collection, Object key)
            throws LoggedException {

        TracedEntry<String> entry = collection.getAsString(key, false, null);

        return switch (entry.getValue()) {

        case "sequence", "sequential" -> SEQUENCE;

        case "select", "selection" -> SELECT;

        default -> throw new UnexpectedValueException(entry);
        };
    }
}
