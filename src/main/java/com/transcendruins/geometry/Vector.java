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

package com.transcendruins.geometry;

/**
 * <code>Vector</code>: A class representing a point in a variable-dimensional
 * space.
 */
public final class Vector extends Matrix {

    /**
     * <code>Vector</code>: A 3D vector with no magnitude or orientation.
     */
    public static final Vector IDENTITY_VECTOR = new Vector(0, 0, 0);

    /**
     * <code>Vector</code>: A 3D vector with all dimensions set to one.
     */
    public static final Vector ONE_VECTOR = new Vector(1, 1, 1);

    /**
     * <code>int</code>: The dimensions of this <code>Vector</code> instance.
     */
    private final int dimensions;

    /**
     * Retrieves the dimensions of this <code>Vector</code> instance.
     * 
     * @return <code>int</code>: The <code>dimensions</code> field of this
     *         <code>Vector</code> instance.
     */
    public int getDimensions() {

        return dimensions;
    }

    /**
     * Creates a new instance of the <code>Vector</code> class using three
     * dimensions.
     * 
     * @param x <code>double</code>: The X coordinate to assign to this
     *          <code>Vector</code> instance.
     * @param y <code>double</code>: The Y coordinate to assign to this
     *          <code>Vector</code> instance.
     * @param z <code>double</code>: The Z coordinate to assign to this
     *          <code>Vector</code> instance.
     */
    public Vector(double x, double y, double z) {

        this(new double[] { x, y, z });
    }

    /**
     * Creates a new instance of the <code>Vector</code> class.
     * 
     * @param values <code>double[dimensions]</code>: The coordinate values to
     *               assign to this <code>Vector</code> instance.
     */
    public Vector(double... values) {

        super(values.length, 1, values);
        dimensions = values.length;
    }

    /**
     * Creates a new instance of the <code>Vector</code> class.
     * 
     * @param matrix <code>Matrix</code>: The <code>Matrix</code> instance from
     *               which this <code>Vector</code> instance should be created.
     */
    public Vector(Matrix matrix) {

        this(matrix.getRow(0));
    }

    /**
     * Creates a new instance of the <code>Vector</code> class as a point on the
     * unit sphere, originating at point <code>&lt;0, 1, 0&gt;</code>.
     * 
     * @param heading <code>double</code>: The heading of the new
     *                <code>Vector</code> instance.
     * @param pitch   <code>double</code>: The pitch of the new <code>Vector</code>
     *                instance.
     * @return <code>Vector</code>: The resulting vector.
     */
    public static Vector fromUnitSphere(double heading, double pitch) {

        double sinH = Math.sin(heading);
        double cosH = Math.cos(heading);

        double sinV = Math.sin(pitch);
        double cosV = Math.cos(pitch);

        Vector val = new Vector(sinV * cosH, cosV, sinV * sinH);

        return val.multiply(1.0 / val.magnitude());
    }

    /**
     * Retrieves the X coordinate of this <code>Vector</code> instance.
     * 
     * @return <code>double</code>: The X coordinate of this <code>Vector</code>
     *         instance.
     */
    public double getX() {

        return get(0);
    }

    /**
     * Retrieves the Y coordinate of this <code>Vector</code> instance.
     * 
     * @return <code>double</code>: The Y coordinate of this <code>Vector</code>
     *         instance.
     */
    public double getY() {

        return get(1);
    }

    /**
     * Retrieves the Z coordinate of this <code>Vector</code> instance.
     * 
     * @return <code>double</code>: The Z coordinate of this <code>Vector</code>
     *         instance.
     */
    public double getZ() {

        return get(2);
    }

    /**
     * Retrieves a dimensional coordinate of this <code>Vector</code> instance.
     * 
     * @param n <code>int</code>: The dimension to retrieve.
     * @return <code>double</code>: The retrieved dimension.
     */
    public double get(int n) {

        return get(n, 0);
    }

