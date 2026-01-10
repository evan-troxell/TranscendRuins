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

package com.transcendruins.assets.animations.interpolation;

import java.util.List;

import com.transcendruins.geometry.Vector;
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
    public final double getTimestamp() {

        return timestamp;
    }

    /**
     * Creates a new interpolation at a specific timestamp.
     * 
     * @param json      <code>TracedCollection</code>: The collection to parse from.
     * @param key       <code>Object</code>: The key to retrieve.
     * @param timestamp <code>double</code>: The timestamp to interpolate at.
     * @return <code>Interpolation</code>: The resulting interpolation.
     * @throws LoggedException Thrown if the collection could not be parsed.
     */
    public static final Interpolation createInterpolation(TracedCollection collection, Object key, double timestamp)
            throws LoggedException {

        TracedEntry<String> typeEntry = collection.get(key, List.of(

                collection.dictCase(entry -> {

                    TracedDictionary interpolationJson = entry.getValue();

                    return interpolationJson.getAsString("type", false, null);
                }), collection.stringCase(entry -> entry)));

        return switch (typeEntry.getValue()) {

        case "step" -> new StepInterpolation(timestamp);

        case "linear" -> new LinearInterpolation(timestamp);

        case "easeIn" -> new EaseInterpolation(collection, key, timestamp, true);

        case "easeOut" -> new EaseInterpolation(collection, key, timestamp, false);

        case "easeInOut" -> new EaseInOutInterpolation(collection, key, timestamp, true);

        case "midpointEase" -> new EaseInOutInterpolation(collection, key, timestamp, false);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    /**
     * Creates a new instance of the <code>Interpolation</code> class.
     * 
     * @param timestamp <code>double</code>: The timestamp of this
     *                  <code>Interpolation</code> instance in seconds.
     */
    public Interpolation(double timestamp) {

        this.timestamp = timestamp;
    }

    /**
     * Retrieves the value of this <code>Interpolation</code> at a given timestamp.
     * 
     * @param timestamp <code>double</code>: The timestamp to compute at.
     * @return <code>double</code>: The resulting value between <code>0.0</code> and
     *         <code>1.0</code>.
     */
    protected abstract double getValue(double timestamp);

    /**
     * <code>StepInterpolation</code>: A class representing an interpolation method
     * which only jumps to the final position when the animation is complete.
     */
    public static final class StepInterpolation extends Interpolation {

        /**
         * Creates a new instance of the <code>StepInterpolation</code> class.
         * 
         * @param timestamp <code>double</code>: The timestamp to interpolate at.
         */
        public StepInterpolation(double timestamp) {

            super(timestamp);
        }

        @Override
        protected double getValue(double timestamp) {

            return 0.0;
        }
    }

    /**
     * <code>LinearInterpolation</code>: A class representing an interpolation
     * method which scales linearly between the start and end timestamp.
     */
    public static final class LinearInterpolation extends Interpolation {

        /**
         * Creates a new instance of the <code>LinearInterpolation</code> class.
         * 
         * @param timestamp <code>double</code>: The timestamp to interpolate at.
         */
        public LinearInterpolation(double timestamp) {

            super(timestamp);
        }

        @Override
        protected double getValue(double timestamp) {

            return timestamp;
        }
    }

    /**
     * <code>EaseInterpolation</code>: A class representing an interpolation method
     * which either begins slow but accelerates quickly (ease-in) or begins fast and
     * decelarates quickly (ease-out).
     */
    public static final class EaseInterpolation extends Interpolation {

        /**
         * <code>double</code>: Approximately the greatest slope to change by at the
         * beginning or ending timestamp. The slope will approach this value as the
         * gradient grows towards positive or negative infinity without bounds, but will
         * approach <code>1.0</code> as this value approaches <code>0.0</code>.
         */
        private final double gradient;

        /**
         * Create a new instance of the <code>EaseInterpolation</code> class.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse.
         * @param key        <code>Object</code>: The key to retrieve.
         * @param timestamp  <code>double</code>: The timestamp to interpolate at.
         * @param easeIn     <code>boolean</code>: Whether or not to ease in. A
         *                   <code>false</code> value will multiply the gradient by
         *                   <code>-1.0</code>.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public EaseInterpolation(TracedCollection collection, Object key, double timestamp, boolean easeIn)
                throws LoggedException {

            super(timestamp);

            gradient = collection.get(key, List.of(

                    collection.stringCase(_ -> 2.0), collection.dictCase(entry -> {

                        TracedDictionary json = entry.getValue();
                        TracedEntry<Double> gradientEntry = json.getAsDouble("gradient", true, 2.0,
                                num -> 0.0 <= num && num <= 10.0);
                        return gradientEntry.getValue();
                    }))) * (easeIn ? 1 : -1);
        }

        @Override
        protected double getValue(double timestamp) {

            // Use a linear approximation for very small gradients.
            if (Math.abs(gradient) <= 10e-6) {

                return timestamp;
            }

            double exp = Math.exp(gradient);
            return (Math.pow(exp, timestamp) - 1) / (exp - 1);
        }
    }

    /**
     * <code>EaseInOutInterpolation</code>: A class representing an interpolation
     * method which either begins and ends slow but accelerates quickly in the
     * middle (ease-in-out) or begins and ends fast and decelarates quickly in the
     * middle (ease-out-in).
     */
    public static final class EaseInOutInterpolation extends Interpolation {

        /**
         * <code>double</code>: Approximately the greatest slope to change by at
         * beginning and end or in the middle timestamp. The slope will approach this
         * value as the gradient grows towards positive or negative infinity without
         * bounds, but will approach <code>1.0</code> as this value approaches
         * <code>0.0</code>.
         */
        private final double gradient;

        /**
         * Create a new instance of the <code>EaseInOutInterpolation</code> class.
         * 
         * @param collection  <code>TracedCollection</code>: The collection to parse.
         * @param key         <code>Object</code>: The key to retrieve.
         * @param timestamp   <code>double</code>: The timestamp to interpolate at.
         * @param forwardEase <code>boolean</code>: Whether or not to ease forward. A
         *                    <code>false</code> value will multiply the gradient by
         *                    <code>-1.0</code>.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
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
            if (Math.abs(gradient) <= 10e-6) {
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

    /**
     * Retrieves the interpolation value between two keyframes at a given timestamp.
     * 
     * @param lastInterp      <code>Interpolation</code>: The first keyframe (and
     *                        the interpolation method) to use.
     * @param nextInterp      <code>Interpolation</code>: The second keyframe to
     *                        use.
     * @param timestamp       <code>double</code>: The timestamp to interpolate at.
     * @param animationLength <code>double</code>: The total length of the
     *                        animation, used to downscale the timestamp.
     * @return <code>double</code>: The interpolation value as a number between
     *         <code>0.0</code> and <code>1.0</code>.
     */
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

    /**
     * Linearly interpolates between a beginning and end value at an intermediate
     * timestamp.
     * 
     * @param start <code>double</code>: The start value to use.
     * @param end   <code>double</code>: The end value to use.
     * @param inter <code>double</code>: The intermediate timestamp to use.
     * @return <code>double</code>: The interpolated value.
     */
    public static double lerp(double start, double end, double inter) {

        return (1 - inter) * start + inter * end;
    }

    /**
     * Linearly interpolates between a beginning and end vector at an intermediate
     * timestamp.
     * 
     * @param start <code>Vector</code>: The start vector to use.
     * @param end   <code>Vector</code>: The end vector to use.
     * @param inter <code>double</code>: The intermediate timestamp to use.
     * @return <code>Vector</code>: The interpolated vector.
     */
    public static Vector lerp(Vector start, Vector end, double inter) {

        return start.multiply(1 - inter).add(end.multiply(inter));
    }

    /**
     * Spherically linearly interpolates between a beginning and end vector at an
     * intermediate timestamp.
     * 
     * @param start <code>Vector</code>: The start vector to use.
     * @param end   <code>Vector</code>: The end vector to use.
     * @param inter <code>double</code>: The intermediate timestamp to use.
     * @return <code>Vector</code>: The interpolated vector.
     */
    public static Vector slerp(Vector start, Vector end, double inter) {

        double v1Mag = start.magnitude();
        double v2Mag = end.magnitude();

        if (v1Mag == 0.0 || v2Mag == 0.0) {

            return Vector.IDENTITY_VECTOR;
        }

        start = start.multiply(1.0 / v1Mag);
        end = end.multiply(1.0 / v2Mag);

        double cosO = start.dot(end);

        double o = (cosO < -1) ? Math.PI : ((cosO > 1) ? -Math.PI : Math.acos(cosO));
        double sinO = Math.sin(o);

        return (sinO == 0) ? start
                : start.multiply(Math.sin(o * (1 - inter)) / sinO).add(end.multiply(Math.sin(o * inter) / sinO));
    }
}
