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
 * <code>Matrix</code>: A class representing a matrix of variable dimensions
 * with assignable values.
 */
sealed public class Matrix permits Vector {

    public static final Matrix IDENTITY_3X3 = getScaledMatrix3X3(1, 1, 1);

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
     * <code>double[cols * rows]</code>: The values of this <code>Matrix</code>
     * instance.
     */
    private final double[] values;

    /**
     * <code>int</code>: The number of columns in this <code>Matrix</code> instance.
     */
    private final int cols;

    /**
     * Retrieves the number of columns of this <code>Matrix</code> instance.
     * 
     * @return <code>int</code>: The <code>cols</code> field of this
     *         <code>Matrix</code> instance.
     */
    public final int getCols() {

        return cols;
    }

    /**
     * <code>int</code>: The number of rows in this <code>Matrix</code> instance.
     */
    private final int rows;

    /**
     * Retrieves the number of rows of this <code>Matrix</code> instance.
     * 
     * @return <code>int</code>: The <code>rows</code> field of this
     *         <code>Matrix</code> instance.
     */
    public final int getRows() {

        return rows;
    }

    /**
     * Creates a new instance of the <code>Matrix</code> class.
     * 
     * @param cols   <code>int</code>: The number of columns in this
     *               <code>Matrix</code> instance.
     * @param rows   <code>int</code>: The number of rows in this
     *               <code>Matrix</code> instance.
     * @param values <code>double[cols * rows]</code>: The values to assign to this
     *               <code>Matrix</code> instance.
     */
    public Matrix(int cols, int rows, double[] values) {

        this.cols = cols;
        this.rows = rows;

        this.values = new double[cols * rows];
        System.arraycopy(values, 0, this.values, 0, cols * rows);
    }

    /**
     * Creates a new instance of the <code>Matrix</code> class which represents a
     * scalar multiplication along the X, Y, and Z axis.
     * 
     * @param xScale <code>double</code>: The scale of the matrix along the X axis.
     * @param yScale <code>double</code>: The scale of the matrix along the Y axis.
     * @param zScale <code>double</code>: The scale of the matrix along the Z axis.
     * @return <code>Matrix</code>: The scaled <code>Matrix</code> instance.
     */
    public static final Matrix getScaledMatrix3X3(double xScale, double yScale, double zScale) {

        // Creates a matrix in which the X, Y, and Z axis are scaled by the input
        // perameters.
        Matrix scale = new Matrix(3, 3, new double[] {

                xScale, 0, 0, 0, yScale, 0, 0, 0, zScale });

        return scale;
    }

    /**
     * Retrieves a value from this <code>Matrix</code> instance.
     * 
     * @param col <code>int</code>: The column of the value.
     * @param row <code>int</code>: The row of the value.
     * @return <code>double</code>: The retrieved value.
     */
    public final double get(int col, int row) {

        return values[col + row * cols];
    }

    /**
     * Retrieves an entire column of values from this <code>Matrix</code> instance.
     * 
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
     * 
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
     * Transforms this <code>Matrix</code> instance by adding the values of another
     * <code>Matrix</code> instance. If this <code>Matrix</code> instance is
     * incompatible with the <code>Matrix</code> instance being transformed by (the
     * <code>cols</code> field of this <code>Matrix</code> instance does not equal
     * the <code>cols</code> field of the <code>matrix</code> perameter or the
     * <code>rows</code> field of this <code>Matrix</code> instance does not equal
     * the <code>rows</code> field of the <code>matrix</code> perameter), then
     * <code>null</code> will be returned.
     * 
     * @param matrix <code>Matrix</code>: The matrix values to add to this
     *               <code>Matrix</code> instance.
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
     * Transforms this <code>Matrix</code> instance by subtracting the values of
     * another <code>Matrix</code> instance. If this <code>Matrix</code> instance is
     * incompatible with the <code>Matrix</code> instance being transformed by (the
     * <code>cols</code> field of this <code>Matrix</code> instance does not equal
     * the <code>cols</code> field of the <code>matrix</code> perameter or the
     * <code>rows</code> field of this <code>Matrix</code> instance does not equal
     * the <code>rows</code> field of the <code>matrix</code> perameter), then
     * <code>null</code> will be returned.
     * 
     * @param matrix <code>Matrix</code>: The matrix values to subtract from this
     *               <code>Matrix</code> instance.
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
     * Transforms this <code>Matrix</code> instance by multiplying by a scalar to
     * produce another <code>Matrix</code> instance.
     * 
     * @param scalar <code>double</code>: The scalar value to multiply by.
     * @return <code>Matrix</code>: The generated <code>Matrix</code> instance.
     */
    public Matrix multiply(double scalar) {

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
     * Transforms this <code>Matrix</code> instance by multiplying by another
     * together to produce a third. If this <code>Matrix</code> instance is
     * incompatible with the <code>Matrix</code> instance being transformed by (the
     * <code>cols</code> field of this <code>Matrix</code> instance does not equal
     * the <code>rows</code> field of the <code>matrix</code> perameter), then
     * <code>null</code> will be returned.
     * 
     * @param matrix <code>Matrix</code>: The <code>Matrix</code> instance to
     *               multiply by.
     * @return <code>Matrix</code>: The generated <code>Matrix</code> instance.
     */
    public Matrix multiply(Matrix matrix) {

        if (matrix == null || cols != matrix.rows) {

            return null;
        }

        int matrixCols = matrix.cols, matrixRows = rows;

        double[] newValues = new double[matrixCols * matrixRows];

        // Sum up the product between each entry in the row of this matrix by each entry
        // in the column of the perameter matrix.
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
     * Transposes this <code>Matrix</code> instance by switching its rows and
     * columns.
     * 
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
     * Rotates this <code>Matrix</code> instance about the origin.
     * 
     * @param quat <code>Quaternion</code>: The rotational quaternion to rotate
     *             using.
     * @return <code>Matrix</code>: The resulting matrix.
     */
    public Matrix rotate(Quaternion quat) {

        Matrix rot = quat.toMatrix();

        return rot.multiply(this).multiply(rot.transpose());
    }

    /**
     * <code>String</code>: Returns the string representation of this
     * <code>Matrix</code> instance.
     * 
     * @return <code>String</code>: This <code>Matrix</code> instance in the
     *         following string representation: <br>
     *         "<code>[[a, b, c...],<br> [d, e, f...],<br> [g, h, i...]...]</code>"
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
