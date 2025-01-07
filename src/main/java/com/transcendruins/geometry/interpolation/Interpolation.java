package com.transcendruins.geometry.interpolation;

import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Interpolation</code>: A class used to interpolate between various points.
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
     * <code>double</code>: The timestamp of this <code>Interpolation</code> instance.
     */
    private final double timestamp;

    /**
     * <code>double</code>: A value used to assist in the interpolation of this <code>Interpolation</code> instance.
     */
    private double c;

    /**
     * <code>int</code>: The interpolation method used by this <code>Interpolation</code> instance.
     */
    public final int type;

    /**
     * Creates a new instance of the <code>Interpolation</code> class.
     * @param entry <code>TracedDictionary</code>: The entry to parse into this <code>Interpolation</code> instance.
     * @param timestamp <code>double</code>: The timestamp of this <code>Interpolation</code> instance.
     * @throws LoggedException Thrown if any exception is raised while creating this <code>Interpolation</code> instance.
     */
    public Interpolation(TracedDictionary entry, double timestamp) throws LoggedException {

        this.timestamp = timestamp;

        TracedEntry<TracedDictionary> interpolationEntry = entry.getAsDictionary("interpolation", false);
        TracedDictionary interpolationJson = (TracedDictionary) interpolationEntry.getValue();

        TracedEntry<String> typeEntry = interpolationJson.getAsString("type", false, null);
        String interpolationString = typeEntry.getValue();

        type = switch (interpolationString) {

            case "step" -> STEP_INTERPOLATION;

            case "linear" -> LINEAR_INTERPOLATION;

            case "logistic" -> {

                TracedEntry<Double> adjustmentValueEntry = interpolationJson.getAsDouble("adjustmentValue", false, null, 0.01, 0.49);
                c = adjustmentValueEntry.getValue();

                yield LOGISTIC_INTERPOLATION;
            }

            default -> {
                
                throw new UnexpectedValueException(typeEntry);
            }
        };
    }

    protected double getInter(double nextTimestamp, double t) {

        if (nextTimestamp == timestamp) {
            
            return 0;
        }

        return switch (type) {

            case STEP_INTERPOLATION -> 0;

            case LINEAR_INTERPOLATION -> (t - timestamp) / (nextTimestamp - timestamp);
            
            case LOGISTIC_INTERPOLATION -> {
                double a = (Math.log(1 / (-c + 1d) - 1d) * (nextTimestamp - timestamp)) / (timestamp - (timestamp + nextTimestamp) / 2);
                double v = 1 / (1 + Math.exp(a * (t - (timestamp + nextTimestamp) / 2) / (nextTimestamp - timestamp)));
                yield ((v - 0.5) / (0.5 - c) - 1) / -2;
            }
            default -> 0;
        };
    }
    
    public static double interpolate(double start, double end, double inter) {
        
        return (1 - inter) * start + inter * end;
    }

    public static Vector interpolate(Vector start, Vector end, double inter) {
        
        return start.multiplyScalar(1 - inter).addVector(end.multiplyScalar(inter));
    }

    public static Vector sphericalInterpolate(Vector v1, Vector v2, double inter) {

        v1 = v1.multiplyScalar(1.0 / v1.magnitude());
        v2 = v2.multiplyScalar(1.0 / v2.magnitude());

        double cos_o = v1.dot(v2);

        double o = (cos_o < -1) ? Math.PI : ((cos_o > 1) ? -Math.PI : Math.acos(cos_o));
        double sin_o = Math.sin(o);

        return (sin_o == 0) ? v1 : v1.multiplyScalar(Math.sin(o * (1 - inter)) / sin_o).addVector(v2.multiplyScalar(Math.sin(o * inter) / sin_o));
    }

    public double getTimestamp() {

        return timestamp;
    }
}
