package com.transcendruins.graphics3d.geometry;

import java.awt.Color;

import com.transcendruins.world.assetinstances.rendermaterials.RenderMaterialInstance;

/**
 * <code>Triangle3D</code>: A class representing a triangle in a 3D space with
 * each vertice represented by a <code>Vector</code> instance.
 */
public final class Triangle3D {

    /**
     * <code>Vector</code>: The first vertex of this <code>Triangle3D</code>
     * instance.
     */
    private final Vector vertex1;

    /**
     * <code>Vector</code>: The second vertex of this <code>Triangle3D</code>
     * instance.
     */
    private final Vector vertex2;

    /**
     * <code>Vector</code>: The third vertex of this <code>Triangle3D</code>
     * instance.
     */
    private final Vector vertex3;

    /**
     * <code>double</code>: The vector values of the normal to the plane formed by
     * this <code>Triangle3D</code>.
     */
    private final double normalX, normalY, normalZ;

    /**
     * <code>double</code>: The plane shift used to represent the plane formed by
     * this <code>Triangle3D</code> instance.
     */
    private final double planeShift;

    /**
     * <code>double</code>: The cosine value of the angle between the normal of the
     * plane formed by the vertices of this <code>Triangle3D</code> and the viewing
     * vector.
     */
    private final double viewCosine;

    /**
     * Retrieves the cosine of the angle between the plane normal of this
     * <code>Triangle3D</code> instance and the viewing plane normal.
     * 
     * @return <code>double</code>: The <code>viewCosine</code> field of this
     *         <code>Triangle3D</code> instance.
     */
    public double getViewCosine() {

        return viewCosine;
    }

    /**
     * <code>Vector</code>: The center of this <code>Triangle3D</code> instance.
     */
    private final Vector center;

    /**
     * Retrieves the center of this <code>Triangle3D</code> instance.
     * 
     * @return <code>Vector</code>: The <code>center</code> field of this
     *         <code>Triangle3D</code> instance.
     */
    public Vector getCenter() {

        return center;
    }

    /**
     * <code>int</code>: The minimum X bounds of this <code>Triangle3D</code>
     * instance.
     */
    private final int minX;

    /**
     * Retrieves the minimum X value of this <code>Triangle3D</code> instance.
     * 
     * @return <code>int</code>: The <code>minX</code> field of this
     *         <code>Triangle3D</code> instance.
     */
    public int getMinX() {

        return minX;
    }

    /**
     * <code>int</code>: The maximum X bounds of this <code>Triangle3D</code>
     * instance.
     */
    private final int maxX;

    /**
     * Retrieves the maximum X value of this <code>Triangle3D</code> instance.
     * 
     * @return <code>int</code>: The <code>maxX</code> field of this
     *         <code>Triangle3D</code> instance.
     */
    public int getMaxX() {

        return maxX;
    }

    /**
     * <code>int</code>: The minimum Y bounds of this <code>Triangle3D</code>
     * instance.
     */
    private final int minY;

    /**
     * Retrieves the minimum Y value of this <code>Triangle3D</code> instance.
     * 
     * @return <code>int</code>: The <code>minY</code> field of this
     *         <code>Triangle3D</code> instance.
     */
    public int getMinY() {

        return minY;
    }

    /**
     * <code>int</code>: The maximum Y bounds of this <code>Triangle3D</code>
     * instance.
     */
    private final int maxY;

    /**
     * Retrieves the maximum Y value of this <code>Triangle3D</code> instance.
     * 
     * @return <code>int</code>: The <code>maxY</code> field of this
     *         <code>Triangle3D</code> instance.
     */
    public int getMaxY() {

        return maxY;
    }

    /**
     * <code>Color</code>: The color assigned to this <code>Triangle3D</code>
     * instance.
     */
    private final Color color;

    /**
     * Retrieves the color of this <code>Triangle3D</code> instance.
     * 
     * @return <code>Color</code>: The <code>color</code> field of this
     *         <code>Triangle3D</code> instance.
     */
    public Color getColor() {

        return color;
    }

    /**
     * <code>RenderMaterialInstance</code>: The render material used to render this
     * <code>Triangle3D</code> instance.
     */
    private RenderMaterialInstance renderMaterial;

    /**
     * Retrieves the render material used to render this <code>Triangle3D</code>
     * instance.
     * 
     * @return <code>RenderMaterialInstance</code>: The <code>renderMaterial</code>
     *         field of this <code>Triangle3D</code> instance.
     */
    public RenderMaterialInstance getRenderMaterial() {

        return renderMaterial;
    }

