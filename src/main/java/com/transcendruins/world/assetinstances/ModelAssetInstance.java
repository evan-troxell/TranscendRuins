package com.transcendruins.world.assetinstances;

import java.util.HashMap;

import com.transcendruins.graphics3d.Position3D;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.ModelAssetSchemaAttributes;
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
     * @param schema            <code>AssetPresets</code>: The presets used to
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
    public ModelAssetInstance(AssetPresets presets, World world, long tileX, long tileZ, int cardinalDirection,
            Vector tileOffset) {

        super(presets, world);
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
        offset.setRotation(Math.PI / 2 * -cardinalDirection, 0, true);
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
                (animationController == null) ? new HashMap<>() : animationController.evaluateAnimations(), offset);
    }

    /**
     * Applies an attribute set to this <code>ModelAssetInstance</code> instance.
     * 
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to
     *                     apply.
     */
    @Override
    protected final void attributeSetApplier(AssetSchemaAttributes attributeSet) {

        ModelAssetSchemaAttributes attributes = (ModelAssetSchemaAttributes) attributeSet;

        if (attributes.getModel() != null) {

            model = new ModelInstance(attributes.getModel(), getWorld());
        }

        if (attributes.getRenderMaterial() != null) {

            renderMaterial = new RenderMaterialInstance(attributes.getRenderMaterial(), getWorld());
        }

        if (attributes.getAnimationController() != null) {

            animationController = new AnimationControllerInstance(attributes.getAnimationController(), getWorld());
        }

        applyAttributeSet(attributeSet);
    }

    /**
     * Performs all update actions of this <code>ModelAsseetInstance</code>
     * instance.
     */
    public final void onUpdate() {

        if (animationController != null) {

            animationController.evaluateTransitions();
        }
        update();
    }

    /**
     * Performs all instance updates of this <code>ModelAssetInstance</code>
     * instance.
     */
    protected abstract void update();
}
