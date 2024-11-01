package com.transcendruins.rendering;

import com.transcendruins.geometry.MatrixOperations;
import com.transcendruins.geometry.Position3D;
import com.transcendruins.geometry.Vector;

/**
 * <code>Camera3D</code>: A class representing the position, orientation, and zoom of a camera in space.
 */
public final class Camera3D extends Position3D {

    /**
     * <code>double</code>: The zoom factor of this <code>Camera3D</code> instance. The output zoom is equal to <code>Math.exp(zoom)</code>.
     */
    private double zoom = 0;

    /**
     * <code>Double[2]</code>: The zoom bounds representing the maximum and minimum zoom value allowed in the <code>zoom</code> property of this <code>Camera3D</code> instance.
     */
    private Double[] zoomBounds = new Double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};

    /**
     * <code>int</code>: The width of the frame to center on.
     */
    private int frameWidth = 0;

    /**
     * <code>int</code>: The height of the frame to center on.
     */
    private int frameHeight = 0;

    /**
    * Creates a new instance of the <code>Camera3D</code> class whose position is centered around the origin.
    */
    public Camera3D() {

        this(new Vector(0, 0, 0), 0, 0, true, 0);
    }

    /**
     * Creates a new instance of the <code>Camera3D</code> class.
     * @param position <code>Vector</code>: The position to render from.
     * @param heading <code>double</code>: The heading to render from.
     * @param pitch <code>double</code>: The pitch to render from.
     * @param isRadians <code>boolean</code>: Whether or not the <code>heading</code> and <code>pitch</code> perameters are specified in radians.
     * @param zoom <code>double</code>: The zoom to render from.
     */
    public Camera3D(Vector position, double heading, double pitch, boolean isRadians, double zoom) {

        super(position, heading, pitch, isRadians);
        setZoom(zoom);
    }

    /**
     * Increases the <code>zoom</code> factor of this <code>Camera3D</code> instance by the <code>zoomFactor</code> perameter. This method essentially multiplies the output zoom by <code>exp(zoomFactor)</code>, in which a <code>zoomFactor</code> of <code>0</code> has no effect, a positive <code>zoomFactor</code> exponentially increases the output zoom, and a negative <code>zoomFactor</code> exponentially decreases the output zoom.
     * @param zoomTransform <code>double</code>: The zoom factor to modify the output zoom of this <code>Camera3D</code> instance by.
     * @return <code>double</code>: The output zoom of this <code>Camera3D</code> instance.
     */
    public double zoomBy(double zoomTransform) {

        return setZoom(zoom + zoomTransform);
    }

    /**
     * Sets the frame dimensions of this <code>Camera3D</code> instance.
     * @param newFrameWidth <code>int</code>: The width of the frame.
     * @param newFrameHeight <code>int</code>: The height of the frame.
     * @return <code>int[2]</code>: The dimensions of the frame, in the form <code>[width, height]</code>.
     */
    public int[] setDimensions(int newFrameWidth, int newFrameHeight) {

        this.frameWidth = newFrameWidth;
        this.frameHeight = newFrameHeight;

        update();
        return new int[] {frameWidth, frameHeight};
    }

    /**
     * Sets the <code>zoom</code> property of this <code>Camera3D</code> instance to the value of the <code>zoom</code> perameter.
     * @param zoomValue <code>double</code>: The value to assign to the <code>zoom</code> property of this <code>Camera3D</code> instance.
     * @return <code>double</code>: The resulting output zoom value.
     */
    public double setZoom(double zoomValue) {

        zoom = Math.min(zoomBounds[1], Math.max(zoomBounds[0], zoomValue));

        update();
        return getZoom();
    }

    /**
     * Sets the maximum and minimum bounds of the <code>zoom</code> property of this <code>Camera3D</code> instance.
     * @param min <code>Double</code>: The minimum zoom boundary of this <code>Camera3D</code> instance. A null value represents no boundary being assigned.
     * @param max <code>Double</code>: The maximum zoom boundary of this <code>Camera3D</code> instance. A null value represents no boundary being assigned.
     * @return <code>boolean</code>: Whether or not the boundaries were successfully applied to this <code>Camera3D</code> instance.
     */
    public boolean setZoomBounds(Double min, Double max) {

        if (min != null && max != null && min > max) {

            return false;
        }

        if (min == null) {

            min = Double.NEGATIVE_INFINITY;
        }
        if (max == null) {

            max = Double.POSITIVE_INFINITY;
        }

        zoomBounds = new Double[] {min, max};
        setZoom(getZoom());

        return true;
    }

    /**
     * Retrieves the zoom of this <code>Camera3D</code> instance.
     * @return <code>double</code>: The output zoom of this <code>Camera3D</code> instance.
     */
    public double getZoom() {

        return Math.exp(zoom);
    }

    /**
     * Updates the <code>renderTransform</code> property after any change to the position, orientation, or zoom of this <code>Camera3D</code> instance.
     */
    @Override
    protected void update() {

        Vector frameCenter = new Vector(frameWidth / 2, frameHeight / 2, 0);

        super.update();
        getRenderTransform().addOperation(MatrixOperations.Operations.MULTIPLY_SCALAR, getZoom())
                        .addOperation(MatrixOperations.Operations.ADD_MATRIX, frameCenter);
    }
}
