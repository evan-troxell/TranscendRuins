package com.transcendruins.graphics3d.geometry;

/**
 * <code>Matrix</code>: A class representing a matrix of variable dimensions with assignable values.
 */
public class Matrix {

    /**
     * <code>int</code>: An enum representing a rotation about the X axis.
     */
    public static final int X_AXIS = 1;

    /**
     * <code>int</code>: An enum representing a rotation about the Y axis.
     */
    public static final int Y_AXIS = 2;

    /**
     * <code>int</code>: An enum representing a rotation about the Z axis.
     */
    public static final int Z_AXIS = 3;

    /**
     * <code>double[cols * rows]</code>: The values of this <code>Matrix</code> instance.
     */
    private final double[] values;

    /**
     * <code>int</code>: The number of columns in this <code>Matrix</code> instance.
     */
    private final int cols;

    /**
     * <code>int</code>: The number of rows in this <code>Matrix</code> instance.
     */
    private final int rows;

    /**
     * Creates a new instance of the <code>Matrix</code> class.
     * @param cols <code>int</code>: The number of columns in this <code>Matrix</code> instance.
     * @param rows <code>int</code>: The number of rows in this <code>Matrix</code> instance.
     * @param values <code>double[cols * rows]</code>: The values to assign to this <code>Matrix</code> instance.
     */
    public Matrix(int cols, int rows, double[] values) {

        this.cols = cols;
        this.rows = rows;

        this.values = new double[cols * rows];
        System.arraycopy(values, 0, this.values, 0, cols * rows);
    }

    /**
     * Creates a new instance of the <code>Matrix</code> class which represents a rotation about the X, Y, or Z axis.
     * @param radians <code>double</code>: The angle, in radians, of the rotated matrix.
     * @param axis <code>int</code>: The axis of the rotation, represented by the enums <code>X_AXIS</code>, <code>Y_AXIS</code>, and <code>Z_AXIS</code>.
     * @return <code>Matrix</code>: The rotated <code>Matrix</code> instance.
     */
    public static final Matrix getRotationalMatrix3X3(double radians, int axis) {

        // Create a different rotational matrix for each of the 3 axes.
        return switch (axis) {
            case X_AXIS -> new Matrix(Vector.DIMENSION_3D, Vector.DIMENSION_3D, new double[] {

                    1, 0, 0,
                    0, Math.cos(radians), Math.sin(radians),
                    0, -Math.sin(radians), Math.cos(radians)
                });

            case Y_AXIS -> new Matrix(Vector.DIMENSION_3D, Vector.DIMENSION_3D, new double[] {

                    Math.cos(radians), 0, -Math.sin(radians),
                    0, 1, 0,
                    Math.sin(radians), 0, Math.cos(radians)
                });

            case Z_AXIS -> new Matrix(Vector.DIMENSION_3D, Vector.DIMENSION_3D, new double[] {

                    Math.cos(radians), -Math.sin(radians), 0,
                    Math.sin(radians), Math.cos(radians), 0,
                    0, 0, 1
                });

            default -> null;
        };
    }

    /**
     * Creates a new instance of the <code>Matrix</code> class which represents a rotation about the X, Y, and Z axis.
     * @param radiansX <code>double</code>: The angle, in radians, of the matrix about the X axis.
     * @param radiansY <code>double</code>: The angle, in radians, of the matrix about the Y axis.
     * @param radiansZ <code>double</code>: The angle, in radians, of the matrix about the Z axis.
     * @return <code>Matrix</code>: The rotated <code>Matrix</code> instance.
     */
    public static final Matrix getRotationalMatrix3X3(double radiansX, double radiansY, double radiansZ) {

        // Create each axis from the input rotations.
        Matrix xRotation = getRotationalMatrix3X3(radiansX, X_AXIS);
        Matrix yRotation = getRotationalMatrix3X3(radiansY, Y_AXIS);
        Matrix zRotation = getRotationalMatrix3X3(radiansZ, Z_AXIS);

        // Combine the axis together before returning.
        // Matrix multiplication is commutative, which means that given matrices A, B, and C, (A * B) * C = A * (B * C).
        return xRotation.multiplyMatrix(yRotation).multiplyMatrix(zRotation);
    }

