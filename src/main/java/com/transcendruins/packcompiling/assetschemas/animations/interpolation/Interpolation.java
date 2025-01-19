package com.transcendruins.packcompiling.assetschemas.animations.interpolation;

import org.json.simple.JSONObject;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Interpolation</code>: A class used to interpolate between various
 * points.
 */
public final class Interpolation {

    /**
     * <code>int</code>: An enum value representing a step interpolation.
     * Interpolates based on the exact position of the previous keyframe.
     */
    private static final int STEP_INTERPOLATION = 1;

    /**
     * <code>int</code>: An enum value representing a linear interpolation.
     * Interpolates based on a constant slope between the nearest neighbor points.
     */
    private static final int LINEAR_INTERPOLATION = 2;

    /**
     * <code>int</code>: An enum value representing a logistic interpolation.
     * Interpolates based on a logistic slope between the nearest neighbor points.
     */
    private static final int LOGISTIC_INTERPOLATION = 3;

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

    /**
     * <code>double</code>: A value used to assist in the interpolation of this
     * <code>Interpolation</code> instance.
     */
    private final double c;

    /**
     * <code>int</code>: The interpolation method used by this
     * <code>Interpolation</code> instance.
     */
    private final int type;

    /**
     * Retrieves the interpolation type of this <code>Interpolation</code> instance.
     * 
     * @return <code>int</code>: The <code>type</code> field of this
     *         <code>Interpolation</code> instance.
     */
    public int getType() {

        return type;
    }

    /**
     * Creates a new instance of the <code>Interpolation</code> class.
     * 
     * @param entry     <code>TracedDictionary</code>: The entry to parse into
     *                  this
     *                  <code>Interpolation</code> instance.
     * @param timestamp <code>double</code>: The timestamp of this
     *                  <code>Interpolation</code> instance in seconds.
     * @throws LoggedException Thrown if any exception is raised while creating this
     *                         <code>Interpolation</code> instance.
     */
    public Interpolation(TracedDictionary entry, double timestamp) throws LoggedException {

        this.timestamp = timestamp;

        String interpolationString;

        TracedEntry<?> value = entry.get("interpolation", false, null, String.class, JSONObject.class);
        TracedEntry<String> typeEntry;

        if (value.getValue() instanceof TracedDictionary interpolationJson) {

            typeEntry = interpolationJson.getAsString("type", false, null);
            interpolationString = typeEntry.getValue();

            TracedEntry<Double> adjustmentValueEntry = interpolationJson.getAsDouble("adjustmentValue", true, 0.5,
                    0.0, 1.0);

            double tempC = adjustmentValueEntry.getValue();
            c = Math.pow(tempC, 1.0 / tempC) / 2;
        } else {

            typeEntry = entry.getAsString("interpolation", false, null);
            interpolationString = typeEntry.getValue();

            c = 0.005;
        }

        type = switch (interpolationString) {

            case "step" -> STEP_INTERPOLATION;

            case "linear" -> LINEAR_INTERPOLATION;

            case "logistic" -> {

                yield LOGISTIC_INTERPOLATION;
            }

            default -> {

                throw new UnexpectedValueException(typeEntry);
            }
        };
    }

    protected static double getInter(Interpolation lastInterp, Interpolation nextInterp, double timestamp,
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

        if (last == next || timestamp == last) {

            return 0;
        }

        if (timestamp == next) {

            return 1;
        }

        double d = next - last;

        return switch (lastInterp.type) {

            case STEP_INTERPOLATION -> 0;

            case LINEAR_INTERPOLATION -> (timestamp - last) / d;

            case LOGISTIC_INTERPOLATION -> {

                if (lastInterp.c == 0.0) {

                    yield (timestamp >= (next + last) / 2.0) ? 1.0 : 0.0;
                }

                if (lastInterp.c == 1.0) {

                    yield (timestamp - last) / d;
                }

                double a = 2.0 * Math.log(2.0 / lastInterp.c - 1.0);
                double v = 1.0 / (1.0 + Math.exp(a * (timestamp - (last + next) / 2) / d));

                yield (1.0 - lastInterp.c / 2.0 - v) / (1.0 - lastInterp.c);
            }
            default -> 0.0;
        };
    }

    public static double lerp(double start, double end, double inter) {

        return (1 - inter) * start + inter * end;
    }

    public static Vector lerp(Vector start, Vector end, double inter) {

        return start.multiplyScalar(1 - inter).addVector(end.multiplyScalar(inter));
    }

    public static Vector slerp(Vector v1, Vector v2, double inter) {

        v1 = v1.multiplyScalar(1.0 / v1.magnitude());
        v2 = v2.multiplyScalar(1.0 / v2.magnitude());

        double cos_o = v1.dot(v2);

        double o = (cos_o < -1) ? Math.PI : ((cos_o > 1) ? -Math.PI : Math.acos(cos_o));
        double sin_o = Math.sin(o);

        return (sin_o == 0) ? v1
                : v1.multiplyScalar(Math.sin(o * (1 - inter)) / sin_o)
                        .addVector(v2.multiplyScalar(Math.sin(o * inter) / sin_o));
    }
}
