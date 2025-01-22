package com.transcendruins.world.assetinstances.models;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.models.ModelSchemaAttributes;
import com.transcendruins.rendering.Model;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.AssetInstance;

/**
 * <code>ModelInstance</code>: A class representing a generated model instance.
 */
public final class ModelInstance extends AssetInstance {

    /**
     * <code>Model</code>: The current model which this <code>ModelInstance</code>
     * instance represents.
     */
    private Model model;

    /**
     * Retrieves the current model of this <code>ModelInstance</code> instance.
     * 
     * @return <code>Model</code>: The <code>model</code> field of this
     *         <code>ModelInstance</code> instance.
     */
    public Model getModel() {

        return model;
    }

    /**
     * <code>double</code>: The angle of this <code>ModelInstance</code> instance.
     */
    private double angle;

    /**
     * Retrieves the angle of this <code>ModelInstance</code> instance.
     * 
     * @return <code>double</code>: The <code>angle</code> field of this
     *         <code>ModelInstance</code> instance.
     */
    public double getAngle() {

        return angle;
    }

    /**
     * <code>double</code>: The heading of the axis of rotation of this
     * <code>ModelInstance</code> instance.
     */
    private double axisHeading;

    /**
     * Retrieves the heading of the axis of rotation of this
     * <code>ModelInstance</code> instance.
     * 
     * @return <code>double</code>: The <code>axisHeading</code> field of this
     *         <code>ModelInstance</code> instance.
     */
    public double getAxisHeading() {

        return axisHeading;
    }

    /**
     * <code>double</code>: The pitch of the axis of rotation of this
     * <code>ModelInstance</code> instance.
     */
    private double axisPitch;

    /**
     * Retrieves the pitch of the axis of rotation of this
     * <code>ModelInstance</code> instance.
     * 
     * @return <code>double</code>: The <code>axisPitch</code> field of this
     *         <code>ModelInstance</code> instance.
     */
    public double getAxisPitch() {

        return axisPitch;
    }

    /**
     * Creates a new instance of the <code>ModelInstance</code> class.
     * 
     * @param presets <code>ModelPresets</code>: The presets used to generate this
     *                <code>ModelInstance</code> instance.
     * @param world   <code>World</code>: The world copy to assign to this
     *                <code>ModelInstance</code> instance.
     */
    public ModelInstance(ModelPresets presets, World world) {

        super(presets, world);
    }

    /**
     * Applies an attribute set to this <code>ModelInstance</code> instance.
     * 
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to
     *                     apply.
     */
    @Override
    protected void applyAttributeSet(AssetSchemaAttributes attributeSet) {

        ModelSchemaAttributes attributes = (ModelSchemaAttributes) attributeSet;

        if (attributes.getModel() != null) {

            model = attributes.getModel();
        }

        if (attributes.getAngle() != null) {

            angle = attributes.getAngle();
        }

        if (attributes.getAxisHeading() != null) {

            axisHeading = attributes.getAxisHeading();
        }

        if (attributes.getAxisPitch() != null) {

            axisPitch = attributes.getAxisPitch();
        }
    }
}