    /**
     * Creates a new instance of the <code>Matrix</code> class which represents a scalar multiplication along the X, Y, and Z axis.
     * @param xScale <code>double</code>: The scale of the matrix along the X axis.
     * @param yScale <code>double</code>: The scale of the matrix along the Y axis.
     * @param zScale <code>double</code>: The scale of the matrix along the Z axis.
     * @return <code>Matrix</code>: The scaled <code>Matrix</code> instance.
     */
    public static final Matrix getScaledMatrix3X3(double xScale, double yScale, double zScale) {

        // Creates a matrix in which the X, Y, and Z axis are scaled by the input perameters.
        Matrix scale = new Matrix(Vector.DIMENSION_3D, Vector.DIMENSION_3D, new double[] {

            xScale, 0, 0,
            0, yScale, 0,
            0, 0, zScale
        });

        return scale;
    }

    /**
     * Retrieves a value from this <code>Matrix</code> instance.
     * @param col <code>int</code>: The column of the value.
     * @param row <code>int</code>: The row of the value.
     * @return <code>double</code>: The retrieved value.
     */
    public final double get(int col, int row) {

        return values[col + row * cols];
    }

    /**
     * Retrieves an entire column of values from this <code>Matrix</code> instance.
     * @param col <code>int</code>: The column of values to retrieve.
     * @return <code>double[rows]</code>: The retrieved column of values.
     */
    public final double[] getCol(int col) {

        double[] newValues = new double[rows];

        // Retrieve each value in the column.
        for (int row = 0; row < rows; row++) {

            newValues[row] = get(col, row);
        }

        return newValues;
    }

    /**
     * Retrieves an entire row of values from this <code>Matrix</code> instance.
     * @param row <code>int</code>: The row of values to retrieve.
     * @return <code>double[cols]</code>: The retrieved row of values.
     */
    public final double[] getRow(int row) {

        double[] newValues = new double[cols];
        for (int col = 0; col < cols; col++) {

            newValues[col] = get(col, row);
        }

        return newValues;
    }

    /**
     * Transforms this <code>Matrix</code> instance by adding the values of another <code>Matrix</code> instance. If this <code>Matrix</code> instance is incompatible with the <code>Matrix</code> instance being transformed by (the <code>cols</code> field of this <code>Matrix</code> instance does not equal the <code>cols</code> field of the <code>matrix</code> perameter or the <code>rows</code> field of this <code>Matrix</code> instance does not equal the <code>rows</code> field of the <code>matrix</code> perameter), then <code>null</code> will be returned.
     * @param matrix <code>Matrix</code>: The matrix values to add to this <code>Matrix</code> instance.
     * @return <code>Matrix</code>: The generated <code>Matrix</code> instance.
     */
    public Matrix addMatrix(Matrix matrix) {

        if (matrix == null || cols != matrix.cols || rows != matrix.rows) {

            return null;
        }

        double[] newValues = new double[cols * rows];

        // Combine the values of each column and row together.
        for (int col = 0; col < cols; col++) {

            for (int row = 0; row < rows; row++) {

                newValues[col + row * cols] = get(col, row) + matrix.get(col, row);
            }
        }

        return new Matrix(cols, rows, newValues);
    }

    /**
     * Transforms this <code>Matrix</code> instance by subtracting the values of another <code>Matrix</code> instance. If this <code>Matrix</code> instance is incompatible with the <code>Matrix</code> instance being transformed by (the <code>cols</code> field of this <code>Matrix</code> instance does not equal the <code>cols</code> field of the <code>matrix</code> perameter or the <code>rows</code> field of this <code>Matrix</code> instance does not equal the <code>rows</code> field of the <code>matrix</code> perameter), then <code>null</code> will be returned.
     * @param matrix <code>Matrix</code>: The matrix values to subtract from this <code>Matrix</code> instance.
     * @return <code>Matrix</code>: The generated <code>Matrix</code> instance.
     */
    public Matrix subtractMatrix(Matrix matrix) {

        if (matrix == null || cols != matrix.cols || rows != matrix.rows) {

            return null;
        }

        double[] newValues = new double[cols * rows];

        // Take the difference of every row and column.
        for (int col = 0; col < cols; col++) {

            for (int row = 0; row < rows; row++) {

                newValues[col + row * cols] = get(col, row) - matrix.get(col, row);
            }
        }

        return new Matrix(cols, rows, newValues);
    }

    /**
     * Transforms this <code>Matrix</code> instance by multiplying by a scalar to produce another <code>Matrix</code> instance.
     * @param scalar <code>double</code>: The scalar value to multiply by.
     * @return <code>Matrix</code>: The generated <code>Matrix</code> instance.
     */
    public Matrix multiplyScalar(double scalar) {

        double[] newValues = new double[cols * rows];

        // Multiply each row and column by the scalar value.
        for (int col = 0; col < cols; col++) {

            for (int row = 0; row < rows; row++) {

                newValues[col + row * cols] = get(col, row) * scalar;
            }
        }

        return new Matrix(cols, rows, newValues);
    }

