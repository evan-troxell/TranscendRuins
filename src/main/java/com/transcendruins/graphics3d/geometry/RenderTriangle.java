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

/**
 * <code>RenderTriangle</code>: A class representing a triangle in a 3D space
 * with each vertice represented by a <code>Vector</code> instance.
 */
public final class RenderTriangle extends Triangle {

    /**
     * <code>double</code>: The vector values of the normal to the plane formed by
     * this <code>RenderTriangle</code>.
     */
    private final double normalX, normalY, normalZ;

    /**
     * <code>double</code>: The area of this <code>RenderTriangle</code> instance.
     */
    private final double area;

    /**
     * <code>double</code>: The plane shift used to represent the plane formed by
     * this <code>RenderTriangle</code> instance.
     */
    private final double planeShift;

    /**
     * <code>double</code>: The cosine value of the angle between the normal of the
     * plane formed by the vertices of this <code>RenderTriangle</code> and the
     * viewing
     * vector.
     */
    private final double viewCosine;

    /**
     * Retrieves the cosine of the angle between the plane normal of this
     * <code>RenderTriangle</code> instance and the viewing plane normal.
     * 
     * @return <code>double</code>: The <code>viewCosine</code> field of this
     *         <code>RenderTriangle</code> instance.
     */
    public double getViewCosine() {

        return viewCosine;
    }

    /**
     * <code>Triangle</code>: The 3 UV vectors of this <code>RenderTriangle</code>
     * instance composed into a triangle.
     */
    private final Triangle uvs;

    /**
     * Retrieves the 3 UV vectors of this <code>RenderTriangle</code> instance.
     * 
     * @return <code>Triangle</code>: The <code>uvs</code> field of this
     *         <code>RenderTriangle</code> instance.
     */
    public Triangle getUvs() {

        return uvs;
    }

    /**
     * Creates a new instance of the <code>RenderTriangle</code> class.
     * 
     * @param vertex1 <code>Vector</code>: The first vertice of this
     *                <code>RenderTriangle</code> instance.
     * @param vertex2 <code>Vector</code>: The second vertice of this
     *                <code>RenderTriangle</code> instance.
     * @param vertex3 <code>Vector</code>: The third vertice of this
     *                <code>RenderTriangle</code> instance.
     */
    public RenderTriangle(Triangle triangle, Triangle uvs) {

        super(triangle);
        this.uvs = uvs;
        this.area = calculateArea(getVertex1(), getVertex2(), getVertex3());

        // Find the two vectors used to construct the plane.
        Vector a = getVertex2().subtract(getVertex1());
        Vector b = getVertex3().subtract(getVertex1());

        // Find the vector orthogonal to the plane by using the cross product.
        Vector normal = a.cross3D(b);

        normalX = normal.getX();
        normalY = normal.getY();
        normalZ = normal.getZ();
        planeShift = -getVertex1().dot(normal);

        Vector n = new Vector(0, 0, 1);
        viewCosine = n.dot(normal) / normal.magnitude();
    }

    /**
     * Calculates whether or not this <code>RenderTriangle</code> instance is being
     * viewed from the backside.
     * 
     * @return <code>boolean</code>: Whether or not this <code>RenderTriangle</code>
     *         instance is being viewed from the backside.
     */
    public boolean facingBackside() {

        // If the depth of the cross product of vector <v2, v1> and vector <v3, v1> is
        // greater than 0, the origin point is facing the back of the triangle.
        return normalZ > 0;
    }

    /**
     * Calculates whether or not this <code>RenderTriangle</code> instance is in
     * frame.
     * 
     * @param width  <code>int</code>: The width of the frame.
     * @param height <code>int</code>: The height of the frame.
     * @return <code>boolean</code>: Whether or not this <code>RenderTriangle</code>
     *         instance is in frame.
     */
    public boolean inFrame(int width, int height) {

        return (getMaxX() >= 0 && getMaxY() >= 0 && getMinX() < width && getMinY() < height);
    }

    /**
     * Retrieves the depth of this <code>RenderTriangle</code> instance in 3D space
     * at a
     * specific point.
     * 
     * @param x <code>int</code>: The X coordinate of the point to retrieve.
     * @param y <code>int</code>: The Y coordinate of the point to retrieve.
     * @return <code>double</code>: The depth of this <code>RenderTriangle</code>
     *         instance at the point <code>point</code>.
     */
    public double depthAtPoint(int x, int y) {

        // The formula of a plane is nx(x - px) + ny(y - py) + nz(z - pz) = 0, where
        // (px, py, pz) is a point on the plane and the vector <nx, ny, nz> is normal
        // (orthoganol) to the plane. Adjust the formula to isolate Z.
        return -(x * normalX + y * normalY + planeShift) / normalZ;
    }

    /**
     * Retrieves the Y bounds of this <code>RenderTriangle</code> instance at input
     * X.
     * 
     * @param x <code>int</code>: The X coordinate whose Y bounds should be found.
     * @return <code>int[2]</code>: The Y bounds of this <code>RenderTriangle</code>
     *         instance at input X.
     */
    public int[] findYBoundsAtX(int x) {

        Vector v1 = getVertex1();
        Vector v2 = getVertex2();
        Vector v3 = getVertex3();

        // Find the Y value on the line v1v2 at point X.
        Double boundV1V2 = getYAtX(x, v1.getX(), v1.getY(), v2.getX(), v2.getY());

        // Find the Y value on the line v2v3 at point X.
        Double boundV2V3 = getYAtX(x, v2.getX(), v2.getY(), v3.getX(), v3.getY());

        // Find the Y value on the line v3v1 at point X.
        Double boundV3V1 = getYAtX(x, v3.getX(), v3.getY(), v1.getX(), v1.getY());

        // Find the minimum and maximum Y values of the triangle at point X.
        int[] newValues = { min(boundV1V2, min(boundV2V3, boundV3V1)).intValue(),
                (int) max(boundV1V2, max(boundV2V3, boundV3V1)).intValue() };

        return newValues;
    }

