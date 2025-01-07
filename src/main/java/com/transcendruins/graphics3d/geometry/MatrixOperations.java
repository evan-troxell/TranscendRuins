package com.transcendruins.graphics3d.geometry;

import java.util.ArrayList;

/**
 * <code>MatrixOperations</code>: A class representing a list of matrix operations to be performed on a <code>Matrix</code> instance or a <code>Vector</code> instance.
 */
public final class MatrixOperations {

    /**
     * <code>MatrixOperations.Operations</code>: An enum class representing the various matrix operations to be performed.
     */
    public static enum Operations {

        /**
         * <code>MatrixOperations.Operations</code>: An enum representing addition of two matrices.
         */
        ADD_MATRIX,

        /**
         * <code>MatrixOperations.Operations</code>: An enum representing subtraction of two matrices.
         */
        SUBTRACT_MATRIX,

        /**
         * <code>MatrixOperations.Operations</code>: An enum representing multiplication between a matrix and a scalar.
         */
        MULTIPLY_SCALAR,

        /**
         * <code>MatrixOperations.Operations</code>: An enum representing multiplication between two matrices.
         */
        MULTIPLY_MATRIX,

        /**
         * <code>MatrixOperations.Operations</code>: An enum representing the transposition of a matrix.
         */
        TRANSPOSE,

        /**
         * <code>MatrixOperations.Operations</code>: An enum representing the determinant of a matrix
         */
        DETERMINANT,

        /**
         * <code>MatrixOperations.Operations</code>: An enum representing the dot product between two vectors.
         */
        DOT_PRODUCT,

        /**
         * <code>MatrixOperations.Operations</code>: An enum representing the cross product between two vectors.
         */
        CROSS_PRODUCT
    }

    /**
     * <code>Object[]</code>: The set of operations to be performed.
     */
    private final ArrayList<Object[]> operations = new ArrayList<>();

    /**
     * Creates a new instance of the <code>MatrixOperations</code> class.
     */
    public MatrixOperations() {}

    /**
     * Adds an operation type to the list of operations of this <code>MatrixOperations</code> instance.
     * @param type <code>MatrixOperations.Operations</code>: The operation enum to assign.
     * @param value <code>Object</code>: The operation value to assign.
     * @return <code>MatrixOperations</code>: This <code>MatrixOperations</code> instance, complete with the operation added.
     */
    public MatrixOperations addOperation(Operations type, Object value) {

        operations.add(new Object[] {type, value});
        return this;
    }

    /**
     * Adds all operations in another <code>MatrixOperations</code> instance to this <code>MatrixOperations</code> instance.
     * @param newOperations <code>MatrixOperations</code>: The operations to add.
     * @return <code>MatrixOperations</code>: This <code>MatrixOperations</code> instance, complete with the operations added.
     */
    public MatrixOperations addOperations(MatrixOperations newOperations) {

        operations.addAll(newOperations.operations);
        return this;
    }

    /**
     * Performs the set out list of operations on a <code>Matrix</code> instance.
     * @param matrix <code>Matrix</code>: The matrix to perform the operations in this <code>MatrixOperations</code> instance on.
     * @return <code>Object</code>: The resulting value.
     */
    public Object runOperations(Matrix matrix) {

        Object prevVal = matrix;

        for (Object[] i : operations) {

            Operations operation = (Operations) i[0];
            prevVal = switch (operation) {

                case ADD_MATRIX -> ((Matrix) prevVal).addMatrix((Matrix) i[1]);

                case SUBTRACT_MATRIX -> ((Matrix) prevVal).subtractMatrix((Matrix) i[1]);

                case MULTIPLY_SCALAR -> ((Matrix) prevVal).multiplyScalar((double) i[1]);

                case MULTIPLY_MATRIX -> ((Matrix) prevVal).multiplyMatrix((Matrix) i[1]);

                case TRANSPOSE -> ((Matrix) prevVal).transpose();

                case DETERMINANT -> ((Matrix) prevVal).determinant();

                default -> null;
            };

            if (prevVal == null) {

                return null;
            }
        }

        return prevVal;
    }

    /**
     * Performs the set out list of operations on a <code>Vector</code> instance.
     * @param vector <code>Vector</code>: The vector to perform the operations in this <code>MatrixOperations</code> instance on.
     * @return <code>Vector</code>: The resulting value.
     */
    public Object runOperations(Vector vector) {

        Object prevVal = vector;

        for (Object[] i : operations) {

            Operations operation = (Operations) i[0];
            prevVal = switch (operation) {

                case ADD_MATRIX -> {
                    if (!(i[1] instanceof Matrix) && !(i[1] instanceof Vector)) {

                        yield null;
                    }
                    yield ((Vector) prevVal).addMatrix((Matrix) i[1]);
                }
                    case SUBTRACT_MATRIX -> {
                        if (!(i[1] instanceof Matrix) && !(i[1] instanceof Vector)) {
                            
                            yield null;
                        }
                        yield ((Vector) prevVal).subtractMatrix(((Matrix) i[1]));
                }

                case MULTIPLY_SCALAR -> {
                    if (!(i[1] instanceof Double)) {

                        yield null;
                    }
                    yield ((Vector) prevVal).multiplyScalar((double) i[1]);
                }

                case MULTIPLY_MATRIX -> {
                    if (!(i[1] instanceof Matrix)) {

                        yield null;
                    }
                    yield ((Vector) prevVal).multiplyMatrix((Matrix) i[1]);
                }

                case DOT_PRODUCT -> {
                    if (!(i[1] instanceof Vector)) {
                        
                        yield null;
                    }
                    yield ((Vector) prevVal).dot((Vector) i[1]);
                }
                    case CROSS_PRODUCT -> {
                        if (!(i[1] instanceof Vector)) {

                            yield null;
                        }
                        yield ((Vector) prevVal).cross3D((Vector) i[1]);
                }

                default -> null;
            };

            if (prevVal == null) {

                return null;
            }
        }

        return prevVal;
    }
}
