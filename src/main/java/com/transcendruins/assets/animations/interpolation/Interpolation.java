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

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
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
     * <code>float</code>: The timestamp of this <code>Interpolation</code>
     * instance.
     */
    private final float timestamp;

    /**
     * Retrieves the timestamp of this <code>Interpolation</code> instance.
     * 
     * @return <code>float</code>: The <code>timestamp</code> field of this
     *         <code>Interpolation</code> instance.
     */
    public final float getTimestamp() {

        return timestamp;
    }

    /**
     * Creates a new interpolation at a specific timestamp.
     * 
     * @param json      <code>TracedCollection</code>: The collection to parse from.
     * @param key       <code>Object</code>: The key to retrieve.
     * @param timestamp <code>float</code>: The timestamp to interpolate at.
     * @return <code>Interpolation</code>: The resulting interpolation.
     * @throws LoggedException Thrown if the collection could not be parsed.
     */
    public static final Interpolation createInterpolation(TracedCollection collection, Object key, float timestamp)
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
     * @param timestamp <code>float</code>: The timestamp of this
     *                  <code>Interpolation</code> instance in seconds.
     */
    public Interpolation(float timestamp) {

        this.timestamp = timestamp;
    }

    /**
     * Retrieves the value of this <code>Interpolation</code> at a given timestamp.
     * 
     * @param timestamp <code>float</code>: The timestamp to compute at.
     * @return <code>float</code>: The resulting value between <code>0.0</code> and
     *         <code>1.0</code>.
     */
    protected abstract float getValue(float timestamp);

    /**
     * <code>StepInterpolation</code>: A class representing an interpolation method
     * which only jumps to the final position when the animation is complete.
     */
    public static final class StepInterpolation extends Interpolation {

        /**
         * Creates a new instance of the <code>StepInterpolation</code> class.
         * 
         * @param timestamp <code>float</code>: The timestamp to interpolate at.
         */
        public StepInterpolation(float timestamp) {

            super(timestamp);
        }

        @Override
        protected float getValue(float timestamp) {

            return 0.0f;
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
         * @param timestamp <code>float</code>: The timestamp to interpolate at.
         */
        public LinearInterpolation(float timestamp) {

            super(timestamp);
        }

        @Override
        protected float getValue(float timestamp) {

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
         * <code>float</code>: Approximately the greatest slope to change by at the
         * beginning or ending timestamp. The slope will approach this value as the
         * gradient grows towards positive or negative infinity without bounds, but will
         * approach <code>1.0</code> as this value approaches <code>0.0</code>.
         */
        private final float gradient;

        /**
         * Create a new instance of the <code>EaseInterpolation</code> class.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse.
         * @param key        <code>Object</code>: The key to retrieve.
         * @param timestamp  <code>float</code>: The timestamp to interpolate at.
         * @param easeIn     <code>boolean</code>: Whether or not to ease in. A
         *                   <code>false</code> value will multiply the gradient by
         *                   <code>-1.0</code>.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public EaseInterpolation(TracedCollection collection, Object key, float timestamp, boolean easeIn)
                throws LoggedException {

            super(timestamp);

            gradient = collection.get(key, List.of(

                    collection.stringCase(_ -> 2.0f), collection.dictCase(entry -> {

                        TracedDictionary json = entry.getValue();
                        TracedEntry<Float> gradientEntry = json.getAsFloat("gradient", true, 2.0f,
                                num -> 0.0 <= num && num <= 10.0);
                        return gradientEntry.getValue();
                    }))) * (easeIn ? 1 : -1);
        }

        @Override
        protected float getValue(float timestamp) {

            // Use a linear approximation for very small gradients.
            if (Math.abs(gradient) <= 10e-6) {

                return timestamp;
            }

            float exp = FastMath.exp(gradient);
            return (FastMath.pow(exp, timestamp) - 1) / (exp - 1);
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
         * <code>float</code>: Approximately the greatest slope to change by at
         * beginning and end or in the middle timestamp. The slope will approach this
         * value as the gradient grows towards positive or negative infinity without
         * bounds, but will approach <code>1.0</code> as this value approaches
         * <code>0.0</code>.
         */
        private final float gradient;

        /**
         * Create a new instance of the <code>EaseInOutInterpolation</code> class.
         * 
         * @param collection  <code>TracedCollection</code>: The collection to parse.
         * @param key         <code>Object</code>: The key to retrieve.
         * @param timestamp   <code>float</code>: The timestamp to interpolate at.
         * @param forwardEase <code>boolean</code>: Whether or not to ease forward. A
         *                    <code>false</code> value will multiply the gradient by
         *                    <code>-1.0</code>.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public EaseInOutInterpolation(TracedCollection collection, Object key, float timestamp, boolean forwardEase)
                throws LoggedException {
            super(timestamp);

            gradient = collection.get(key, List.of(

                    collection.stringCase(_ -> 2.0f),

                    collection.dictCase(entry -> {

                        TracedDictionary json = entry.getValue();

                        TracedEntry<Float> gradientEntry = json.getAsFloat("gradient", true, 2.0f,

                                num -> 0.0 <= num && num <= 10.0);
                        return gradientEntry.getValue();
                    }))) * (forwardEase ? 1 : -1);
        }

        @Override
        protected float getValue(float timestamp) {

            // Use a linear approximation for very small timestamps.
            if (Math.abs(gradient) <= 10e-6) {
                return timestamp;
            }

            float exp = FastMath.exp(gradient);
            float t = timestamp * 2;

            if (t < 1) {

                return 0.5f * (FastMath.pow(exp, t) - 1) / (exp - 1);
            } else {

                return 0.5f * (2 - (FastMath.pow(exp, 2 - t) - 1) / (exp - 1));
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
     * @param timestamp       <code>float</code>: The timestamp to interpolate at.
     * @param animationLength <code>float</code>: The total length of the animation,
     *                        used to downscale the timestamp.
     * @return <code>float</code>: The interpolation value as a number between
     *         <code>0.0</code> and <code>1.0</code>.
     */
    protected static final float getInter(Interpolation lastInterp, Interpolation nextInterp, float timestamp,
            float animationLength) {

        float last = lastInterp.timestamp;
        float next = nextInterp.timestamp;

        if (next < last) {

            if (timestamp < next) {

                last -= animationLength;
            } else {

                next += animationLength;
            }
        }

        float length = next - last;
        timestamp -= last;

        if (timestamp == 0 || length == 0) {

            return 0;
        }

        if (timestamp == length) {

            return 1;
        }

        return lastInterp.getValue(FastMath.clamp(timestamp / length, 0, 1));
    }

    /**
     * Linearly interpolates between a beginning and end value at an intermediate
     * timestamp.
     * 
     * @param start <code>float</code>: The start value to use.
     * @param end   <code>float</code>: The end value to use.
     * @param inter <code>float</code>: The intermediate timestamp to use.
     * @return <code>float</code>: The interpolated value.
     */
    public static float lerp(float start, float end, float inter) {

        return (1 - inter) * start + inter * end;
    }

    /**
     * Linearly interpolates between a beginning and end vector at an intermediate
     * timestamp.
     * 
     * @param start <code>Vector3f</code>: The start vector to use.
     * @param end   <code>Vector3f</code>: The end vector to use.
     * @param inter <code>float</code>: The intermediate timestamp to use.
     * @return <code>Vector3f</code>: The interpolated vector.
     */
    public static Vector3f lerp(Vector3f start, Vector3f end, float inter) {

        return start.mult(1 - inter).add(end.mult(inter));
    }

    /**
     * Spherically linearly interpolates between a beginning and end vector at an
     * intermediate timestamp.
     * 
     * @param start <code>Vector3f</code>: The start vector to use.
     * @param end   <code>Vector3f</code>: The end vector to use.
     * @param inter <code>float</code>: The intermediate timestamp to use.
     * @return <code>Vector3f</code>: The interpolated vector.
     */
    public static Vector3f slerp(Vector3f start, Vector3f end, float inter) {

        float v1Mag = start.length();
        float v2Mag = end.length();

        if (v1Mag == 0.0 || v2Mag == 0.0) {

            return Vector3f.ZERO;
        }

        start = start.mult(1 / v1Mag);
        end = end.mult(1 / v2Mag);

        float cosO = start.dot(end);

        float o = (cosO < -1) ? FastMath.PI : ((cosO > 1) ? -FastMath.PI : FastMath.acos(cosO));
        float sinO = FastMath.sin(o);

        return (sinO == 0) ? start
                : start.mult(FastMath.sin(o * (1 - inter)) / sinO).add(end.mult(FastMath.sin(o * inter) / sinO));
    }
}
