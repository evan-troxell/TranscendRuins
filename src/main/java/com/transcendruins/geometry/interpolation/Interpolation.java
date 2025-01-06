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
     * <code>int</code>: An enum value representing a jump interpolation.
     * Interpolates based on the exact position of the previous keyframe.
     */
    private static final int JUMP_INTERPOLATION = 1;

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
     * <code>AlignedVector</code>: The aligned vector to be interpolated.
     */
    public final AlignedVector vector;

    /**
     * Creates a new instance of the <code>Interpolation</code> class.
     * @param entry <code>TracedDictionary</code>: The entry to parse into this <code>Interpolation</code> instance.
     * @param key <code>String</code>: The key which should be retrieved for the translation portion of this <code>Interpolation</code> instance.
     * @param timestamp <code>double</code>: The timestamp of this <code>Interpolation</code> instance.
     * @throws LoggedException Thrown if any exception is raised while creating this <code>Interpolation</code> instance.
     */
    public Interpolation(TracedDictionary entry, String key, double timestamp) throws LoggedException {

        this.timestamp = timestamp;

        TracedEntry<Vector> keyEntry = entry.getAsVector(key, false, Vector.DIMENSION_3D);
        Vector translation = keyEntry.getValue();

        TracedEntry<Vector> axisAlignEntry = entry.getAsVector("axisAlignment", false, Vector.DIMENSION_3D);
        Vector axisAlign = axisAlignEntry.getValue();

        vector = new AlignedVector(translation, axisAlign);

        TracedEntry<TracedDictionary> interpolationEntry = entry.getAsDictionary("interpolation", false);
        TracedDictionary interpolationJson = (TracedDictionary) interpolationEntry.getValue();

        TracedEntry<String> typeEntry = interpolationJson.getAsString("type", false, null);
        String interpolationString = typeEntry.getValue();

        type = switch (interpolationString) {

            case "jump" -> JUMP_INTERPOLATION;

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

    public AlignedVector interpolate(Interpolation nextKeyframe, double interpolationTimestamp) {

        double[] transform = new double[Vector.DIMENSION_3D];
        double[] axisAlignment = new double[Vector.DIMENSION_3D];

        for (int i = 0; i < transform.length; i++) {

            double x1 = timestamp;
            double y1 = vector.transformation.get(i);

            double x2 = nextKeyframe.timestamp;
            double y2 = nextKeyframe.vector.transformation.get(i);

            transform[i] = interpolate(x1, y1, x2, y2, interpolationTimestamp);
        }

        for (int i = 0; i < axisAlignment.length; i++) {

            double x1 = timestamp;
            double y1 = vector.axisAlignment.get(i);

            double x2 = nextKeyframe.timestamp;
            double y2 = nextKeyframe.vector.axisAlignment.get(i);

            axisAlignment[i] = interpolate(x1, y1, x2, y2, interpolationTimestamp);
        }

        return new AlignedVector(new Vector(transform), new Vector(axisAlignment));
    }

    /**
     * Interpolates between points (x1, y1) and (x2, y2) at input x using the interpolation method of this <code>Interpolation</code> instance.
     * @param x1 <code>double</code>: The x value of the first point on the interpolation function.
     * @param y1 <code>double</code>: The y value of the first point on the interpolation function.
     * @param x2 <code>double</code>: The x value of the second point on the interpolation function.
     * @param y2 <code>double</code>: The y value of the second point on the interpolation function.
     * @param x <code>double</code>: The input value whose interpolation to find.
     * @return <code>double</code>: The resulting interplation value.
     */
    private double interpolate(double x1, double y1, double x2, double y2, double x) {

        return switch (type) {

            case JUMP_INTERPOLATION -> y1;

            case LINEAR_INTERPOLATION -> (y2 - y1) / (x2 - x1) * (x - x1) + y1;
            
            case LOGISTIC_INTERPOLATION -> {
                double a = (Math.log(1 / (-c + 1d) - 1d) * (x2 - x1)) / (x1 - (x1 + x2) / 2);
                double v = 1 / (1 + Math.exp(a * (x - (x1 + x2) / 2) / (x2 - x1)));
                yield ((v - 0.5) / (0.5 - c) * (y1 - y2) + y1 + y2) / 2;
            }
            default -> 0;
        };
    }
}
