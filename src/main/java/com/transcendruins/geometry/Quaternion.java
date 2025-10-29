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
 * <code>Quaternion</code>: A class representing a mathematical quaternion.
 */
public final class Quaternion {

    public static final Quaternion IDENTITY_QUATERNION = new Quaternion(1, 0, 0, 0);

    /**
     * <code>double</code>: The real (<code>r</code>)component of this
     * <code>Quaternion</code> instance.
     */
    private final double r;

    /**
     * Retrieves the <code>real</code> (w) component of this <code>Quaternion</code>
     * instance.
     * 
     * @return <code>double</code>: The <code>r</code> field of this
     *         <code>Quaternion</code> instance.
     */
    public double getR() {

        return r;
    }

    /**
     * <code>double</code>: The first imaginary (<code>i</code>) component of this
     * <code>Quaternion</code> instance.
     */
    private final double i;

    /**
     * Retrieves the <code>i</code> (x) component of this <code>Quaternion</code>
     * instance.
     * 
     * @return <code>double</code>: The <code>i</code> field of this
     *         <code>Quaternion</code> instance.
     */
    public double getI() {

        return i;
    }

    /**
     * <code>double</code>: The second imaginary (<code>j</code>) component of this
     * <code>Quaternion</code> instance.
     */
    private final double j;

    /**
     * Retrieves the <code>j</code> (y) component of this <code>Quaternion</code>
     * instance.
     * 
     * @return <code>double</code>: The <code>j</code> field of this
     *         <code>Quaternion</code> instance.
     */
    public double getJ() {

        return j;
    }

    /**
     * <code>double</code>: The third imaginary (<code>k</code>) component of this
     * <code>Quaternion</code> instance.
     */
    private final double k;

    /**
     * Retrieves the <code>k</code> (z) component of this <code>Quaternion</code>
     * instance.
     * 
     * @return <code>double</code>: The <code>k</code> field of this
     *         <code>Quaternion</code> instance.
     */
    public double getK() {

        return k;
    }

