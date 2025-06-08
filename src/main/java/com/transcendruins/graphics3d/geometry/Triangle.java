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

package com.transcendruins.graphics3d.geometry;

sealed public class Triangle permits RenderTriangle {

    private final Vector vertex1;

    public final Vector getVertex1() {

        return vertex1;
    }

    private final Vector vertex2;

    public final Vector getVertex2() {

        return vertex2;
    }

    private final Vector vertex3;

    public final Vector getVertex3() {

        return vertex3;
    }

    public Triangle(Vector vertex1, Vector vertex2, Vector vertex3) {

        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.vertex3 = vertex3;
    }

    public Triangle(Triangle vertices) {

        this.vertex1 = vertices.getVertex1();
        this.vertex2 = vertices.getVertex2();
        this.vertex3 = vertices.getVertex3();
    }

    /**
     * Retrieves the minimum X value of this <code>RenderTriangle</code> instance.
     * 
     * @return <code>double</code>: The lowest X coordinate of the vertices of this
     *         <code>RenderTriangle</code> instance.
     */
    public final double getMinX() {

        return Math.min(vertex1.getX(), Math.min(vertex2.getX(), vertex3.getX()));
    }

    /**
     * Retrieves the minimum Y value of this <code>RenderTriangle</code> instance.
     * 
     * @return <code>double</code>: The lowest Y coordinate of the vertices of this
     *         <code>RenderTriangle</code> instance.
     */
    public final double getMinY() {

        return Math.min(vertex1.getY(), Math.min(vertex2.getY(), vertex3.getY()));
    }

    /**
     * Retrieves the maximum X value of this <code>RenderTriangle</code> instance.
     * 
     * @return <code>double</code>: The highest X coordinate of the vertices of this
     *         <code>RenderTriangle</code> instance.
     */
    public final double getMaxX() {

        return Math.max(vertex1.getX(), Math.max(vertex2.getX(), vertex3.getX()));
    }

    /**
     * Retrieves the maximum Y value of this <code>RenderTriangle</code> instance.
     * 
     * @return <code>double</code>: The highest Y coordinate of the vertices of this
     *         <code>RenderTriangle</code> instance.
     */
    public final double getMaxY() {

        return Math.max(vertex1.getY(), Math.max(vertex2.getY(), vertex3.getY()));
    }

    public final Vector getCenter() {

        return Vector.getAverage(vertex1, vertex2, vertex3);
    }

    public final Triangle add(Vector vector) {

        return new Triangle(vertex1.add(vector), vertex2.add(vector), vertex3.add(vector));
    }

    public final Triangle subtract(Vector vector) {

        return new Triangle(vertex1.subtract(vector), vertex2.subtract(vector), vertex3.subtract(vector));
    }

    public final Triangle multiply(double scalar) {

        return new Triangle(vertex1.multiply(scalar), vertex2.multiply(scalar), vertex3.multiply(scalar));
    }

    public final Triangle multiply(Matrix matrix) {

        return new Triangle(vertex1.multiply(matrix), vertex2.multiply(matrix), vertex3.multiply(matrix));
    }

    public final Triangle rotate(Quaternion quat) {

        return new Triangle(vertex1.rotate(quat), vertex2.rotate(quat), vertex3.rotate(quat));
    }

    /**
     * Calculates the area of a triangle formed by 3 vectors.
     * 
     * @param a <code>Vector</code>: The vector forming the first vertex of the
     *          triangle.
     * @param b <code>Vector</code>: The vector forming the second vertex of the
     *          triangle.
     * @param c <code>Vector</code>: The vector forming the third vertex of the
     *          triangle.
     * @return <code>double</code>: The resulting area.
     */
    public static double calculateArea(Vector a, Vector b, Vector c) {

        return 0.5 * Math.abs((a.getX() * (b.getY() - c.getY()) + b.getX() * (c.getY() - a.getY())
                + c.getX() * (a.getY() - b.getY())));
    }

    @Override
    public final String toString() {

        return "[" + vertex1 + ", " + vertex2 + ", " + vertex3 + "]";
    }
}