    /**
     * Creates a new instance of the <code>Triangle3D</code> class.
     * 
     * @param vertex1 <code>Vector</code>: The first vertice of this
     *                <code>Triangle3D</code> instance.
     * @param vertex2 <code>Vector</code>: The second vertice of this
     *                <code>Triangle3D</code> instance.
     * @param vertex3 <code>Vector</code>: The third vertice of this
     *                <code>Triangle3D</code> instance.
     * @param color   <code>Color</code>: The color to assign to this
     *                <code>Triangle3D</code> instance.
     */
    public Triangle3D(Vector vertex1, Vector vertex2, Vector vertex3, Color color) {

        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.vertex3 = vertex3;
        this.color = color;

        minX = (int) Math.min(vertex1.getX(), Math.min(vertex2.getX(), vertex3.getX()));
        maxX = (int) Math.max(vertex1.getX(), Math.max(vertex2.getX(), vertex3.getX()));

        minY = (int) Math.min(vertex1.getY(), Math.min(vertex2.getY(), vertex3.getY()));
        maxY = (int) Math.max(vertex1.getY(), Math.max(vertex2.getY(), vertex3.getY()));

        // Find the two vectors used to construct the plane.
        Vector a = vertex2.subtractVector(vertex1);
        Vector b = vertex3.subtractVector(vertex1);

        // Find the vector orthogonal to the plane by using the cross product.
        Vector normal = a.cross3D(b);

        normalX = normal.getX();
        normalY = normal.getY();
        normalZ = normal.getZ();
        planeShift = -vertex1.dot(normal);

        Vector n = new Vector(0, 0, 20);
        viewCosine = n.dot(normal) / (normal.magnitude() * n.magnitude());

        center = vertex1.addVector(vertex2).addVector(vertex3).multiplyScalar(1.0 / 3.0);
    }

    /**
     * Creates a new instance of the <code>Triangle3D</code> class by performing a
     * set of matrix operations on the existing vertices in this
     * <code>Triangle3D</code> instance.
     * 
     * @param operations <code>MatrixOperations</code>: The matrix operations to
     *                   perform on the vertices of this <code>Triangle3D</code>
     *                   instance.
     * @return <code>Triangle3D</code> The generated <code>Triangle3D</code>
     *         instance.
     */
    public Triangle3D getAdjustedInstance(MatrixOperations operations) {

        Vector vertex1Adjusted = (Vector) operations.runOperations(vertex1);
        Vector vertex2Adjusted = (Vector) operations.runOperations(vertex2);
        Vector vertex3Adjusted = (Vector) operations.runOperations(vertex3);

        return new Triangle3D(vertex1Adjusted, vertex2Adjusted, vertex3Adjusted, color);
    }

    /**
     * Calculates whether or not this <code>Triangle3D</code> instance is being
     * viewed from the backside.
     * 
     * @return <code>boolean</code>: Whether or not this <code>Triangle3D</code>
     *         instance is being viewed from the backside.
     */
    public boolean facingBackside() {

        // If the depth of the cross product of vector <v2, v1> and vector <v3, v1> is
        // greater than 0, the origin point is facing the back of the triangle.
        return normalZ > 0;
    }

    /**
     * Calculates whether or not this <code>Triangle3D</code> instance is in frame.
     * 
     * @param width  <code>int</code>: The width of the frame.
     * @param height <code>int</code>: The height of the frame.
     * @return <code>boolean</code>: Whether or not this <code>Triangle3D</code>
     *         instance is in frame.
     */
    public boolean inFrame(int width, int height) {

        return (maxX >= 0 && maxY >= 0 && minX < width && minY < height);
    }

    /**
     * Retrieves the depth of this <code>Triangle3D</code> instance in 3D space at a
     * specific point.
     * 
     * @param x <code>int</code>: The X coordinate of the point to retrieve.
     * @param y <code>int</code>: The Y coordinate of the point to retrieve.
     * @return <code>double</code>: The depth of this <code>Triangle3D</code>
     *         instance at the point <code>point</code>.
     */
    public double depthAtPoint(int x, int y) {

        // The formula of a plane is nx(x - px) + ny(y - py) + nz(z - pz) = 0, where
        // (px, py, pz) is a point on the plane and the vector <nx, ny, nz> is normal
        // (orthoganol) to the plane.
        // Adjust the formula to isolate Z.
        return -(x * normalX + y * normalY + planeShift) / normalZ;
    }

