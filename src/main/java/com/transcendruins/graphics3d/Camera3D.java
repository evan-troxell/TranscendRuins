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

package com.transcendruins.graphics3d;

import com.transcendruins.graphics3d.geometry.Vector;

/**
 * <code>Camera3D</code>: A class representing the position, orientation, and
 * zoom of a camera in space.
 */
public final class Camera3D extends Position3D {

    /**
     * <code>double</code>: The zoom factor of this <code>Camera3D</code> instance.
     * The output zoom is equal to <code>Math.exp(zoom)</code>.
     */
    private double zoom = 0;

    /**
     * Retrieves the zoom of this <code>Camera3D</code> instance.
     * 
     * @return <code>double</code>: The output zoom of this <code>Camera3D</code>
     *         instance.
     */
    public double getZoom() {

        return Math.exp(zoom);
    }

    /**
     * <code>Double[2]</code>: The zoom bounds representing the maximum and minimum
     * zoom value allowed in the <code>zoom</code> field of this
     * <code>Camera3D</code> instance.
     */
    private Double[] zoomBounds = { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };

    /**
     * Creates a new instance of the <code>Camera3D</code> class whose position is
     * centered around the origin.
     */
    public Camera3D() {

        this(new Vector(0, 0, 0), 0, 0, true, 0);
    }

    /**
     * Creates a new instance of the <code>Camera3D</code> class.
     * 
     * @param position  <code>Vector</code>: The position to render from.
     * @param heading   <code>double</code>: The heading to render from.
     * @param pitch     <code>double</code>: The pitch to render from.
     * @param isRadians <code>boolean</code>: Whether or not the
     *                  <code>heading</code> and <code>pitch</code> perameters are
     *                  specified in radians.
     * @param zoom      <code>double</code>: The zoom to render from.
     */
    public Camera3D(Vector position, double heading, double pitch, boolean isRadians, double zoom) {

        super(position, heading, pitch, isRadians);
        setZoom(zoom);
    }

    /**
     * Increases the <code>zoom</code> factor of this <code>Camera3D</code> instance
     * by the <code>zoomFactor</code> perameter. This method essentially multiplies
     * the output zoom by <code>exp(zoomFactor)</code>, in which a
     * <code>zoomFactor</code> of <code>0</code> has no effect, a positive
     * <code>zoomFactor</code> exponentially increases the output zoom, and a
     * negative <code>zoomFactor</code> exponentially decreases the output zoom.
     * 
     * @param zoomTransform <code>double</code>: The zoom factor to modify the
     *                      output zoom of this <code>Camera3D</code> instance by.
     * @return <code>double</code>: The output zoom of this <code>Camera3D</code>
     *         instance.
     */
    public double zoomBy(double zoomTransform) {

        return setZoom(zoom + zoomTransform);
    }

    /**
     * Sets the <code>zoom</code> field of this <code>Camera3D</code> instance to
     * the value of the <code>zoom</code> perameter.
     * 
     * @param zoomValue <code>double</code>: The value to assign to the
     *                  <code>zoom</code> field of this <code>Camera3D</code>
     *                  instance.
     * @return <code>double</code>: The resulting output zoom value.
     */
    public double setZoom(double zoomValue) {

        zoom = Math.min(zoomBounds[1], Math.max(zoomBounds[0], zoomValue));

        return getZoom();
    }

    /**
     * Sets the maximum and minimum bounds of the <code>zoom</code> field of this
     * <code>Camera3D</code> instance.
     * 
     * @param min <code>Double</code>: The minimum zoom boundary of this
     *            <code>Camera3D</code> instance. A null value represents no
     *            boundary being assigned.
     * @param max <code>Double</code>: The maximum zoom boundary of this
     *            <code>Camera3D</code> instance. A null value represents no
     *            boundary being assigned.
     * @return <code>boolean</code>: Whether or not the boundaries were successfully
     *         applied to this <code>Camera3D</code> instance.
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

        zoomBounds = new Double[] { min, max };
        setZoom(getZoom());

        return true;
    }
}
