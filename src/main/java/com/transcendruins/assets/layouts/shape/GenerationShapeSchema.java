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

package com.transcendruins.assets.layouts.shape;

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.assets.extra.Range;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public abstract class GenerationShapeSchema {

    public static final WeightedRoll<GenerationShapeSchema> DEFAULT_SHAPE = new WeightedRoll<>(
            new GenerationShapeSchema() {
            });

    private final double chance;

    public final double getChance() {

        return chance;
    }

    protected GenerationShapeSchema() {

        chance = 100.0;
    }

    protected GenerationShapeSchema(TracedDictionary json) throws LoggedException {

        TracedEntry<Double> chanceEntry = json.getAsDouble("chance", true, 100.0, num -> num > 0);
        chance = chanceEntry.getValue();
    }

    public static final WeightedRoll<GenerationShapeSchema> createShape(TracedCollection collection, Object key)
            throws LoggedException {

        return collection.get(key, List.of(

                collection.arrayCase(entry -> {

                    TracedArray array = entry.getValue();

                    ArrayList<WeightedRoll.Entry<GenerationShapeSchema>> entries = new ArrayList<>();

                    for (int i : array) {

                        TracedEntry<TracedDictionary> shapeEntry = array.getAsDict(i, false);
                        TracedDictionary shapeJson = shapeEntry.getValue();

                        GenerationShapeSchema shape = createShape(shapeJson);

                        entries.add(new WeightedRoll.Entry<>(shape, shape.getChance()));
                    }

                    return new WeightedRoll<>(entry, array, entries);
                }),

                collection.dictCase(entry -> new WeightedRoll<>(createShape(entry.getValue()))),

                collection.nullCase(_ -> DEFAULT_SHAPE)));
    }

    private static GenerationShapeSchema createShape(TracedDictionary json) throws LoggedException {

        TracedEntry<String> shapeEntry = json.getAsString("shape", false, null);

        String shape = shapeEntry.getValue();
        return switch (shape) {

        case "point" -> new PointSchema(json);

        case "square" -> new SquareSchema(json);

        case "rectangle" -> new RectangleSchema(json);

        case "circle" -> new CircleSchema(json);

        case "ring" -> new RingSchema(json);

        default -> throw new UnexpectedValueException(shapeEntry);
        };
    }

    public static final class PointSchema extends GenerationShapeSchema {

        public PointSchema(TracedDictionary json) throws LoggedException {

            super(json);
        }
    }

    public static final class SquareSchema extends GenerationShapeSchema {

        private final Range width;

        public Range getWidth() {

            return width;
        }

        protected SquareSchema(TracedDictionary json) throws LoggedException {

            super(json);

            width = Range.createRange(json, "width", false, num -> num >= 1);
        }
    }

    public static final class RectangleSchema extends GenerationShapeSchema {

        private final Range width;

        public Range getWidth() {

            return width;
        }

        private final Range length;

        public Range getLength() {

            return length;
        }

        protected RectangleSchema(TracedDictionary json) throws LoggedException {

            super(json);

            width = Range.createRange(json, "width", false, num -> num >= 1);
            length = json.containsKey("length") ? Range.createRange(json, "length", false, num -> num >= 1) : width;
        }
    }

    public static final class CircleSchema extends GenerationShapeSchema {

        private final Range radius;

        public Range getRadius() {

            return radius;
        }

        protected CircleSchema(TracedDictionary json) throws LoggedException {

            super(json);

            radius = Range.createRange(json, "radius", false, num -> num >= 1);
        }
    }

    public static final class RingSchema extends GenerationShapeSchema {

        private final Range inner;

        public Range getInner() {

            return inner;
        }

        private final Range outer;

        public Range getOuter() {

            return outer;
        }

        protected RingSchema(TracedDictionary json) throws LoggedException {

            super(json);

            TracedEntry<TracedArray> radiusEntry = json.getAsArray("radius", false);
            TracedArray radiusJson = radiusEntry.getValue();

            inner = Range.createRange(radiusJson, 0, false, num -> num >= 1);
            outer = Range.createRange(radiusJson, 1, false, num -> num > inner.getMax());
        }
    }
}