    /**
     * Transforms this <code>Vector</code> instance by adding the coordinate values
     * of another <code>Matrix</code> instance. If this <code>Vector</code> instance
     * is incompatible with the <code>Matrix</code> instance being transformed by
     * (the <code>dimensions</code> field of this <code>Vector</code> instance does
     * not equal the <code>cols</code> field of the <code>matrix</code> perameter or
     * the <code>rows</code> field of the <code>matrix</code> perameter does not
     * equal 1), then <code>null</code> will be returned.
     * 
     * @param matrix <code>Matrix</code>: The matrix values to add to this
     *               <code>Vector</code> instance.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    @Override
    public Vector addMatrix(Matrix matrix) {

        if (matrix == null) {

            return null;
        }

        Matrix vectorMatrix = super.addMatrix(matrix);
        if (vectorMatrix == null) {

            return null;
        }
        return new Vector(vectorMatrix);
    }

    /**
     * Transforms this <code>Vector</code> instance by subtracting the coordinate
     * values of another <code>Matrix</code> instance. If this <code>Vector</code>
     * instance is incompatible with the <code>Matrix</code> instance being
     * transformed by (the <code>dimensions</code> field of this <code>Vector</code>
     * instance does not equal the <code>cols</code> field of the
     * <code>matrix</code> perameter or the <code>rows</code> field of the
     * <code>matrix</code> perameter does not equal 1), then <code>null</code> will
     * be returned.
     * 
     * @param matrix <code>Matrix</code>: The matrix values to subtract from this
     *               <code>Vector</code> instance.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    @Override
    public Vector subtractMatrix(Matrix matrix) {

        if (matrix == null) {

            return null;
        }

        Matrix vectorMatrix = super.subtractMatrix(matrix);
        if (vectorMatrix == null) {

            return null;
        }
        return new Vector(vectorMatrix);
    }

    /**
     * Transforms this <code>Vector</code> instance by the coordinate values of
     * another <code>Vector</code> instance. If this <code>Vector</code> instance is
     * incompatible with the <code>Vector</code> instance being transformed by (the
     * <code>dimensions</code> field of this <code>Vector</code> instance does not
     * equal the <code>dimensions</code> field of the <code>vector</code>
     * perameter), then <code>null</code> will be returned.
     * 
     * @param vector <code>Vector</code>: The vector values to add to this
     *               <code>Vector</code> instance.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    public final Vector add(Vector vector) {

        if (vector == IDENTITY_VECTOR) {

            return this;
        }

        return addMatrix(vector);
    }

    /**
     * Transforms this <code>Vector</code> instance by the negative coordinate
     * values of another <code>Vector</code> instance. If this <code>Vector</code>
     * instance is incompatible with the <code>Vector</code> instance being
     * transformed by (the <code>dimensions</code> field of this <code>Vector</code>
     * instance does not equal the <code>dimensions</code> field of the
     * <code>vector</code> perameter), then <code>null</code> will be returned.
     * 
     * @param vector <code>Vector</code>: The vector values to subtract from this
     *               <code>Vector</code> instance.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    public final Vector subtract(Vector vector) {

        if (vector == IDENTITY_VECTOR) {

            return this;
        }

        return subtractMatrix(vector);
    }

    /**
     * Transforms this <code>Vector</code> instance by multiplying by a scalar to
     * produce another <code>Vector</code> instance.
     * 
     * @param scalar <code>double</code>: The scalar value to multiply by.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    @Override
    public Vector multiply(double scalar) {

        Matrix vectorMatrix = super.multiply(scalar);
        if (vectorMatrix == null) {

            return null;
        }

        return new Vector(vectorMatrix);
    }

    /**
     * Transforms this <code>Vector</code> instance by multiplying by a
     * <code>Matrix</code> instance to produce another <code>Vector</code> instance.
     * If this <code>Vector</code> instance is incompatible with the
     * <code>Matrix</code> instance being transformed by (the
     * <code>dimensions</code> field of this <code>Vector</code> instance does not
     * equal the <code>rows</code> field of the <code>matrix</code> perameter), then
     * <code>null</code> will be returned.
     * 
     * @param matrix <code>Matrix</code>: The <code>Matrix</code> instance to
     *               multiply by.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    @Override
    public final Vector multiply(Matrix matrix) {

        if (matrix == Matrix.IDENTITY_3X3) {

            return this;
        }

        Matrix vectorMatrix = super.multiply(matrix);
        if (vectorMatrix == null) {

            return null;
        }

        return new Vector(vectorMatrix);
    }

    /**
     * Transforms this <code>Vector</code> instance by projecting another
     * <code>Vector</code> instance to produce another <code>Vector</code> instance.
     * If this <code>Vector</code> instance is incompatible with the
     * <code>Vector</code> instance being transformed by (the
     * <code>dimensions</code> field of this <code>Vector</code> instance does not
     * equal the <code>dimensions</code> field of the <code>vector</code>
     * perameter), then <code>null</code> will be returned.
     * 
     * @param vector <code>Vector</code>: The vector to take the dot product using.
     * @return <code>double</code>: The dot product of the two <code>Vector</code>
     *         instances.
     */
    public final Double dot(Vector vector) {

        if (vector == null || dimensions != vector.dimensions) {

            return null;
        }

        double newValue = 0.0;

        if (dimensions == 3) {

            return getX() * vector.getX() + getY() * vector.getY() + getZ() * vector.getZ();
        }

        for (int i = 0; i < dimensions; i++) {

            newValue += get(i) * vector.get(i);
        }

        return newValue;
    }