    /**
     * Retrieves the Y bounds of this <code>Triangle3D</code> instance at input X.
     * 
     * @param x <code>int</code>: The X coordinate whose Y bounds should be found.
     * @return <code>int[2]</code>: The Y bounds of this <code>Triangle3D</code>
     *         instance at input X.
     */
    public int[] findYBoundsAtX(int x) {

        // Find the Y value on the line v1v2 at point X.
        Integer boundV1V2 = getYAtX(x, (int) vertex1.getX(), (int) vertex1.getY(), (int) vertex2.getX(),
                (int) vertex2.getY());

        // Find the Y value on the line v2v3 at point X.
        Integer boundV2V3 = getYAtX(x, (int) vertex2.getX(), (int) vertex2.getY(), (int) vertex3.getX(),
                (int) vertex3.getY());

        // Find the Y value on the line v3v1 at point X.
        Integer boundV3V1 = getYAtX(x, (int) vertex3.getX(), (int) vertex3.getY(), (int) vertex1.getX(),
                (int) vertex1.getY());

        // Find the minimum and maximum Y values of the triangle at point X.
        int[] newValues = { min(boundV1V2, min(boundV2V3, boundV3V1)),
                max(boundV1V2, max(boundV2V3, boundV3V1)) };

        return newValues;
    }

    /**
     * Retrieves the X bounds of this <code>Triangle3D</code> instance at input Y.
     * 
     * @param y <code>int</code>: The Y coordinate whose X bounds should be found.
     * @return <code>int[2]</code>: The X bounds of this <code>Triangle3D</code>
     *         instance at input Y.
     */
    public int[] findXBoundsAtY(int y) {

        // Find the X value on the line v1v2 at point Y.
        Integer boundV1V2 = getYAtX(y, (int) vertex1.getY(), (int) vertex1.getX(), (int) vertex2.getY(),
                (int) vertex2.getX());

        // Find the X value on the line v2v3 at point Y.
        Integer boundV2V3 = getYAtX(y, (int) vertex2.getY(), (int) vertex2.getX(), (int) vertex3.getY(),
                (int) vertex3.getX());

        // Find the X value on the line v3v1 at point Y.
        Integer boundV3V1 = getYAtX(y, (int) vertex3.getY(), (int) vertex3.getX(), (int) vertex1.getY(),
                (int) vertex1.getX());

        // Find the minimum and maximum X values of the triangle at point Y.
        int[] newValues = new int[] { min(boundV1V2, min(boundV2V3, boundV3V1)),
                max(boundV1V2, max(boundV2V3, boundV3V1)) };

        return newValues;
    }

    /**
     * Sets the <code>renderMaterial</code> field of this <code>Triangle3D</code>
     * instance.
     * 
     * @param renderMaterial <code>RenderMaterialInstance</code>: The render
     *                       material used to render this <code>Triangle3D</code>
     *                       instance.
     */
    public void setRenderMaterial(RenderMaterialInstance renderMaterial) {

        this.renderMaterial = renderMaterial;
    }

    /**
     * Retrieves the Y coordinate of input X on the line between points (x1, y1) and
     * (x2, y2).
     * 
     * @param x  <code>int</code>: The X coordinate whose pair should be found.
     * @param x1 <code>int</code>: The X coordinate of the first point on the line.
     * @param y1 <code>int</code>: The Y coordinate of the first point on the line.
     * @param x2 <code>int</code>: The X coordinate of the first point on the line.
     * @param y2 <code>int</code>: The Y coordinate of the first point on the line.
     * @return <code>int</code>: The Y coordinate of input X on the line.
     */
    private static Integer getYAtX(int x, int x1, int y1, int x2, int y2) {

        if (x < x1 && x < x2 || x > x1 && x > x2) {

            return null;
        }

        // If the slope is 0.0 or the slope is vertical, return the Y coordinate of the
        // first point.
        if (x1 == x2 || y1 == y2) {

            return y1;
        }

        // Calculate the slope of the line.
        double m = ((double) y2 - y1) / ((double) x2 - x1);

        // Calculate the Y value corresponding to the given X.
        int y = (int) (m * (x - x1)) + y1;

        return y;
    }

    /**
     * Returns the smaller of two <code>Integer</code> values, with the possibility
     * of a <code>null</code> value in either value.
     * 
     * @param val1 <code>Integer</code>: The first value to check.
     * @param val2 <code>Integer</code>: The second value to check.
     * @return <code>Integer</code>: The lowest value between <code>val1</code> and
     *         <code>val2</code>, or <code>null</code> if neither perameter has a
     *         value.
     */
    private static Integer min(Integer val1, Integer val2) {

        if (val1 == null) {

            return val2;
        }

        if (val2 == null) {

            return val1;
        }

        return (val1 < val2) ? val1 : val2;
    }

    /**
     * Returns the larger of two <code>Integer</code> values, with the possibility
     * of a <code>null</code> value in either value.
     * 
     * @param val1 <code>Integer</code>: The first value to check.
     * @param val2 <code>Integer</code>: The second value to check.
     * @return <code>Integer</code>: The highest value between <code>val1</code> and
     *         <code>val2</code>, or <code>null</code> if neither perameter has a
     *         value.
     */
    private static Integer max(Integer val1, Integer val2) {

        if (val1 == null) {

            return val2;
        }

        if (val2 == null) {

            return val1;
        }

        return (val1 > val2) ? val1 : val2;
    }
}
