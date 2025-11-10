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

package com.transcendruins.assets.layouts.placement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

import com.transcendruins.assets.layouts.placement.GenerationShapeSchema.CircleGenerationShapeSchema;
import com.transcendruins.assets.layouts.placement.GenerationShapeSchema.PointGenerationShapeSchema;
import com.transcendruins.assets.layouts.placement.GenerationShapeSchema.RectangleGenerationShapeSchema;
import com.transcendruins.assets.layouts.placement.GenerationShapeSchema.RingGenerationShapeSchema;
import com.transcendruins.assets.layouts.placement.GenerationShapeSchema.SpanGenerationShapeSchema;
import com.transcendruins.assets.layouts.placement.GenerationShapeSchema.SquareGenerationShapeSchema;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.utilities.selection.WeightedRoll;

public abstract class GenerationShapeInstance {

    public static final GenerationShapeInstance DEFAULT = new GenerationShapeInstance(GenerationDistribution.UNIFORM, 0,
            0, 0, 0) {

        @Override
        public final Iterator<Point> createIterator() {

            return new ArrayList<Point>().iterator();
        }
    };

    public static final GenerationShapeInstance createShape(GenerationShapeSchema schema, DeterministicRandom random,
            GenerationDistribution distribution, int centerX, int centerZ, int parentWidth, int parentLength,
            int childWidth, int childLength) {

        if (parentWidth == 0 || parentLength == 0) {

            return DEFAULT;
        }

        return switch (schema) {

        case SpanGenerationShapeSchema _ -> new SpanGenerationShapeInstance(distribution, centerX, centerZ, parentWidth,
                parentLength, childWidth, childLength);

        case PointGenerationShapeSchema _ -> new PointGenerationShapeInstance(distribution, centerX, centerZ,
                childWidth, childLength);

        case SquareGenerationShapeSchema square -> new SquareGenerationShapeInstance(square, random, distribution,
                centerX, centerZ, childWidth, childLength);

        case RectangleGenerationShapeSchema rectangle -> new RectangleGenerationShapeInstance(rectangle, random,
                distribution, centerX, centerZ, childWidth, childLength);

        case CircleGenerationShapeSchema circle -> new CircleGenerationShapeInstance(circle, random, distribution,
                centerX, centerZ, childWidth, childLength);

        case RingGenerationShapeSchema ring -> new RingGenerationShapeInstance(ring, random, distribution, centerX,
                centerZ, childWidth, childLength);

        default -> DEFAULT;
        };
    }

    private final GenerationDistribution distribution;

    protected final int centerX;

    protected final int centerZ;

    private final int childWidth;

    private final int childLength;

    public GenerationShapeInstance(GenerationDistribution distribution, int centerX, int centerZ, int childWidth,
            int childLength) {

        this.distribution = distribution;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.childWidth = childWidth;
        this.childLength = childLength;
    }

    public final Point getPoint(Function<Point, Boolean> filter, DeterministicRandom random) {

        ArrayList<Point> options = new ArrayList<>();

        Iterator<Point> it = createIterator();
        while (it.hasNext()) {

            Point p = it.next();

            if (filter.apply(p)) {

                options.add(p);
            }
        }

        if (options.isEmpty()) {

            return null;
        }

        WeightedRoll<Point> roll = new WeightedRoll<>(options.stream(),
                p -> distribution.getChance(p.x + childWidth / 2 - centerX, p.y + childLength / 2 - centerZ, random));
        return roll.get(random.next());
    }

    protected abstract Iterator<Point> createIterator();

    public static final class SpanGenerationShapeInstance extends GenerationShapeInstance {

        private final int parentWidth;

        private final int parentLength;

        public SpanGenerationShapeInstance(GenerationDistribution distribution, int centerX, int centerZ,
                int parentWidth, int parentLength, int childWidth, int childLength) {

            super(distribution, centerX, centerZ, childWidth, childLength);

            this.parentWidth = parentWidth;
            this.parentLength = parentLength;
        }

        @Override
        public final Iterator<Point> createIterator() {

            return new Iterator<>() {

                private int x = 0;
                private int z = 0;

                @Override
                public final boolean hasNext() {

                    return z < parentLength;
                }

                @Override
                public final Point next() {

                    Point p = new Point(x, z);

                    x++;
                    if (x >= parentWidth) {

                        x = 0;
                        z++;
                    }

                    return p;
                }
            };
        }
    };

    public static final class PointGenerationShapeInstance extends GenerationShapeInstance {

        public PointGenerationShapeInstance(GenerationDistribution distribution, int centerX, int centerZ,
                int childWidth, int childLength) {

            super(distribution, centerX, centerZ, childWidth, childLength);
        }

        @Override
        public final Iterator<Point> createIterator() {

            return new Iterator<>() {

                private boolean consumed = false;

                @Override
                public final boolean hasNext() {

                    return !consumed;
                }

                @Override
                public final Point next() {

                    consumed = true;
                    return new Point(centerX, centerZ);
                }
            };
        }
    }

    public static final class SquareGenerationShapeInstance extends GenerationShapeInstance {

        private final int width;

        private final int startX;

        private final int startZ;

        private final int ceilX;

        private final int ceilZ;