    /**
     * Transforms this <code>Vector</code> instance by taking the cross product of
     * it and another <code>Vector</code> instance to produce another
     * <code>Vector</code> instance. If this <code>Vector</code> instance is
     * incompatible with the <code>Vector</code> instance being transformed by (the
     * <code>dimensions</code> field of this <code>Vector</code> instance does not
     * equal 3 or the <code>dimensions</code> field of the <code>vector</code>
     * perameter does not equal 3), then <code>null</code> will be returned.
     * 
     * @param vector <code>Vector</code>: The vector to take the cross product
     *               using.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    public final Vector cross3D(Vector vector) {

        if (vector == null || dimensions != 3 || vector.dimensions != 3) {

            return null;
        }

        Matrix skewSymmetric = new Matrix(3, 3, new double[] {

                0, -vector.getZ(), vector.getY(), vector.getZ(), 0, -vector.getX(), -vector.getY(), vector.getX(), 0 });

        return multiply(skewSymmetric);
    }

    /**
     * Rotates this <code>Vector</code> instance about the origin.
     * 
     * @param quat <code>Quaternion</code>: The rotational quaternion to rotate
     *             using.
     * @return <code>Vector</code>: The resulting vector.
     */
    @Override
    public final Vector rotate(Quaternion quat) {

        if (quat == Quaternion.IDENTITY_QUATERNION) {

            return this;
        }

        return quat.multiply(toQuaternion()).multiply(quat.toConjugate()).toVector();
    }

    /**
     * Retrieves the magnitude value of this <code>Vector</code> instance (the
     * absolute distance between this <code>Vector</code> instance and the origin).
     * 
     * @return <code>double</code>: The magnitude of this <code>Vector</code>
     *         instance.
     */
    public final double magnitude() {

        double newValue = 0d;

        for (int i = 0; i < dimensions; i++) {

            newValue += get(i) * get(i);
        }

        return Math.sqrt(newValue);
    }

    /**
     * Converts this <code>Vector</code> instance into a quaternion represented only
     * by its <code>&lt;x, y, z&gt;</code> components.
     * 
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public Quaternion toQuaternion() {

        return Quaternion.fromVector(this);
    }

    /**
     * Computes the average position between a list of input vectors.
     * 
     * @param vectors <code>Vector...</code>: The vectors whose average to take.
     * @return <code>Vector</code>: The resulting average.
     */
    public static Vector getAverage(Vector... vectors) {

        Vector sum = IDENTITY_VECTOR;

        for (Vector vector : vectors) {

            sum = sum.add(vector);
        }

        return sum.multiply(1.0 / Math.max(vectors.length, 1));
    }

    /**
     * Returns the string representation of this <code>Vector</code> instance.
     * 
     * @return <code>String</code>: This <code>Vector</code> instance in the
     *         following string representation: <br>
     *         "<code>&lt;a, b, c...&gt;</code>"
     */
    @Override
    public String toString() {

        String returnString = "<";

        for (int i = 0; i < dimensions; i++) {

            returnString += get(i);
            if (i != dimensions - 1) {

                returnString += ", ";
            }
        }
        returnString += ">";
        return returnString;
    }
}
