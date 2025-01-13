package com.transcendruins.graphics3d;

import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.MatrixOperations;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.ui.Render3D;

/**
 * <code>Position3D</code>: A class representing a position and orientation in
 * 3D space.
 */
public class Position3D {

    /**
     * <code>Vector</code>: The position to render from.
     */
    private Vector position;

    /**
     * Retrieves the value of the <code>position</code> field of this
     * <code>Position3D</code> instance.
     * 
     * @return <code>Vector</code>: The <code>position</code> field of this
     *         <code>Position3D</code> instance.
     */
    public final Vector getPosition() {

        return position;
    }

    /**
     * <code>Vector[2]</code>: The positional bounds representing the maximum and
     * minimum X, Y, and Z values allowed in the <code>position</code> field of this
     * <code>Position3D</code> instance.
     */
    private Vector[] positionBounds = new Vector[] {
            new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
            new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY) };

    /**
     * <code>double</code>: The heading of this <code>Position3D</code> instance,
     * specified in radians.
     */
    private double heading;

    /**
     * Retrieves the value of the <code>heading</code> field of this
     * <code>Position3D</code> instance.
     * 
     * @return <code>double</code>: The <code>heading</code> field of this
     *         <code>Position3D</code> instance, in radians.
     */
    public final double getHeading() {

        return heading;
    }

    /**
     * <code>Double[2]</code>: The rotation bounds representing the maximum and
     * minimum heading value allowed in the <code>heading</code> field of this
     * <code>Position3D</code> instance, specified in radians.
     */
    private Double[] headingBounds = new Double[] { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };

    /**
     * <code>double</code>: The pitch of this <code>Position3D</code> instance,
     * specified in radians.
     */
    private double pitch;

    /**
     * Retrieves the value of the <code>pitch</code> field of this
     * <code>Position3D</code> instance.
     * 
     * @return <code>double</code>: The <code>pitch</code> field of this
     *         <code>Position3D</code> instance, in radians.
     */
    public final double getPitch() {

        return pitch;
    }

    /**
     * <code>Double[2]</code>: The rotation bounds representing the maximum and
     * minimum pitch value allowed in the <code>pitch</code> field of this
     * <code>Position3D</code> instance, specified in radians.
     */
    private Double[] pitchBounds = new Double[] { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };

    /**
     * <code>MatrixOperations</code>: The operations used to normalize a polygon to
     * the position and orientation of this <code>Position3D</code> instance.
     */
    private MatrixOperations renderTransform;

    /**
     * Retrieves the <code>renderTransform</code> field of this
     * <code>Position3D</code> instance, adjusted to be in the center of the frame.
     * 
     * @return <code>MatrixOperations</code>: The operations used to normalize and
     *         center a polygon onto the frame.
     */
    public final MatrixOperations getRenderTransform() {

        return renderTransform;
    }

    /**
     * Creates a new instance of the <code>Position3D</code> class whose position is
     * centered around the origin.
     */
    public Position3D() {

        this(new Vector(0, 0, 0), 0, 0, false);
    }

    /**
     * Creates a new instance of the <code>Position3D</code> class.
     * 
     * @param position  <code>Vector</code>: The position to render from.
     * @param heading   <code>double</code>: The heading to render from.
     * @param pitch     <code>double</code>: The pitch to render from.
     * @param isRadians <code>boolean</code>: Whether or not the
     *                  <code>heading</code> and <code>pitch</code> perameters are
     *                  specified in radians.
     */
    public Position3D(Vector position, double heading, double pitch, boolean isRadians) {

        setPosition(position);
        setRotation(heading, pitch, isRadians);
    }

    /**
     * Transforms the <code>position</code> field of this <code>Position3D</code>
     * instance by the vector values of the <code>transform</code> perameter.
     * 
     * @param positionTransform <code>Vector</code>: The <code>Vector</code>
     *                          instance to transform by.
     * @return <code>Vector</code>: The resulting transformed vector.
     */
    public final Vector transformBy(Vector positionTransform) {

        return setPosition(position.addVector(positionTransform));
    }

    /**
     * Rotates the <code>heading</code> and <code>pitch</code> fields of this
     * <code>Position3D</code> instance by the values of the <code>heading</code>
     * perameter <code>pitch</code> perameter.
     * 
     * @param headingTransform <code>double</code>: The amount to transform the
     *                         <code>heading</code> field of this
     *                         <code>Position3D</code> instance by.
     * @param pitchTransform   <code>double</code>: The amount to transform the
     *                         <code>pitch</code> field of this
     *                         <code>Position3D</code> instance by.
     * @param isRadians        <code>boolean</code>: Whether or not the
     *                         <code>heading</code> and <code>pitch</code>
     *                         perameters are specified in radians.
     * @return <code>double[2]</code>: An array containing the values of the
     *         <code>heading</code> field and the <code>pitch</code> field of this
     *         <code>Position3D</code> instance, in radians.
     */
    public final double[] rotateBy(double headingTransform, double pitchTransform, boolean isRadians) {

        if (!isRadians) {

            headingTransform = Math.toRadians(headingTransform);
            pitchTransform = Math.toRadians(pitchTransform);

        }

        return setRotation(heading + headingTransform, pitch + pitchTransform, true);
    }

    /**
     * Sets the <code>position</code> field of this <code>Position3D</code> instance
     * to the vector values of the <code>position</code> perameter.
     * 
     * @param positionValue <code>Vector</code>: The <code>Vector</code> instance to
     *                      assign to the <code>position</code> field ofthis
     *                      <code>Position3D</code> instance.
     * @return <code>Vector</code>: The resulting position vector.
     */
    public final Vector setPosition(Vector positionValue) {

        double x = Math.min(positionBounds[1].getX(), Math.max(positionBounds[0].getX(), positionValue.getX()));
        double y = Math.min(positionBounds[1].getY(), Math.max(positionBounds[0].getY(), positionValue.getY()));
        double z = Math.min(positionBounds[1].getZ(), Math.max(positionBounds[0].getZ(), positionValue.getZ()));

        position = new Vector(x, y, z);

        update();
        return position;
    }

    /**
     * Sets the <code>heading</code> field of this <code>Position3D</code> instance
     * to the value of the <code>heading</code> perameter.
     * 
     * @param headingValue <code>double</code>: The value to assign to the
     *                     <code>heading</code> field ofthis <code>Position3D</code>
     *                     instance.
     * @param newHeading   <code>boolean</code>: Whether or not the
     *                     <code>heading</code> perameter is specified in radians.
     * @return <code>double</code>: The resulting heading value, in radians.
     */
    public final double setHeading(double newHeading, boolean isRadians) {

        if (!isRadians) {

            newHeading = Math.toRadians(newHeading);

        }
        heading = Math.min(headingBounds[1], Math.max(headingBounds[0], newHeading));

        update();
        return getHeading();
    }

    /**
     * Sets the <code>pitch</code> field of this <code>Position3D</code> instance to
     * the value of the <code>pitch</code> perameter.
     * 
     * @param newPitch  <code>double</code>: The value to assign to the
     *                  <code>pitch</code> field ofthis <code>Position3D</code>
     *                  instance.
     * @param isRadians <code>boolean</code>: Whether or not the <code>pitch</code>
     *                  perameter is specified in radians.
     * @return <code>double</code>: The resulting pitch value, in radians.
     */
    public final double setPitch(double newPitch, boolean isRadians) {

        if (!isRadians) {

            newPitch = Math.toRadians(newPitch);
        }
        pitch = Math.min(pitchBounds[1], Math.max(pitchBounds[0], newPitch));

        update();
        return getPitch();
    }

    /**
     * Sets the <code>heading</code> field and the <code>pitch</code> field of this
     * <code>Position3D</code> instance to the values of the <code>heading</code>
     * perameter and the <code>pitch</code> perameter.
     * 
     * @param newHeading <code>double</code>: The value to assign to the
     *                   <code>heading</code> field ofthis <code>Position3D</code>
     *                   instance.
     * @param newPitch   <code>double</code>: The value to assign to the
     *                   <code>pitch</code> field ofthis <code>Position3D</code>
     *                   instance.
     * @param isRadians  <code>boolean</code>: Whether or not the
     *                   <code>heading</code> and <code>pitch</code> perameters are
     *                   specified in radians.
     * @return <code>double[2]</code>: An array containing the values of the
     *         <code>heading</code> field and the <code>pitch</code> field of this
     *         <code>Position3D</code> instance, in radians.
     */
    public final double[] setRotation(double newHeading, double newPitch, boolean isRadians) {

        return new double[] { setHeading(newHeading, isRadians), setPitch(newPitch, isRadians) };
    }

    /**
     * Sets the maximum and minimum bounds of the <code>position</code> field of
     * this <code>Position3D</code> instance.
     * 
     * @param min <code>Vector</code>: The minimum positional boundary of this
     *            <code>Position3D</code> instance. A null value represents no
     *            boundary being assigned.
     * @param max <code>Vector</code>: The maximum positional boundary of this
     *            <code>Position3D</code> instance. A null value represents no
     *            boundary being assigned.
     * @return <code>boolean</code>: Whether or not the boundaries were successfully
     *         applied to this <code>Position3D</code> instance.
     */
    public final boolean setPositionBounds(Vector min, Vector max) {

        if (min != null && max != null
                && (min.getX() > max.getX() || min.getY() > max.getY() || min.getZ() > max.getZ())) {

            return false;
        }

        if (min == null) {

            min = new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        }
        if (max == null) {

            max = new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        positionBounds = new Vector[] { min, max };
        setPosition(getPosition());

        return true;
    }

    /**
     * Sets the maximum and minimum bounds of the <code>heading</code> field of this
     * <code>Position3D</code> instance.
     * 
     * @param min       <code>Double</code>: The minimum heading boundary of this
     *                  <code>Position3D</code> instance. A null value represents no
     *                  boundary being assigned.
     * @param max       <code>Double</code>: The maximum heading boundary of this
     *                  <code>Position3D</code> instance. A null value represents no
     *                  boundary being assigned.
     * @param isRadians <code>boolean</code>: Whether or not the <code>pitch</code>
     *                  perameter is specified in radians.
     * @return <code>boolean</code>: Whether or not the boundaries were successfully
     *         applied to this <code>Position3D</code> instance.
     */
    public final boolean setHeadingBounds(Double min, Double max, boolean isRadians) {

        if (min != null && max != null && min > max) {

            return false;
        }

        if (min == null) {

            min = Double.NEGATIVE_INFINITY;
        }
        if (max == null) {

            max = Double.POSITIVE_INFINITY;
        }

        if (!isRadians) {

            min = Math.toRadians(min);
            max = Math.toRadians(max);
        }

        headingBounds = new Double[] { min, max };
        setHeading(getHeading(), true);

        return true;
    }

    /**
     * Sets the maximum and minimum bounds of the <code>pitch</code> field of this
     * <code>Position3D</code> instance.
     * 
     * @param min       <code>Double</code>: The minimum pitch boundary of this
     *                  <code>Position3D</code> instance. A null value represents no
     *                  boundary being assigned.
     * @param max       <code>Double</code>: The maximum pitch boundary of this
     *                  <code>Position3D</code> instance. A null value represents no
     *                  boundary being assigned.
     * @param isRadians <code>boolean</code>: Whether or not the <code>pitch</code>
     *                  perameter is specified in radians.
     * @return <code>boolean</code>: Whether or not the boundaries were successfully
     *         applied to this <code>Position3D</code> instance.
     */
    public final boolean setPitchBounds(Double min, Double max, boolean isRadians) {

        if (min != null && max != null && min > max) {

            return false;
        }

        if (min == null) {

            min = Double.NEGATIVE_INFINITY;
        }
        if (max == null) {

            max = Double.POSITIVE_INFINITY;
        }

        if (!isRadians) {

            min = Math.toRadians(min);
            max = Math.toRadians(max);
        }

        pitchBounds = new Double[] { min, max };
        setPitch(getPitch(), true);

        return true;
    }

    /**
     * Sets the maximum and minimum bounds of the <code>heading</code> field and the
     * <code>pitch</code> field of this <code>Position3D</code> instance.
     * 
     * @param minHeading <code>Double</code>: The minimum heading boundary of this
     *                   <code>Position3D</code> instance. A null value represents
     *                   no boundary being assigned.
     * @param maxHeading <code>Double</code>: The maximum heading boundary of this
     *                   <code>Position3D</code> instance. A null value represents
     *                   no boundary being assigned.
     * @param minPitch   <code>Double</code>: The minimum pitch boundary of this
     *                   <code>Position3D</code> instance. A null value represents
     *                   no boundary being assigned.
     * @param maxPitch   <code>Double</code>: The maximum pitch boundary of this
     *                   <code>Position3D</code> instance. A null value represents
     *                   no boundary being assigned.
     * @param isRadians  <code>boolean</code>: Whether or not the
     *                   <code>heading</code> and <code>pitch</code> perameters are
     *                   specified in radians.
     * @return <code>boolean</code>: Whether or not the boundaries were successfully
     *         applied to this <code>Position3D</code> instance.
     */
    public final boolean setRotationalBounds(Double minHeading, Double maxHeading, Double minPitch, Double maxPitch,
            boolean isRadians) {

        return setHeadingBounds(minHeading, maxHeading, isRadians) && setHeadingBounds(minPitch, maxPitch, isRadians);

    }

    /**
     * Updates the <code>renderTransform</code> field after any change to the
     * position or orientation of this <code>Position3D</code> instance.
     */
    protected void update() {

        // Creates the transform to rotate about the Y axis.
        Matrix headingTransform = Matrix.getRotationalMatrix3X3(getHeading(), Matrix.Y_AXIS);

        // Creates the transform to rotate about the X axis.
        // Because the heading transform already accounted for rotation about the y
        // axis, all vectors will now be adjusted to the x axis as if the x axis was
        // orthagonal to the orientation.
        // Thus, the only necessary transformation is rotation about the X axis to
        // account for pitch.
        Matrix pitchTransform = Matrix.getRotationalMatrix3X3(-getPitch(), Matrix.X_AXIS);

        // Combines the 'headingTransform' and 'pitchTransform' matrices into a single
        // axis-aligning matrix.
        Matrix rotationalTransform = headingTransform.multiplyMatrix(pitchTransform);

        renderTransform = new MatrixOperations()
                .addOperation(MatrixOperations.Operations.MULTIPLY_MATRIX, Render3D.DISPLAY_TRANSFORM)
                .addOperation(MatrixOperations.Operations.SUBTRACT_MATRIX, getPosition())
                .addOperation(MatrixOperations.Operations.MULTIPLY_MATRIX, rotationalTransform);
    }
}
