package com.transcendruins.packcompiling.assetschemas.models;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.rendering.Model;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>ModelSchemaAttributes</code>: A class which represents the attributes
 * of a <code>ModelSchema</code> instance.
 */
public final class ModelSchemaAttributes extends AssetSchemaAttributes {

    /**
     * <code>Model</code>: The model which represents all polygons and bones of this
     * <code>ModelSchema</code> instance.
     */
    private final Model model;

    /**
     * Retrieves the model of this <code>ModelSchemaAttributes</code> instance.
     * 
     * @return <code>Identifier</code>: The <code>model</code> field of this
     *         <code>ModelSchemaAttributes</code> instance.
     */
    public Model getModel() {

        return model;
    }

    /**
     * <code>Double
     * </code>: The angle of this
     * <code>ModelPresets</code> instance.
     */
    private final Double angle;

    /**
     * Retrieves the angle of this
     * <code>ModelPresets</code> instance.
     * 
     * @return <code>Double</code>: The <code>angle</code> field of this
     *         <code>ModelPresets</code> instance.
     */
    public final Double getAngle() {

        return angle;
    }

    /**
     * <code>Double</code>: The heading of the axis of rotation of this
     * <code>ModelPresets</code> instance.
     */
    private final Double axisHeading;

    /**
     * Retrieves the heading of the axis of rotation of this
     * <code>ModelPresets</code> instance.
     * 
     * @return <code>Double</code>: The <code>axisHeading</code> field of this
     *         <code>ModelPresets</code> instance.
     */
    public final Double getAxisHeading() {

        return axisHeading;
    }

    /**
     * <code>Double</code>: The pitch of the axis of rotation of this
     * <code>ModelPresets</code> instance.
     */
    private final Double axisPitch;

    /**
     * Retrieves the pitch of the axis of rotation of this
     * <code>ModelPresets</code> instance.
     * 
     * @return <code>Double</code>: The <code>axisPitch</code> field of this
     *         <code>ModelPresets</code> instance.
     */
    public final Double getAxisPitch() {

        return axisPitch;
    }

    /**
     * Compiles this <code>ModelSchemaAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>ModelSchema</code>: The schema which created this
     *               <code>ModelSchemaAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to
     *               compile this <code>json</code> perameter.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>ModelSchemaAttributes</code> instance is the base
     *               attribute set of a <code>ModelSchemaAttributes</code>
     *               instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>ModelSchemaAttributes</code> instance.
     */
    public ModelSchemaAttributes(ModelSchema schema, TracedDictionary json, boolean isBase)
            throws LoggedException {

        super(schema, json, isBase);

        TracedEntry<TracedDictionary> modelEntry = json.getAsDictionary("model", !isBase);
        model = (modelEntry.containsValue()) ? new Model(modelEntry.getValue()) : null;

        TracedEntry<Double> angleEntry = json.getAsDouble("angle", true, isBase ? 0.0 : null);
        angle = (angleEntry.containsValue()) ? Math.toRadians(angleEntry.getValue()) : null;

        TracedEntry<Double> axisHeadingEntry = json.getAsDouble("axisHeading", true, isBase ? 0.0 : null);
        axisHeading = (axisHeadingEntry.containsValue()) ? Math.toRadians(axisHeadingEntry.getValue()) : null;

        TracedEntry<Double> axisPitchEntry = json.getAsDouble("axisPitch", true, isBase ? 0.0 : null);
        axisPitch = (axisPitchEntry.containsValue()) ? Math.toRadians(axisPitchEntry.getValue()) : null;

        finalizeData();
    }

    @Override
    public void finalizeData() {

    }
}
