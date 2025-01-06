package com.transcendruins.geometry;

/**
 * <code>Quaternion</code>: A class representing a mathematical quaternion.
 */
public final class Quaternion {

    /**
     * <code>double</code>: The real (<code>r</code>)component of this <code>Quaternion</code> instance.
     */
    private final double r;

    /**
     * <code>double</code>: The first imaginary (<code>i</code>) component of this <code>Quaternion</code> instance.
     */
    private final double i;

    /**
     * <code>double</code>: The second imaginary (<code>j</code>) component of this <code>Quaternion</code> instance.
     */
    private final double j;

    /**
     * <code>double</code>: The third imaginary (<code>k</code>) component of this <code>Quaternion</code> instance.
     */
    private final double k;

    /**
     * Creates a new instance of the <code>Quaternion</code> class.
     * @param r <code>double</code>: The real (<code>r</code>)component of this <code>Quaternion</code> instance.
     * @param i <code>double</code>: The first imaginary (<code>i</code>) component of this <code>Quaternion</code> instance.
     * @param j <code>double</code>: The second imaginary (<code>j</code>) component of this <code>Quaternion</code> instance.
     * @param k <code>double</code>: The third imaginary (<code>k</code>) component of this <code>Quaternion</code> instance.
     */
    private Quaternion(double r, double i, double j, double k) {

        this.r = r;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    /**
     * Creates a new <code>Quaternion</code> instance using the raw vector in the form <code>e^(θ•[uX•i + uY•j + uZ•k])</code>.
     * @param vector <code>Vector</code>: The vector of this <code>Quaternion</code> instance.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public static Quaternion fromVector(Vector vector) {

        return new Quaternion(0, vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Creates a new <code>Quaternion</code> instance using the Euler quaternion rotational formula <code>e^(θ•[uX•i + uY•j + uZ•k])</code>.
     * @param theta <code>double</code>: The rotation component of this <code>Quaternion</code> instance.
     * @param u <code>Vector</code>: The rotational axis of this <code>Quaternion</code> instance.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public static Quaternion fromEulerRotation(double theta, Vector u) {

        double sin_half = Math.sin(theta / 2);

        return new Quaternion(Math.cos(theta / 2), u.getX() * sin_half, u.getY() * sin_half, u.getZ() * sin_half);
    }

    /**
     * Creates a new <code>Quaternion</code> instance using the Euler quaternion rotational formula <code>e^(θ•[uX•i + uY•j + uZ•k])</code>.
     * Note that the components <code>uX•i + uY•j + uZ•k</code> are calculated using the provided horizontal and vertical unit sphere coordinates.
     * @param theta <code>double</code>: The rotation component of this <code>Quaternion</code> instance.
     * @param axisHeading <code>double</code>: The horizontal rotation of the axis of rotation of this <code>Quaternion</code> instance.
     * @param axisPitch <code>double</code>: The vertical rotation of the axis of rotation of this <code>Quaternion</code> instance.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public static Quaternion fromEulerRotation(double theta, double axisHeading, double axisPitch) {

        return fromEulerRotation(theta, Vector.fromUnitSphere(axisHeading, axisPitch));
    }

    /**
     * Multiplies this <code>Quaternion</code> by a scalar value.
     * @param scalar <code>double</code>: The scalar to multiply by.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public Quaternion multiply(double scalar) {

        return new Quaternion(getR() * scalar, getI() * scalar, getJ() * scalar, getK() * scalar);
    }

    /**
     * Transforms this <code>Quaternion</code> instance by multiplying itself by another <code>Quaternion</code> instance.
     * @param quaternion <code>Quaternion</code>: The quaternion to multiply by.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public Quaternion multiply(Quaternion quaternion) {

        double A = getR(), B = getI(), C = getJ(), D = getK();
        double a = quaternion.getR(), b = quaternion.getI(), c = quaternion.getJ(), d = quaternion.getK();

        return new Quaternion(
            A*a - B*b - C*c - D*d,
            A*b + B*a + C*d - D*c,
            A*c - B*d + C*a + D*b,
            A*d + B*c - C*b + D*a
        );
    }

    /**
     * Retrieves the magnitude value of this <code>Quaternion</code> instance (the absolute distance between this <code>Quaternion</code> instance and the origin).
     * @return <code>double</code>: The magnitude of this <code>Quaternion</code> instance.
     */
    public double magnitude() {

        return Math.sqrt(getR()*getR() + getI()*getI() + getJ()*getJ() + getK()*getK());
    }

    /**
     * Transforms this <code>Quaternion</code> into its conjugate by inversing its <code>&lt;i, j, k&gt;</code> components.
     * @return <code>Quaternion</code>: The resulting quaternion.
     */
    public Quaternion toConjugate() {
        
        return new Quaternion(r, -i, -j, -k);
    }

    /**
     * Converts this <code>Quaternion</code> instance into a matrix encoding the entire rotation in a single step.
     * @return <code>Matrix</code>: The resulting matrix.
     */
    public Matrix toMatrix() {

        return new Matrix(3, 3, new double[] {
            1 - 2*j*j - 2*k*k, 2*i*j - 2*r*k, 2*i*k + 2*r*k,
            2*i*j + 2*r*k, 1 - 2*i*i - 2*k*k, 2*j*k - 2*r*i,
            2*i*k - 2*r*j, 2*j*k + 2*r*i, 1 - 2*i*i - 2*j*j
        });
    }

    /**
     * Converts this <code>Quaternion</code> instance into a vector represented only by its <code>&lt;i, j, k&gt;</code> components.
     * @return <code>Vector</code>: The resulting vector.
     */
    public Vector toVector() {

        return new Vector(getI(), getJ(), getK());
    }

    /**
     * Retrieves the <code>real</code> (w) component of this <code>Quaternion</code> instance.
     * @return <code>double</code>: The <code>r</code> field of this <code>Quaternion</code> instance.
     */
    public double getR() {

        return r;
    }

    /**
     * Retrieves the <code>i</code> (x) component of this <code>Quaternion</code> instance.
     * @return <code>double</code>: The <code>i</code> field of this <code>Quaternion</code> instance.
     */
    public double getI() {

        return i;
    }

    /**
     * Retrieves the <code>j</code> (y) component of this <code>Quaternion</code> instance.
     * @return <code>double</code>: The <code>j</code> field of this <code>Quaternion</code> instance.
     */
    public double getJ() {

        return j;
    }

    /**
     * Retrieves the <code>k</code> (z) component of this <code>Quaternion</code> instance.
     * @return <code>double</code>: The <code>k</code> field of this <code>Quaternion</code> instance.
     */
    public double getK() {

        return k;
    }

    /**
     * <code>String</code>: Returns the string representation of this <code>Quaternion</code> instance.
     * @return <code>String</code>: This <code>Quaternion</code> instance in the following string representation:
     * <br>"<code>a + bi + cj + dk</code>"
     */
    @Override
    public String toString() {

        return r + " + " + i + "i + " + j + "j + " + k + "k";
    }
}