    /**
     * Creates a new instance of the <code>Quaternion</code> class.
     * 
     * @param r <code>double</code>: The real (<code>r</code>)component of this
     *          <code>Quaternion</code> instance.
     * @param i <code>double</code>: The first imaginary (<code>i</code>) component
     *          of this <code>Quaternion</code> instance.
     * @param j <code>double</code>: The second imaginary (<code>j</code>) component
     *          of this <code>Quaternion</code> instance.
     * @param k <code>double</code>: The third imaginary (<code>k</code>) component
     *          of this <code>Quaternion</code> instance.
     */
    public Quaternion(double r, double i, double j, double k) {

        this.r = r;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    /**
     * Creates a new <code>Quaternion</code> instance using the raw vector in the
     * form <code>e^(θ•[uX•i + uY•j + uZ•k])</code>.
     * 
     * @param vector <code>Vector</code>: The vector of this <code>Quaternion</code>
     *               instance.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public static Quaternion fromVector(Vector vector) {

        return new Quaternion(0, vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Creates a new <code>Quaternion</code> instance using the Euler quaternion
     * rotational formula <code>e^(θ•[uX•i + uY•j + uZ•k])</code>.
     * 
     * @param theta <code>double</code>: The rotation component of this
     *              <code>Quaternion</code> instance.
     * @param u     <code>Vector</code>: The rotational axis of this
     *              <code>Quaternion</code> instance.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public static Quaternion fromEulerRotation(double theta, Vector u) {

        double magnitude = u.magnitude();

        if (theta % Math.TAU == 0 || magnitude == 0) {

            return Quaternion.IDENTITY_QUATERNION;
        }

        u = u.multiply(1.0 / magnitude);

        double sinHalf = Math.sin(theta / 2);

        return new Quaternion(Math.cos(theta / 2), u.getX() * sinHalf, u.getY() * sinHalf, u.getZ() * sinHalf);
    }

    public static Quaternion fromHeading(double heading) {

        return fromEulerRotation(heading, 0, 0);
    }

    public static Quaternion fromPitch(double pitch) {

        return fromEulerRotation(pitch, 0, Math.PI / 2);
    }

    public static Quaternion fromRoll(double roll) {

        return fromEulerRotation(roll, Math.PI / 2, Math.PI / 2);
    }

    public static Quaternion fromEulerCoordinates(double heading, double pitch, double roll) {

        return fromRoll(roll).multiply(fromPitch(pitch)).multiply(fromHeading(heading));
    }

    /**
     * Creates a new <code>Quaternion</code> instance using the Euler quaternion
     * rotational formula <code>e^(θ • [uXi + uYj + uZk])</code>. Note that the
     * components <code>uXi + uYj + uZk</code> are calculated using the provided
     * horizontal and vertical unit sphere coordinates.
     * 
     * @param theta       <code>double</code>: The rotation component of this
     *                    <code>Quaternion</code> instance.
     * @param axisHeading <code>double</code>: The horizontal rotation of the axis
     *                    of rotation of this <code>Quaternion</code> instance.
     * @param axisPitch   <code>double</code>: The vertical rotation of the axis of
     *                    rotation of this <code>Quaternion</code> instance.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public static Quaternion fromEulerRotation(double theta, double axisHeading, double axisPitch) {

        return fromEulerRotation(theta, Vector.fromUnitSphere(axisHeading, axisPitch));
    }

    /**
     * Transforms this <code>Quaternion</code> instance by multiplying itself by
     * another <code>Quaternion</code> instance.
     * 
     * @param quaternion <code>Quaternion</code>: The quaternion to multiply by.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public Quaternion multiply(Quaternion quaternion) {

        double A = getR(), B = getI(), C = getJ(), D = getK();
        double a = quaternion.getR(), b = quaternion.getI(), c = quaternion.getJ(), d = quaternion.getK();

        return new Quaternion(A * a - B * b - C * c - D * d, A * b + B * a + C * d - D * c,
                A * c - B * d + C * a + D * b, A * d + B * c - C * b + D * a);
    }

    /**
     * Retrieves the magnitude value of this <code>Quaternion</code> instance (the
     * absolute distance between this <code>Quaternion</code> instance and the
     * origin).
     * 
     * @return <code>double</code>: The magnitude of this <code>Quaternion</code>
     *         instance.
     */
    public double magnitude() {

        return Math.sqrt(getR() * getR() + getI() * getI() + getJ() * getJ() + getK() * getK());
    }

    /**
     * Transforms this <code>Quaternion</code> into its conjugate by inversing its
     * <code>&lt;i, j, k&gt;</code> components.
     * 
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public Quaternion toConjugate() {

        return new Quaternion(r, -i, -j, -k);
    }

    /**
     * Converts this <code>Quaternion</code> instance into a matrix encoding the
     * entire rotation in a single step.
     * 
     * @return <code>Matrix</code>: The resulting matrix.
     */
    public Matrix toMatrix() {

        return new Matrix(3, 3, new double[] { 1 - 2 * (j * j + k * k), 2 * (i * j - r * k), 2 * (i * k + r * j),

                2 * (i * j + r * k), 1 - 2 * (i * i + k * k), 2 * (j * k - r * i),

                2 * (i * k - r * j), 2 * (j * k + r * i), 1 - 2 * (i * i + j * j) });
    }

    /**
     * Converts this <code>Quaternion</code> instance into a vector represented only
     * by its <code>&lt;i, j, k&gt;</code> components.
     * 
     * @return <code>Vector</code>: The resulting vector.
     */
    public Vector toVector() {

        return new Vector(getI(), getJ(), getK());
    }

    public Vector rotate(Vector position) {

        if (r == 1) {

            return position;
        }

        Quaternion vec = position.toQuaternion();
        return multiply(vec).multiply(toConjugate()).toVector();
    }

    public Matrix rotate(Matrix transform) {

        if (r == 1) {

            return transform;
        }

        Matrix rot = toMatrix();
        Matrix rotT = rot.transpose();
        return rot.multiply(transform).multiply(rotT);
    }

    /**
     * <code>String</code>: Returns the string representation of this
     * <code>Quaternion</code> instance.
     * 
     * @return <code>String</code>: This <code>Quaternion</code> instance in the
     *         following string representation: <br>
     *         "<code>a + bi + cj + dk</code>"
     */
    @Override
    public String toString() {

        StringBuilder concat = new StringBuilder().append(r);

        add(i, "i", concat);
        add(j, "j", concat);
        add(k, "k", concat);

        return concat.toString();
    }

    private static void add(double num, String unit, StringBuilder concat) {

        if (num == 0) {

            return;
        }

        if (concat.length() > 0) {

            concat.append(num > 0 ? " + " : " - ").append(Math.abs(num));
        } else {

            concat.append(num);
        }

        concat.append(unit);
    }
}
