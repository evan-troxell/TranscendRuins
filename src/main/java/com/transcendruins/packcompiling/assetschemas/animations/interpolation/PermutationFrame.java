package com.transcendruins.packcompiling.assetschemas.animations.interpolation;

public final class PermutationFrame {

    private PositionFrame position;

    private RotationFrame rotation;

    private ScaleFrame scale;

    public PermutationFrame() {
    }

    public PermutationFrame(PositionFrame position, RotationFrame rotation, ScaleFrame scale) {

        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void applyPermutation(PermutationFrame permutation) {

        if (position == null) {

            position = permutation.position;
        }

        if (rotation == null) {

            rotation = permutation.rotation;
        }

        if (scale == null) {

            scale = permutation.scale;
        }
    }

    public boolean isComplete() {

        return position != null && rotation != null && scale != null;
    }

    public PositionFrame getPosition() {

        return position;
    }

    public RotationFrame getRotation() {

        return rotation;
    }

    public ScaleFrame getScale() {

        return scale;
    }
}