        public SquareGenerationShapeInstance(GenerationShapeSchema.SquareGenerationShapeSchema schema,
                DeterministicRandom random, GenerationDistribution distribution, int centerX, int centerZ,
                int childWidth, int childLength) {

            super(distribution, centerX, centerZ, childWidth, childLength);

            width = schema.getWidth().get(random.next());

            startX = centerX - width / 2;
            startZ = centerZ - width / 2;

            ceilX = centerX + (int) (Math.ceil(width / 2.0));
            ceilZ = centerZ + (int) Math.ceil(width / 2.0);
        }

        @Override
        public final Iterator<Point> createIterator() {

            return new Iterator<>() {

                private int x = startX;

                private int z = startZ;

                @Override
                public final boolean hasNext() {

                    return z < ceilZ;
                }

                @Override
                public final Point next() {

                    Point p = new Point(x, z);

                    x++;
                    if (x >= ceilX) {

                        x = centerX - width / 2;
                        z++;
                    }

                    return p;
                }
            };
        }

    }

    public static final class RectangleGenerationShapeInstance extends GenerationShapeInstance {

        private final int width;

        private final int length;

        private final int startX;

        private final int startZ;

        private final int ceilX;

        private final int ceilZ;

        protected RectangleGenerationShapeInstance(GenerationShapeSchema.RectangleGenerationShapeSchema schema,
                DeterministicRandom random, GenerationDistribution distribution, int centerX, int centerZ,
                int childWidth, int childLength) {

            super(distribution, centerX, centerZ, childWidth, childLength);

            width = schema.getWidth().get(random.next());
            length = schema.getLength().get(random.next());

            startX = centerX - width / 2;
            startZ = centerZ - length / 2;

            ceilX = centerX + (int) (Math.ceil(width / 2.0));
            ceilZ = centerZ + (int) Math.ceil(length / 2.0);
        }

        @Override
        public final Iterator<Point> createIterator() {

            return new Iterator<>() {

                private int x = startX;

                private int z = startZ;

                @Override
                public final boolean hasNext() {

                    return z < ceilZ;
                }

                @Override
                public final Point next() {

                    Point p = new Point(x, z);

                    x++;
                    if (x >= ceilX) {

                        x = centerX - width / 2;
                        z++;
                    }

                    return p;
                }

            };
        }
    }

    public static final class CircleGenerationShapeInstance extends GenerationShapeInstance {

        private final int radius;

        private final int startX;

        private final int startZ;

        private final int startCeilX;

        protected CircleGenerationShapeInstance(GenerationShapeSchema.CircleGenerationShapeSchema schema,
                DeterministicRandom random, GenerationDistribution distribution, int centerX, int centerZ,
                int childWidth, int childLength) {

            super(distribution, centerX, centerZ, childWidth, childLength);

            radius = schema.getRadius().get(random.next());

            startX = centerX;
            startCeilX = centerX;

            startZ = centerZ - radius;
        }

        @Override
        public final Iterator<Point> createIterator() {

            return new Iterator<>() {

                private int x = startX;

                private int ceilX = startCeilX;

                private int z = startZ;

                @Override
                public final boolean hasNext() {

                    return z <= centerZ + radius;
                }

                @Override
                public final Point next() {

                    Point p = new Point(x, z);

                    x++;
                    if (x > ceilX) {

                        z++;
                        int desc = radius * radius - (z - centerZ) * (z - centerZ);
                        if (desc < 0) {

                            desc = 0;
                        }

                        int r = (int) Math.round(Math.sqrt(desc));

                        x = centerX - r;
                        ceilX = centerX + r;
                    }

                    return p;
                }
            };
        }
    }

    public static final class RingGenerationShapeInstance extends GenerationShapeInstance {

        private final int inner;

        private final int outer;

        private final int startX;

        private final int startZ;

        private final int startCeilX;

        private final int startSkipX;

        protected RingGenerationShapeInstance(GenerationShapeSchema.RingGenerationShapeSchema schema,
                DeterministicRandom random, GenerationDistribution distribution, int centerX, int centerZ,
                int childWidth, int childLength) {

            super(distribution, centerX, centerZ, childWidth, childLength);

            inner = schema.getInner().get(random.next());
            outer = schema.getInner().get(random.next());

            startX = centerX;
            startZ = centerZ - outer;

            startCeilX = centerX;
            startSkipX = -1;
        }

        @Override
        public final Iterator<Point> createIterator() {

            return new Iterator<>() {

                private int x = startX;

                private int ceilX = startCeilX;

                private int skipX = startSkipX;

                private int z = startZ;

                @Override
                public final boolean hasNext() {

                    return z <= centerZ + outer;
                }

                @Override
                public final Point next() {

                    Point p = new Point(x, z);

                    x++;
                    if (skipX > -1 && centerX - skipX == x) {

                        x = centerX + skipX + 1;
                    }

                    if (x > ceilX) {

                        z++;
                        int descOut = outer * outer - (z - centerZ) * (z - centerZ);
                        if (descOut < 0) {

                            descOut = 0;
                        }

                        int rOut = (int) Math.round(Math.sqrt(descOut));

                        x = centerX - rOut;
                        ceilX = centerX + rOut;

                        int descIn = inner * inner - (z - centerZ) * (z - centerZ);
                        if (descIn < 0) {

                            skipX = -1;
                        } else {

                            int rIn = (int) Math.round(Math.sqrt(descIn));
                            skipX = rIn;
                        }
                    }

                    return p;
                }
            };
        }
    }
}
