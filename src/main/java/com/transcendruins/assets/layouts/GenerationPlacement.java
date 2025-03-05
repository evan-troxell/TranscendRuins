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

package com.transcendruins.assets.layouts;

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.assets.layouts.shape.GenerationShapeInstance;
import com.transcendruins.assets.layouts.shape.GenerationShapeSchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.world.World;

public final class GenerationPlacement {

    private final WeightedRoll<Placement> entries;

    public Placement getPlacement(double random) {

        return entries.get(random);
    }

    public GenerationPlacement(TracedCollection json, Object key)
            throws LoggedException {

        entries = switch (json.getType(key)) {

            case ARRAY -> {

                TracedEntry<TracedArray> placementEntries = json.getAsArray(key, false);
                TracedArray placementsJson = placementEntries.getValue();

                ArrayList<WeightedRoll.Entry<Placement>> placements = new ArrayList<>();
                for (int i : placementsJson.getIndices()) {

                    TracedEntry<TracedDictionary> placementEntry = placementsJson.getAsDict(i, false);
                    TracedDictionary placementJson = placementEntry.getValue();

                    TracedEntry<Double> chanceVal = placementJson.getAsDouble("chance", false, 100.0, num -> num > 0);
                    double chance = chanceVal.getValue();

                    Placement placement = new Placement(placementJson);

                    placements.add(new WeightedRoll.Entry<>(placement, chance));
                }

                yield new WeightedRoll<>(placements);
            }

            case DICT -> {

                TracedEntry<TracedDictionary> placementEntries = json.getAsDict(key, false);
                TracedDictionary placementsJson = placementEntries.getValue();

                yield new WeightedRoll<>(new Placement(placementsJson));
            }

            default -> new WeightedRoll<>(new Placement());
        };
    }

    public final class Placement {

        private final WeightedRoll<GenerationShapeSchema> shape;

        public GenerationShapeInstance getShape(World world) {

            return GenerationShapeInstance.createShape(shape.get(world.nextRandom()), world);
        }

        private Placement() {

            shape = new WeightedRoll<>(GenerationShapeSchema.DEFAULT_SHAPE);
        }

        private Placement(TracedDictionary json) throws LoggedException {

            shape = json.get("shape", List.of(
                    json.arrayCase(entry -> {

                        TracedArray shapesJson = entry.getValue();

                        if (shapesJson.isEmpty()) {

                            throw new CollectionSizeException(entry, shapesJson);
                        }

                        ArrayList<WeightedRoll.Entry<GenerationShapeSchema>> generations = new ArrayList<>();

                        for (int i : shapesJson.getIndices()) {

                            TracedEntry<TracedDictionary> shapeEntry = shapesJson.getAsDict(i, false);
                            TracedDictionary shapeJson = (TracedDictionary) shapeEntry.getValue();

                            TracedEntry<Double> changeEntry = shapeJson.getAsDouble("chance", true, 100.0,
                                    num -> num > 0);
                            double weight = changeEntry.getValue();

                            generations
                                    .add(new WeightedRoll.Entry<>(GenerationShapeSchema.createShape(shapeJson),
                                            weight));
                        }

                        return new WeightedRoll<>(generations);
                    }),
                    json.dictCase(entry -> {

                        TracedDictionary shapeJson = entry.getValue();

                        return new WeightedRoll<>(GenerationShapeSchema.createShape(shapeJson));
                    })));
        }
    }
}
