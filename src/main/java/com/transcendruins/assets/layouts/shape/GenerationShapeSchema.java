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

import com.transcendruins.assets.extra.Range;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public abstract class GenerationShapeSchema {

    public static final GenerationShapeSchema DEFAULT_SHAPE = new DefaultSchema();

    private static final class DefaultSchema extends GenerationShapeSchema {
    }

    public static final GenerationShapeSchema createShape(TracedDictionary json)
            throws LoggedException {

        TracedEntry<String> shapeEntry = json.getAsString("shape", true, null);
        if (!shapeEntry.containsValue()) {

            return DEFAULT_SHAPE;
        }

        String shape = shapeEntry.getValue();
        return switch (shape) {

            case "point" -> new PointSchema();

            case "square" -> new SquareSchema(json);

            case "rectangle" -> new RectangleSchema(json);

            case "circle" -> new CircleSchema(json);

            case "ring" -> new RingSchema(json);

            default -> DEFAULT_SHAPE;
        };
    }

    public static final class PointSchema extends GenerationShapeSchema {
    }

    public static final class SquareSchema extends GenerationShapeSchema {

        private final Range width;

        public Range getWidth() {

            return width;
        }

        protected SquareSchema(TracedDictionary json)
                throws LoggedException {

            width = Range.createRange(json, "width", false, true, num -> num >= 1);
        }
    }

    public static final class RectangleSchema extends GenerationShapeSchema {

        private final Range width;

        public Range getWidth() {

            return width;
        }

        private final Range height;

        public Range getHeight() {

            return height;
        }

        protected RectangleSchema(TracedDictionary json)
                throws LoggedException {

            width = Range.createRange(json, "width", false, true, num -> num >= 1);
            height = json.containsKey("height") ? Range.createRange(json, "height", false, true, num -> num >= 1)
                    : width;
        }
    }

    public static final class CircleSchema extends GenerationShapeSchema {

        private final Range radius;

        public Range getRadius() {

            return radius;
        }

        protected CircleSchema(TracedDictionary json)
                throws LoggedException {

            radius = Range.createRange(json, "radius", false, true, num -> num >= 1);
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

        protected RingSchema(TracedDictionary json)
                throws LoggedException {

            TracedEntry<TracedArray> radiusEntry = json.getAsArray("radius", false);
            TracedArray radiusJson = radiusEntry.getValue();

            inner = Range.createRange(radiusJson, 0, false, true, num -> num >= 1);
            outer = Range.createRange(radiusJson, 1, false, true, num -> num >= inner.getMax() + 1);
        }
    }
}
