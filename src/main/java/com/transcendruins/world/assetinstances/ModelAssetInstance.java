package com.transcendruins.world.assetinstances;

import java.util.HashMap;

import com.transcendruins.graphics3d.Position3D;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.packcompiling.assetschemas.AssetSchema;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.packcompiling.assetschemas.ModelAssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.animationcontrollers.AnimationControllerSchema;
import com.transcendruins.packcompiling.assetschemas.models.ModelSchema;
import com.transcendruins.packcompiling.assetschemas.rendermaterials.RenderMaterialSchema;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.animationcontrollers.AnimationControllerInstance;
import com.transcendruins.world.assetinstances.models.ModelInstance;
import com.transcendruins.world.assetinstances.rendermaterials.RenderMaterialInstance;

/**
 * <code>ModelAsset</code>: A class representing an <code>AssetInstance</code>
 * instance which has the capability of being rendered using the standard
 * <code>RenderInstance</code> method.
 */
public abstract class ModelAssetInstance extends AssetInstance {

    /**
     * <code>Position3D</code>: The position of this <code>ModelAssetInstance</code>
     * instance.
     */
    private final Position3D offset = new Position3D();

    /**
     * Retrieves the offset of this <code>ModelAssetInstance</code> instance.
     * 
     * @return <code>Position3D</code>: The <code>offset</code> field of this
     *         <code>ModelAssetInstance</code> instance.
     */
    public final Position3D getOffset() {

        return offset;
    }

    /**
     * <code>boolean</code>: Whether or not the position of this
     * <code>ModelAssetInstance</code> has been initialized.
     */
    private boolean positionInitialized = false;

    /**
     * Retrieves whether or not the position of this <code>ModelAssetInstance</code>
     * instance has been initialized.
     * 
     * @return <code>boolean</code>: The <code>positionInitialized</code> field of
     *         this <code>ModelAssetInstance</code> instance.
     */
    public final boolean getPositionInitialized() {

        return positionInitialized;
    }

    /**
     * <code>ModelInstance</code>: The model of this <code>ModelAssetInstance</code>
     * instance.
     */
    private ModelInstance model;

    private double rotationOffset, axisHeading, axisPitch;

    /**
     * <code>RenderMaterialInstance</code>: The render material of this
     * <code>ModelAssetInstance</code> instance.
     */
    private RenderMaterialInstance renderMaterial;

    /**
     * <code>AnimationControllerInstance</code>: The animation controller of this
     * <code>ModelAssetInstance</code> instance.
     */
    private AnimationControllerInstance animationController;

    /**
     * Creates a new instance of the <code>ModelAssetInstance</code> class.
     * 
     * @param schema            <code>AssetSchema</code>: The schema used to
     *                          generate this <code>ModelAssetInstance</code>
     *                          instance.
     * @param world             <code>World</code>: The world copy to assign to this
     *                          <code>ModelAssetInstance</code> instance.
     * @param tileX             <code>long</code>: The X coordinate of the tile to
     *                          assign to this <code>ModelAssetInstance</code>
     *                          instance.
     * @param tileZ             <code>long</code>: The Z coordinate of the tile to
     *                          assign to this <code>ModelAssetInstance</code>
     *                          instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to assign
     *                          to this <code>ModelAssetInstance</code> instance,
     *                          represented by the cardinal direction enums of the
     *                          <code>World</code> class.
     * @param tileOffset        <code>Vector</code>: The tile offset to assign to
     *                          this <code>ModelAssetInstance</code> instance.
     */
    public ModelAssetInstance(AssetSchema schema, World world, long tileX, long tileZ, int cardinalDirection,
            Vector tileOffset) {

        super(schema, world);
        setOffset(tileX, tileZ, cardinalDirection, tileOffset);
    }

    /**
     * Sets the offset of this <code>ModelAssetInstance</code> instance.
     * 
     * @param tileX             <code>long</code>: The X coordinate of the tile to
     *                          assign to this <code>ModelAssetInstance</code>
     *                          instance.
     * @param tileZ             <code>long</code>: The Z coordinate of the tile to
     *                          assign to this <code>ModelAssetInstance</code>
     *                          instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to assign
     *                          to this <code>ModelAssetInstance</code> instance,
     *                          represented by the cardinal direction enums of the
     *                          <code>World</code> class.
     * @param tileOffset        <code>Vector</code>: The tile offset to assign to
     *                          this <code>ModelAssetInstance</code> instance.
     */
    private void setOffset(long tileX, long tileZ, int cardinalDirection, Vector tileOffset) {

        positionInitialized = true;
        applyOffset(tileX, tileZ, cardinalDirection, tileOffset);
    }

    /**
     * Applies an offset to this <code>ModelAssetInstance</code> instance.
     * 
     * @param tileX             <code>long</code>: The X coordinate of the tile to
     *                          assign to this <code>ModelAssetInstance</code>
     *                          instance.
     * @param tileZ             <code>long</code>: The Z coordinate of the tile to
     *                          assign to this <code>ModelAssetInstance</code>
     *                          instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to assign
     *                          to this <code>ModelAssetInstance</code> instance,
     *                          represented by the cardinal direction enums of the
     *                          <code>World</code> class.
     * @param tileOffset        <code>Vector</code>: The tile offset to assign to
     *                          this <code>ModelAssetInstance</code> instance.
     */
    protected void applyOffset(long tileX, long tileZ, int cardinalDirection, Vector tileOffset) {

        Vector tileVector = new Vector(tileX * World.UNIT_TILE, 0, tileZ * World.UNIT_TILE);
        offset.setPosition(tileVector.addVector(tileOffset));
        offset.setRotation(Math.PI / 2 * cardinalDirection, 0, true);
    }

    /**
     * Retrieves the model and render context of this
     * <code>ModelAssetInstance</code> instance packaged into a render instance.
     * 
     * @return <code>RenderInstance</code>: The generated
     *         <code>RenderInstance</code> instance.
     */
    public final RenderInstance getRenderInstance() {

        return new RenderInstance(model, renderMaterial,
                (animationController == null) ? new HashMap<>() : animationController.evaluateAnimations(), offset,
                rotationOffset, axisHeading, axisPitch);
    }

    /**
     * Applies a attribute set to this <code>ModelAssetInstance</code> instance.
     * 
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to
     *                     apply.
     */
    @Override
    protected final void attributeSetApplier(AssetSchemaAttributes attributeSet) {

        ModelAssetSchemaAttributes attributes = (ModelAssetSchemaAttributes) attributeSet;

        if (attributes.getModelIdentifier() != null) {

            model = new ModelInstance((ModelSchema) getSchema(AssetType.MODEL, attributes.getModelIdentifier()),
                    getWorld());
        }

        if (attributes.getRenderMaterialIdentifier() != null) {

            renderMaterial = new RenderMaterialInstance((RenderMaterialSchema) getSchema(AssetType.RENDER_MATERIAL,
                    attributes.getRenderMaterialIdentifier()), getWorld());
        }

        if (attributes.getAnimationControllerIdentifier() != null) {

            animationController = new AnimationControllerInstance((AnimationControllerSchema) getSchema(
                    AssetType.ANIMATION_CONTROLLER, attributes.getAnimationControllerIdentifier()), getWorld());
        }

        if (attributes.getRotationOffset() != null) {

            rotationOffset = attributes.getRotationOffset();
        }

        if (attributes.getAxisHeading() != null) {

            axisHeading = attributes.getAxisHeading();
        }

        if (attributes.getAxisPitch() != null) {

            axisPitch = attributes.getAxisPitch();
        }

        applyAttributeSet(attributeSet);
    }
}
