package com.transcendruins.geometry.interpolation;

import com.transcendruins.geometry.Vector;

/**
 * <code>AlignedVector</code> A class representing an axis-aligned 3D transformation about a set of rotated axis.
 */
public class AlignedVector {

    /**
     * <code>Vector</code>: The transformation of this <code>AlignedVector</code> instance.
     */
    public final Vector transformation;

    /**
     * <code>Vector</code>: The axis alignment to apply to this <code>AlignedVector</code> instance.
     */
    public final Vector axisAlignment;

    /**
     * Creates a new instance of the <code>AlignedVector</code> class.
     * @param transformation <code>Vector</code>: The transformation to apply to this <code>AlignedVector</code> instance.
     * @param axisAlignment <code>Vector</code>: The axis alignment to apply to this <code>AlignedVector</code> instance.
     */
    public AlignedVector(Vector transformation, Vector axisAlignment) {

        this.transformation = transformation;
        this.axisAlignment = axisAlignment;
    }
}
