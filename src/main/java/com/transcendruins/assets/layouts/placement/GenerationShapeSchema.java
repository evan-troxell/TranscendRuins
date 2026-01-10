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

package com.transcendruins.assets.layouts.placement;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.selection.DiscreteRange;
import com.transcendruins.utilities.selection.WeightedRoll;

public abstract class GenerationShapeSchema {

    public static final WeightedRoll<GenerationShapeSchema> createShape(TracedCollection collection, Object key)
            throws LoggedException {

        return collection.getAsRoll(key, true, PointGenerationShapeSchema.DEFAULT,
                entry -> createShape(entry.getValue()));
    }

    private static GenerationShapeSchema createShape(TracedDictionary json) throws LoggedException {

        TracedEntry<String> shapeEntry = json.getAsString("type", false, null);

        String shape = shapeEntry.getValue();
        return switch (shape) {

        case "span" -> SpanGenerationShapeSchema.DEFAULT;

        case "point" -> PointGenerationShapeSchema.DEFAULT;

        case "square" -> new SquareGenerationShapeSchema(json);

        case "rectangle" -> new RectangleGenerationShapeSchema(json);

        case "circle" -> new CircleGenerationShapeSchema(json);

        case "ring" -> new RingGenerationShapeSchema(json);

        default -> throw new UnexpectedValueException(shapeEntry);
        };
    }

    public static final class SpanGenerationShapeSchema extends GenerationShapeSchema {

        public static final SpanGenerationShapeSchema DEFAULT = new SpanGenerationShapeSchema();

        private SpanGenerationShapeSchema() {
        }
    }

    public static final class PointGenerationShapeSchema extends GenerationShapeSchema {

        public static final PointGenerationShapeSchema DEFAULT = new PointGenerationShapeSchema();

        private PointGenerationShapeSchema() {
        }
    }

    public static final class SquareGenerationShapeSchema extends GenerationShapeSchema {

        private final DiscreteRange width;

        public DiscreteRange getWidth() {

            return width;
        }

        protected SquareGenerationShapeSchema(TracedDictionary json) throws LoggedException {

            width = DiscreteRange.createRange(json, "width", false, -1, num -> num > 0);
        }
    }

    public static final class RectangleGenerationShapeSchema extends GenerationShapeSchema {

        private final DiscreteRange width;

        public DiscreteRange getWidth() {

            return width;
        }

        private final DiscreteRange length;

        public DiscreteRange getLength() {

            return length;
        }

        protected RectangleGenerationShapeSchema(TracedDictionary json) throws LoggedException {

            width = DiscreteRange.createRange(json, "width", false, -1, num -> num > 0);
            length = json.containsKey("length") ? DiscreteRange.createRange(json, "length", false, -1, num -> num > 0)
                    : width;
        }
    }

    public static final class CircleGenerationShapeSchema extends GenerationShapeSchema {

        private final DiscreteRange radius;

        public DiscreteRange getRadius() {

            return radius;
        }

        protected CircleGenerationShapeSchema(TracedDictionary json) throws LoggedException {

            radius = DiscreteRange.createRange(json, "radius", false, -1, num -> num >= 0);
        }
    }

    public static final class RingGenerationShapeSchema extends GenerationShapeSchema {

        private final DiscreteRange inner;

        public DiscreteRange getInner() {

            return inner;
        }

        private final DiscreteRange outer;

        public DiscreteRange getOuter() {

            return outer;
        }

        protected RingGenerationShapeSchema(TracedDictionary json) throws LoggedException {

            TracedEntry<TracedArray> radiusEntry = json.getAsArray("radius", false);
            TracedArray radiusJson = radiusEntry.getValue();

            inner = DiscreteRange.createRange(radiusJson, 0, false, -1, num -> num >= 0);
            outer = DiscreteRange.createRange(radiusJson, 1, false, -1, num -> num > inner.getMax());
        }
    }
}