    /**
     * Retrieves the X bounds of this <code>RenderTriangle</code> instance at input
     * Y.
     * 
     * @param y <code>int</code>: The Y coordinate whose X bounds should be found.
     * @return <code>int[2]</code>: The X bounds of this <code>RenderTriangle</code>
     *         instance at input Y.
     */
    public int[] findXBoundsAtY(int y) {

        Vector v1 = getVertex1();
        Vector v2 = getVertex2();
        Vector v3 = getVertex3();

        // Find the X value on the line v1v2 at point Y.
        Double boundV1V2 = getYAtX(y, v1.getY(), v1.getX(), v2.getY(), v2.getX());

        // Find the X value on the line v2v3 at point Y.
        Double boundV2V3 = getYAtX(y, v2.getY(), v2.getX(), v3.getY(), v3.getX());

        // Find the X value on the line v3v1 at point Y.
        Double boundV3V1 = getYAtX(y, v3.getY(), v3.getX(), v1.getY(), v1.getX());

        // Find the minimum and maximum X values of the triangle at point Y.
        int[] newValues = { min(boundV1V2, min(boundV2V3, boundV3V1)).intValue(),
                (int) max(boundV1V2, max(boundV2V3, boundV3V1)).intValue() };

        return newValues;
    }

    /**
     * Retrieves the Y coordinate of input X on the line between points (x1, y1) and
     * (x2, y2).
     * 
     * @param x  <code>double</code>: The X coordinate whose pair should be found.
     * @param x1 <code>double</code>: The X coordinate of the first point on the
     *           line.
     * @param y1 <code>double</code>: The Y coordinate of the first point on the
     *           line.
     * @param x2 <code>double</code>: The X coordinate of the first point on the
     *           line.
     * @param y2 <code>double</code>: The Y coordinate of the first point on the
     *           line.
     * @return <code>Double</code>: The Y coordinate of input X on the line.
     */
    private static Double getYAtX(double x, double x1, double y1, double x2, double y2) {

        if (x < x1 && x < x2 || x > x1 && x > x2) {

            return null;
        }

        // If the slope is 0.0 or the slope is vertical, return the Y coordinate of the
        // first point.
        if (x1 == x2 || y1 == y2) {

            return y1;
        }

        // Calculate the slope of the line.
        double m = (y2 - y1) / (x2 - x1);

        // Calculate the Y value corresponding to the given X.
        return (m * (x - x1)) + y1;
    }

    /**
     * Returns the smaller of two <code>Double</code> values, with the possibility
     * of a <code>null</code> value in either value.
     * 
     * @param val1 <code>Double</code>: The first value to check.
     * @param val2 <code>Double</code>: The second value to check.
     * @return <code>Double</code>: The lowest value between <code>val1</code> and
     *         <code>val2</code>, or <code>null</code> if neither perameter has a
     *         value.
     */
    private static Double min(Double val1, Double val2) {

        if (val1 == null) {

            return val2;
        }

        if (val2 == null) {

            return val1;
        }

        return (val1 < val2) ? val1 : val2;
    }

    /**
     * Returns the larger of two <code>Double</code> values, with the possibility
     * of a <code>null</code> value in either value.
     * 
     * @param val1 <code>Double</code>: The first value to check.
     * @param val2 <code>Double</code>: The second value to check.
     * @return <code>Double</code>: The highest value between <code>val1</code> and
     *         <code>val2</code>, or <code>null</code> if neither perameter has a
     *         value.
     */
    private static Double max(Double val1, Double val2) {

        if (val1 == null) {

            return val2;
        }

        if (val2 == null) {

            return val1;
        }

        return (val1 > val2) ? val1 : val2;
    }

    /**
     * Retrieves the UV coordinate of a point on this <code>RenderTriangle</code>
     * instance.
     * 
     * @param x <code>double</code>: The X coordinate of the point.
     * @param y <code>double</code>: The Y coordinate of the point.
     * @return <code>double[]</code>: The resulting UV coordinates, based off of the
     *         UV triangle of this <code>RenderTriangle</code> instance.
     */
    public double[] getUvCoordinates(double x, double y) {

        Vector p = new Vector(x, y);
        Vector v1 = getVertex1();
        Vector v2 = getVertex2();
        Vector v3 = getVertex3();

        double w1 = calculateArea(p, v2, v3) / area;
        double w2 = calculateArea(v1, p, v3) / area;
        double w3 = calculateArea(v1, v2, p) / area;

        Vector uv1 = uvs.getVertex1();
        Vector uv2 = uvs.getVertex2();
        Vector uv3 = uvs.getVertex3();

        double u = w1 * uv1.getX() + w2 * uv2.getX() + w3 * uv3.getX();
        double v = w1 * uv1.getY() + w2 * uv2.getY() + w3 * uv3.getY();

        return new double[] { u, v };
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
    private static double calculateArea(Vector a, Vector b, Vector c) {

        return Math.abs((a.getX() * (b.getY() - c.getY()) +
                b.getX() * (c.getY() - a.getY()) +
                c.getX() * (a.getY() - b.getY())) / 2.0);
    }
}
