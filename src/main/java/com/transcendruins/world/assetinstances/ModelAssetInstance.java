package com.transcendruins.world.assetinstances;

import java.util.HashMap;

import com.transcendruins.geometry.Position3D;
import com.transcendruins.geometry.Vector;
import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.packcompiling.assetschemas.ModelAssetSchemaComponents;
import com.transcendruins.packcompiling.assetschemas.animationcontrollers.AnimationControllerSchema;
import com.transcendruins.packcompiling.assetschemas.models.ModelSchema;
import com.transcendruins.packcompiling.assetschemas.rendermaterials.RenderMaterialSchema;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.animationcontrollers.AnimationControllerInstance;
import com.transcendruins.world.assetinstances.models.ModelInstance;
import com.transcendruins.world.assetinstances.rendermaterials.RenderMaterialInstance;

/**
 * <code>ModelAsset</code>: A class representing an <code>AssetInstance</code> instance which has the capability of being rendered using the standard <code>RenderInstance</code> method.
 */
public abstract class ModelAssetInstance extends AssetInstance {

    /**
     * <code>Position3D</code>: The position of this <code>ModelAssetInstance</code> instance.
     */
    private final Position3D position = new Position3D();

    /**
     * <code>boolean</code>: Whether or not the position of this <code>ModelAssetInstance</code> has been initialized.
     */
    private boolean positionInitialized = false;

    /**
     * <code>ModelInstance</code>: The model of this <code>ModelAssetInstance</code> instance.
     */
    private ModelInstance model;

    private double rotationOffset, axisHeading, axisPitch;

    /**
     * <code>RenderMaterialInstance</code>: The render material of this <code>ModelAssetInstance</code> instance.
     */
    private RenderMaterialInstance renderMaterial;

    /**
     * <code>AnimationControllerInstance</code>: The animation controller of this <code>ModelAssetInstance</code> instance.
     */
    private AnimationControllerInstance animationController;

    /**
     * Creates a new instance of the <code>ModelAssetInstance</code> class.
     * @param schema <code>AssetSchema</code>: The schema used to generate this <code>ModelAssetInstance</code> instance.
     * @param tileX <code>long</code>: The X coordinate of the tile to assign to this <code>ModelAssetInstance</code> instance.
     * @param tileZ <code>long</code>: The Z coordinate of the tile to assign to this <code>ModelAssetInstance</code> instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to combine with the heading, represented by the cardinal direction enums of the <code>World</code> class.
     * @param tileOffset <code>Position3D</code>: The tile offset to assign to this <code>ModelAssetInstance</code> instance.
     */
    public ModelAssetInstance(AssetSchema schema, long tileX, long tileZ, int cardinalDirection, Position3D tileOffset) {

        super(schema);
        setPosition(tileX, tileZ, cardinalDirection, tileOffset);
    }

    /**
     * Sets the <code>position</code> field of this <code>ModelAssetInstance</code> instance.
     * @param tileX <code>long</code>: The X coordinate of the tile to assign to this <code>ModelAssetInstance</code> instance.
     * @param tileZ <code>long</code>: The Z coordinate of the tile to assign to this <code>ModelAssetInstance</code> instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to combine with the heading, represented by the cardinal direction enums of the <code>World</code> class.
     * @param tileOffset <code>Position3D</code>: The tile offset to assign to this <code>ModelAssetInstance</code> instance.
     * @return <code>Position3D</code>: The adjusted <code>position</code> field of this <code>ModelAssetInstance</code> instance.
     */
    private void setPosition(long tileX, long tileZ, int cardinalDirection, Position3D tileOffset) {

        positionInitialized = true;
        applyPosition(tileX, tileZ, cardinalDirection, tileOffset);
    }

    /**
     * Applies a position to this <code>ModelAssetInstance</code> instance.
     * @param tileX <code>long</code>: The X coordinate of the tile to assign to this <code>ModelAssetInstance</code> instance.
     * @param tileZ <code>long</code>: The Z coordinate of the tile to assign to this <code>ModelAssetInstance</code> instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to combine with the heading, represented by the cardinal direction enums of the <code>World</code> class.
     * @param tileOffset <code>Position3D</code>: The tile offset to assign to this <code>ModelAssetInstance</code> instance.
     */
    protected void applyPosition(long tileX, long tileZ, int cardinalDirection, Position3D tileOffset) {

        Vector tileVector = new Vector(tileX * World.UNIT_TILE, 0, tileZ * World.UNIT_TILE);
        Vector tilePosition = tileVector.addVector(tileOffset.getPosition());

        position.setPosition(tilePosition);
        position.setRotation(tileOffset.getHeading() + Math.PI / 2 * cardinalDirection, tileOffset.getPitch(), true);
    }

    /**
     * Retrieves the position of this <code>ModelAssetInstance</code> instance.
     * @return <code>Position3D</code>: The <code>position</code> field of this <code>ModelAssetInstance</code> instance.
     */
    public final Position3D getPosition() {

        return position;
    }

    /**
     * Retrieves whether or not the position of this <code>ModelAssetInstance</code> instance has been initialized.
     * @return <code>boolean</code>: The <code>positionInitialized</code> field of this <code>ModelAssetInstance</code> instance.
     */
    public final boolean getPositionInitialized() {

        return positionInitialized;
    }

    /**
     * Retrieves the model and render context of this <code>ModelAssetInstance</code> instance packaged into a render instance.
     * @return <code>RenderInstance</code>: The generated <code>RenderInstance</code> instance.
     */
    public final RenderInstance getRenderInstance() {

        return new RenderInstance(model, renderMaterial, (animationController == null) ? new HashMap<>() : animationController.getBoneActors(), position, rotationOffset, axisHeading, axisPitch);
    }

    /**
     * Applies a component set to this <code>ModelAssetInstance</code> instance.
     * @param componentSet <code>AssetSchemaComponents</code>: The component set to apply.
     */
    @Override
    protected final void componentSetApplier(AssetSchemaComponents componentSet) {

        ModelAssetSchemaComponents components = (ModelAssetSchemaComponents) componentSet;

        if (components.getModelIdentifier() != null) {

            model = new ModelInstance((ModelSchema) getSchema(AssetType.MODEL, components.getModelIdentifier()));
        }

        if (components.getRenderMaterialIdentifier() != null) {

            renderMaterial = new RenderMaterialInstance((RenderMaterialSchema) getSchema(AssetType.RENDER_MATERIAL, components.getRenderMaterialIdentifier()));
        }

        if (components.getAnimationControllerIdentifier() != null) {

            animationController = new AnimationControllerInstance((AnimationControllerSchema) getSchema(AssetType.ANIMATION_CONTROLLER, components.getAnimationControllerIdentifier()));
        }

        rotationOffset = components.getRotationOffset();
        axisHeading = components.getAxisHeading();
        axisPitch = components.getAxisPitch();

        applyComponentSet(componentSet);
    }
}