    /**
     * Transforms this <code>Matrix</code> instance by multiplying by another together to produce a third. If this <code>Matrix</code> instance is incompatible with the <code>Matrix</code> instance being transformed by (the <code>cols</code> field of this <code>Matrix</code> instance does not equal the <code>rows</code> field of the <code>matrix</code> perameter), then <code>null</code> will be returned.
     * @param matrix <code>Matrix</code>: The <code>Matrix</code> instance to multiply by.
     * @return <code>Matrix</code>: The generated <code>Matrix</code> instance.
     */
    public Matrix multiplyMatrix(Matrix matrix) {

        if (matrix == null || cols != matrix.rows) {

            return null;
        }

        int matrixCols = matrix.cols, matrixRows = rows;

        double[] newValues = new double[matrixCols * matrixRows];

        // Sum up the product between each entry in the row of this matrix by each entry in the column of the perameter matrix.
        for (int col = 0; col < matrixCols; col++) {

            for (int row = 0; row < matrixRows; row++) {

                double val = 0;
                for (int mult = 0; mult < cols; mult++) {

                    val += get(mult, row) * matrix.get(col, mult);
                }

                newValues[col + row * matrixCols] = val;
            }
        }

        return new Matrix(matrixCols, matrixRows, newValues);
    }

    /**
     * Transposes this <code>Matrix</code> instance by switching its rows and columns.
     * @return <code>Matrix</code>: The generated <code>Matrix</code> instance.
     */
    public Matrix transpose() {

        double[] newValues = new double[cols * rows];

        // For every column, copy the column into the next slot of the new array.
        for (int col = 0; col < cols; col++) {

            double[] column = getCol(col);
            System.arraycopy(column, 0, newValues, col * column.length, column.length);
        }

        return new Matrix(rows, cols, newValues);
    }

    /**
     * Takes the determinant of this <code>Matrix</code> instance.
     * @return <code>double</code>: The determinant of this <code>Matrix</code> instance.
     */
    public double determinant() {

        double newValue = 0;

        // Run the addition portion of the determinant.
        for (int col = 0; col < cols; col++) {

            double colValue = 1;
            for (int row = 0; row < rows; row++) {

                int colAdjusted = col + row;

                // If the sum of the column numbe and the row number is greater than the number of columns, the number of columns should be added onto the column adjusted to prevent from indexing outside of the matrix.
                if (col + row > cols) {

                    colAdjusted -= cols;
                }
                colValue *= get(colAdjusted, row);
            }

            newValue += colValue;
        }

        // Run the subtraction portion of the determinant.
        for (int col = 0; col < cols; col++) {

            double colValue = 1;
            for (int row = 0; row < rows; row++) {

                int colAdjusted = col + row;

                // If the  column number is less than the row number, the number of columns should be added onto the column adjusted to prevent from indexing outside of the matrix.
                if (col - row < 0) {

                    colAdjusted += cols;
                }
                colValue *= get(colAdjusted, row);
            }

            newValue -= colValue;
        }

        return newValue;
    }

    /**
     * Retrieves the number of columns of this <code>Matrix</code> instance.
     * @return <code>int</code>: The <code>cols</code> field of this <code>Matrix</code> instance.
     */
    public final int getCols() {

        return cols;
    }

    /**
     * Retrieves the number of rows of this <code>Matrix</code> instance.
     * @return <code>int</code>: The <code>rows</code> field of this <code>Matrix</code> instance.
     */
    public final int getRows() {

        return rows;
    }

    /**
     * <code>String</code>: Returns the string representation of this <code>Matrix</code> instance.
     * @return <code>String</code>: This <code>Matrix</code> instance in the following string representation: <br>"<code>[[a, b, c...],<br> [d, e, f...],<br> [g, h, i...]...]</code>"
     */
    @Override
    public String toString() {

        String returnVal = "[";

        for (int row = 0; row < rows; row++) {

            returnVal += "[";
            for (int col = 0; col < cols; col++) {

                returnVal += get(col, row);
                if (col != cols - 1) {

                    returnVal += ", ";
                }
            }
            returnVal += "]";

            if (row != rows - 1) {

                returnVal += ",\n ";
            }
        }

        return returnVal + "]";
    }
}
