package com.transcendruins.graphics3d.geometry;

/**
 * <code>Vector</code>: A class representing a point in a variable-dimensional space.
 */
public final class
Vector extends Matrix {

    /**
     * <code>Vector</code>: A vector with no magnitude or orientation.
     */
    public static final Vector DEFAULT_VECTOR = new Vector(0, 0, 0);

    /**
     * <code>int</code>: A value representing 3 dimensions.
     */
    public static final int DIMENSION_3D = 3;

    /**
     * <code>int</code>: The dimensions of this <code>Vector</code> instance.
     */
    private final int dimensions;

    /**
     * Retrieves the dimensions of this <code>Vector</code> instance.
     * @return <code>int</code>: The <code>dimensions</code> field of this <code>Vector</code> instance.
     */
    public int getDimensions() {

        return dimensions;
    }

    /**
     * Creates a new instance of the <code>Vector</code> class using three dimensions.
     * @param x <code>double</code>: The X coordinate to assign to this <code>Vector</code> instance.
     * @param y <code>double</code>: The Y coordinate to assign to this <code>Vector</code> instance.
     * @param z <code>double</code>: The Z coordinate to assign to this <code>Vector</code> instance.
     */
    public Vector(double x, double y, double z) {

        this(new double[] {x, y, z});
    }

    /**
     * Creates a new instance of the <code>Vector</code> class.
     * @param values <code>double[dimensions]</code>: The coordinate values to assign to this <code>Vector</code> instance.
     */
    public Vector(double... values) {

        super(values.length, 1, values);
        dimensions = values.length;
    }

    /**
     * Creates a new instance of the <code>Vector</code> class.
     * @param matrix <code>Matrix</code>: The <code>Matrix</code> instance from which this <code>Vector</code> instance should be created.
     * @param use_column <code>boolean</code>: Whether or not the output matrix should utilize the first column instead of the first row.
     */
    public Vector(Matrix matrix, boolean use_column) {

        this(use_column ? matrix.getCol(0) : matrix.getRow(0));
    }

    /**
     * Creates a new instance of the <code>Vector</code> class as a point on the unit sphere, originating at point <code>&lt;0, 1, 0&gt;</code>.
     * @param heading <code>double</code>: The heading of the new <code>Vector</code> instance.
     * @param pitch <code>double</code>: The pitch of the new <code>Vector</code> instance.
     * @return <code>Vector</code>: The resulting vector.
     */
    public static Vector fromUnitSphere(double heading, double pitch) {

        double sinH = Math.sin(heading);
        double cosH = Math.cos(heading);

        double sinV = Math.sin(pitch);
        double cosV = Math.cos(pitch);

        return new Vector(sinV * cosH, cosV, sinV * sinH);
    }

    /**
     * Retrieves the X coordinate of this <code>Vector</code> instance.
     * @return <code>double</code>: The X coordinate of this <code>Vector</code> instance.
     */
    public double getX() {

        return get(0);
    }

    /**
     * Retrieves the Y coordinate of this <code>Vector</code> instance.
     * @return <code>double</code>: The Y coordinate of this <code>Vector</code> instance.
     */
    public double getY() {

        return get(1);
    }

    /**
     * Retrieves the Z coordinate of this <code>Vector</code> instance.
     * @return <code>double</code>: The Z coordinate of this <code>Vector</code> instance.
     */
    public double getZ() {

        return get(2);
    }

    /**
     * Retrieves a dimensional coordinate of this <code>Vector</code> instance.
     * @param n <code>int</code>: The dimension to retrieve.
     * @return <code>double</code>: The retrieved dimension.
     */
    public double get(int n) {

        return get(n, 0);
    }

    /**
     * Transforms this <code>Vector</code> instance by adding the coordinate values of another <code>Matrix</code> instance. If this <code>Vector</code> instance is incompatible with the <code>Matrix</code> instance being transformed by (the <code>dimensions</code> field of this <code>Vector</code> instance does not equal the <code>cols</code> field of the <code>matrix</code> perameter or the <code>rows</code> field of the <code>matrix</code> perameter does not equal 1), then <code>null</code> will be returned.
     * @param matrix <code>Matrix</code>: The matrix values to add to this <code>Vector</code> instance.
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
        return new Vector(vectorMatrix, false);
    }

    /**
     * Transforms this <code>Vector</code> instance by subtracting the coordinate values of another <code>Matrix</code> instance. If this <code>Vector</code> instance is incompatible with the <code>Matrix</code> instance being transformed by (the <code>dimensions</code> field of this <code>Vector</code> instance does not equal the <code>cols</code> field of the <code>matrix</code> perameter or the <code>rows</code> field of the <code>matrix</code> perameter does not equal 1), then <code>null</code> will be returned.
     * @param matrix <code>Matrix</code>: The matrix values to subtract from this <code>Vector</code> instance.
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
        return new Vector(vectorMatrix, false);
    }

    /**
     * Transforms this <code>Vector</code> instance by the coordinate values of another <code>Vector</code> instance. If this <code>Vector</code> instance is incompatible with the <code>Vector</code> instance being transformed by (the <code>dimensions</code> field of this <code>Vector</code> instance does not equal the <code>dimensions</code> field of the <code>vector</code> perameter), then <code>null</code> will be returned.
     * @param vector <code>Vector</code>: The vector values to add to this <code>Vector</code> instance.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    public Vector addVector(Vector vector) {

        if (vector == null) {

            return null;
        }

        return addMatrix(vector);
    }

    /**
     * Transforms this <code>Vector</code> instance by the negative coordinate values of another <code>Vector</code> instance. If this <code>Vector</code> instance is incompatible with the <code>Vector</code> instance being transformed by (the <code>dimensions</code> field of this <code>Vector</code> instance does not equal the <code>dimensions</code> field of the <code>vector</code> perameter), then <code>null</code> will be returned.
     * @param vector <code>Vector</code>: The vector values to subtract from this <code>Vector</code> instance.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    public Vector subtractVector(Vector vector) {

        if (vector == null) {

            return null;
        }

        return subtractMatrix(vector);
    }

    /**
     * Transforms this <code>Vector</code> instance by multiplying by a scalar to produce another <code>Vector</code> instance.
     * @param scalar <code>double</code>: The scalar value to multiply by.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    @Override
    public Vector multiplyScalar(double scalar) {

        Matrix vectorMatrix = super.multiplyScalar(scalar);
        if (vectorMatrix == null) {

            return null;
        }

        return new Vector(vectorMatrix, false);
    }

    /**
     * Transforms this <code>Vector</code> instance by multiplying by a <code>Matrix</code> instance to produce another <code>Vector</code> instance. If this <code>Vector</code> instance is incompatible with the <code>Matrix</code> instance being transformed by (the <code>dimensions</code> field of this <code>Vector</code> instance does not equal the <code>rows</code> field of the <code>matrix</code> perameter), then <code>null</code> will be returned.
     * @param matrix <code>Matrix</code>: The <code>Matrix</code> instance to multiply by.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    @Override
    public Vector multiplyMatrix(Matrix matrix) {

        if (matrix == null) {

            return null;
        }

        Matrix vectorMatrix = super.multiplyMatrix(matrix);
        if (vectorMatrix == null) {

            return null;
        }
        return new Vector(vectorMatrix, false);
    }

    /**
     * Transforms this <code>Vector</code> instance by projecting another <code>Vector</code> instance to produce another <code>Vector</code> instance. If this <code>Vector</code> instance is incompatible with the <code>Vector</code> instance being transformed by (the <code>dimensions</code> field of this <code>Vector</code> instance does not equal the <code>dimensions</code> field of the <code>vector</code> perameter), then <code>null</code> will be returned.
     * @param vector <code>Vector</code>: The vector to take the dot product using.
     * @return <code>double</code>: The dot product of the two <code>Vector</code> instances.
     */
    public Double dot(Vector vector) {

        if (vector == null || dimensions != vector.dimensions) {

            return null;
        }

        double newValue = 0.0;

        if (dimensions == DIMENSION_3D) {

            return getX() * vector.getX() + getY() * vector.getY() + getZ() * vector.getZ();
        }

        for (int i = 0; i < dimensions; i++) {

            newValue += get(i) * vector.get(i);
        }

        return newValue;
    }

    /**
     * Transforms this <code>Vector</code> instance by taking the cross product of it and another <code>Vector</code> instance to produce another <code>Vector</code> instance. If this <code>Vector</code> instance is incompatible with the <code>Vector</code> instance being transformed by (the <code>dimensions</code> field of this <code>Vector</code> instance does not equal 3 or the <code>dimensions</code> field of the <code>vector</code> perameter does not equal 3), then <code>null</code> will be returned.
     * @param vector <code>Vector</code>: The vector to take the cross product using.
     * @return <code>Vector</code>: The generated <code>Vector</code> instance.
     */
    public Vector cross3D(Vector vector) {

        if (vector == null || dimensions != DIMENSION_3D || vector.dimensions != DIMENSION_3D) {

            return null;
        }

        Matrix skewSymmetric = new Matrix(DIMENSION_3D, DIMENSION_3D, new double[] {

            0, -getZ(), getY(),
            getZ(), 0, -getX(),
            -getY(), getX(), 0
        });

        Matrix returnMatrix = skewSymmetric.multiplyMatrix(vector.transpose());

        // The resulting matrix will have the dimensions [1, 3], and thus the first column should be used instead of the first row.
        return new Vector(returnMatrix, true);
    }

    /**
     * Retrieves the magnitude value of this <code>Vector</code> instance (the absolute distance between this <code>Vector</code> instance and the origin).
     * @return <code>double</code>: The magnitude of this <code>Vector</code> instance.
     */
    public double magnitude() {

        double newValue = 0d;

        for (int i = 0; i < dimensions; i++) {

            newValue += get(i) * get(i);
        }

        return Math.sqrt(newValue);
    }

    /**
     * Converts this <code>Vector</code> instance into a quaternion represented only by its <code>&lt;x, y, z&gt;</code> components.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public Quaternion toQuaternion() {

        return Quaternion.fromVector(this);
    }

    /**
     * Returns the string representation of this <code>Vector</code> instance.
     * @return <code>String</code>: This <code>Vector</code> instance in the following string representation:
     * <br>"<code>&lt;a, b, c...&gt;</code>"
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
