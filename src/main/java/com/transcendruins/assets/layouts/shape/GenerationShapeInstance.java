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

import com.transcendruins.world.World;

public abstract class GenerationShapeInstance {

    public abstract boolean isValidPlacement(int x, int y, int centerX, int centerY);

    public static final GenerationShapeInstance buildShape(GenerationShapeSchema schema, World world) {

        return switch (schema) {

            case GenerationShapeSchema.PointSchema _ -> new PointInstance();

            case GenerationShapeSchema.SquareSchema square -> new SquareInstance(square, world);

            case GenerationShapeSchema.RectangleSchema rectangle -> new RectangleInstance(rectangle, world);

            case GenerationShapeSchema.CircleSchema circle -> new CircleInstance(circle, world);

            case GenerationShapeSchema.RingSchema ring -> new RingInstance(ring, world);

            default -> new GenerationShapeInstance() {

                @Override
                public boolean isValidPlacement(int x, int y, int centerX, int centerY) {

                    return true;
                }
            };
        };
    }

    public static final class PointInstance extends GenerationShapeInstance {

        @Override
        public boolean isValidPlacement(int x, int y, int centerX, int centerY) {

            return x == centerX && y == centerY;
        }
    }

    public static final class SquareInstance extends GenerationShapeInstance {

        private final int width;

        protected SquareInstance(GenerationShapeSchema.SquareSchema schema, World world) {

            width = schema.getWidth().getIntegerValue(world.nextRandom());
        }

        @Override
        public boolean isValidPlacement(int x, int y, int centerX, int centerY) {

            int dx = x - centerX;
            int dy = y - centerY;

            int halfWidth = width / 2;

            return -halfWidth <= dx && dx < halfWidth
                    && -halfWidth <= dy && dy < halfWidth;
        }
    }

    public static final class RectangleInstance extends GenerationShapeInstance {

        private final int width;

        private final int height;

        protected RectangleInstance(GenerationShapeSchema.RectangleSchema schema, World world) {

            width = schema.getWidth().getIntegerValue(world.nextRandom());
            height = schema.getHeight().getIntegerValue(world.nextRandom());
        }

        @Override
        public boolean isValidPlacement(int x, int y, int centerX, int centerY) {

            int dx = x - centerX;
            int dy = y - centerY;

            int halfWidth = width / 2;
            int halfHeight = height / 2;

            return -halfWidth <= dx && dx < halfWidth
                    && -halfHeight <= dy && dy < halfHeight;
        }
    }

    public static final class CircleInstance extends GenerationShapeInstance {

        private final int radius;

        protected CircleInstance(GenerationShapeSchema.CircleSchema schema, World world) {

            radius = schema.getRadius().getIntegerValue(world.nextRandom());
        }

        @Override
        public boolean isValidPlacement(int x, int y, int centerX, int centerY) {

            int dx = x - centerX;
            int dy = y - centerY;

            double distance = Math.hypot(dx, dy);

            return distance <= radius;
        }
    }

    public static final class RingInstance extends GenerationShapeInstance {

        private final int inner;

        private final int outer;

        protected RingInstance(GenerationShapeSchema.RingSchema schema, World world) {

            inner = schema.getInner().getIntegerValue(world.nextRandom());
            outer = schema.getInner().getIntegerValue(world.nextRandom());
        }

        @Override
        public boolean isValidPlacement(int x, int y, int centerX, int centerY) {

            int dx = x - centerX;
            int dy = y - centerY;

            double distance = Math.hypot(dx, dy);

            return distance >= inner && distance <= outer;
        }
    }
}
