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

package com.transcendruins.assets.animations.interpolation;

import java.util.List;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Interpolation</code>: A class used to interpolate between various
 * points.
 */
public abstract class Interpolation {

    /**
     * <code>double</code>: The timestamp of this <code>Interpolation</code>
     * instance.
     */
    private final double timestamp;

    /**
     * Retrieves the timestamp of this <code>Interpolation</code> instance.
     * 
     * @return <code>double</code>: The <code>timestamp</code> field of this
     *         <code>Interpolation</code> instance.
     */
    public double getTimestamp() {

        return timestamp;
    }

    public static Interpolation createInterpolation(TracedCollection json, Object key, double timestamp)
            throws LoggedException {

        TracedEntry<String> typeEntry = json.get(key, List.of(

                json.dictCase(entry -> {

                    TracedDictionary interpolationJson = entry.getValue();

                    return interpolationJson.getAsString("type", false, null);
                }),
                json.stringCase(entry -> entry)));

        return switch (typeEntry.getValue()) {

            case "step" -> new StepInterpolation(timestamp);

            case "linear" -> new LinearInterpolation(timestamp);

            case "easeIn" -> new EaseInterpolation(json, key, timestamp, true);

            case "easeOut" -> new EaseInterpolation(json, key, timestamp, false);

            case "easeInOut" -> new EaseInOutInterpolation(json, key, timestamp, true);

            case "midpointEase" -> new EaseInOutInterpolation(json, key, timestamp, false);

            default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    /**
     * Creates a new instance of the <code>Interpolation</code> class.
     * 
     * @param timestamp <code>double</code>: The timestamp of this
     *                  <code>Interpolation</code> instance in seconds.
     * @throws LoggedException Thrown if any exception is raised while creating this
     *                         <code>Interpolation</code> instance.
     */
    public Interpolation(double timestamp) throws LoggedException {

        this.timestamp = timestamp;
    }

    protected abstract double getValue(double timestamp);

    public static final class StepInterpolation extends Interpolation {

        public StepInterpolation(double timestamp) throws LoggedException {

            super(timestamp);
        }

        @Override
        protected double getValue(double timestamp) {

            return 0;
        }
    }

    public static final class LinearInterpolation extends Interpolation {

        public LinearInterpolation(double timestamp) throws LoggedException {

            super(timestamp);
        }

        @Override
        protected double getValue(double timestamp) {

            return timestamp;
        }
    }

    public static final class EaseInterpolation extends Interpolation {

        private final double gradient;

        public EaseInterpolation(TracedCollection collection, Object key, double timestamp, boolean easeIn)
                throws LoggedException {

            super(timestamp);

            gradient = collection.get(key, List.of(

                    collection.stringCase(_ -> 2.0),
                    collection.dictCase(entry -> {

                        TracedDictionary json = entry.getValue();
                        TracedEntry<Double> gradientEntry = json.getAsDouble("gradient", true, 2.0,
                                num -> 0.0 <= num && num <= 10.0);
                        return gradientEntry.getValue();
                    }))) * (easeIn ? 1 : -1);
        }

        @Override
        protected double getValue(double timestamp) {

            // Use a linear approximation for very small timestamps.
            if (gradient <= 10e-6) {

                return timestamp;
            }

            double exp = Math.exp(gradient);
            return (Math.pow(exp, timestamp) - 1) / (exp - 1);
        }
    }

    public static final class EaseInOutInterpolation extends Interpolation {

        private final double gradient;

        public EaseInOutInterpolation(TracedCollection collection, Object key, double timestamp, boolean forwardEase)
                throws LoggedException {
            super(timestamp);

            gradient = collection.get(key, List.of(

                    collection.stringCase(_ -> 2.0),

                    collection.dictCase(entry -> {

                        TracedDictionary json = entry.getValue();

                        TracedEntry<Double> gradientEntry = json.getAsDouble("gradient", true, 2.0,

                                num -> 0.0 <= num && num <= 10.0);
                        return gradientEntry.getValue();
                    }))) * (forwardEase ? 1 : -1);
        }

        @Override
        protected double getValue(double timestamp) {

            // Use a linear approximation for very small timestamps.
            if (gradient <= 10e-6) {
                return timestamp;
            }

            double exp = Math.exp(gradient);
            double t = timestamp * 2;

            if (t < 1) {

                return 0.5 * (Math.pow(exp, t) - 1) / (exp - 1);
            } else {

                return 0.5 * (2 - (Math.pow(exp, 2 - t) - 1) / (exp - 1));
            }
        }
    }

    protected static final double getInter(Interpolation lastInterp, Interpolation nextInterp, double timestamp,
            double animationLength) {

        double last = lastInterp.timestamp;
        double next = nextInterp.timestamp;

        if (next < last) {

            if (timestamp < next) {

                last -= animationLength;
            } else {

                next += animationLength;
            }
        }

        double length = next - last;
        timestamp -= last;

        if (timestamp == 0 || length == 0) {

            return 0;
        }

        if (timestamp == length) {

            return 1;
        }

        return lastInterp.getValue(Math.clamp(timestamp / length, 0.0, 1.0));
    }

    public static double lerp(double start, double end, double inter) {

        return (1 - inter) * start + inter * end;
    }

    public static Vector lerp(Vector start, Vector end, double inter) {

        return start.multiply(1 - inter).add(end.multiply(inter));
    }

    public static Vector slerp(Vector v1, Vector v2, double inter) {

        v1 = v1.multiply(1.0 / v1.magnitude());
        v2 = v2.multiply(1.0 / v2.magnitude());

        double cosO = v1.dot(v2);

        double o = (cosO < -1) ? Math.PI : ((cosO > 1) ? -Math.PI : Math.acos(cosO));
        double sinO = Math.sin(o);

        return (sinO == 0) ? v1
                : v1.multiply(Math.sin(o * (1 - inter)) / sinO)
                        .add(v2.multiply(Math.sin(o * inter) / sinO));
    }
}
